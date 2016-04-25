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

package com.roche.iceboar.downloader;

import com.roche.iceboar.IceBoarException;
import com.roche.iceboar.progressevent.*;
import com.roche.iceboar.settings.GlobalSettings;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class download all JAR files needed to start a target application.
 */
public class JarDownloader implements ProgressEventObserver {

    private GlobalSettings settings;
    private FileUtilsFacade fileUtils;
    private final ProgressEventFactory progressEventFactory;
    private final ProgressEventQueue progressEventQueue;

    public JarDownloader(GlobalSettings settings, FileUtilsFacade fileUtils,
                         ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {
        this.settings = settings;
        this.fileUtils = fileUtils;
        this.progressEventFactory = progressEventFactory;
        this.progressEventQueue = progressEventQueue;
    }

    private void downloadJar(String jarUrl) {
        try {
            System.out.println("Start download: " + jarUrl);
            URL url = new URL(jarUrl);
            String destinationPath = settings.getDestinationPathForJar(jarUrl);
            System.out.println("Destination path: " + destinationPath);
            File destination = new File(destinationPath);
            fileUtils.saveContentFromURLToFile(url, destination);
            System.out.println("Download of " + jarUrl + " finished");
            progressEventQueue.update(progressEventFactory.getDownloadJarFinishEvent(jarUrl));
        } catch (IOException e) {
            throw new IceBoarException("Download of " + jarUrl + " failed. Please try again.", e);
        }
    }

    public void update(ProgressEvent event) {
        if (event instanceof DownloadJarStartEvent) {
            downloadJar(event.getEventName());
        }
    }
}
