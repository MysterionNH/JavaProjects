package com.mysterionnh.tinker.umlgenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.mysterionnh.util.Logger;

public class UMLGenerator {
  private static final String LOG_PATH = "..\\latest.log";
  private static final String defaultFilePath = "..\\source.java";
  private static final String defaultFolderPath = "..\\soureFolder";
  
  private static ArrayList<File> files = new ArrayList<File>();

  public static void main(String[] args) throws IOException {
    Logger log = new Logger(LOG_PATH);
    log.startLogging(true);
    if (args.length == 1) {
      if (args[0].equals("-folder")) {
        buildFromFolder(defaultFolderPath, log);
      } else if (args[0].equals("-file")) {
        buildFromFile(defaultFilePath, log, 0);
      } else {
        log.logString("Unknowm argument: \"" + args[0] + "\". Available arguments are \"-file\" and \"-folder\""); //$NON-NLS-2$
        System.exit(-1);
      }
    } else {
      log.logError("Too many or no argumets! Try either \"-file\" or \"-folder\"");
      System.exit(-1);
    }
    log.stopLogging(true);
  }
  
  public static void buildFromFolder(String path, Logger log) throws IOException {
    int fileNum = 0;
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdir();
    }
    for (File cF : folder.listFiles()) {
      files.add(cF);
      
      if (cF.isDirectory()) {
        buildFromFolder(files.get(files.size() - 1).getAbsolutePath(), log);
      }
    }
    
    for (File f : files) {
      if (f.getName().endsWith(".java")) {
        buildFromFile(f.getPath(), log, fileNum);
        fileNum++;
      } else {
        // not java file
      }
    }
  }
  
  public static void buildFromFile(String path, Logger log, int fileNum) throws IOException {
    FileParser parser = new FileParser(log, fileNum);
    parser.parseFile(path);
    UMLBuilder builder = new UMLBuilder(log);
    builder.createNodes(parser, fileNum);
    //packOdt(new File(path).getParentFile().getAbsolutePath());
  }
  
  //TODO: Add support for nested classes
    
   /*
    * @param str:        String to count in
    *        reg:        what should be counted
    * @return            returns Occurrences of reg in original or -1 if reg or str where empty
    *
   private static int countOccurrencesOf(String str, String reg) {
     if (!str.isEmpty() || !reg.isEmpty()) {
       return str.length() - str.replace(reg, "").length();
     } else {
       return -1;
     }
   }
   
   */
}
