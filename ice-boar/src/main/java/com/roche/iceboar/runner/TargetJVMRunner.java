/*
 * ****************************************************************************
 *  Copyright © 2015 Hoffmann-La Roche
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.roche.iceboar.runner;

import com.roche.iceboar.downloader.FileUtilsFacade;
import com.roche.iceboar.progressevent.*;
import com.roche.iceboar.settings.GlobalSettings;

/**
 * Run a target application on downloaded Java Virtual Machine.
 */
public class TargetJVMRunner extends AbstractJVMRunner implements ProgressEventObserver {

    private JREUnzippedDetailInfo detailInfo;

    public TargetJVMRunner(GlobalSettings settings, ExecutableCommandFactory executableCommandFactory, ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {
        super(settings, progressEventQueue, progressEventFactory, executableCommandFactory);
    }


    public void update(ProgressEvent event) {
        if (event.equals(progressEventFactory.getJREUnzippedEvent())) {
            detailInfo = (JREUnzippedDetailInfo) event.getDetailInfo();
        }
        if (event.equals(progressEventFactory.getAppStartingEvent())) {
            runOnJVM();
        }
    }

    public void runOnJVM() {
        makeJavaCommandExecutableOnMac();
        runMainClass();
    }

    private void makeJavaCommandExecutableOnMac() {
        System.out.println("OS: " + settings.getOperationSystemName());
        if (settings.isOperationSystemMacOSX()) {
            String javaPath = FileUtilsFacade.addJavaCommandPathToPath(detailInfo.getPathToJreUnzipDir());
            ExecutableCommand command = executableCommandFactory.createJavaExecutableCommand(javaPath);
            command.exec();
        }
    }

    @Override
    protected void runMainClass() {
        ExecutableCommand command = executableCommandFactory.createRunTargetApplicationCommand(
                settings, detailInfo.getPathToJreUnzipDir());

        Process process = command.exec();

        redirectProcessOutputsToDebugWindow(process);
        progressEventQueue.update(progressEventFactory.getAppStartedEvent());
    }

}
