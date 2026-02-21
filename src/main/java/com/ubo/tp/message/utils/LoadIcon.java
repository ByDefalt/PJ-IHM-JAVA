package com.ubo.tp.message.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoadIcon {
    public static Image loadIcon(String path) {
        try {
            URL url = LoadIcon.class.getResource(path);
            if (url == null) {
                return null;
            }
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }
}
