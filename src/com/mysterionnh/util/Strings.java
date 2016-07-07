package com.mysterionnh.util;

public class Strings {
  public static String revert(String str) {
    String temp = "";
    
    for (int i = str.length() - 1; i > -1; i--) {
      temp += str.charAt(i);
    }
    
    return temp;
  }
  
  public static String beautifyBinStr(String binStr) {
    String temp = "";
    
    binStr = leftPad(binStr, 31, '0');
    
    for (int i = binStr.length() - 1; i > -1; i--) {
      if (((((binStr.length() - 1) - i) % 8) == 0) && (i != (binStr.length() - 1))) {
        temp += " ";
      }
      temp += binStr.charAt(i);
    }
    
    return revert(temp);
  }
  
  public static String beautifyHexStr(String hexStr) {
    String temp = "";

    hexStr = leftPad(hexStr, 7, '0');
    
    for (int i = hexStr.length() - 1; i > -1; i--) {
      if (((((hexStr.length() - 1) - i) % 2) == 0) && (i != (hexStr.length() - 1))) {
        temp += "       ";
      }
      temp += hexStr.charAt(i);
    }
    
    return "       " + revert(temp);
  }
  
  public static String leftPad(String str, int padding, char padder) {
    
    if (!(str.length() >= padding)) {
      for (int i = padding - (str.length()); i > -1; i--) {
        str = padder + str;
      }
    }
    
    return str;
  }
}
