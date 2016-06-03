package com.mysterionnh.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  
  private PrintWriter writer;
  private final String dateFormat = "dd.MM.yyyy HH:mm:ss";
  private long startTime;
  private long endTime;
  
  public Logger(String outputFilePath) throws IOException {
    writer = new PrintWriter(outputFilePath, "UTF8");
    clearFile(outputFilePath);
  }
  
  public void startLogging(boolean showTimeStamp) {
    startTime = System.currentTimeMillis();
    writer.write(showTimeStamp ? "Started logging at " + formatTime(dateFormat, startTime) + "\n\n" : "");
  }
  
  public void logString(String msg) {
    System.out.println(msg);
    writer.println(msg);
  }
  
  public void logError(String msg) {
    System.err.println("ERROR: " + msg);
    writer.println(msg);
    writer.println("ERROR");
    writer.close();
  }
  
  private void clearFile(String filePath) throws IOException {
    PrintWriter w = new PrintWriter(filePath, "UTF8");
    w.print("");
    w.close();    
  }
  
  public void stopLogging(boolean showTimeStamp) {
    endTime = System.currentTimeMillis();
    writer.write(showTimeStamp ?  "\nStopped logging at " +
                                  formatTime(dateFormat, endTime) +
                                  ". The process took " +
                                  formatTime("MM:ss:nnnn", endTime - startTime) + "." : "");
    writer.close();
  }
  
  private String formatTime(String format, long time) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Date date = new Date(time);
    return sdf.format(date);
  }
}
