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

import com.roche.iceboar.downloader.FileUtilsFacade;
import com.roche.iceboar.settings.GlobalSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for Executable commands.
 */
public class ExecutableCommandFactory {

    public ExecutableCommand createRunTargetApplicationCommand(GlobalSettings settings, String pathToJreUnzipDir) {
        String javaCmdPath = FileUtilsFacade.addJavaCommandPathToPath(pathToJreUnzipDir);
        List<String> cmdList = new ArrayList<String>();
        cmdList.add(javaCmdPath);
        cmdList.addAll(settings.getAllPropertiesForTarget());
        cmdList.add(settings.getInitialHeapSize());
        cmdList.add(settings.getMaxHeapSize());
        cmdList.addAll(settings.getJavaVmArgs());
        cmdList.add("-cp");
        // without patch separator on begin doesn't work I don't know why
        cmdList.add(settings.getPathSeparator() + settings.getClassPathAsText());
        cmdList.add(settings.getMainClass());
        cmdList.addAll(settings.getApplicationArguments());

        List<String> strings = removeBlankString(cmdList);

        return new ExecutableCommand(strings.toArray(new String[strings.size()]));
    }

    private List<String> removeBlankString(List<String> cmdList) {
        List<String> result = new ArrayList<String>();
        for (String s : cmdList) {
            if (StringUtils.isNotBlank(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public ExecutableCommand createJavaExecutableCommand(String javaCmdPath) {
        String[] cmd = {"chmod", "+x", javaCmdPath};
        return new ExecutableCommand(cmd);
    }

    public ExecutableCommand createJavaGetVersionNumberCommand(String path) {
        String executablePath = FileUtilsFacade.addJavaCommandPathToPath(path);
        String[] cmd = {executablePath, "-version"};
        return new ExecutableCommand(cmd);
    }
}
