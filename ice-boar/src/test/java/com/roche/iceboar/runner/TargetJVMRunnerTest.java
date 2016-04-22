package com.roche.iceboar.runner;

import com.roche.iceboar.progressevent.JREUnzippedDetailInfo;
import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static java.lang.Thread.sleep;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TargetJVMRunnerTest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
    private final ProgressEventFactory progressEventFactory = new ProgressEventFactory();
    private final ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
    private ByteArrayOutputStream outputStream;
    private PrintStream oldOut;

    @BeforeClass
    public void beforeClass() {
        oldOut = System.out;

        outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        System.setOut(ps);
    }

    @AfterClass
    public void afterClass() {
        System.setOut(oldOut);
    }

    @BeforeMethod
    public void before() {
        outputStream.reset();
    }


    @Test
    public void shouldExecCommandWhenAppStartingEvent() throws InterruptedException {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .operationSystemName("Win7")
                                                .build();
        TargetJVMRunner runner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        when(executableCommandFactory.createRunTargetApplicationCommand(any(GlobalSettings.class), anyString()))
                .thenReturn(command);

        updateJREUnzippedEventOnRunner(runner);

        ProgressEvent appStartingEvent = progressEventFactory.getAppStartingEvent();
        Process process = createMockProcessWithEmptyStreams();
        when(command.exec())
                .thenReturn(process);

        // when
        runner.update(appStartingEvent);

        // then
        sleepAndWaitForAnotherThreads();
        verify(command).exec();
    }

    private Process createMockProcessWithEmptyStreams() {
        Process process = mock(Process.class);
        when(process.getInputStream())
                .thenReturn(new ByteArrayInputStream(new byte [] {}));
        when(process.getErrorStream())
                .thenReturn(new ByteArrayInputStream(new byte [] {}));
        return process;
    }

    @Test
    public void shouldReadFromInputStreamWhenAppStartingEvent() throws InterruptedException {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .operationSystemName("Win7")
                                                .build();
        TargetJVMRunner runner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        Process process = mock(Process.class);
        byte [] input = "some text\nsecond line".getBytes();
        InputStream inputStream = new ByteArrayInputStream(input);
        when(process.getInputStream())
                .thenReturn(inputStream);
        when(process.getErrorStream())
                .thenReturn(new ByteArrayInputStream(new byte [] {}));
        when(command.exec())
                .thenReturn(process);
        when(executableCommandFactory.createRunTargetApplicationCommand(any(GlobalSettings.class), anyString()))
                .thenReturn(command);

        updateJREUnzippedEventOnRunner(runner);

        ProgressEvent appStartingEvent = progressEventFactory.getAppStartingEvent();

        // when
        runner.update(appStartingEvent);

        // then
        sleepAndWaitForAnotherThreads();
        Assertions.assertThat(outputStream.toString())
                  .isEqualTo("OS: Win7" + LINE_SEPARATOR
                          + "Process input: some text" + LINE_SEPARATOR
                          + "Process input: second line" + LINE_SEPARATOR);
    }

    @Test
    public void shouldReadFromErrorStreamWhenAppStartingEvent() throws InterruptedException {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .operationSystemName("Win7")
                                                .build();
        TargetJVMRunner runner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        Process process = mock(Process.class);
        byte [] input = "some text\nsecond line".getBytes();
        InputStream inputStream = new ByteArrayInputStream(input);
        when(process.getInputStream())
                .thenReturn(new ByteArrayInputStream(new byte [] {}));
        when(process.getErrorStream())
                .thenReturn(inputStream);   // read from error stream
        when(command.exec())
                .thenReturn(process);
        when(executableCommandFactory.createRunTargetApplicationCommand(any(GlobalSettings.class), anyString()))
                .thenReturn(command);

        updateJREUnzippedEventOnRunner(runner);

        ProgressEvent appStartingEvent = progressEventFactory.getAppStartingEvent();

        // when
        runner.update(appStartingEvent);

        // then
        sleepAndWaitForAnotherThreads();
        Assertions.assertThat(outputStream.toString())      // always write to System.out
                  .isEqualTo("OS: Win7" + LINE_SEPARATOR
                          + "Process error: some text" + LINE_SEPARATOR
                          + "Process error: second line" + LINE_SEPARATOR);
    }

    private void updateJREUnzippedEventOnRunner(TargetJVMRunner runner) {
        ProgressEvent jreUnzippedEvent = progressEventFactory.getJREUnzippedEvent();
        JREUnzippedDetailInfo info = new JREUnzippedDetailInfo("/tmp/jre_unzip_dir");
        jreUnzippedEvent.addDetailInfo(info);
        runner.update(jreUnzippedEvent);
    }

    @Test
    public void shouldExecuteJavaExecutableCommandWhenAppStartingEventAndSystemMacOS() throws InterruptedException {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .operationSystemName("Mac OS X")
                                                .build();
        TargetJVMRunner runner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        when(executableCommandFactory.createJavaExecutableCommand(anyString()))
                .thenReturn(command);

        updateJREUnzippedEventOnRunner(runner);
        Process process = createMockProcessWithEmptyStreams();
        when(command.exec())
                .thenReturn(process);
        ProgressEvent appStartingEvent = progressEventFactory.getAppStartingEvent();

        // when
        runner.update(appStartingEvent);

        // then
        sleepAndWaitForAnotherThreads();
        verify(command).exec();
    }

    /*
    A threads for reading standard output and error output of separate processes need time for execution, so we need
    to wait here.
     */
    private void sleepAndWaitForAnotherThreads() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}