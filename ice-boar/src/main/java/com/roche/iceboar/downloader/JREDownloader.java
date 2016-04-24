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
import com.roche.iceboar.runner.ExecutableCommand;
import com.roche.iceboar.runner.ExecutableCommandFactory;
import com.roche.iceboar.settings.GlobalSettings;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class download JRE from custom localization defined in {@value
 * com.roche.iceboar.settings.GlobalSettings#JNLP_TARGET_JAVA_URL}.
 */
public class JREDownloader implements ProgressEventObserver {

    private GlobalSettings settings;
    private FileUtilsFacade fileUtils;
    private final ProgressEventFactory progressEventFactory;
    private ProgressEventQueue progressEventQueue;
    private ExecutableCommandFactory executableCommandFactory;


    public JREDownloader(GlobalSettings settings, FileUtilsFacade fileUtils,
                         ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue,
                         ExecutableCommandFactory executableCommandFactory) {
        this.settings = settings;
        this.fileUtils = fileUtils;
        this.progressEventFactory = progressEventFactory;
        this.progressEventQueue = progressEventQueue;
        this.executableCommandFactory = executableCommandFactory;
    }

    public void update(ProgressEvent event) {
        if (event.equals(progressEventFactory.getJREDownloadEvent())) {
            downloadJavaAndCreateEvent();
        } else if (event.equals(progressEventFactory.getJREUnzipEvent())) {
            unzipJavaAndCreateEvent();
        }
    }

    private void downloadJavaAndCreateEvent() {
        ProgressEvent jreDownloadedEvent = progressEventFactory.getJREDownloadedEvent();
        JREDownloadedDetailInfo detailInfo = new JREDownloadedDetailInfo();
        if(settings.runOnTargetJVM()) {
            if (!canUseDownloadedJreZipFile()) {
                downloadJava();
                detailInfo.setPathToJreZipFile(settings.getDestinationJREPath());
            } else {
                System.out.println("Download of JRE skipped");
                detailInfo.setPathToJreZipFile(settings.getDestinationJreZipPathFromCache());
            }
        }
        jreDownloadedEvent.addDetailInfo(detailInfo);
        progressEventQueue.update(jreDownloadedEvent);
    }

    private void downloadJava() {
        try {
            String urlText = settings.getTargetJavaURL();
            System.out.println("Start download JRE " + urlText);
            if (StringUtils.isBlank(urlText)) {
                throw new RuntimeException("Please define " + GlobalSettings.JNLP_TARGET_JAVA_URL);
            }
            URL url = new URL(urlText);
            String destinationPath = settings.getDestinationJREPath();
            System.out.println("Destination path: " + destinationPath);
            File destination = new File(destinationPath);

            fileUtils.saveContentFromURLToFile(url, destination);

            System.out.println("JVM download finished");
        } catch (IOException e) {
            throw new IceBoarException("Download of JRE failed. Please try again.", e);
        }
    }

    private void unzipJavaAndCreateEvent() {
        ProgressEvent jreUnzippedEvent = progressEventFactory.getJREUnzippedEvent();
        JREUnzippedDetailInfo detailInfo = new JREUnzippedDetailInfo();
        if(settings.runOnTargetJVM()) {
            if (!canUseUnzippedJre()) {
                extractJava();
                detailInfo.setPathToJreUnzipDir(settings.getUnzipPath());
            } else {
                System.out.println("Unzipping of JRE skipped");
                detailInfo.setPathToJreUnzipDir(settings.getUnzippedJrePathFromCache());
            }
        }
        jreUnzippedEvent.addDetailInfo(detailInfo);
        progressEventQueue.update(jreUnzippedEvent);
    }

    private void extractJava() {
        System.out.println("Unzip a JRE...");
        String jrePath = settings.getDestinationJREPath();
        if (canUseDownloadedJreZipFile()) {
            jrePath = settings.getDestinationJreZipPathFromCache();
            System.out.println("Using JRE from Cache: " + jrePath);
        }
        try {
            fileUtils.extractZipFile(jrePath, settings.getUnzipPath());
            System.out.println("Unzip path: " + settings.getUnzipPath());
            System.out.println("JVM unzip finished");
        } catch (ZipException e) {
            throw new IceBoarException("Unzip JRE failed. Please try again.", e);
        }
    }

    private boolean canUseDownloadedJreZipFile() {
        String destination = settings.getDestinationJreZipPathFromCache();
        return fileUtils.checkFileExist(destination);
    }

    private boolean canUseUnzippedJre() {
        String path = settings.getUnzippedJrePathFromCache();
        if (StringUtils.isBlank(path)) {
            return false;
        }
        ExecutableCommand cmd = executableCommandFactory.createJavaGetVersionNumberCommand(path);
        int exitValue = -1;
        try {
            Process exec = cmd.exec();
            exitValue = exec.waitFor();
            System.out.println("Check Java version from cache, exit Value: " + exitValue);
        } catch (Exception e) {
            System.out.println("Can't use unzipped JRE. Exception message: " + e.getMessage());
        }
        return exitValue == 0;
    }
}
