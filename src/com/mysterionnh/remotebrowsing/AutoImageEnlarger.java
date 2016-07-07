package com.mysterionnh.remotebrowsing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.mysterionnh.util.IO;
import com.mysterionnh.util.Logger;

public class AutoImageEnlarger {
  
  private Logger log;
  private WebDriver driver;
  
  private ArrayList<File> files = new ArrayList<File>();

  private String path = "";
  
  private long startTime;
  
  private int folderCount = 1;
  private int imageCount = 0;
  private int denoiseLvl = 0;

  public AutoImageEnlarger(Logger _log, WebDriver _driver) {
    log = _log;
    driver = _driver;
  }
  
  public void setPath(String path) {
    this.path = path;
  }

  public void setDenoiseLevel(int denoiseLvl) {
    this.denoiseLvl = denoiseLvl;
  }

  public void enlarge() {
    boolean done = false;
    String site = "http://waifu2x.booru.pics";
    
    try {
      getFiles(path);
    } catch (IOException e) {
      log.logError(this, "Unable to access Images!", true, e);
    }
    
    startTime = System.currentTimeMillis();
    
    for (File image : files) {
      done = false;
      
      if (image.getAbsolutePath().endsWith("png") || image.getAbsolutePath().endsWith("jpg")) {
        System.out.println("Testing \"" + image.getAbsolutePath() + "\"...");
                
        if (!isSmallEnough(image)) {
          System.err.println("Already big enough...\n");
        } else {
          driver.get(site);
          
          // test whether this file was already uploaded, if yes, saves a ton of time
          driver.get("http://waifu2x.booru.pics/outfile/" + IO.calcSHA1(image) + "_s2_n0.png");
          try {
            getImage(driver.getCurrentUrl(), image);
            done = true;
          } catch (Exception e) {
            // image wasn't uploaded before
            done = false;
          }
          
          driver.get(site);
          
          if (!driver.getPageSource().contains("PNG") && !done) {
            driver.get(site);
            
            wait(500);
            
            for (WebElement radio : driver.findElements(By.tagName("input"))) {
              if (radio.getAttribute("name").equals("denoise") && radio.getAttribute("value").equals(String.valueOf(denoiseLvl))) {
                radio.click();
                break;
              }
            }
          
            WebElement fileUpload = driver.findElement(By.name("img"));
            if (fileUpload != null) {
              fileUpload.sendKeys(image.getAbsolutePath());
              driver.findElement(By.id("submit")).click();
            }
            
            if (!(driver.getPageSource().contains("is too large") || driver.getPageSource().contains("was uploaded"))) {
              try {
                WebElement progressBar = driver.findElement(By.className("progress-bar"));
                while (!progressBar.getAttribute("aria-valuenow").equals("1")) {
                  // still waiting
                  System.out.println(progressBar.getAttribute("aria-valuenow"));
                  wait(5000); // do not throttle the connection
                }
                wait(1000); // annoying transition, if we hit in it, the image won't be downloaded
              } catch (Exception e) {
                log.logError(this, "No progressbar\n", false);
                recieveNewImage(driver, image);
              }
              recieveNewImage(driver, image);
            } else {
              System.err.println("Already big enough...\n");
            }
          } else {
            recieveNewImage(driver, image);
          }
        }
      } else {
        // Not a supported file
      }
    }
    
    driver.close();
    
    System.out.println("\nDone. Completed " + imageCount + " images from "
        + folderCount + " folders in " + (System.currentTimeMillis() - startTime)/1000 + "s.");
  }
  
  private void getFiles(String path) throws IOException {
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdir();
    }
    for (File f : folder.listFiles()) {
      files.add(f);
      
      if (f.isDirectory()) {
        folderCount++;
        getFiles(f.getAbsolutePath());
      }
    }
  }
  
  private void recieveNewImage(WebDriver driver, File image) {
    for (WebElement e : driver.findElements(By.tagName("a"))) {
      if (e.getText().equals("PNG")) {
        String output = e.getAttribute("href");
        try {
          getImage(output, image);
          
          imageCount++;
          break;
        } catch (Exception ex) {
          log.logError(this, "No .png available", false);
        }
      }
    }
  }
  
  private void getImage(String url, File image) throws Exception {
    FileOutputStream out = null;
    File temp = new File(image.getAbsolutePath() + ".temp");
    try {
      URLConnection urlConnection = new URL(url).openConnection();
      urlConnection.setRequestProperty("User-Agent", "NING/1.0");
      
      out = new FileOutputStream(temp.getAbsolutePath());
      
      ImageIO.write(ImageIO.read(urlConnection.getInputStream()), "png", out);
      
      out.close();
      
      ImageIO.write(ImageIO.read(temp), "png", image);
      
      Files.deleteIfExists(Paths.get(temp.getAbsolutePath()));
      
      System.out.println("Done: \"" + image.getAbsolutePath() + "\".\n");
    } catch (Exception e) {      
      out.close();      
      Files.deleteIfExists(Paths.get(temp.getAbsolutePath()));
      throw e;
    }
  }
  
  private void wait(int timeInMs) {
    try {
      Thread.sleep(timeInMs);
    } catch (InterruptedException e) {
      log.logError(this, "", true, e);
    }
  }
  
  private boolean isSmallEnough(File image) {
    // test whether image is already larger than max supported size
    BufferedImage bimg = null;
    try {
      bimg = ImageIO.read(image);
    } catch (IOException e) {
      log.logError(this, "Unable to access Image!", true, e);
    }
    int width          = bimg.getWidth();
    int height         = bimg.getHeight();
    
    return (width < 2560 && height < 2560 && image.length() < 5000000L);
  }
  
  public void enlargeWithGoogle() {
    try {
      getFiles(path);
    } catch (IOException e) {
      log.logError(this, "Unable to access Images!", true, e);
    }
    
    for (File image : files) {     
      driver.get("https://www.google.com/imghp");
      driver.findElement(By.className("gsst_a")).click();
      driver.findElement(By.cssSelector(".qbtbha.qbtbtxt.qbclr")).click();
      
      WebElement fileUpload = driver.findElement(By.id("qbfile"));
      
      if (fileUpload != null) {
        fileUpload.sendKeys(image.getAbsolutePath());
        //driver.findElement(By.id("submit")).click();
      }
      
      wait(7500);
      
      
    }
  }
}