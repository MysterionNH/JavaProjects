package com.mysterionnh.remotebrowsing.aie;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AutoImageEnlarger {
  
  private ArrayList<File> files = new ArrayList<File>();
  private long startTime;
  private int imageCount = 0, folderCount = 1;

  public static void main(String[] args) throws Exception {
    
    AutoImageEnlarger aie = new AutoImageEnlarger();
    
    aie.enlarge(args[0], Integer.parseInt(args[1]), args[2]);
  }
  
  private void enlarge(String path, int denoiseLvl, String chromeDriverPath) throws Exception {
    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    
    ChromeOptions options = new ChromeOptions();
    List<String> chromeArguments = new ArrayList<String>();
    //makes chrome window hidden by putting it off screen
    //this does not mean it's unaccessable, it is still in the task bar and can be maximized and put back on screen
    //chromeArguments.add("--window-position=10000,10000");
    chromeArguments.add("--window-position=10000,10000");
    options.addArguments(chromeArguments);
    WebDriver driver = new ChromeDriver(options);
    
    String site = "http://waifu2x.booru.pics";
    
    getFiles(path);
    
    startTime = System.currentTimeMillis();
    
    for (File image : files) {
      if (image.getAbsolutePath().endsWith("png") || image.getAbsolutePath().endsWith("jpg")) {
        System.out.println("Testing \"" + image.getAbsolutePath() + "\"...");
        // test whether image is already larger than max supported size
        BufferedImage bimg = ImageIO.read(image);
        int width          = bimg.getWidth();
        int height         = bimg.getHeight();
        
        if (width > 2560 || height > 2560 || image.length() > 5000000) {
          System.err.println("Already big enough...\n");
        } else {
          driver.get(site);
          
          // test whether this file was already uploaded, if yes, saves a ton of time
          //driver.get("http://waifu2x.booru.pics/Home/show?hash=" + calcSHA1(image) + "_s2_n1");
          
          if (!driver.getPageSource().contains("PNG")) {
            driver.get(site);
            
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
              WebElement progressBar = driver.findElement(By.className("progress-bar"));
              while (!progressBar.getAttribute("aria-valuenow").equals("1")) {
                // still waiting
                System.out.println("Waiting for upload to finish...");
                Thread.sleep(5000); // do not throttle the connection
              }
              Thread.sleep(1000); // annoying transition, if we hit in it, the image won't be downloaded
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
  
  private String calcSHA1(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {

    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
    
    try (InputStream input = new FileInputStream(file)) {

      byte[] buffer = new byte[8192];
      int len = input.read(buffer);
    
      while (len != -1) {
          sha1.update(buffer, 0, len);
          len = input.read(buffer);
      }

      return (new HexBinaryAdapter().marshal(sha1.digest())).toLowerCase();
    }
  }
  
  private void recieveNewImage(WebDriver driver, File image) {
    for (WebElement e : driver.findElements(By.tagName("a"))) {
      if (e.getText().equals("PNG")) {
        String output = e.getAttribute("href");
        try {
          URLConnection urlConnection = new URL(output).openConnection();
          urlConnection.setRequestProperty("User-Agent", "NING/1.0");
          
          FileOutputStream out = new FileOutputStream(image.getAbsolutePath());
          
          ImageIO.write((RenderedImage)ImageIO.read(urlConnection.getInputStream()), "png", out);
          
          System.out.println("Done: \"" + image.getAbsolutePath() + "\".\n");
          
          out.close();
          
          imageCount++;
          break;
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
