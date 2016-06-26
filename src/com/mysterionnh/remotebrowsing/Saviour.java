package com.mysterionnh.remotebrowsing;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mysterionnh.util.Logger;

public class Saviour {
  
  public static final int ARGS = 3;
  
  private Logger log;
  private WebDriver driver;
  
  private Stack<String> galleries = new Stack<String>();
  
  private String tag, folderPath;
  
  private List<String> blacklist = new ArrayList<String>(), done = new ArrayList<String>();

  public Saviour(Logger _log, WebDriver _driver) {
    log = _log;
    driver = _driver;
  }

  public void setUrl(String url) {
    galleries.add(url.toLowerCase());
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public void setFolderPath(String folderPath) {
    this.folderPath = folderPath.toLowerCase();
  }
  
  public void setBlacklist(List<String> bl) {
    blacklist = bl;
  }
  
  public void ripSite(String start) {
    while (!galleries.isEmpty()) {
      if (!galleries.peek().equals(start)) done.add(galleries.peek());
      
      driver.get(galleries.pop());
  
      String currentUrl;
      String path = folderPath + driver.getCurrentUrl().substring(driver.getCurrentUrl().indexOf("--/"), driver.getCurrentUrl().length() - 1);
      File f = new File(path);
      f.mkdir();
      
      List<WebElement> urls = driver.findElements(By.tagName(tag));
      
      int counter = 0;
      
      for (WebElement url : urls) {
        try {
          currentUrl = url.getAttribute("href");
          if (!blacklisted(currentUrl)) {
            if (isImageUrl(currentUrl)) {
              URLConnection urlConnection = new URL(currentUrl).openConnection();
              urlConnection.setRequestProperty("User-Agent", "NING/1.0");
              
              FileOutputStream fos = new FileOutputStream(path + "\\pic_" + counter + ".png");
                      
              ImageIO.write(ImageIO.read(urlConnection.getInputStream()), "png", fos);
              fos.close();
              counter++;
            } else {
              // https://i.imgur.com/dJFG4K6.png!
              galleries.push(currentUrl);
            }
          } else {
            // url contains blacklisted string, do nothing ^^
          }
        } catch (Exception e) {
          log.logError(this, "Error", false, e);
        }
      }      
    }
    log.logString("\nDone.");
    
    for (String s : galleries) {
      log.logString(s + "\n");
    }
  }
  
  private boolean blacklisted(String url) {
    url = url.toLowerCase();
    for (String s : blacklist) {
      if (url.contains(s.toLowerCase()) || url.endsWith("albums/")) return true;
    }
    
    for (String s : done) {
      if (url.equals(s)) return true;
    }
    return false;
  }
  
  private boolean isImageUrl(String url) {
    url = url.toLowerCase();
    return url.endsWith("png") || url.endsWith("gif") || url.endsWith("jpg")|| url.endsWith("jpeg"); // TODO: better way, this fails on cache 'n stuff
  }
}