package com.mysterionnh.remotebrowsing;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.mysterionnh.Constants;
import com.mysterionnh.util.Logger;

public class RemoteChrome {
  
  //private Logger log;
  private WebDriver driver;

  public RemoteChrome(Logger _log, String[] args) {
    //log = _log;
    iniDriver();
    driver.get(args[1]);
  }
  
  private void iniDriver() {
    System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_PATH);
  
    ChromeOptions options = new ChromeOptions();
    List<String> chromeArguments = new ArrayList<String>();
    chromeArguments.add("--start-fullscreen");
    options.addArguments(chromeArguments);
    driver = new ChromeDriver(options);
  }
}
