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

import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.progressview.ProgressUpdater;
import com.roche.iceboar.settings.GlobalSettings;

/**
 * A Factory for proper JVMRunner. Now {@link TargetJVMRunner} is always returned by ths factory, but in feature it
 * can be {@link CurrentJVMRunner}.
 */
public class JVMRunnerFactory {

    public JVMRunner create(GlobalSettings settings, ExecutableCommandFactory executableCommandFactory,
                            ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {

        // For now only TargetJVMRunner implementation works and is supported
        JVMRunner jvmRunner = new TargetJVMRunner(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

        return jvmRunner;
    }
}
