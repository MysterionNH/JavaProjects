package com.mysterionnh.tinker;

import java.math.BigInteger;

import com.mysterionnh.exception.InvalidCharInNumberException;
import com.mysterionnh.util.Logger;
import com.mysterionnh.util.Strings;

public class NumberConverter {
  
  private Logger log;
  
  private char givenFormat = 'x';
  private BigInteger number;
  BigInteger TWO = BigInteger.valueOf(2L);
  BigInteger SIXTEEN = BigInteger.valueOf(16L);
  
  public NumberConverter(Logger _log) {
    log = _log;
  }
  
  public boolean setFormat(char _givenFormat) {
    if (_givenFormat == 'd' || _givenFormat == 'b' || _givenFormat == 'h') {
      givenFormat = _givenFormat;
    } else {
      log.logError(this, String.format("Invalid format: %s", _givenFormat), false);
      return false;
    }
    return true;
  }
  
  public void setNumber(String numberToConvert) throws InvalidCharInNumberException {
    //numberToConvert = numberToConvert.replaceAll(".", "");
    
    switch (givenFormat) {
      case 'd':
      {
        number = decodeDecNumber(numberToConvert);
        break;
      }
      case 'b':
      {
        number = decodeBinNumber(numberToConvert);
        break;
      }
      case 'h':
      {
        number = decodeHexNumber(numberToConvert);
        break;
      }
      default: log.logError(this, "Invalid number format!", false);
    }
  }

  public void show() {
    switch (givenFormat) {
      case 'd':
      {
        log.logString(String.format("\nHex:\t%s\nBin:\t%s\n",
            Strings.beautifyHexStr(Strings.toHexString(number.toByteArray()).toUpperCase()),
            Strings.beautifyBinStr(Strings.toBinString(number.toByteArray()))));
        break;
      }
      case 'b':
      {
        log.logString(String.format("\nDec:\t%s\nHex:\t%s\n",
            Strings.leftPad(number.toString(), calcPadding(number.bitCount()), ' '),
            Strings.beautifyHexStr(Strings.toHexString(number.toByteArray()).toUpperCase())));
        break;
      }
      case 'h':
      {
        log.logString(String.format("\nDec:\t%s\nBin:\t%s\n",
            Strings.leftPad(number.toString(), calcPadding(number.bitCount()), ' '),
            Strings.beautifyBinStr(Strings.toBinString(number.toByteArray()))));
        break;
      }
      default: log.logError(this, "Invalid number format!", false);
    }
  }
  // TODO: Optimize to only use BIMath when needed, because it really lacks performance ^^
  private BigInteger decodeBinNumber(String str) throws InvalidCharInNumberException {
    BigInteger localTwo = TWO;
    BigInteger result = new BigInteger("0");
    str = Strings.revert(str);
    
    if (Strings.isValidBinString(str)) {
      for (int i = 0; i < str.length(); i++) {
        localTwo = TWO;
        BigInteger temp = localTwo.pow(i);
        result = result.add(temp.multiply(new BigInteger(String.valueOf(str.charAt(i)))));
      }
    } else {
      throw new InvalidCharInNumberException();
    }
    
    return result;
  }
  // TODO: Optimize to only use BIMath when needed, because it really lacks performance ^^
  private BigInteger decodeHexNumber(String str) throws InvalidCharInNumberException {
    BigInteger localSixteen = SIXTEEN;
    BigInteger result = new BigInteger("0");
    str = Strings.revert(str);
    
    if (Strings.isValidBinString(str)) {
      for (int i = 0; i < str.length(); i++) {
        localSixteen = SIXTEEN;
        BigInteger temp = localSixteen.pow(i);
        result = result.add(temp.multiply(new BigInteger(String.valueOf(Character.getNumericValue(str.charAt(i))))));
      }
    } else {
      throw new InvalidCharInNumberException();
    }
    
    return result;
  }
  
  private BigInteger decodeDecNumber(String str) throws InvalidCharInNumberException {
    if (Strings.isValidDecString(str)) {
      return new BigInteger(str);
    } else {
      throw new InvalidCharInNumberException();
    }
  }
  
  private int calcPadding(int numLength) {
    return numLength < 32 ? 31 : (int)(Math.ceil(number.bitCount() / 8)); //FIXME: 08.07.16
  }
}