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

package com.roche.iceboar.runner;

import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class ExecutableCommandFactoryTest {

    @Test
    public void shouldCreateRunTargetApplicationCommand() {
        // given
        GlobalSettings settings = GlobalSettings.builder()
                                                .allPropertiesForTarget(asList("prop1", "prop2"))
                                                .initialHeapSize("128M")
                                                .maxHeapSize("256M")
                                                .javaVmArgs("xxx")
                                                .pathSeparator("|")
                                                .jarURLs(asList("jar1", "jar2", "jar3"))
                                                .mainClass("MainClass")
                                                .applicationArguments(new String[]{"arg1", "arg2"})
                                                .tempDirectory("/temp/temp")
                                                .build();
        ExecutableCommandFactory factory = new ExecutableCommandFactory();

        // when
        ExecutableCommand command = factory.createRunTargetApplicationCommand(settings, "xxx");

        // then
        String cmdText = command.getReadable();
        assertThat(cmdText).isEqualTo("xxx" + File.separator + "bin" + File.separator + "java prop1 prop2 " +
                "-Xms128M -Xmx256M xxx " + "-cp |/temp/temp" + File.separator + "IceBoar_0" + File.separator +
                "jar1|/temp/temp" + File.separator + "IceBoar_0" + File.separator + "jar2|/temp/temp" + File.separator
                + "IceBoar_0" + File.separator + "jar3 " + "MainClass arg1 arg2");
    }

    @Test
    public void shouldCreateJavaExecutableCommand() {
        // given
        ExecutableCommandFactory factory = new ExecutableCommandFactory();


        // when
        ExecutableCommand command = factory.createJavaExecutableCommand("xxxyyy");

        // then
        String cmdText = command.getReadable();
        assertThat(cmdText).isEqualTo("chmod +x xxxyyy");
    }

    @Test
    public void shouldCreateJavaGetVersionNumberCommand() {
        // given
        ExecutableCommandFactory factory = new ExecutableCommandFactory();


        // when
        ExecutableCommand command = factory.createJavaGetVersionNumberCommand("aaa");

        // then
        String cmdText = command.getReadable();
        assertThat(cmdText).isEqualTo("aaa" + File.separator + "bin" + File.separator + "java -version");
    }
}