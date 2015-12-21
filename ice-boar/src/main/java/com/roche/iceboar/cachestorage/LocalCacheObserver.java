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

import com.roche.iceboar.progressevent.*;
import com.roche.iceboar.settings.GlobalSettings;

/**
 * Catches 2 events (when JRE is downloaded and when JRE is unzipped) and stores information about it in local cache
 * file.
 */
public class LocalCacheObserver implements ProgressEventObserver {

    private GlobalSettings settings;
    private ProgressEventFactory progressEventFactory;
    private LocalCacheStorage localCacheStorage;

    public LocalCacheObserver(GlobalSettings settings, ProgressEventFactory progressEventFactory,
                              LocalCacheStorage localCacheStorage) {
        this.settings = settings;
        this.progressEventFactory = progressEventFactory;
        this.localCacheStorage = localCacheStorage;
    }

    public void update(ProgressEvent event) {
        if (event.equals(progressEventFactory.getJREDownloadedEvent())) {
            localCacheStorage.addAndSaveDownloadedJreInCache(settings, (JREDownloadedDetailInfo) event.getDetailInfo());
        }
        if (event.equals(progressEventFactory.getJREUnzippedEvent())) {
            localCacheStorage.addAndSaveUnzippedJreInCache(settings, (JREUnzippedDetailInfo) event.getDetailInfo());
        }
    }
}
