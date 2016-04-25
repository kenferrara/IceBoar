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
import com.roche.iceboar.settings.GlobalSettingsFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.ServiceManagerStub;
import javax.jnlp.UnavailableServiceException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class GlobalSettingsFactoryTest {

    @BeforeMethod
    public void setup() {
        // reset all properties
        System.clearProperty("jnlp.IceBoar.jar.0");
        System.clearProperty("jnlp.IceBoar.jar.1");
        System.clearProperty("jnlp.IceBoar.jar.2");
        System.clearProperty("jnlp.IceBoar.targetJavaURL");
        System.clearProperty("jnlp.IceBoar.showDebug");
        System.clearProperty("jnlp.IceBoar.frameTitle");
        System.clearProperty("jnlp.IceBoar.targetJavaVersion");
        System.clearProperty("jnlp.IceBoar.main-class");
        System.clearProperty("jnlp.IceBoar.initial-heap-size");
        System.clearProperty("jnlp.IceBoar.max-heap-size");
        System.clearProperty("jnlp.IceBoar.java-vm-args");
        System.clearProperty("jnlp.setting1");
        System.clearProperty("jnlp.setting2");
        System.clearProperty("jnlp.setting3");
    }

    @Test
    public void shouldThrowExceptionWhenTargetJarIsNotDefined() {
        // given
        // it is minimum to set to verify that target JAR is not defined
        System.setProperty("jnlp.IceBoar.targetJavaURL", "abc");

        // when
        try {
            GlobalSettingsFactory.getGlobalSettings(null);
            // then
            fail("It should throw an BootstrapException");
        } catch (IceBoarException e) {
            assertThat(e.getMessage())
                    .isEqualTo("Please specify minimum a one JAR file in property: jnlp.IceBoar.jar.0");
        }
    }

    @Test
    public void shouldReadShowDebugSettings() {
        // given
        System.setProperty("jnlp.IceBoar.showDebug", "true");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.isShowDebug())
                .isEqualTo(true);
    }

    @Test
    public void shouldReadFrameTitle() {
        // given
        System.setProperty("jnlp.IceBoar.frameTitle", "My Test Frame title");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getFrameTitle())
                .isEqualTo("My Test Frame title");
    }

    @Test
    public void shouldReadTargetJavaVersion() {
        // given
        System.setProperty("jnlp.IceBoar.targetJavaVersion", "1.7");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getTargetJavaVersion())
                .isEqualTo("1.7");
    }

    @Test
    public void shouldReadMainClass() {
        // given
        System.setProperty("jnlp.IceBoar.main-class", "MyClass");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getMainClass())
                .isEqualTo("MyClass");
    }

    @Test
    public void shouldReadTargetJavaURL() {
        // given
        System.setProperty("jnlp.IceBoar.targetJavaURL", "myURL");
        System.setProperty("jnlp.IceBoar.jar.0", "xyz.jar");

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getTargetJavaURL())
                .isEqualTo("myURL");
    }

    @Test
    public void shouldReadTargetJavaURLRelativePath() throws UnavailableServiceException, MalformedURLException {
        // given
        System.setProperty("jnlp.IceBoar.targetJavaURL", "myURL.zip");
        System.setProperty("jnlp.IceBoar.jar.0", "xyz.jar");
        BasicService service = mock(BasicService.class);
        when(service.getCodeBase()).thenReturn(new URL("http://example.com/codebase/"));
        ServiceManagerStub stub = mock(ServiceManagerStub.class);
        when(stub.lookup("javax.jnlp.BasicService")).thenReturn(service);
        ServiceManager.setServiceManagerStub(stub); //lookup("javax.jnlp.BasicService")).getCodeBase()

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getTargetJavaURL())
                .isEqualTo("http://example.com/codebase/myURL.zip");
    }

    @Test
    public void shouldReadAllDependencies() {
        // given
        System.setProperty("jnlp.IceBoar.jar.0", "MyJar1.jar");
        System.setProperty("jnlp.IceBoar.jar.1", "MyJar2.jar");
        System.setProperty("jnlp.IceBoar.jar.2", "MyJar3.jar");
        System.setProperty("jnlp.IceBoar.targetJavaURL", "terget");

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getJarURLs())
                .containsOnly("MyJar1.jar", "MyJar2.jar", "MyJar3.jar");
    }

    @Test
    public void shouldReadAllCustomProperties() {
        // given
        System.setProperty("jnlp.setting1", "abc");
        System.setProperty("jnlp.setting2", "def");
        System.setProperty("jnlp.setting3", "ghi");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getAllPropertiesForTarget())
                .containsOnly("-Djnlp.setting1=\"abc\"", "-Djnlp.setting2=\"def\"", "-Djnlp.setting3=\"ghi\"");
    }

    @Test
    public void shouldReadAllCustomPropertiesAsText() {
        // given
        System.setProperty("jnlp.setting1", "abc");
        System.setProperty("jnlp.setting2", "def");
        System.setProperty("jnlp.setting3", "ghi");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getAllPropertiesForTargetAsText())
                .isEqualTo("-Djnlp.setting3=\"ghi\" -Djnlp.setting2=\"def\" -Djnlp.setting1=\"abc\"");
    }

    @Test
    public void shouldReadApplicationArguments() {
        // given
        minimumSettingsProperties();
        String[] args = new String[]{"arg1", "arg2"};

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(args);

        // then
        assertThat(settings.getApplicationArguments())
                .containsOnly("arg1", "arg2");
    }

    @Test
    public void shouldReadApplicationArgumentsAsString() {
        // given
        minimumSettingsProperties();
        String[] args = new String[]{"arg1", "arg2"};

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(args);

        // then
        assertThat(settings.getApplicationArgumentsAsString())
                .isEqualTo("arg1 arg2");
    }

    @Test
    public void shouldReadInitialHeapSize() {
        // given
        System.setProperty("jnlp.IceBoar.initial-heap-size", "128m");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getInitialHeapSize())
                .isEqualTo("-Xms128m");
    }

    @Test
    public void shouldReadEmptyInitialHeapSize() {
        // given
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getInitialHeapSize())
                .isEqualTo("");
    }

    @Test
    public void shouldReadMaxHeapSize() {
        // given
        System.setProperty("jnlp.IceBoar.max-heap-size", "256m");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getMaxHeapSize())
                .isEqualTo("-Xmx256m");
    }

    @Test
    public void shouldReadEmptyMaxHeapSize() {
        // given
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getMaxHeapSize())
                .isEqualTo("");
    }

    @Test
    public void shouldReadJavaVmArgs() {
        // given
        System.setProperty("jnlp.IceBoar.java-vm-args", "-Xdebug -XX:+UseParallelGC");
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getJavaVmArgsAsText())
                .isEqualTo("-Xdebug -XX:+UseParallelGC");
        assertThat(settings.getJavaVmArgs())
                .containsOnly("-Xdebug", "-XX:+UseParallelGC");
    }

    @Test
    public void shouldReadEmptyJavaVmArgs() {
        // given
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(new String[]{});

        // then
        assertThat(settings.getJavaVmArgsAsText())
                .isEqualTo("");
        assertThat(settings.getJavaVmArgs())
                .isEmpty();
    }

    @Test
    public void shouldReadNullJavaVmArgs() {
        // given
        minimumSettingsProperties();

        // when
        GlobalSettings settings = GlobalSettingsFactory.getGlobalSettings(null);

        // then
        assertThat(settings.getJavaVmArgsAsText())
                .isEqualTo("");
        assertThat(settings.getJavaVmArgs())
                .isEmpty();
    }

    private void minimumSettingsProperties() {
        System.setProperty("jnlp.IceBoar.targetJavaURL", "abc");
        System.setProperty("jnlp.IceBoar.jar.0", "xyz.jar");
    }
}