package com.mysterionnh.remotebrowsing;

import com.mysterionnh.util.IO;
import com.mysterionnh.util.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoImageEnlarger {

    private final Logger log;
    private final WebDriver driver;

    private ArrayList<File> files = new ArrayList<>();

    private String path = "";

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
        boolean done;
        String site = "http://waifu2x.booru.pics";

        files = IO.getFiles(path);

        long startTime = System.currentTimeMillis();

        for (File image : files) {

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
                                receiveNewImage(driver, image);
                            }
                            receiveNewImage(driver, image);
                        } else System.err.println("Already big enough...\n");
                    } else {
                        receiveNewImage(driver, image);
                    }
                }
            } else {
                // Not a supported file
            }
        }

        driver.close();

        System.out.println("\nDone. Completed " + imageCount + " images in " + (System.currentTimeMillis() - startTime) / 1000 + "s.");
    }

    private void receiveNewImage(WebDriver driver, File image) {
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

    private void getImage(String url, File image) {
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
            try {
                out.close();
                Files.deleteIfExists(Paths.get(temp.getAbsolutePath()));
            } catch (IOException ex) {
                // well.. fuck
                log.logError(this, "", true, ex);
            }
            log.logError(this, "", true);
        }
    }

    private boolean isSmallEnough(File image) {
        // test whether image is already larger than max supported size
        BufferedImage img = null;
        try {
            img = ImageIO.read(image);
        } catch (IOException e) {
            log.logError(this, "Unable to access Image!", true, e);
        }
        int width = img.getWidth();
        int height = img.getHeight();

        return (width < 2560 && height < 2560 && image.length() < 5000000L);
    }

    // weird hackery, but works
    public void enlargeWithGoogle() {
        files.addAll(IO.getFiles(path).stream().filter(IO::pretendsToBeImage).collect(Collectors.toList()));

        for (File image : files) {
            driver.get("https://www.google.com/imghp");

            wait(20);

            driver.findElement(By.className("gsst_a")).click();

            driver.findElement(By.cssSelector(".qbtbha.qbtbtxt.qbclr")).click();

            driver.findElement(By.id("qbfile")).sendKeys(image.getAbsolutePath());

            List<WebElement> allSizeLink = driver.findElements(By.tagName("a")).stream().filter(this::isAllSizesLink).collect(Collectors.toList());

            if (allSizeLink.isEmpty()) {
                log.logString(String.format("No other image found for image at \"%s\"\n", image.getAbsolutePath()));
                continue;
            }

            allSizeLink.get(0).click();

            // not necessary, but looks better
            // TODO: resulted in error when image pretends to have larger versions but hasn't (yes that happened) - testing
            if (driver.findElements(By.className("rg_l")) != null)
                ((WebElement) driver.findElements(By.className("rg_l")).toArray()[0]).click();

            String src = driver.getPageSource();

            BufferedImage img = null;
            try {
                img = ImageIO.read(image);
            } catch (IOException e) {
                log.logError(this, "Unable to access Image!", true, e);
            }

            if (Integer.valueOf(src.substring(src.indexOf("\",\"oh\":") + 7, src.indexOf(",\"ou\":\""))) * Integer.valueOf(src.substring(src.indexOf("\",\"ow\":") + 7, src.indexOf(",\"pt\":\""))) > img.getWidth() * img.getHeight() + 1) {
                getImage(src.substring(src.indexOf(",\"ou\":\"") + 7, src.indexOf("\",\"ow\":")), image);
            } else {
                log.logString(String.format("Image found on google is smaller than given one, skipping image at \"%s\"\n", image.getAbsolutePath()));
            }
        }
    }

    public boolean isAllSizesLink(WebElement e) {
        return e.getText().toLowerCase().contains("all sizes");
    }

    private void wait(int timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            log.logError(this, "", true, e);
        }
    }
}