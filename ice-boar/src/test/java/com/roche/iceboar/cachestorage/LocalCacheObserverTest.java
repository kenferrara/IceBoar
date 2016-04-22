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
import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class LocalCacheObserverTest {

    private static final ProgressEvent JRE_DOWNLOADED_EVENT = new ProgressEvent("jre downloaded", "");
    private static final ProgressEvent JRE_UNZIPPED_EVENT = new ProgressEvent("jre unzipped", "");

    @Test
    public void shouldHandleJREDownloadedEvent() {
        // given
        LocalCacheStorage localCacheStorage = mock(LocalCacheStorage.class);
        GlobalSettings settings = GlobalSettings.builder().build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREDownloadedEvent())
                .thenReturn(JRE_DOWNLOADED_EVENT);
        LocalCacheObserver observer = new LocalCacheObserver(settings, progressEventFactory,
                localCacheStorage);
        JREDownloadedDetailInfo detailInfo = new JREDownloadedDetailInfo("aa/b/c");
        JRE_DOWNLOADED_EVENT.addDetailInfo(detailInfo);

        // when
        observer.update(JRE_DOWNLOADED_EVENT);

        // then
        verify(localCacheStorage).addAndSaveDownloadedJreInCache(settings, detailInfo);
    }

    @Test
    public void shouldHandleJREUnzippedEvent() {
        // given
        LocalCacheStorage localCacheStorage = mock(LocalCacheStorage.class);
        GlobalSettings settings = GlobalSettings.builder().build();
        ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
        when(progressEventFactory.getJREUnzippedEvent())
                .thenReturn(JRE_UNZIPPED_EVENT);
        LocalCacheObserver observer = new LocalCacheObserver(settings, progressEventFactory,
                localCacheStorage);
        ProgressEvent jreUnzippedEvent = new ProgressEvent("jre unzipped", "");
        JREUnzippedDetailInfo detailInfo = new JREUnzippedDetailInfo("aa/b/c");
        jreUnzippedEvent.addDetailInfo(detailInfo);

        // when
        observer.update(jreUnzippedEvent);

        // then
        verify(localCacheStorage).addAndSaveUnzippedJreInCache(settings, detailInfo);
    }
}