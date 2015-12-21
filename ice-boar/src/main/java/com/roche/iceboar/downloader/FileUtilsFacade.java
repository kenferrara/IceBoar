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

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class is an abstraction for disk operations. It can be mocked in unit test.
 */
public class FileUtilsFacade {

    public static String extractFilenameFromURL(String url) {
        if (url.contains("/")) {
            return url.substring(url.lastIndexOf('/') + 1, url.length());
        }
        return url;
    }

    public static String addJavaCommandPathToPath(String path) {
        return path + File.separator + "bin" + File.separator + "java";
    }

    public void saveContentFromURLToFile(URL url, File destination) throws IOException {
        FileUtils.copyURLToFile(url, destination);
    }

    public void extractZipFile(String zipFileName, String destinationPath) throws ZipException {
        ZipFile zipFile = new ZipFile(zipFileName);
        zipFile.extractAll(destinationPath);
    }

    public boolean checkFileExist(String destination) {
        return new File(destination).exists();
    }
}
