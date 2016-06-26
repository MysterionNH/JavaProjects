package com.mysterionnh.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
  
  public static void cloneTextFileLinebyLineWithBlacklist(String sourcePath, String targetPath, List<String> blacklist) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(new File(sourcePath)));
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath))));
    
    String line = "";
    boolean blacklisted = false;
    
    while ((line = br.readLine()) != null) {
      line += "\n";
      for (String s : blacklist) {
        if (line.contains(s)) {
          blacklisted = true;
          break;
        }
      }
      if (!blacklisted) {
        bw.write(line);
      }
      blacklisted = false;
    }
    br.close();
    bw.close();
  }
}
