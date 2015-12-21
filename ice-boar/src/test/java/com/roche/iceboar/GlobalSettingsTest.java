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

package com.roche.iceboar;

import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public class GlobalSettingsTest {

    @Test
    public void shouldThrowExceptionWhenTempDirectoryIsNotSet() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .build();

        // when
        try {
            settings.getTempDirectory();

            // then
            fail("It should throw an Exception");
        } catch (IceBoarException e) {
            assertThat(e.getMessage())
                    .isEqualTo("User temp directory is not defined!");
        }
    }

    @Test
    public void shouldAlwaysReturnTempDirectoryWithFileSeparatorAtEnd() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .tempDirectory("/tmp")
                                                .build();

        // when
        String actual = settings.getTempDirectory();

        // then
        assertThat(actual)
                .isEqualTo("/tmp" + File.separator);
    }

    @Test
    public void shouldUnzipJavaCommandPath() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .tempDirectory("/tmp")
                                                .targetJavaURL("http://www.example.com/jre1.zip")
                                                .build();

        // when
        String actual = settings.getUnzipJavaCommandPath();

        // then
        assertThat(actual)
                .isEqualTo("/tmp" + File.separator + "jre1_0" + File.separator + "bin" + File.separator + "java");
    }

    @Test
    public void shouldGetClassPathAsText() {
        // given
        List<String> jarURLs = Arrays.asList("jar1.jar", "jar2.jar", "jar3.jar");
        GlobalSettings settings = GlobalSettings.builder()
                                                .tempDirectory("/tmp")
                                                .jarURLs(jarURLs)
                                                .pathSeparator(";")
                                                .build();

        // when
        String actual = settings.getClassPathAsText();

        // then
        assertThat(actual)
                .isEqualTo("/tmp" + File.separator + "IceBoar_0" + File.separator + "jar1.jar;" +
                        "/tmp" + File.separator + "IceBoar_0" + File.separator + "jar2.jar;" +
                        "/tmp" + File.separator + "IceBoar_0" + File.separator + "jar3.jar");
    }

    @Test
    public void shouldGetOperationSystemName() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .tempDirectory("/tmp")
                                                .operationSystemName("Win98")
                                                .build();

        // when
        String actual = settings.getOperationSystemName();

        // then
        assertThat(actual)
                .isEqualTo("Win98");
    }

}