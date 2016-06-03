package com.mysterionnh;

import java.io.IOException;

import com.mysterionnh.tinker.binarier.Binarier;

public class JavaProjects {

  public static void main(String[] args) throws IOException {    
    Binarier b = new Binarier();
    b.setDivider(Integer.parseInt(args[1]));
    b.setEncoding(args[2]);
    b.printBytes(args[0]);
  }
}
