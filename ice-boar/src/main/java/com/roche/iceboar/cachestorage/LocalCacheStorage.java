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

package com.roche.iceboar.cachestorage;

import com.roche.iceboar.progressevent.JREDownloadedDetailInfo;
import com.roche.iceboar.progressevent.JREUnzippedDetailInfo;
import com.roche.iceboar.settings.GlobalSettings;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Provide operations for cache like storing and reading from them.
 */
public class LocalCacheStorage {

    public LocalCacheStorage() {
    }

    public CacheStatus loadCacheStatus(String cachePath) {
        InputStream file = null;
        InputStream buffer = null;
        ObjectInput input = null;
        try {
            file = new FileInputStream(cachePath);
            buffer = new BufferedInputStream(file);
            input = new ObjectInputStream(buffer);

            CacheStatus cacheStatus = (CacheStatus) input.readObject();
            return cacheStatus;
        } catch (Exception e) {
            System.out.println("Can't open file with cache settings. Expected file here: " + cachePath);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new CacheStatus();
    }

    public void addAndSaveDownloadedJreInCache(GlobalSettings settings, JREDownloadedDetailInfo detailInfo) {
        if(StringUtils.isNotBlank(detailInfo.getPathToJreZipFile())) {
            CacheStatus cacheStatus = loadCacheStatus(settings.getCachePath());
            StatusInfo status = new StatusInfo(StatusInfo.Status.JRE_DOWNLOADED, settings.getTargetJavaVersion(),
                    detailInfo.getPathToJreZipFile());
            cacheStatus.add(status);
            store(cacheStatus, settings.getCachePath());
        }
    }

    public void addAndSaveUnzippedJreInCache(GlobalSettings settings, JREUnzippedDetailInfo detailInfo) {
        if(StringUtils.isNotBlank(detailInfo.getPathToJreUnzipDir())) {
            CacheStatus cacheStatus = loadCacheStatus(settings.getCachePath());
            StatusInfo status = new StatusInfo(StatusInfo.Status.JRE_UNZIPPED, settings.getTargetJavaVersion(),
                    detailInfo.getPathToJreUnzipDir());
            cacheStatus.add(status);
            store(cacheStatus, settings.getCachePath());
        }
    }

    private void store(CacheStatus cacheStatus, String cachePath) {
        OutputStream file = null;
        OutputStream buffer = null;
        ObjectOutput output = null;
        try {
            //use buffering
            file = new FileOutputStream(cachePath);
            buffer = new BufferedOutputStream(file);
            output = new ObjectOutputStream(buffer);
            output.writeObject(cacheStatus);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
