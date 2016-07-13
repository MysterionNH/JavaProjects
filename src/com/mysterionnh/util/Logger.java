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
    @SuppressWarnings("FieldCanBeLocal")
    private long endTime;


    // TODO: Real exception handling
    public Logger() {
        try {
            writer = new PrintWriter("log.txt", "UTF8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(-1);
            return;
        }
        try {
            IO.clearFile("log.txt");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
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
            IO.clearFile("log.txt");
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

    public void logString(Object o) {
        System.out.print(String.valueOf(o));
        writer.print(String.valueOf(o));
    }

    public void logString(String msg, Object[] o) {
        System.out.print(String.format(msg, o));
        writer.print(String.format(msg, o));
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
        writer.println(String.format("\nERROR in %s: %s\n", getContext(context), msg));
        ex.printStackTrace();
        ex.printStackTrace(writer);
        if (fatal) {
            stopLogging(true);
            System.exit(-1);
        }
    }

    public void stopLogging(boolean showTimeStamp) {
        endTime = System.currentTimeMillis();
        writer.write(showTimeStamp ? "\n\n\nStopped logging at " +
                formatTime(dateFormat, endTime) +
                ". The program run for " +
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

        // TODO: This can be more useful
    }

    public PrintWriter getWriter() {
        return writer;
    }
}
