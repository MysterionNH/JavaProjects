package com.mysterionnh;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mysterionnh.remotebrowsing.aie.AutoImageEnlarger;
import com.mysterionnh.remotebrowsing.rc.RemoteChrome;
import com.mysterionnh.remotebrowsing.saviour.Saviour;
import com.mysterionnh.tinker.binarier.Binarier;
import com.mysterionnh.tinker.golsim.GameOfLife;
import com.mysterionnh.tinker.libobabts.LibraryOfBabel;
import com.mysterionnh.tinker.primegen.Primes;
import com.mysterionnh.tinker.umlgenerator.UMLGenerator;
import com.mysterionnh.util.Logger;
import com.mysterionnh.util.R;

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
        log.logString(String.format("\n%s\n", R.getResource(Constants.STRING_RESOURCE_PATH, "main_general_help")));
      }
      inputPromt();
    } else if (modules.contains(args[0])) {
      switch (modules.indexOf(args[0])) {
        case 1:
        {
          new AutoImageEnlarger(log, args);
          return;
        }
        case 2:
        {
          new RemoteChrome(log, args);
          return;
        }
        case 3:
        {
          new Saviour(log, args);
          return;
        }
        case 4:
        {
          new Binarier(log, args);
          return;
        }
        case 5:
        {
          new GameOfLife(log, args);
          return;
        }
        case 6:
        {
          new LibraryOfBabel(log, args);
          return;
        }
        case 7:
        {
          new Primes(log, args);
          return;
        }
        case 8:
        {
          new UMLGenerator(log, args);
          return;
        }
        default:
        {
          // This should never happen.
          log.logError(this, "\nDon't even ask", true);
        }
      }
    } else if (args[0].isEmpty()){
      inputPromt();
    } else if (args[0] == "exit"){
      return;
    } else if (args[0] == "gui") {
      log.logString("Not yet buddy, sorry :(");
      inputPromt();
    }
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
}
