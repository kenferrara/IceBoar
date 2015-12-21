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

package com.roche.iceboar.progressevent;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Queue for Progress Events.
 */
public class ProgressEventQueue {

    private List<ProgressEventObserver> observers = new ArrayList<ProgressEventObserver>();
    private List<ProgressEvent> eventsToReplay = new ArrayList<ProgressEvent>();
    private boolean isInProgress = false;

    public void registerObserver(ProgressEventObserver observer) {
        observers.add(observer);
    }

    public void update(ProgressEvent event) {
        if (isInProgress) {
            eventsToReplay.add(event);
            return;
        }
        if (eventsToReplay.size() == 0) {
            informAllObservers(event);
        }
        while (eventsToReplay.size() > 0) {
            informAllObservers(eventsToReplay.remove(0));
        }
    }

    private void informAllObservers(ProgressEvent event) {
        isInProgress = true;
        for (ProgressEventObserver observer : observers) {
            observer.update(event);
        }
        isInProgress = false;
    }

}
