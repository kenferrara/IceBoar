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

import com.roche.iceboar.settings.GlobalSettings;

import java.util.*;

import static com.roche.iceboar.downloader.FileUtilsFacade.extractFilenameFromURL;

/**
 * Factory for Progress Events.
 */
public class ProgressEventFactory {

    public static final String EVENT_JRE_DOWNLOAD = "jre download";
    public static final String EVENT_JRE_DOWNLOADED = "jre downloaded";
    public static final String EVENT_JRE_UNZIP = "jre unzip";
    public static final String EVENT_JRE_UNZIPPED = "jre unzipped";
    public static final String EVENT_APP_STARTING = "app starting";
    public static final String EVENT_APP_STARTED = "app started";

    private Set<ProgressEvent> allEvents = new HashSet<ProgressEvent>();
    private List<ProgressEvent> eventsToReply = new ArrayList<ProgressEvent>();

    public void init(GlobalSettings settings) {
        allEvents.add(getJREDownloadEvent());
        allEvents.add(getJREDownloadedEvent());
        allEvents.add(getJREUnzipEvent());
        allEvents.add(getJREUnzippedEvent());
        for (String url : settings.getJarURLs()) {
            allEvents.add(createDownloadStartEvent(url));
            allEvents.add(getDownloadJarFinishEvent(url));
        }
        allEvents.add(getAppStartingEvent());
        allEvents.add(getAppStartedEvent());

        eventsToReply.add(getJREDownloadEvent());
        eventsToReply.add(getJREUnzipEvent());
        for (String url : settings.getJarURLs()) {
            eventsToReply.add(createDownloadStartEvent(url));
        }
        eventsToReply.add(getAppStartingEvent());
    }

    private ProgressEvent createDownloadStartEvent(String url) {
        return getDownloadJarStartEvent(url);
    }

    public Set<ProgressEvent> getAllProgressEvents() {
        return Collections.unmodifiableSet(allEvents);
    }

    public ProgressEvent getJREDownloadEvent() {
        return new ProgressEvent(EVENT_JRE_DOWNLOAD, "JVM download...");
    }

    public ProgressEvent getJREDownloadedEvent() {
        return new ProgressEvent(EVENT_JRE_DOWNLOADED, "JVM download finished");
    }

    public ProgressEvent getJREUnzipEvent() {
        return new ProgressEvent(EVENT_JRE_UNZIP, "JVM unzip...");
    }

    public ProgressEvent getJREUnzippedEvent() {
        return new ProgressEvent(EVENT_JRE_UNZIPPED, "JVM unzip finished");
    }

    public ProgressEvent getAppStartingEvent() {
        return new ProgressEvent(EVENT_APP_STARTING, "Starting application");
    }

    public ProgressEvent getAppStartedEvent() {
        return new ProgressEvent(EVENT_APP_STARTED, "Application is started");
    }

    public ProgressEvent getDownloadJarFinishEvent(String jarUrl) {
        return new DownloadJarFinishEvent(jarUrl, "Download of " + extractFilenameFromURL(jarUrl) + " " +
                "finished");
    }

    public ProgressEvent getDownloadJarStartEvent(String jarUrl) {
        return new DownloadJarStartEvent(jarUrl, "Download of " + extractFilenameFromURL(jarUrl));
    }

    public List<ProgressEvent> getEventsToReply() {
        return eventsToReply;
    }
}
