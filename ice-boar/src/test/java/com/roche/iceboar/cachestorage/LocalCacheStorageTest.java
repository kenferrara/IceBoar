package com.roche.iceboar.cachestorage;

import com.roche.iceboar.progressevent.JREDownloadedDetailInfo;
import com.roche.iceboar.progressevent.JREUnzippedDetailInfo;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalCacheStorageTest {

    private static String cachePath = "src/test/resources/cache02_save.cache";

    private LocalCacheStorage cache;

    @BeforeClass
    public void beforeClass() {
        cache = new LocalCacheStorage();
    }

    @BeforeMethod
    @AfterClass
    public void cleanUp() {
        File file = new File(cachePath);
        file.delete();
    }

    @Test
    public void shouldReadDownloadedStatusInfoFromCache() {
        // given

        // when
        CacheStatus cacheStatus = cache.loadCacheStatus("src/test/resources/cache01_ok.cache");

        // then
        StatusInfo statusInfo = cacheStatus.getJreDownloadedStatusInfo("1.7.0_01");
        assertThat(statusInfo.getPath())
                .isEqualTo("/var/folders/_s/4m_b_3js5px16n_7hn9841xr0000gn/T/jre-1.7.0_01-macosx-x64.zip");
    }

    @Test
    public void shouldReadUnzippedStatusInfoFromCache() {
        // given

        // when
        CacheStatus cacheStatus = cache.loadCacheStatus("src/test/resources/cache01_ok.cache");

        // then
        StatusInfo statusInfo = cacheStatus.getJreUnzippedStatusInfo("1.7.0_01");
        assertThat(statusInfo.getPath())
                .isEqualTo("/var/folders/_s/4m_b_3js5px16n_7hn9841xr0000gn/T/jre-1.7.0_01-macosx-x64_1455230359049");
    }

    @Test
    public void shouldStoreJreDownloadedStatusInCache() {
        // given
        String jreVersion = "1.2.3";
        String jrePath = "/path/to/jre/zip/file.zip";
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaVersion(jreVersion)
                                                .cachePath(cachePath)
                                                .build();

        JREDownloadedDetailInfo detailInfo = new JREDownloadedDetailInfo(jrePath);


        // when
        cache.addAndSaveDownloadedJreInCache(settings, detailInfo);

        // then
        CacheStatus cacheStatus = cache.loadCacheStatus(cachePath);
        StatusInfo statusInfo = cacheStatus.getJreDownloadedStatusInfo(jreVersion);
        assertThat(statusInfo.getPath())
                .isEqualTo(jrePath);
    }

    @Test
    public void shouldStoreJreUnzippedStatusInCache() {
        // given
        String jreVersion = "1.2.3.4";
        String jrePath = "/path/to/jre/zip/file2.zip";
        GlobalSettings settings = GlobalSettings.builder()
                                                .targetJavaVersion(jreVersion)
                                                .cachePath(cachePath)
                                                .build();

        JREUnzippedDetailInfo detailInfo = new JREUnzippedDetailInfo(jrePath);


        // when
        cache.addAndSaveUnzippedJreInCache(settings, detailInfo);

        // then
        CacheStatus cacheStatus = cache.loadCacheStatus(cachePath);
        StatusInfo statusInfo = cacheStatus.getJreUnzippedStatusInfo(jreVersion);
        assertThat(statusInfo.getPath())
                .isEqualTo(jrePath);
    }
}