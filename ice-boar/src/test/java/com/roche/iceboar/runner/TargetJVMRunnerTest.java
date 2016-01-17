package com.roche.iceboar.runner;

import com.roche.iceboar.progressevent.JREUnzippedDetailInfo;
import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TargetJVMRunnerTest {

    private final ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
    private final ProgressEventFactory progressEventFactory = new ProgressEventFactory();
    private final ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);

    @Test
    public void shouldExecCommandWhenAppStartingEvent() {
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

        // when
        runner.update(appStartingEvent);

        // then
        verify(command).exec();
    }

    private void updateJREUnzippedEventOnRunner(TargetJVMRunner runner) {
        ProgressEvent jreUnzippedEvent = progressEventFactory.getJREUnzippedEvent();
        JREUnzippedDetailInfo info = new JREUnzippedDetailInfo("/tmp/jre_unzip_dir");
        jreUnzippedEvent.addDetailInfo(info);
        runner.update(jreUnzippedEvent);
    }

    @Test
    public void shouldExecuteJavaExecutableCommandWhenAppStartingEventAndSystemMacOS() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .operationSystemName("Mac OS X")
                                                .build();
        TargetJVMRunner runner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        ExecutableCommand command = mock(ExecutableCommand.class);
        when(executableCommandFactory.createJavaExecutableCommand(anyString()))
                .thenReturn(command);

        updateJREUnzippedEventOnRunner(runner);

        ProgressEvent appStartingEvent = progressEventFactory.getAppStartingEvent();

        // when
        runner.update(appStartingEvent);

        // then
        verify(command).exec();
    }
}