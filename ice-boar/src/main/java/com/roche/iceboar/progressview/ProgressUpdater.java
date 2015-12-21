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

package com.roche.iceboar.progressview;

import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventObserver;
import com.roche.iceboar.settings.GlobalSettings;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is an observer of events. When next event come (by calling {@link #update(com.roche.iceboar.progressevent.ProgressEvent event)}) a progress
 * bar and information label is updated.
 */
public class ProgressUpdater implements ProgressEventObserver {

    private final JProgressBar progressBar;
    private final JLabel messageLabel;
    private Set<ProgressEvent> events;
    private int amountOfEvents;

    public ProgressUpdater(JProgressBar progressBar, JLabel messageLabel,
                           ProgressEventFactory progressEventFactory) {
        this.progressBar = progressBar;
        this.messageLabel = messageLabel;
        this.events = new HashSet<ProgressEvent>(progressEventFactory.getAllProgressEvents());
        amountOfEvents = events.size();

        progressBar.setMinimum(0);
        progressBar.setMaximum(amountOfEvents);
        progressBar.setValue(0);
        progressBar.setString("0 %");
    }

    public void update(final ProgressEvent event) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (events.contains(event)) {
                    events.remove(event);
                    progressBar.setValue(progressBar.getValue() + 1);
                    progressBar.setString(calculatePercent());
                    if (event.getMessage() != null) {
                        messageLabel.setText(event.getMessage());
                    }
                } else {
                    String message = "Not found event: \"" + event.getEventName() + "\" "
                            + prepareStackTrace(stackTrace);
                    System.out.println(message);
                    throw new RuntimeException(message);
                }
            }
        });
    }

    private String prepareStackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace.length < 5) {
            // return only interesting part
            return StringUtils.join(stackTrace[2], "\n\t", stackTrace[3], "\n\t", stackTrace[4], "\n\t");
        }
        return StringUtils.join(stackTrace, "\n\t");
    }

    private String calculatePercent() {
        return String.format("%1.0f %%", ((double) (progressBar.getValue() * 100)) / amountOfEvents);
    }
}
