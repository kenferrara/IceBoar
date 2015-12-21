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

package com.roche.iceboar.debugview;

import com.roche.iceboar.debugview.logging.MessageConsole;
import com.roche.iceboar.settings.GlobalSettings;

import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

/**
 * Show JFrame with debug information's (redirecting from System.out and System.err). It's deactivated by default. To
 * see it please set {@value GlobalSettings#JNLP_SHOW_DEBUG} to <tt>true</tt> in your JNLP file.
 * <p/>
 * For explanation why I use System.out and System.err see FAQ in Readme.md.
 */
public class DebugJFrame extends JFrame {

    public void init(GlobalSettings settings) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Web Start Bootstrap");
        JTextArea jTextArea = new JTextArea(createText(settings));
        JScrollPane pane = new JScrollPane(jTextArea);
        getContentPane().add(pane);
        setSize(1000, 400);

        MessageConsole console = new MessageConsole(jTextArea);
        console.redirectOut();
        console.redirectErr(Color.RED, null);
    }

    private String createText(GlobalSettings settings) {
        String text = getCurrentJavaVersion(settings);
        text = appendCommandlineArguments(settings, text);
        text = appendJarPath(text);
        text = appendOnlyJnlpSpecificProperties(text);
        text = appendAllProperties(text);
        return text;
    }

    private String getCurrentJavaVersion(GlobalSettings settings) {
        String javaVersion = settings.getCurrentJavaVersion();
        return "Java Version: " + javaVersion + "\nsettings: ";
    }

    private String appendCommandlineArguments(GlobalSettings settings, String text) {
        for (String arg : settings.getApplicationArguments()) {
            text = text + arg + "\n";
        }
        return text;
    }

    private String appendJarPath(String text) {
        try {
            String path = DebugJFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            text = text + "\nJAR path: " + path + "\n";
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private String appendOnlyJnlpSpecificProperties(String text) {
        Properties allProperties = System.getProperties();
        text = text + "\nOnly jnlp.* properties:\n";
        for (Map.Entry<Object, Object> entry : allProperties.entrySet()) {
            if (entry.getKey().toString().startsWith("jnlp.")) {
                text = text + entry.getKey() + "=" + entry.getValue() + "\n";
            }
        }
        return text;
    }

    private String appendAllProperties(String text) {
        Properties allProperties = System.getProperties();
        text = text + "\nAll properties:\n";
        for (Map.Entry<Object, Object> entry : allProperties.entrySet()) {
            text = text + entry.getKey() + "=" + entry.getValue() + "\n";
        }
        return text;
    }
}
