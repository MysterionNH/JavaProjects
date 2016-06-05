package com.mysterionnh.tinker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.mysterionnh.util.Logger;

public class LibraryOfBabel {
  private String[][] replacements = { {"\\", ".bs."},
                                      {"\"", ".qu."},
                                      {"'", ".at."},
                                      {"!", ".em."},
                                      {"?", ".qm."},
                                      {"(", ".lp."},
                                      {")", ".rp."},
                                      {"{", ".lc."},
                                      {"}", ".rc."},
                                      {"[", ".lb."},
                                      {"]", ".rb."},
                                      {"<", ".la."},
                                      {">", ".ra."},
                                      {"-", ".mi."},
                                      {"=", ".eq."},
                                      {"_", ".us."},
                                      {"+", ".pl."},
                                      {"$", ".do."},
                                      {"€", ".eu."},
                                      {"#", ".ht."},
                                      {"%", ".pc."},
                                      {"@", ".at."},
                                      {":", ".cl."},
                                      {";", ".sc."},
                                      {"*", ".as."},
                                      {"^", ".to."},
                                      {"/", ".sl."},
                                      {"&", ".ad."},
                                      {"~", ".ti."},
                                      {"´", ".al."},
                                      {"`", ".ar."},
                                      {"ü", ".ue."},
                                      {"ä", ".ae."},
                                      {"ö", ".oe."},
                                      {"ß", ".sz."},
                                      {"§", ".pa."},
                                      {"0", ".ze."},
                                      {"1", ".on."},
                                      {"2", ".tw."},
                                      {"3", ".tr."},
                                      {"4", ".fo."},
                                      {"5", ".fi."},
                                      {"6", ".si."},
                                      {"7", ".se."},
                                      {"8", ".ei."},
                                      {"9", ".ni."}
                                      //{".", ",,dot,,"} // preventing corruption/hijacking
                                    };
  private Logger log;
  private WebDriver driver;

  public LibraryOfBabel(Logger _log, String[] args) {
    log = _log;
    
    boolean fetch = false;
    
    boolean error = false;
    String errorMsg = "";

    // verify arguments
    if (args.length > 0 && args[1] != null && args[2] != null) {
      if (args[1].equals("--d") || args[1].equals("--f")) {
        fetch = (args[1].equals("--f"));
      } else {
        System.out.println(args[1] == "--d");
        errorMsg = "Invalid argument \'" + args[1] + "\'! ";
        error = true;
      }
    } else {
      errorMsg = "Given and allowed argument count differ in length! ";
      error = true;
    }
    
    if (error) {
      System.err.println(errorMsg + "Valid arguments are: [path to textfile] --[d/f]\nd\tdeploy a given text to LoB, "
          + "return key\nf\tfetch a text from a given key");
      System.exit(1);
    }

    iniDriver();

    if (fetch) {
      fetch(getAddressFromFile(args[1]), args[2]);
    } else {
      deploy(getTextFromFile(args[1]), args[2]);
    }
    
    driver.close();
  }
  
  private void iniDriver() {
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    
    ChromeOptions options = new ChromeOptions();
    List<String> chromeArguments = new ArrayList<String>();
    //makes chrome window hidden by putting it off screen
    //this does not mean it's unaccessable, it is still in the task bar and can be maximized and put back on screen
    //chromeArguments.add("--window-position=10000,10000");
    chromeArguments.add("--window-position=10000,10000");
    chromeArguments.add("--start-maximized");
    options.addArguments(chromeArguments);
    driver = new ChromeDriver(options);
  }
  
  private String getAddressFromFile(String path) {
    String address = null;
    
    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      address = br.readLine();
    } catch (Exception e) {
      System.err.println("Invalid file path! Make sure that you don\'t have spelling mistakes in your path.");
      System.exit(1);
    }
    if (address.isEmpty()) {
      System.err.println("Invalid file path! Make sure that you don\'t have spelling mistakes in your path.");
      System.exit(1);
    }
    return address;
  }
  
  private void fetch(String address, String filePath) {
    driver.get("https://libraryofbabel.info/book.cgi?" + address);
    
    WebElement text = driver.findElement(By.id("textblock"));
    
    for (String s : (text.getText()).replaceAll("\n", "").split(".nl.")) {
      log.getWriter().append("\n" + decodeString(s));
    }
    log.getWriter().flush();
    // May (read: most likely) doesn't work, I just changed it to somehow be like it was before.. needs testing //TODO
  }
  
  private String getTextFromFile(String path) {
    String text = "";
    
    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
      String line;
      while ((line = br.readLine()) != null) {
         text += line + ".nl.";
      }
    } catch (Exception e) {
      System.err.println("Invalid file path! Make sure that you don\'t have spelling mistakes in your path.");
      System.exit(1);
    }
    text = encodeString(text);
    if (text.length() > 3200) {
      System.err.println("Encoded String is longer then max size (3200), parts will be missing.");
    }
    return text;
  }
  
  private void deploy(String text, String filePath) {
    driver.get("https://libraryofbabel.info/search.html");

    WebElement searchBox = driver.findElement(By.name("find"));
    searchBox.sendKeys(text);
    searchBox.submit();
    
    WebElement match = driver.findElement(By.tagName("a"));
    List<WebElement> page = driver.findElements(By.tagName("b"));
    String[] parts = match.getText().split("w");
    
    log.getWriter().append(match.getAttribute("onclick").split("'")[1] + "-w" + parts[parts.length - 1] + ":" + Integer.parseInt(page.get(1).getText()));
    log.getWriter().flush();
    // May (read: most likely) doesn't work, I just changed it to somehow be like it was before.. needs testing //TODO
  }
  
  private String encodeString(String str) {
    for (int i = 0; i < replacements.length; i++) {
      for (int j = 0; j < str.length(); j++) {
        if (Character.isUpperCase(str.charAt(j))) {
          str = str.substring(0, j) + ".up." + String.valueOf(str.charAt(j)).toLowerCase() + str.substring(j + 1, str.length());
        }
        if (str.charAt(j) == replacements[i][0].charAt(0)) {
          str = str.substring(0, j) + replacements[i][1] + str.substring(j + 1, str.length());
          j--;
        }
      }
    }
    System.out.println(str);
    return str;
  }
  
  private String decodeString(String str) {
    int index = 0;
    for (int i = 0; i < replacements.length; i++) {
      if ((index = str.indexOf(replacements[i][1])) > -1) {
        str = str.substring(0, index) + replacements[i][0] + str.substring(index + 4, str.length());
        i--;
      }
    }
    
    while ((index = str.indexOf(".up.")) != -1) {
      str = str.substring(0, index) + (String.valueOf(str.charAt(index + 4)).toUpperCase() + str.substring(index + 5, str.length()));
    }
    return str;
  }
}

//http://seleniumhq.github.io/selenium/docs/api/java/index.html