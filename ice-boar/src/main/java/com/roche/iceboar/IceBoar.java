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

import com.roche.iceboar.cachestorage.CacheStatus;
import com.roche.iceboar.debugview.DebugJFrame;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.progressview.ImageLoader;
import com.roche.iceboar.progressview.ProgressJFrame;
import com.roche.iceboar.progressview.ProgressUpdater;
import com.roche.iceboar.runner.ExecutableCommandFactory;
import com.roche.iceboar.runner.TargetApplicationRunner;
import com.roche.iceboar.settings.GlobalSettings;
import com.roche.iceboar.settings.GlobalSettingsFactory;

import javax.swing.*;

/**
 * This is the main class/entry point of the project. It is executed by Java Web Start.
 * <p>
 * It reads a configuration from JNLP file, downloads a custom JRE (from a given location), downloads the target Java
 * application with all dependencies and runs it.
 * <p>
 * <h3>How to debug this class / application?</h3>
 * <p>
 * It is hard to debug Ice Boar. In real environment it is started by javaws. To see whats happen by the
 * running this class please set <tt>jnlp.IceBoar.showDebug</tt> to <tt>true</tt> into your JNLP file.
 * This setting shows {@link com.roche.iceboar.debugview.DebugJFrame} with some debug information's.
 */
public final class IceBoar {

    public static final int HALF_OF_SECOND = 500;

    private GlobalSettings settings;
    private ProgressUpdater progress;
    private ImageLoader imageLoader;
    private ProgressEventFactory progressEventFactory;
    private ProgressEventQueue progressEventQueue;

    public static void main(String[] args) {
        IceBoar bootstrap = new IceBoar();
        bootstrap.start(args);
    }

    private IceBoar() {
    }

    public void start(String[] args) {
        try {
            initGlobalSettings(args);
            initIconsLoader();
            showDebugFrameIfItsNeeded();
            printCacheInfo();
            initProgressEventFactory();
            initProgressEventQueue();
            showProgressFrame();
            runTargetApplication();
        } catch (IceBoarException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void initGlobalSettings(String[] args) {
        settings = GlobalSettingsFactory.getGlobalSettings(args);
    }

    private void initIconsLoader() {
        imageLoader = new ImageLoader();
    }

    private void showDebugFrameIfItsNeeded() {
        boolean showDebug = settings.isShowDebug();

        if (showDebug) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    DebugJFrame jFrame = new DebugJFrame();
                    jFrame.init(settings, imageLoader);
                    jFrame.setVisible(true);
                }
            });
            // time for initializing jFrame and combine System.out with jTextArea
            try {
                Thread.sleep(HALF_OF_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printCacheInfo() {
        CacheStatus cacheStatus = settings.getCacheStatus();
        System.out.println("Read from cache: " + settings.getCachePath());
        System.out.println(cacheStatus);
    }

    private void initProgressEventFactory() {
        progressEventFactory = new ProgressEventFactory();
        progressEventFactory.init(settings);
    }

    private void initProgressEventQueue() {
        progressEventQueue = new ProgressEventQueue();
    }

    private void showProgressFrame() {
        ProgressJFrame jFrame = new ProgressJFrame();
        progress = jFrame.init(settings, progressEventFactory, imageLoader);
        progressEventQueue.registerObserver(progress);
        jFrame.setVisible(true);
    }


    private void runTargetApplication() {
        TargetApplicationRunner runner = new TargetApplicationRunner();
        ExecutableCommandFactory commandFactory = new ExecutableCommandFactory();
        runner.run(settings, commandFactory, progressEventFactory, progressEventQueue);
    }

}
