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

package com.roche.iceboar.progressview;

import com.apple.eawt.Application;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.settings.GlobalSettings;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Show JFrame with progress of downloading / extracting and running a target application.
 */
public class ProgressJFrame extends JFrame {

    public ProgressUpdater init(GlobalSettings settings, ProgressEventFactory progressEventFactory, ImageLoader imageLoader) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(settings.getFrameTitle());

        loadIcons(imageLoader, settings);

        setSize(300, 100);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        LayoutManager layoutManager = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(layoutManager);


        Image splashScreenImage = imageLoader.loadSplashScreen(settings);
        if(splashScreenImage != null){
            JLabel splashScreen = new JLabel(new ImageIcon(splashScreenImage));
            splashScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(splashScreen);
        }

        JProgressBar progressBar = new JProgressBar();
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setStringPainted(true);

        JLabel informationLabel = new JLabel("In progress...");
        informationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(progressBar);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(informationLabel);
        mainPanel.add(Box.createVerticalGlue());
        setContentPane(mainPanel);
        if(settings.isHideFrameBorder()) {
            setUndecorated(true);
        }
        pack();

        return new ProgressUpdater(progressBar, informationLabel, progressEventFactory);
    }

    private void loadIcons(ImageLoader imageLoader, GlobalSettings settings) {
        List<Image> icons = imageLoader.loadIcons(settings);
        setIconImages(icons);

        if (settings.isOperationSystemMacOSX()) {
            Application application = Application.getApplication();
            application.setDockIconImage(findTheBiggest(icons));
        }
    }

    private Image findTheBiggest(List<Image> icons) {
        Image theBiggest = icons.get(0);
        for (int i = 1; i < icons.size(); i++) {
            if (icons.get(i).getWidth(null) > theBiggest.getWidth(null)) {
                theBiggest = icons.get(i);
            }
        }
        return theBiggest;
    }
}
