package com.roche.iceboar.progressview;

import com.roche.iceboar.settings.GlobalSettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can load icons used in JFrames as application icons.
 */
public class ImageLoader {

    private final String[] DEFAULT_ICONS = new String[]{"/img/IceBoar-icon-128x128.png",
            "/img/IceBoar-icon-64x64.png", "/img/IceBoar-icon-32x32.png", "/img/IceBoar-icon-16x16.png"};

    public List<Image> loadDefaultIcons() {
        List<Image> icons = new ArrayList<Image>();
        for (String iconPath : DEFAULT_ICONS) {
            URL iconURL = getClass().getResource(iconPath);
            ImageIcon icon = new ImageIcon(iconURL);
            icons.add(icon.getImage());
        }
        return icons;
    }

    public List<Image> loadIcons(GlobalSettings settings) {
        List<String> iconsULRs = settings.getIcons();
        if (iconsULRs.isEmpty()) {
            return loadDefaultIcons();
        }

        return loadIconsFromServer(iconsULRs.toArray(new String[iconsULRs.size()]));
    }

    public Image loadSplashScreen(GlobalSettings settings) {
        String splashImage = settings.getCustomSplashImage();
        return loadImage(splashImage);
    }

    private List<Image> loadIconsFromServer(String... iconsURLs) {
        List<Image> icons = new ArrayList<Image>();
        for (String url : iconsURLs) {
            icons.add(loadImage(url));
        }
        return icons;
    }

    private Image loadImage(String url) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            System.out.println("Load icon successful: " + url);
            return image;
        } catch (IOException e) {
            System.out.println("Fail to load icon: " + url);
        }
        return null;
    }
}
