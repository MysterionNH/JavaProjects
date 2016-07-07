package com.mysterionnh.tinker;

import com.mysterionnh.util.Logger;
import com.mysterionnh.util.Strings;

public class NumberConverter {
  
  private Logger log;
  
  public NumberConverter(Logger _log) {
    log = _log;
  }
  
  private char givenFormat = 'x';
  private int number = 0;
  
  public boolean setFormat(char _givenFormat) {
    if (_givenFormat == 'd' || _givenFormat == 'b' || _givenFormat == 'h') {
      givenFormat = _givenFormat;
    } else {
      log.logError(this, String.format("Invalid format: %s", _givenFormat), false);
      return false;
    }
    return true;
  }
  
  public void setNumber(String numberToConvert) {
    //numberToConvert = numberToConvert.replaceAll(".", "");
    
    switch (givenFormat) {
      case 'd':
      {
        number = Integer.parseInt(numberToConvert);
        break;
      }
      case 'b':
      {
        number = decodeBinaryNumber(numberToConvert);
        break;
      }
      case 'h':
      {
        numberToConvert = "0x" + numberToConvert;
        number = Integer.decode(numberToConvert);
        break;
      }
      default: log.logError(this, "Invalid number format!", false);
    }
  }

  public void show() {
    switch (givenFormat) {
      case 'd':
      {
        log.logString(String.format("\nBin:\t%s\nHex:\t%s\n", Strings.beautifyBinStr(Integer.toBinaryString(number)), Strings.beautifyHexStr(Integer.toHexString(number).toUpperCase())));
        break;
      }
      case 'b':
      {
        log.logString(String.format("\nDec:\t%s\nHex:\t%s\n", Strings.leftPad(Integer.toString(number), 34, ' '), Strings.beautifyHexStr(Integer.toHexString(number).toUpperCase())));
        break;
      }
      case 'h':
      {
        log.logString(String.format("\nDec:\t%s\nBin:\t%s\n", Strings.leftPad(Integer.toString(number), 34, ' '), Strings.beautifyBinStr(Integer.toBinaryString(number))));
        break;
      }
      default: log.logError(this, "Invalid number format!", false);
    }
  }
  
  private int decodeBinaryNumber(String str) {
    int solution = 0;
    str = Strings.revert(str);
    
    if (isBinaryString(str)) {
      for (int i = 0; i < str.length(); i++) {
        solution += (Integer.parseInt(String.valueOf(str.charAt(i))) * Math.pow(2, i));
      }
    }
    
    return solution;
  }
  
  private boolean isBinaryString(String str) {
    for (char c : str.toCharArray()) {
      if (!(c != '1' || c != '0')) {
        return false;
      }
    }
    return true;
  }
}