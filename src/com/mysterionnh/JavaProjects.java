package com.mysterionnh;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mysterionnh.util.R;
import com.mysterionnh.util.Logger;

import com.mysterionnh.tinker.Primes;
import com.mysterionnh.tinker.Binarier;
import com.mysterionnh.tinker.GameOfLife;
import com.mysterionnh.tinker.LibraryOfBabel;
import com.mysterionnh.tinker.umlgenerator.UMLGenerator;

import com.mysterionnh.remotebrowsing.Saviour;
import com.mysterionnh.remotebrowsing.RemoteChrome;
import com.mysterionnh.remotebrowsing.AutoImageEnlarger;

public class JavaProjects {

  private static Scanner scanner;
  private static Logger log;
  
  private static List<String> helpCommands = new ArrayList<String>();
  private static List<String> modules      = new ArrayList<String>();

  public static void main(String[] args) {
    if (args.length == 0 || args == null) {
      System.out.println("Hello World!");
    } else {
      JavaProjects jp = new JavaProjects();
      jp.handleInput(args);
      log.stopLogging(true);
    }
  }
  
  public JavaProjects() {
    iniLists();  
    scanner = new Scanner(System.in);
    log = new Logger(true);
  }
  
  private void handleInput(String[] _args) {
    String[] args = new String[Constants.MAX_ARGS_LENGHT];
    for (int i = 0; i < Constants.MAX_ARGS_LENGHT; i++) {
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
      } else {
        switch (modules.indexOf(args[0])) {
          case 1:
          {
            new AutoImageEnlarger(log, args);
            break;
          }
          case 2:
          {
            new RemoteChrome(log, args);
            break;
          }
          case 3:
          {
            new Saviour(log, args);
            break;
          }
          case 4:
          {
            new Binarier(log, args);
            break;
          }
          case 5:
          {
            new GameOfLife(log, args);
            break;
          }
          case 6:
          {
            new LibraryOfBabel(log, args);
            break;
          }
          case 7:
          {
            new Primes(log, args);
            break;
          }
          case 8:
          {
            new UMLGenerator(log, args);
            break;
          }
          default:
          {
            // This should never happen.
            log.logError(this, "\nDon't even ask", true);
          }
        }
      }
    } else if (args[0].isEmpty()) {
      // do nothin'
    } else if (args[0].equals("exit")) {
      return;
    } else if (args[0] == "gui") {
      log.logString("Not yet buddy, sorry :(");
    }
    inputPromt();
  }
  
  private static void iniLists() {
    helpCommands.add("help");
    helpCommands.add("h");
    helpCommands.add("?");

    modules.add("nosuchmodule");
    modules.add("aie");
    modules.add("rec");
    modules.add("sav");
    modules.add("bir");
    modules.add("gof");
    modules.add("lob");
    modules.add("pri");
    modules.add("umg");
    //modules.add("muf");
  }
  
  private void inputPromt() {
    log.logString("\n> ");
    handleInput(scanner.nextLine().split(" "));
  }
  
  private String buildHelpText() {
    StringBuilder b = new StringBuilder();
    b.append(String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, "main_general_help")));
    return String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, "main_general_help"));
  }
}
