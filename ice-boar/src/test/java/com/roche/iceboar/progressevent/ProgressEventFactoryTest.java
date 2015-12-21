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
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgressEventFactoryTest {

    @Test
    public void shouldCreateAllEvents() {
        // given
        ProgressEventFactory factory = new ProgressEventFactory();
        GlobalSettings settings = GlobalSettings.builder()
                                                .jarURLs(Arrays.asList("jar1", "jar2"))
                                                .build();
        factory.init(settings);
        Set<ProgressEvent> expectedAllEvents = new HashSet<ProgressEvent>();
        expectedAllEvents.add(new ProgressEvent("jre download", "JVM download..."));
        expectedAllEvents.add(new ProgressEvent("jre downloaded", "JVM download finished"));
        expectedAllEvents.add(new ProgressEvent("jre unzip", "JVM unzip..."));
        expectedAllEvents.add(new ProgressEvent("jre unzipped", "JVM unzip finished"));
        expectedAllEvents.add(new ProgressEvent("app starting", "Starting application"));
        expectedAllEvents.add(new ProgressEvent("app started", "Application is started"));
        expectedAllEvents.add(new DownloadJarFinishEvent("jar1", "Download of jar1 finished"));
        expectedAllEvents.add(new DownloadJarStartEvent("jar1", "Download of jar1"));
        expectedAllEvents.add(new DownloadJarFinishEvent("jar2", "Download of jar2 finished"));
        expectedAllEvents.add(new DownloadJarStartEvent("jar2", "Download of jar2"));

        // when
        Set<ProgressEvent> allProgressEvents = factory.getAllProgressEvents();

        // then
        assertThat(allProgressEvents).containsAll(expectedAllEvents);
    }

    @Test
    public void shouldCreateEventsToReply() {
        // given
        ProgressEventFactory factory = new ProgressEventFactory();
        GlobalSettings settings = GlobalSettings.builder()
                                                .jarURLs(Arrays.asList("jar1", "jar2"))
                                                .build();
        factory.init(settings);
        Set<ProgressEvent> expectedAllEvents = new HashSet<ProgressEvent>();
        expectedAllEvents.add(new ProgressEvent("jre download", "JVM download..."));
        expectedAllEvents.add(new ProgressEvent("jre unzip", "JVM unzip..."));
        expectedAllEvents.add(new ProgressEvent("app starting", "Starting application"));
        expectedAllEvents.add(new DownloadJarFinishEvent("jar1", "Download of jar1 finished"));
        expectedAllEvents.add(new DownloadJarStartEvent("jar1", "Download of jar1"));
        expectedAllEvents.add(new DownloadJarFinishEvent("jar2", "Download of jar2 finished"));
        expectedAllEvents.add(new DownloadJarStartEvent("jar2", "Download of jar2"));

        // when
        Set<ProgressEvent> allProgressEvents = factory.getAllProgressEvents();

        // then
        assertThat(allProgressEvents).containsAll(expectedAllEvents);
    }
}