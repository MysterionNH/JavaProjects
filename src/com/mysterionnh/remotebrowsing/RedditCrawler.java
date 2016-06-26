package com.mysterionnh.remotebrowsing;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mysterionnh.util.Logger;

public class RedditCrawler {
  private WebDriver driver;
  private Logger log;
  
  public RedditCrawler(Logger _log, WebDriver _driver) {
    log = _log;
    driver = _driver;
  }
  
  public void crawlImageSubreddit(String url, int maxCrawlDepth, boolean postFolders) {
    LinkedList<String> imageElementUrls = new LinkedList<String>();
    LinkedList<String> imageElementNames = new LinkedList<String>();
    LinkedList<String> pageUrls = new LinkedList<String>();
    
    pageUrls.add(url);
    
    
    for (int i = 0; i < maxCrawlDepth; i++) {
      if (!pageUrls.peekFirst().isEmpty()) {
        
        driver.get(pageUrls.pollFirst());
        
        if (driver.getPageSource().toLowerCase().contains("over18")) {
          for (WebElement temp : driver.findElements(By.name("over18"))) {
            if (temp.getAttribute("value").toLowerCase().equals("yes")) temp.click(); 
          }
        }
        
        List<WebElement> links = driver.findElements(By.tagName("a"));
        
        for (WebElement e : links) {
          if (e.getAttribute("class").equals("title may-blank ") && isImageUrl(e.getAttribute("href"))) {
            imageElementUrls.add(e.getAttribute("href"));
            imageElementNames.add(e.getText());
            System.out.println(e.getText());
          }
          if (e.getAttribute("rel").equals("nofollow next")) pageUrls.add(e.getAttribute("href"));
        }
      } else {
        // reached last page of subreddit
      }
    }
    
    for (int i = 0; i < imageElementUrls.size(); i++) {
      
      String path = String.format("pics/%s", postFolders ? imageElementNames.get(i) : "");
      
      File f = new File(path);
      f.mkdir();
      
      try {
        String currentUrl = imageElementUrls.get(i);
        
        driver.get(currentUrl); // just for showing off ^^
        
        URLConnection urlConnection = new URL(currentUrl).openConnection();
        urlConnection.setRequestProperty("User-Agent", "NING/1.0");
        
        FileOutputStream fos = new FileOutputStream(path + getImgurName(currentUrl) + ".png");
                
        ImageIO.write(ImageIO.read(urlConnection.getInputStream()), "png", fos);
        fos.close();
        
        log.logString(String.format("\nDownloaded reddit post \"%s\" (imgur address: \"%s\"", imageElementNames.get(i), currentUrl));
      } catch (Exception ex) {
        log.logError(this, "Error", false, ex);
      }
    }
    
    log.logString("\nDone.");
  }

  private boolean isImageUrl(String url) {
    url = url.toLowerCase();
    return url.endsWith("png") || url.endsWith("gif") || url.endsWith("jpg")|| url.endsWith("jpeg"); // TODO: better way, this fails on cache 'n stuff
  }
  
  private String getImgurName(String imgurImageUrl) {
    return imgurImageUrl.substring(imgurImageUrl.lastIndexOf('/'), imgurImageUrl.lastIndexOf('.'));
  }
}
