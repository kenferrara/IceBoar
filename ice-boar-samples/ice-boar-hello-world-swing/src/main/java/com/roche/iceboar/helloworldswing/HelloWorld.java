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

package com.roche.iceboar.helloworldswing;

import com.apple.eawt.Application;
import com.google.common.base.Preconditions;

import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * This is a standalone application used for showing an effects on using IceBoar and Java Web Start. It show JFrame
 * with JTextArea and print there some information's (Java version, command lines arguments, JNLP properties).
 */
public class HelloWorld {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    private static final String[] ICONS = new String[]{"/img/IceBoar-icon-grey-128x128.png",
            "/img/IceBoar-icon-grey-64x64.png", "/img/IceBoar-icon-grey-32x32.png", "/img/IceBoar-icon-grey-16x16.png"};

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Hello World Swing");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadIcons(jFrame);
        JTextArea area = new JTextArea(createText(args));
        JScrollPane pane = new JScrollPane(area);
        jFrame.getContentPane().add(pane);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setVisible(true);
    }

    private static void loadIcons(JFrame jFrame) {
        java.util.List<Image> icons = new ArrayList<Image>();
        for(String iconPath: ICONS) {
            URL iconURL = HelloWorld.class.getResource(iconPath);
            ImageIcon icon = new ImageIcon(iconURL);
            icons.add(icon.getImage());
        }
        jFrame.setIconImages(icons);

        if(isOperationSystemMacOSX()) {
            Application application = Application.getApplication();
            application.setDockIconImage(icons.get(0));
        }
    }

    private static boolean isOperationSystemMacOSX() {
        String osName = System.getProperty("os.name");
        return osName.equals("Mac OS X");
    }

    private static String createText(String[] args) {
        String text = getJavaVersion();
        text = appendCommandLineArguments(args, text);
        text = appendJarPath(text);
        text = appendHeapSize(text);
        text = appendJnlpProperties(text);
        text = appendGuavaPreconditions(text);
        return text;
    }

    private static String getJavaVersion() {
        final String javaVersion = System.getProperty("java.version");
        return "Java Version: " + javaVersion + "\nargs: ";
    }

    private static String appendCommandLineArguments(String[] args, String text) {
        for (String arg : args) {
            text = text + arg + "\n";
        }
        return text;
    }

    private static String appendJarPath(String text) {
        try {
            String path = HelloWorld.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            text = text + "\nJar path: " + path + "\n";
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private static String appendHeapSize(String text) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return text + "Max Heap Size: " + (maxMemory / 1024 / 1024) + " MB\n";
    }

    private static String appendJnlpProperties(String text) {
        Properties allProperties = System.getProperties();
        text = text + "\nOnly jnlp.* properties:\n";
        for (Map.Entry<Object, Object> entry : allProperties.entrySet()) {
            if (entry.getKey().toString().startsWith("jnlp.")) {
                text = text + entry.getKey() + "=" + entry.getValue() + "\n";
            }
        }
        return text;
    }

    private static String appendGuavaPreconditions(String text) {
        Preconditions.checkNotNull(text);       // call some method to verify that guava.jar is visible on classpath
        text = text + "\n\nPreconditions guava works";
        return text;
    }
}
