package com.mysterionnh.remotebrowsing;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.mysterionnh.Constants;
import com.mysterionnh.util.Logger;

public class Saviour {
  
  Logger log;

  public Saviour(Logger _log, String[] args) {
    log = _log;
    
    System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_PATH);
    WebDriver driver = new ChromeDriver();
    
    driver.get(args[1]);
    
    String path = args[2];// + "_" + driver.getTitle();
    File f = new File(path);
    f.mkdir();
    Object o[] = {f.getAbsolutePath()};
    log.logString("\n%s\n", o);
    
    List<WebElement> urls = driver.findElements(By.tagName("a"));
    
    for (int i = 1; i < urls.size() + 1; i++) {
      try {
        URLConnection urlConnection = new URL(urls.get(i - 1).getAttribute("href")).openConnection();
        urlConnection.setRequestProperty("User-Agent", "NING/1.0");
        
        FileOutputStream fos = new FileOutputStream(path + "\\pic_" + i + ".png");
                
        ImageIO.write((RenderedImage)ImageIO.read(urlConnection.getInputStream()), "png", fos);
        fos.close();
        log.logString("\n" + String.valueOf(i));
      } catch (Exception e) {
        log.logError(this, "Error", false);
      }
    }
    //driver.close();
    System.out.println("\nDone.");
  }
}