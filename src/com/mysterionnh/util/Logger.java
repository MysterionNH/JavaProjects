package com.mysterionnh.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  
  private PrintWriter writer;
  private final String dateFormat = "dd.MM.yyyy HH:mm:ss";
  private long startTime;
  private long endTime;
  
  
  // TODO: Real exeception handling
  public Logger() {
    try {
      writer = new PrintWriter("log.txt", "UTF8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
      System.exit(-1);
      return;
    }
    try {
      clearFile("log.txt");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
      return;
    }
  }
  
  public Logger(boolean start) {
    try {
      writer = new PrintWriter("log.txt", "UTF8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
      System.exit(-1);
      return;
    }
    try {
      clearFile("log.txt");
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
      return;
    }
    if (start) startLogging(true);
  }
  
  public void startLogging(boolean showTimeStamp) {
    startTime = System.currentTimeMillis();
    writer.write(showTimeStamp ? "Started logging at " + formatTime(dateFormat, startTime) + "\n\n" : "");
  }
  
  public void logString(String msg) {
    System.out.print(msg);
    writer.print(msg);
  }
  
  public void logError(Object context, String msg, boolean fatal) {
    System.err.printf("\nERROR in %s: %s\n", getContext(context), msg);
    writer.println(msg);
    writer.println("\t\t --ERROR--");
    if (fatal) {
      stopLogging(true);
      System.exit(-1);
    }
  }
  
  public void logError(Object context, String msg, boolean fatal, Exception ex) {
    System.err.printf("\nERROR in %s: %s\n", getContext(context), msg);
    writer.println(msg);
    writer.println("\t\t --ERROR--");
    if (fatal) {
      ex.printStackTrace();
      ex.printStackTrace(writer);
      stopLogging(true);
      System.exit(-1);
    }
  }
  
  private void clearFile(String filePath) throws IOException {
    PrintWriter w = new PrintWriter(filePath, "UTF8");
    w.print("");
    w.close();    
  }
  
  public void stopLogging(boolean showTimeStamp) {
    endTime = System.currentTimeMillis();
    writer.write(showTimeStamp ?  "\n\n\nStopped logging at " +
                                  formatTime(dateFormat, endTime) +
                                  ". The programm run for " +
                                  formatTime("MM:ss", endTime - startTime) + "." : "");
    writer.close();
  }
  
  private String formatTime(String format, long time) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Date date = new Date(time);
    return sdf.format(date);
  }
  
  private String getContext(Object con) {
    return con.getClass().getSimpleName();
    
    // TODO: This can be more uselful
  }
  
  public PrintWriter getWriter() {
    return writer;
  }
}
