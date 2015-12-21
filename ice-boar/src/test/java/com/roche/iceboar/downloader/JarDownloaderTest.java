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

import com.roche.iceboar.progressevent.DownloadJarStartEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JarDownloaderTest {

    @Test
    public void shouldTryToDownloadAJAR() throws IOException {
        // given
        FileUtilsFacade fileUtils = mock(FileUtilsFacade.class);
        String javaTempDir = System.getProperty("java.io.tmpdir");
        GlobalSettings settings = GlobalSettings.builder()
                                                .tempDirectory(javaTempDir)
                                                .build();
        JarDownloader jarDownloader = new JarDownloader(settings, fileUtils,
                mock(ProgressEventFactory.class), mock(ProgressEventQueue.class));

        // when
        jarDownloader.update(new DownloadJarStartEvent("http://www.example.com/jar1.jar", ""));

        // then
        verify(fileUtils)
                .saveContentFromURLToFile(new URL("http://www.example.com/jar1.jar"),
                        new File(expectedDestinationDirFor("jar1.jar")));
    }

    private String expectedDestinationDirFor(String filename) {
        String javaTempDir = System.getProperty("java.io.tmpdir");
        String javaTemp = javaTempDir.endsWith(File.separator) ? javaTempDir : javaTempDir + File.separator;
        return javaTemp + "IceBoar_0" + File.separator + filename;
    }
}