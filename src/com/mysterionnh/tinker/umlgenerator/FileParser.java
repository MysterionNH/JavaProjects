package com.mysterionnh.tinker.umlgenerator;

import com.mysterionnh.util.IO;
import com.mysterionnh.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class FileParser {

    private final Logger log;

    private int _attributes = 0;
    private int _methods = 0;

    private final ArrayList<String> _atts = new ArrayList<>();            // 0
    private final ArrayList<String> _attType = new ArrayList<>();         // 1
    private final ArrayList<String> _attLevel = new ArrayList<>();        // 2

    private final ArrayList<String> _meths = new ArrayList<>();           // 3
    private final ArrayList<String> _paras = new ArrayList<>();           // 4
    private final ArrayList<String> _methType = new ArrayList<>();        // 5
    private final ArrayList<String> _methLevel = new ArrayList<>();       // 6

    private final ArrayList<String> _classNames = new ArrayList<>();      // 7

    private final ArrayList<ArrayList<ArrayList<String>>> nodes = new ArrayList<>();

    public FileParser(Logger logger, int fileNum) {
        log = logger;
        nodes.set(fileNum, (ArrayList<ArrayList<String>>) (Arrays.asList(_atts, _attType, _attLevel, _meths, _paras, _methType, _methLevel)));
    }

    public void parseFile(String path) throws IOException {
        String temp = "";
        boolean cnDone = false;
        int lineCount = IO.countLines(path) + 1;

        char markerChar = ' ';

        File file = new File(path);
        String line;

        log.logString("Reading \"" + path + "\" (" + lineCount + " lines)...\n");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i = 0; (line = br.readLine()) != null; i++) {

                switch (parseLine(line)) {
                    case "Class": {
                        if (!cnDone) {
                            parseClass(line);
                            cnDone = true;
                            markerChar = 'C';
                        }
                        break;
                    }
                    case "Method": {
                        parseMethod(line, i);
                        markerChar = 'M';
                        break;
                    }
                    case "Field": {
                        parseField(line, i);
                        markerChar = 'F';
                        break;
                    }
                    default: {
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
        log.logString("\nDone reading. Found " + _methods + " methods and " + _attributes + " fields.");
    }

    private String parseLine(String line) {
        int charLoc;
        boolean valid;

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
                // (because fields and methods start with the access modifier
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
            } else if (line.contains("(") && line.contains(")") && !line.contains(";")) {
                return "Method";
            } else {
                return "Field";
            }
        } else {
            return "Line";
        }
    }

    private void parseClass(String line) {
        int start;
        int end;

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

        if (Objects.equals(temp, "new")) {
            _methType.set(_methods, _meths.get(_methods));
        }

        // Filter protection level
        start = 0;
        while (line.charAt(start) == ' ') {
            start++;
        }
        end = start;
        while (line.charAt(end) != ' ') {
            end++;
        }

        _methLevel.add(_methods, String.valueOf(getProChar(line.substring(start, end), lineNumber, true)));

        _methods++;
    }

    private void parseField(String line, int lineNumber) {
        //String parts[];
        int start;
        int end;
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

        _atts.add(_attributes, addModifiers(line, start + 1) + line.substring(start + 1, end + 1));

        if (_atts.get(_attributes).contains(";")) {
            _atts.add(_attributes, _atts.get(_attributes).substring(0, _atts.get(_attributes).length() - 1));
        }

        // Return type
        while (line.charAt(start) == ' ') {
            start--;
        }
        end = start + 1;
        while (line.charAt(start - 1) != ' ') {
            start--;
        }
        _attType.add(_attributes, line.substring(start, end));

        // Filter protection level
        start = 0;
        while (line.charAt(start) == ' ') {
            start++;
        }
        end = start;
        while (line.charAt(end) != ' ') {
            end++;
        }
        _attLevel.add(_attributes, String.valueOf(getProChar(line.substring(start, end), lineNumber, false)));

        _attributes++;
        //}
    }

    private String addModifiers(String line, int nameIndex) {
        String temp = "";
        if (line.contains("abstract") && line.indexOf("abstract") < nameIndex) { //$NON-NLS-2$
            temp += "abstract ";
        }
        if (line.contains("final") && line.indexOf("final") < nameIndex) { //$NON-NLS-2$
            temp += "final ";
        }
        if (line.contains("native") && line.indexOf("native") < nameIndex) { //$NON-NLS-2$
            temp += "native ";
        }
        if (line.contains("strictfp") && line.indexOf("strictfp") < nameIndex) { //$NON-NLS-2$
            temp += "strictfp ";
        }
        if (line.contains("static") && line.indexOf("static") < nameIndex) { //$NON-NLS-2$
            temp += "";
        }
        if (line.contains("synchronized") && line.indexOf("synchronized") < nameIndex) { //$NON-NLS-2$
            temp += "synchronized ";
        }
        if (line.contains("transient") && line.indexOf("transient") < nameIndex) { //$NON-NLS-2$
            temp += "transient ";
        }
        if (line.contains("volatile") && line.indexOf("volatile") < nameIndex) { //$NON-NLS-2$
            temp += "volatile ";
        }
        return temp;
    }

    private char getProChar(String str, int line, boolean method) {
        switch (str) {
            case "public": {
                return '+';
            }
            case "private": {
                return '-';
            }
            case "protected": {
                return '~';
            }
            default: {
                log.logError(this, "Whoops, you sure your file is alright? (One " + ((method) ? "method" : "attribute") + " has a weird type, line: " + line + ")", true);
                return ' ';
            }
        }
    }

    public ArrayList<ArrayList<String>> getNode(int num) {
        return nodes.get(num);
    }
}
