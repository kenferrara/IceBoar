package com.roche.iceboar.runner;

import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.Thread.sleep;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CurrentJVMRunnerTest {

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
                                                .currentJavaCommand("/path/to/java")
                                                .build();
        CurrentJVMRunner runner = new CurrentJVMRunner(settings, executableCommandFactory, progressEventFactory,
                progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        when(executableCommandFactory.createRunTargetApplicationCommand(any(GlobalSettings.class), anyString()))
                .thenReturn(command);


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
                .thenReturn(new ByteArrayInputStream(new byte[]{}));
        when(process.getErrorStream())
                .thenReturn(new ByteArrayInputStream(new byte[]{}));
        return process;
    }

    private void sleepAndWaitForAnotherThreads() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}