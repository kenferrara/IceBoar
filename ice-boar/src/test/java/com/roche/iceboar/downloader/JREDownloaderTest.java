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
import com.roche.iceboar.cachestorage.CacheStatus;
import com.roche.iceboar.cachestorage.StatusInfo;
import com.roche.iceboar.progressevent.JREUnzippedDetailInfo;
import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.runner.ExecutableCommand;
import com.roche.iceboar.runner.ExecutableCommandFactory;
import com.roche.iceboar.settings.GlobalSettings;
import net.lingala.zip4j.exception.ZipException;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class JREDownloaderTest {

    private static final ProgressEvent JRE_DOWNLOAD_EVENT = new ProgressEvent("jre download", "");
    private static final ProgressEvent JRE_DOWNLOADED_EVENT = new ProgressEvent("jre downloaded", "");
    private static final ProgressEvent JRE_UNZIP_EVENT = new ProgressEvent("jre unzip", "");
    private static final ProgressEvent JRE_UNZIPPED_EVENT = new ProgressEvent("jre unzipped", "");

    @Test
    public void shouldTryDownloadJRE() throws IOException {
        // given
        String javaTempDir = System.getProperty("java.io.tmpdir");
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaURL("http://www.example.com/jre1.zip")
                                                .tempDirectory(javaTempDir)
                                                .currentJavaVersion("1.3")
                                                .targetJavaVersion("1.6")
                                                .cacheStatus(mock(CacheStatus.class))
                                                .build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREDownloadEvent())
                .thenReturn(JRE_DOWNLOAD_EVENT);
        when(progressEventFactory.getJREDownloadedEvent())
                .thenReturn(JRE_DOWNLOADED_EVENT);
        JREDownloader downloader = new JREDownloader(settings, fileUtils,
                progressEventFactory, mock(ProgressEventQueue.class), mock(ExecutableCommandFactory.class));

        // when
        downloader.update(JRE_DOWNLOAD_EVENT);

        // then
        verify(fileUtils)
                .saveContentFromURLToFile(new URL("http://www.example.com/jre1.zip"), new File(tempDirPlusFilename("jre1.zip")));
    }

    @Test
    public void shouldTryExtractDownloadedJRE() throws ZipException, InterruptedException {
        // given
        String javaTempDir = System.getProperty("java.io.tmpdir");
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaURL("http://www.example.com/jre1.zip")
                                                .tempDirectory(javaTempDir)
                                                .cacheStatus(mock(CacheStatus.class))
                                                .build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREUnzipEvent())
                .thenReturn(JRE_UNZIP_EVENT);
        when(progressEventFactory.getJREUnzippedEvent())
                .thenReturn(JRE_UNZIPPED_EVENT);
        ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
        ExecutableCommand javaCheckVersionCommand = mock(ExecutableCommand.class);
        Process processMock = mock(Process.class);
        when(processMock.waitFor())
                .thenReturn(1);             // unzipping is necessary
        when(javaCheckVersionCommand.exec())
                .thenReturn(processMock);
        when(executableCommandFactory.createJavaGetVersionNumberCommand(anyString()))
                .thenReturn(javaCheckVersionCommand);

        JREDownloader downloader = new JREDownloader(settings, fileUtils,
                progressEventFactory, mock(ProgressEventQueue.class), executableCommandFactory);

        // when
        downloader.update(JRE_UNZIP_EVENT);

        // then
        verify(fileUtils)
                .extractZipFile(tempDirPlusFilename("jre1.zip"), tempDirPlusFilename("jre1_0"));
    }

    @Test
    public void shouldDoesntDownloadWhenTargetJavaUrlIsBlankAndVersionMatch() throws IOException {
        // given
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaURL(" ")
                                                .tempDirectory("/tmp")
                                                .cacheStatus(mock(CacheStatus.class))
                                                .currentJavaVersion("1.6")
                                                .targetJavaVersion("1.5+")
                                                .build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREDownloadEvent())
                .thenReturn(JRE_DOWNLOAD_EVENT);
        when(progressEventFactory.getJREDownloadedEvent())
                .thenReturn(JRE_DOWNLOADED_EVENT);
        ArgumentCaptor<ProgressEvent> captor = ArgumentCaptor.forClass(ProgressEvent.class);
        ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
        JREDownloader downloader = new JREDownloader(settings, fileUtils,
                progressEventFactory, progressEventQueue, mock(ExecutableCommandFactory.class));

        downloader.update(JRE_DOWNLOAD_EVENT);
        verify(progressEventQueue).update(captor.capture());
        assertThat(captor.getValue())
                .isEqualTo(JRE_DOWNLOADED_EVENT);
    }

    @Test
    public void shouldUseUnzippedJre() throws InterruptedException {
        // given
        String javaVersion = "1.7.0_02";
        String jreCachePath = "jre/cache/path";
        String javaTempDir = System.getProperty("java.io.tmpdir");
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        CacheStatus cacheStatus = mock(CacheStatus.class);
        StatusInfo statusInfo = mock(StatusInfo.class);
        when(statusInfo.getPath())
                .thenReturn(jreCachePath);
        when(cacheStatus.getJreUnzippedStatusInfo(javaVersion))
                .thenReturn(statusInfo);
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaURL("http://www.example.com/jre1.zip")
                                                .tempDirectory(javaTempDir)
                                                .currentJavaVersion("1.1")
                                                .cacheStatus(cacheStatus)
                                                .targetJavaVersion(javaVersion)
                                                .build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREUnzipEvent())
                .thenReturn(JRE_UNZIP_EVENT);
        when(progressEventFactory.getJREUnzippedEvent())
                .thenReturn(JRE_UNZIPPED_EVENT);
        ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
        ExecutableCommand executableCommand = mock(ExecutableCommand.class);
        Process process = mock(Process.class);
        when(process.waitFor())
                .thenReturn(0);
        when(executableCommand.exec())
                .thenReturn(process);
        when(executableCommandFactory.createJavaGetVersionNumberCommand(jreCachePath))
                .thenReturn(executableCommand);
        JREDownloader downloader = new JREDownloader(settings, fileUtils,
                progressEventFactory, mock(ProgressEventQueue.class), executableCommandFactory);

        // when
        downloader.update(JRE_UNZIP_EVENT);

        // then
        assertThat(((JREUnzippedDetailInfo) JRE_UNZIPPED_EVENT.getDetailInfo()).getPathToJreUnzipDir())
                .isEqualTo(jreCachePath);
    }

    @Test
    public void shouldNotUseUnzippedJreWhenCommandThrowException() throws InterruptedException {
        // given
        String javaVersion = "1.7.0_02";
        String jreCachePath = "jre/cache/path";
        String javaTempDir = System.getProperty("java.io.tmpdir");
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        CacheStatus cacheStatus = mock(CacheStatus.class);
        StatusInfo statusInfo = mock(StatusInfo.class);
        when(statusInfo.getPath())
                .thenReturn(jreCachePath);
        when(cacheStatus.getJreUnzippedStatusInfo(javaVersion))
                .thenReturn(statusInfo);
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaURL("http://www.example.com/jre1.zip")
                                                .tempDirectory(javaTempDir)
                                                .cacheStatus(cacheStatus)
                                                .targetJavaVersion(javaVersion)
                                                .currentJavaVersion("1.1")
                                                .targetJavaURL("abc.zip")
                                                .jvmStartTime(1234)
                                                .build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREUnzipEvent())
                .thenReturn(JRE_UNZIP_EVENT);
        when(progressEventFactory.getJREUnzippedEvent())
                .thenReturn(JRE_UNZIPPED_EVENT);
        ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
        ExecutableCommand executableCommand = mock(ExecutableCommand.class);
        Process process = mock(Process.class);
        when(process.waitFor())
                .thenThrow(new InterruptedException("Some exception"));
        when(executableCommand.exec())
                .thenReturn(process);
        when(executableCommandFactory.createJavaGetVersionNumberCommand(jreCachePath))
                .thenReturn(executableCommand);
        JREDownloader downloader = new JREDownloader(settings, fileUtils,
                progressEventFactory, mock(ProgressEventQueue.class), executableCommandFactory);

        // when
        downloader.update(JRE_UNZIP_EVENT);

        // then
        assertThat(((JREUnzippedDetailInfo) JRE_UNZIPPED_EVENT.getDetailInfo()).getPathToJreUnzipDir())
                .isEqualTo(dirWithFileSeparatorOnEnd(javaTempDir) + "abc_1234");
    }

    private String dirWithFileSeparatorOnEnd(String dir) {
        if (dir.lastIndexOf(File.separator) == (dir.length() - 1)) {
            return dir;
        }
        return dir + File.separator;
    }

    private String tempDirPlusFilename(String filename) {
        String javaTempDir = System.getProperty("java.io.tmpdir");
        String javaTemp = javaTempDir.endsWith(File.separator) ? javaTempDir : javaTempDir + File.separator;
        return javaTemp + filename;
    }

}