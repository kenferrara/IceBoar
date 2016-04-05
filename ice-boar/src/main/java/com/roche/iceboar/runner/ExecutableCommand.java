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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Wrap executing complex commands in operating system
 */
public class ExecutableCommand {

    private final String[] cmd;

    public ExecutableCommand(String[] cmd) {
        this.cmd = cmd.clone();
    }

    public Process exec() {
        try {
            System.out.println("Try to start: " + getReadable() + "\n");
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("Process: " + process.toString());
            return process;
        } catch (IOException e) {
            System.err.println("Failed to execute: " + getReadable());
            throw new IceBoarException("Failed to start a target application. Please try again. See debug view " +
                    "for more details.", e);
        }
    }

    public String getReadable() {
		String msg = "";
		for (String st : cmd) {
			msg = msg + "\"" + st + "\", ";
		}
		return msg;
    }
}
