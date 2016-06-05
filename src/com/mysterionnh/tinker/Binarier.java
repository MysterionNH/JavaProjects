package com.mysterionnh.tinker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mysterionnh.util.Logger;

public class Binarier {
  
  private Logger log;
  
  private int divider = 8;
  private boolean hex = false;
  
  public Binarier(Logger _log, String[] args) {
    this.log = _log;
    setDivider(Integer.valueOf(args[2]));
    setEncoding(args[3]);
    printBytes(args[1]);
  }
  
  public void setDivider(int div) {
    divider = div;
  }
  
  public void setEncoding(String str) {
    hex = str.equalsIgnoreCase("hex");
  }
  
  public void printBytes(String pathStr) {
    int count = 0;
    Path path = Paths.get(pathStr);
    byte[] data = null;
    try {
      data = Files.readAllBytes(path);
    } catch (Exception e) {
      log.logError(this, "No file at given path!", true, e);
    }
    for (byte b : data) {
      count++;
      
      if (hex) {
        System.out.print((String.format("%02x", b)).toUpperCase());
      } else {
        System.out.print(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
      }
      
      System.out.print(" ");
      
      if (count % divider == 0) {
        System.out.println();
      }
    }
    System.out.printf("\n\n\nFile consited off %d bytes.\n\n", count);
  }
}
