package com.mysterionnh.tinker.umlgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.mysterionnh.util.Logger;

public class UMLBuilder {
  private Logger log;
  
  private final int LETTER_LENGTH = 12;
  private final int LETTER_HEIGTH = 20;
  private final String RESOURCE_PATH = "com.mysterionnh.umlgenerator.strings";
  
  private final String GRAPH_TEMPLATE = getStringFromResource(RESOURCE_PATH, "DraftAndImplementaionDiagrammBuilder.graphTemplate");
  private final String NODE_TEMPLATE = getStringFromResource(RESOURCE_PATH, "DraftAndImplementaionDiagrammBuilder.nodeTemplate");
  private final String ARROW_TEMPLATE = getStringFromResource(RESOURCE_PATH, "DraftAndImplementaionDiagrammBuilder.arrowTemplate");
  
  public UMLBuilder(Logger logger) {
    log = logger;
  }
  
  public void createNodes(FileParser parser, int num) throws IOException {
    ArrayList<ArrayList<String>> strings = parser.getNode(num);
    File tempNode = new File("..\\temp\node" + num + ".temp");
    
    String temp = "";
    String temp2 = "";
    
    int atributes = strings.get(0).size();
    int methods = strings.get(3).size();
    
    String className = strings.get(7).get(0);
    String node = NODE_TEMPLATE;

      for (int j = 0; j < atributes; j++) {
        temp += strings.get(2).get(j).toString() + " " + strings.get(0).get(j) + " : ";
        temp2 = strings.get(1).get(j).replaceAll("<", "&lt;");
        temp += temp2.replaceAll(">", "&gt;") + "\r\n";
      }
      node = node.replace("FIELD_BLOCK", temp);
      temp = "";
      temp2 = "";
      
      temp = "+ " + className + "()\r\n";
      for (int k = 0; k < methods; k++) {
        if (!strings.get(3).get(k).toString().equals(className)) {
          temp += strings.get(6).get(k).toString() + " " + strings.get(3).get(k) + "(";
          temp2 = strings.get(4).get(k).replaceAll("<", "&lt;") + ") : ";
          temp += temp2.replaceAll(">", "&gt;");
          temp2 = strings.get(5).get(k).replaceAll("<", "&lt;");
          temp += temp2.replaceAll(">", "&gt;");
        }
      }
      node = node.replace("METHOD_BLOCK", temp);

      PrintWriter writer = new PrintWriter(tempNode);
      writer.print("");
      writer.close();

      FileOutputStream fos = new FileOutputStream(tempNode);

      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

      for (int i = 0; i < 2; i++) {
        bw.write(node);
        bw.newLine();
      }
      bw.close();
    
    
    atributes = 0;
    methods = 0;
  }  

  public void copy(File sourceLocation, File targetLocation) throws IOException {
    if (sourceLocation.isDirectory()) {
      copyDirectory(sourceLocation, targetLocation);
    } else {
      copyFile(sourceLocation, targetLocation);
    }
  }
  

  private void copyDirectory(File source, File target) throws IOException {
    if (!target.exists()) {
      target.mkdir();
    }

    for (String f : source.list()) {
      copy(new File(source, f), new File(target, f));
    }
  }

  private void copyFile(File source, File target) throws IOException {
    try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
      byte[] buf = new byte[1024];
      int length;
      while ((length = in.read(buf)) > 0) {
        out.write(buf, 0, length);
      }
    }
  }

  public String getStringFromResource(String resPath, String key) {
    ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(resPath);
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}