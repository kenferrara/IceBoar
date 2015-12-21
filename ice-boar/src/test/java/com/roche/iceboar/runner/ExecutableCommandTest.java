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

import com.roche.iceboar.IceBoarException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class ExecutableCommandTest {

    @Test
    public void shouldRunCommand() throws InterruptedException {
        // given
        ExecutableCommand command = new ExecutableCommand(new String[]{"java", "-version"});

        // when
        Process process = command.exec();

        // then
        assertThat(process.waitFor())
                .isEqualTo(0);
    }

    @Test
    public void shouldNotRunCommand() throws InterruptedException {
        // given
        ExecutableCommand command = new ExecutableCommand(new String[]{"javaaaaaaaaaaaaaaaaaa", "-version"});

        try {
            // when
            command.exec();
            fail("Should throw an Exception");
        } catch (IceBoarException e) {
            // then
            assertThat(e.getCause())
                    .isInstanceOf(IOException.class);
        }
    }
}