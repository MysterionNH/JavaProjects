package com.mysterionnh.remotebrowsing.rc;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class RemoteChrome {

  public static void main(String[] args) {
    System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");

    ChromeOptions options = new ChromeOptions();
    List<String> chromeArguments = new ArrayList<String>();
    chromeArguments.add("--start-fullscreen");
    options.addArguments(chromeArguments);
    WebDriver driver = new ChromeDriver(options);
    driver.get(args[0]);
  }
}
