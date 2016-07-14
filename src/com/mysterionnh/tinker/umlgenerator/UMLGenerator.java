package com.mysterionnh.tinker.umlgenerator;

import com.mysterionnh.JavaProjects;
import com.mysterionnh.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UMLGenerator {

    private final ArrayList<File> files = new ArrayList<>();

    public UMLGenerator(Logger _log, String[] args) {
        Logger log = _log;
        if (args.length == 2) {
            try {
                switch (args[1]) {
                    case "-folder":
                        String defaultFolderPath = "../sourceFolder";
                        buildFromFolder(defaultFolderPath, log);
                        break;
                    case "-file":
                        String defaultFilePath = "../source.java";
                        buildFromFile(defaultFilePath, log, 0);
                        break;
                    default:
                        log.logString("Unknown argument: \"" + args[1] + "\". Available arguments are \"-file\" and \"-folder\"");
                        JavaProjects.shutdown(-1, true);
                }
            } catch (IOException e) {
                log.logError(this, "No such file!", true, e);
            }
        } else {
            log.logError(this, "Too many or no arguments! Try either \"-file\" or \"-folder\"", true);
        }
    }

    public void buildFromFolder(String path, Logger log) throws IOException {
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

    public void buildFromFile(String path, Logger log, int fileNum) throws IOException {
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
   private int countOccurrencesOf(String str, String reg) {
     if (!str.isEmpty() || !reg.isEmpty()) {
       return str.length() - str.replace(reg, "").length();
     } else {
       return -1;
     }
   }
   
   */
}
