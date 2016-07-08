package com.mysterionnh.util;

public class Characters {
  public static boolean isHexChar(char c) {
    c = Character.toLowerCase(c);
    return (Character.isDigit(c) || c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f');
  }
}
