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

import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventObserver;
import com.roche.iceboar.settings.GlobalSettings;

/**
 * Close Ice Boar Application, when {@link GlobalSettings#JNLP_CLOSE_ON_END} is set to true.
 */
public class CloseApplicationObserver implements ProgressEventObserver {

    private static final int TWO_SECONDS = 2000;
    private final GlobalSettings settings;
    private final ProgressEventFactory progressEventFactory;

    public CloseApplicationObserver(GlobalSettings settings, ProgressEventFactory progressEventFactory) {
        this.settings = settings;
        this.progressEventFactory = progressEventFactory;
    }

    public void update(ProgressEvent event) {
        if (event.equals(progressEventFactory.getAppStartedEvent())) {
            try {
                Thread.sleep(TWO_SECONDS);     // time for starting target app
                if (settings.isCloseOnEnd()) {
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                System.out.println("Thread sleep interruption");
                e.printStackTrace();
            }
        }
    }
}
