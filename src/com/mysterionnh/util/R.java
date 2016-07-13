package com.mysterionnh.util;

import java.util.ResourceBundle;

// ResourceHandler
public class R {

    public static String getResource(String bundlePath, String key) {
        ResourceBundle RESOURCE_BUNDLE;

        RESOURCE_BUNDLE = ResourceBundle.getBundle(bundlePath);
        return RESOURCE_BUNDLE.getString(key);
    }
}
