package com.roche.iceboar.progressview;

import com.roche.iceboar.settings.GlobalSettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * This class can load icons used in JFrames as application icons.
 */
public class IconsLoader {

    private final String [] DEFAULT_ICONS = new String[] {"/img/IceBoar-icon-128x128.png",
            "/img/IceBoar-icon-64x64.png", "/img/IceBoar-icon-32x32.png", "/img/IceBoar-icon-16x16.png"};

    public List<Image> loadDefaultIcons() {
        List<Image> icons = new ArrayList<Image>();
        for(String iconPath: DEFAULT_ICONS) {
            URL iconURL = getClass().getResource(iconPath);
            ImageIcon icon = new ImageIcon(iconURL);
            icons.add(icon.getImage());
        }
        return icons;
    }

    public List<Image> loadIcons(GlobalSettings settings) {
        List<String> iconsULRs = settings.getIcons();
        if(iconsULRs.isEmpty()) {
            return loadDefaultIcons();
        }

        return loadIconsFromServer(iconsULRs);
    }

    private List<Image> loadIconsFromServer(List<String> iconsURLs) {
        List<Image> icons = new ArrayList<Image>();
        for (String url : iconsURLs) {
            try {
                BufferedImage image = ImageIO.read(new URL(url));
                icons.add(image);
                System.out.println("Load icon successful: " + url);
            } catch (IOException e) {
                System.out.println("Fail to load icon: " + url);
            }
        }
        return icons;
    }
}
