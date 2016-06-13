package com.mysterionnh.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class IO {

  public static String calcSHA1(File file) {

    MessageDigest sha1 = null;
    try {
      sha1 = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      // welp, if sha-1 doesn't exist anymore.. what's the point?
    }
    
    try {
      InputStream input = new FileInputStream(file);
      
      byte[] buffer = new byte[8192];
      int len = input.read(buffer);
    
      while (len != -1) {
          sha1.update(buffer, 0, len);
          len = input.read(buffer);
      }
      
      input.close();

      return (new HexBinaryAdapter().marshal(sha1.digest())).toLowerCase();
    } catch (IOException e) {
      return "";
    }
  }
}
