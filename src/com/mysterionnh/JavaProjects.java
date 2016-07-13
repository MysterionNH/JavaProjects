package com.mysterionnh;

import com.mysterionnh.exception.InvalidCharInNumberException;
import com.mysterionnh.remotebrowsing.AutoImageEnlarger;
import com.mysterionnh.remotebrowsing.RedditCrawler;
import com.mysterionnh.remotebrowsing.RemoteChrome;
import com.mysterionnh.remotebrowsing.Saviour;
import com.mysterionnh.tinker.*;
import com.mysterionnh.tinker.umlgenerator.UMLGenerator;
import com.mysterionnh.util.Logger;
import com.mysterionnh.util.R;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import com.mysterionnh.util.IO;

public class JavaProjects {

    @SuppressWarnings("FieldCanBeLocal")
    private static WebDriver driver;
    private static Scanner scanner;
    private static Logger log;

    private static final List<String> helpCommands = new ArrayList<>();
    private static final List<String> modules = new ArrayList<>();

    // Options
    private boolean browsingGui = true;

    private AutoImageEnlarger aie = null;

    public static void main(String[] args) {
        log = new Logger(true);
        NumberConverter nc = new NumberConverter(log);
        nc.setFormat(args[1].charAt(2));
        try {
            nc.setNumber(args[0]);
        } catch (InvalidCharInNumberException e) {
            e.printStackTrace();
            return;
        }
        nc.show();
    
    /*
    if (args.length == 0 || args == null) {
      System.out.println("Hello World!");
    } else {
      JavaProjects jp = new JavaProjects();
      jp.handleInput(args);
      log.stopLogging(true);
    }
    */
    }

    public JavaProjects() {
        iniLists();
        System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER_PATH);
        scanner = new Scanner(System.in);
        log = new Logger(true);
    }

    private void handleInput(String[] _args) {
        String[] args = new String[Constants.MAX_ARGS_LENGTH];
        for (int i = 0; i < Constants.MAX_ARGS_LENGTH; i++) {
            if (_args.length > i) {
                args[i] = _args[i];
            } else {
                args[i] = "";
            }
        }

        if (helpCommands.contains(args[0])) {
            if (!args[1].isEmpty()) {
                log.logString(String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, String.format("main_%s_help", modules.get((modules.indexOf(args[1]) != -1) ? modules.indexOf(args[1]) : 0)))));
            } else {
                log.logString(buildHelpText());
            }
        } else if (modules.contains(args[0])) {
            if (helpCommands.contains(args[1]) && args[2].isEmpty()) {
                log.logString(String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, String.format("main_%s_help_ext", modules.indexOf(args[1])))));
            } else if (args[1].equals("o")) { // TODO: verify arg count ^^
                switch (args[2].toLowerCase()) {
                    case "browsinggui": {
                        browsingGui = Boolean.valueOf(args[4]);
                        log.logString(String.format("Browsing GUI : %b", browsingGui));
                        break;
                    }
                    default: {
                        log.logString("No such option! Available options are:\n");
                        log.logString(R.getResource(Constants.STRING_RESOURCE_PATH, "available_options"));
                    }
                }
            } else {
                switch (modules.indexOf(args[0])) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:// remote browsing
                    {
                        if (browsingGui) {
                            ChromeOptions options = new ChromeOptions();
                            //options.addArguments("start-maximized"); // just personal preference
                            driver = new ChromeDriver(options);
                        } else {
                            ChromeOptions options = new ChromeOptions();
                            //options.addArguments("start-maximized"); // just personal preference
                            driver = new ChromeDriver(options);
                            //driver = new HtmlUnitDriver(); // not fully tested //TODO: Implement remote browsing in a way HtmlUnitDriver can handle
                        }
                        switch (modules.indexOf(args[0])) { //TODO: many things started, none finished :/ -args.length, driver.close?, help?
                            case 1: {
                                if (aie == null) {
                                    aie = new AutoImageEnlarger(log, driver);
                                }
                                aie.setPath(args[1]);
                                aie.setDenoiseLevel(Integer.valueOf(args[2]));
                                //aie.enlarge();
                                aie.enlargeWithGoogle();
                                break;
                            }
                            case 2: {
                                new RemoteChrome(log, driver);
                                break;
                            }
                            case 3: {
                                //if (args.length == Saviour.ARGS + 1) {
                                Saviour s = new Saviour(log, driver);
                                s.setUrl(args[1]);
                                s.setFolderPath(args[2]);
                                s.setTag(args[3]);
                                List<String> blacklist = new ArrayList<>();
                                for (int i = 4; i < args.length; i++) {
                                    if (!args[i].isEmpty()) {
                                        blacklist.add(args[i]);
                                    }
                                }
                                s.setBlacklist(blacklist);
                                s.ripSite(args[1]);
                                //}
                                break;
                            }
                            case 4: {
                                RedditCrawler rcl = new RedditCrawler(log, driver);
                                rcl.crawlImageSubreddit(args[1], Integer.valueOf(args[2]), Boolean.valueOf(args[3]));
                                driver.close();
                                break;
                            }
                            default:
                                log.logString("Dafuq?!");
                        }
                        break;
                    }
                    case 5: {
                        new Binarier(log, args);
                        break;
                    }
                    case 6: {
                        new GameOfLife(log, args);
                        break;
                    }
                    case 7: {
                        new LibraryOfBabel(log, args);
                        break;
                    }
                    case 8: {
                        new Primes(log, args);
                        break;
                    }
                    case 9: {
                        new UMLGenerator(log, args);
                        break;
                    }
                    default: {
                        // This should never happen.
                        log.logError(this, "\nDon't even ask", true);
                    }
                }
            }
        } else if (args[0].equals("exit")) {
            return;
        } else if (args[0].equals("gui")) {
            log.logString("Not yet buddy, sorry :(");
        }
        inputPrompt();
    }

    private static void iniLists() {
        helpCommands.add("help");
        helpCommands.add("h");
        helpCommands.add("?");

        modules.add("nosuchmodule");
        modules.add("aie");
        modules.add("rec");
        modules.add("sav");
        modules.add("rcl");
        modules.add("bir");
        modules.add("gof");
        modules.add("lob");
        modules.add("pri");
        modules.add("umg");
        //modules.add("muf");
    }

    private void inputPrompt() {
        log.logString("\n> ");
        handleInput(scanner.nextLine().split(" "));
    }

    private String buildHelpText() {
        return String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, "main_general_help"));
    }
}
