package com.mysterionnh.tinker.umlgenerator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.mysterionnh.util.Logger;

public class FileParser {

  private Logger log;

  private int _atributes = 0;
  private int _methods = 0;
  
  private ArrayList<String> _atts = new ArrayList<String>();            // 0
  private ArrayList<String> _attType = new ArrayList<String>();         // 1
  private ArrayList<String> _attLevel = new ArrayList<String>();        // 2
  
  private ArrayList<String> _meths = new ArrayList<String>();           // 3
  private ArrayList<String> _paras = new ArrayList<String>();           // 4
  private ArrayList<String> _methType = new ArrayList<String>();        // 5
  private ArrayList<String> _methLevel = new ArrayList<String>();       // 6
  
  private ArrayList<String> _classNames = new ArrayList<String>();      // 7
  
  private ArrayList<ArrayList<ArrayList<String>>> nodes = new ArrayList<ArrayList<ArrayList<String>>>();
  
  public FileParser(Logger logger, int fileNum) {
    log = logger;
    nodes.set(fileNum, (ArrayList<ArrayList<String>>) (Arrays.asList(_atts, _attType, _attLevel, _meths, _paras, _methType, _methLevel)));
  }
  
  public void parseFile(String path) throws IOException {
    String temp = "";
    boolean cnDone = false;
    int lineCount = countLines(path) + 1;
    
    char markerChar = ' ';
    
    File file = new File(path);
    String line;
    
    log.logString("Reading \"" + path + "\" (" + lineCount + " lines)...\n");

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      for (int i = 0; (line = br.readLine()) != null; i++) {
        
        switch (parseLine(line)) {
          case "Class" : {
            if (!cnDone) {
              parseClass(line);
              cnDone = true;
              markerChar = 'C';
            }
            break;
          }
          case "Method" : {
            parseMethod(line, i);
            markerChar = 'M';
            break;
          }
          case "Field" : {
            parseField(line, i);
            markerChar = 'F';
            break;
          }
          default : {
            break;
          }
        }
        
        if (((i + 1) >= 100) && ((i + 1) < 1000)) {
          temp = "0";
        } else if (i + 1 >= 10) {
          temp = "00";
        } else if (i + 1 >= 0) {
          temp = "000";
        }
        
        log.logString(markerChar + " | " + temp + Integer.toString(i + 1) + " |" + line);
        temp = " ";
        markerChar = ' ';
        }
      } catch (Exception e) {
        e.printStackTrace();
    }
    log.logString("\nDone reading. Found " + _methods + " methods and " + _atributes + " fields.");
  }
  
  private String parseLine(String line) {
    int charLoc = -1;
    boolean valid = false;

    // This makes sure that it could be a marked method/field
    if (line.contains("public")) {
      charLoc = line.indexOf("public");
    } else if (line.contains("private")) {
      charLoc = line.indexOf("private");
    } else if (line.contains("protected")) {
      charLoc = line.indexOf("protected");
    } else {
      charLoc = -1; // invalid
    }
    // This thing ones made sense, now it just works
    if (charLoc >= 0) {
      valid = true;
      while (charLoc - 1 >= 0) {
        // Moving to the beginning of the line, if we find anything that is not space, the line is invalid
        // (because fields and mthods start with the access modifier
        if (line.charAt(charLoc - 1) != ' ') {
          valid = false;
          break;
        } else {
          charLoc--;
        }
      }
    } else {
      valid = false;
    }
    if (valid) {
      if (line.contains("class")) {
        return "Class";
      }
      else if (line.contains("(") && line.contains(")") && !line.contains(";")) {
        return "Method";
      } else {
        return "Field";
      }
    } else {
      return "Line";
    }
  }
  
  private void parseClass(String line) {
    int start = 0;
    int end = 0;

    start = line.indexOf("class") + 5; // we need 5 to go to the end of "class"
    while (line.charAt(start) == ' ') {
      start++;
    }
    end = start;
    while (line.charAt(end) != ' ' && line.charAt(end) != '{' && end < line.length()) {
      end++;
    }
    _classNames.add(addModifiers(line, start) + line.substring(start, end));
  }
  
  private void parseMethod(String line, int lineNumber) {
    String temp = "";
    int start = line.indexOf('(');
    int end = line.lastIndexOf(')');
    _paras.add(_methods, line.substring(start + 1, end));

    // Parse name
    start--;
    while (line.charAt(start) == ' ') {
      start--;
    }
    end = start + 1;
    while (line.charAt(start) != ' ') {
      start--;
    }
    
    _meths.add(_methods, addModifiers(line, start + 1) + line.substring(start + 1, end));
    
    // black magic, don't touch
    while (line.charAt(start) == ' ' && start > 0) {
      start--;
    }
    end = start + 1;
    while (line.charAt(start - 1) != ' ') {
      start--;
    }
    _methType.add(_methods, line.substring(start, end));
    
    if (_methType.get(_methods).equals("public")) {
      _methType.add(_methods, "");
    }

    if (temp == "new") {
      _methType.set(_methods, _meths.get(_methods));
    }
    temp = "";
    
    // Filter protection level
    start = 0;
    while (line.charAt(start) == ' ') {
      start++;
    }
    end = start;
    while (line.charAt(end) != ' ') {
      end++;
    }
    temp = "";

    _methLevel.add(_methods, String.valueOf(getProChar(line.substring(start, end), lineNumber, true)));
    
    temp = "";
    _methods++;
  }
  
  private void parseField(String line, int lineNumber) {
    //String parts[];
    int start = 0;
    int end = line.length();
    //if (line.contains(",")) {
    //  parts = line.split(","); //TODO: continue
    //} else {
      if (line.contains("=")) {
        end = line.indexOf('=') - 1;
      } else {
        end = line.length() - 1;
      }
      while (line.charAt(end) == ' ') {
        end--;
      }
      start = end - 1;
      while (line.charAt(start) != ' ') {
        start--;
      }
  
      _atts.add(_atributes, addModifiers(line, start + 1) + line.substring(start + 1, end + 1));
      
      if (_atts.get(_atributes).contains(";")) {
        _atts.add(_atributes, _atts.get(_atributes).substring(0, _atts.get(_atributes).length() - 1));
      }
  
      // Return type
      while (line.charAt(start) == ' ') {
        start--;
      }
      end = start + 1;
      while (line.charAt(start - 1) != ' ') {
        start--;
      }
      _attType.add(_atributes, line.substring(start, end));
      
      // Filter protection level
      start = 0;
      while (line.charAt(start) == ' ') {
        start++;
      }
      end = start;
      while (line.charAt(end) != ' ') {
        end++;
      }
      _attLevel.add(_atributes, String.valueOf(getProChar(line.substring(start, end), lineNumber, false)));
      
      _atributes++;
    //}
  }
  
  private String addModifiers(String line, int nameIndex) {
    String temp = "";
    if (line.indexOf("abstract") >= 0 && line.indexOf("abstract") < nameIndex) { //$NON-NLS-2$
      temp += "abstract ";
    }
    if (line.indexOf("final") >= 0 && line.indexOf("final") < nameIndex) { //$NON-NLS-2$
      temp += "final ";
    }
    if (line.indexOf("native") >= 0 && line.indexOf("native") < nameIndex) { //$NON-NLS-2$
      temp += "native ";
    }
    if (line.indexOf("strictfp") >= 0 && line.indexOf("strictfp") < nameIndex) { //$NON-NLS-2$
      temp += "strictfp ";
    }
    if (line.indexOf("static") >= 0 && line.indexOf("static") < nameIndex) { //$NON-NLS-2$
      temp += "";
    }
    if (line.indexOf("synchronized") >= 0 && line.indexOf("synchronized") < nameIndex) { //$NON-NLS-2$
      temp += "synchronized ";
    }
    if (line.indexOf("transient") >= 0 && line.indexOf("transient") < nameIndex) { //$NON-NLS-2$
      temp += "transient ";
    }
    if (line.indexOf("volatile") >= 0 && line.indexOf("volatile") < nameIndex) { //$NON-NLS-2$
      temp += "volatile ";
    }
    return temp;
  }
  
  private char getProChar(String str, int line, boolean method) {
    switch (str) {
      case "public" : {
        return '+';
      }
      case "private" : {
        return '-';
      }
      case "protected" : {
        return'~';
      }
      default: {
        log.logError("Whoops, you sure your file is alright? (One " + ((method) ? "method" : "atribute") + " has a weird type, line: " + line + ")");
        return ' ';
      }
    }
  }
  
  public static int countLines(String filename) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(filename));
    try {
      byte[] c = new byte[1024];
      int count = 0;
      int readChars = 0;
      boolean empty = true;
      while ((readChars = is.read(c)) != -1) {
        empty = false;
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n') {
            ++count;
          }
        }
      }
      return (count == 0 && !empty) ? 1 : count;
    } finally {
      is.close();
    }
  }

  public ArrayList<ArrayList<String>> getNode(int num) {
    return nodes.get(num);
  }
}
