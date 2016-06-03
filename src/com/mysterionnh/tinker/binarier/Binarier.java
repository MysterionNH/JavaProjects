package com.mysterionnh.tinker.binarier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Binarier {
  
  private int divider = 8;
  private boolean hex = false;
  
  public Binarier() {
    
  }
  
  public void setDivider(int div) {
    divider = div;
  }
  
  public void setEncoding(String str) {
    hex = str.equalsIgnoreCase("hex");
  }
  
  public void printBytes(String pathStr) throws IOException {
    int count = 0;
    Path path = Paths.get(pathStr);
    byte[] data = Files.readAllBytes(path);
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
