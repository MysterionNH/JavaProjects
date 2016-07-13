package com.mysterionnh.remotebrowsing;

import com.mysterionnh.util.Logger;
import org.openqa.selenium.WebDriver;

public class RemoteChrome {

    private final WebDriver driver;
    private final Logger log;

    public RemoteChrome(Logger _log, WebDriver _driver) {
        log = _log;
        driver = _driver;
    }

    public RemoteChrome(Logger _log, WebDriver _driver, String url) {
        log = _log;
        driver = _driver;
        driver.get(url);
        log.logString(String.format("Navigated to \"%s\"", url));
        log.stopLogging(true);
        System.exit(0);
    }

    public void navigate(String url) {
        driver.get(url);
        log.logString(String.format("Navigated to \"%s\"", url));
    }
}
