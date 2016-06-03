package com.mysterionnh.remotebrowsing.saviour;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Saviour {

  public static void main(String[] args) throws FileNotFoundException, IOException {    
    System.setProperty("webdriver.chrome.driver", "E:\\Niklas\\My Stuff\\PC Files\\Desktop\\RemoteBrowsing\\chromedriver.exe");

    WebDriver driver = new ChromeDriver();
    
    driver.get("E:\\Niklas\\My Stuff\\PC Files\\Desktop\\pica\\h29.html");
    
    String path = "E:\\Niklas\\My Stuff\\PC Files\\Desktop\\pica\\" + (driver.findElement(By.tagName("font")).getText());
    File f = new File(path);
    f.mkdir();
    System.out.println(f.getAbsolutePath());
    
    List<WebElement> urls = driver.findElements(By.tagName("a"));
    
    for (int i = 1; i < urls.size() + 1; i++) {
      try {
        URLConnection urlConnection = new URL(urls.get(i - 1).getAttribute("href")).openConnection();
        urlConnection.setRequestProperty("User-Agent", "NING/1.0");
                
        ImageIO.write((RenderedImage)ImageIO.read(urlConnection.getInputStream()), "png", new FileOutputStream(path + "\\pic_" + i + ".png"));
        System.out.println(i);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    //driver.close();
    System.out.println("Done.");
  }
}
