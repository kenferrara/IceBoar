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

import com.roche.iceboar.cachestorage.LocalCacheObserver;
import com.roche.iceboar.cachestorage.LocalCacheStorage;
import com.roche.iceboar.downloader.FileUtilsFacade;
import com.roche.iceboar.downloader.JREDownloader;
import com.roche.iceboar.downloader.JarDownloader;
import com.roche.iceboar.progressevent.*;
import com.roche.iceboar.settings.GlobalSettings;

import java.util.List;

/**
 * This application handle process of downloading JRE, JARs and run target application. It is event driven.
 */
public class TargetApplicationRunner implements ProgressEventObserver {

    private ProgressEventFactory progressEventFactory;
    private ProgressEventQueue progressEventQueue;
    private JREDownloader jreDownloader;
    private JVMRunner jvmRunner;
    private JarDownloader jarDownloader;
    private List<ProgressEvent> eventsToReplay;

    public void run(GlobalSettings settings, ExecutableCommandFactory executableCommandFactory,
                    ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {
        this.progressEventFactory = progressEventFactory;
        this.progressEventQueue = progressEventQueue;

        JVMRunnerFactory factory = new JVMRunnerFactory();
        jvmRunner = factory.create(settings, executableCommandFactory, progressEventFactory, progressEventQueue);
        progressEventQueue.registerObserver(jvmRunner);

        jreDownloader = new JREDownloader(settings, new FileUtilsFacade(), progressEventFactory, progressEventQueue,
                executableCommandFactory);
        progressEventQueue.registerObserver(jreDownloader);

        jarDownloader = new JarDownloader(settings, new FileUtilsFacade(), progressEventFactory, progressEventQueue);
        progressEventQueue.registerObserver(jarDownloader);

        LocalCacheObserver localCache = new LocalCacheObserver(settings, progressEventFactory, new LocalCacheStorage());
        progressEventQueue.registerObserver(localCache);

        progressEventQueue.registerObserver(this);

        progressEventQueue.registerObserver(new CloseApplicationObserver(settings, progressEventFactory));

        eventsToReplay = progressEventFactory.getEventsToReply();
        replyNextEvent();
    }

    private void replyNextEvent() {
        progressEventQueue.update(eventsToReplay.remove(0));
    }

    public void update(ProgressEvent event) {
        if (canReplayNextEvent(event)) {
            replyNextEvent();
        }
    }

    private boolean canReplayNextEvent(ProgressEvent event) {
        return event.equals(progressEventFactory.getJREDownloadedEvent())
                || event.equals(progressEventFactory.getJREUnzippedEvent())
                || event instanceof DownloadJarFinishEvent;
    }

}
