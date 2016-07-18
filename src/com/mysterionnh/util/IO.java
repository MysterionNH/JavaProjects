package com.mysterionnh.util;

import com.mysterionnh.Constants;
import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
                assert sha1 != null : "Hash is null!";
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            input.close();

            return (new HexBinaryAdapter().marshal(sha1 != null ? sha1.digest() : new byte[0])).toLowerCase();
        } catch (IOException e) {
            return "";
        }
    }

    public static void cloneTextFileLineByLineWithBlacklist(String sourcePath, String targetPath, List<String> blacklist) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(sourcePath)));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPath))));

        String line;
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

    public static void clearFile(String filePath) throws IOException {
        PrintWriter w = new PrintWriter(filePath, "UTF8");
        w.print("");
        w.close();
    }

    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        for (String f : sourceDir.list()) {
            copy(new File(sourceDir, f), new File(targetDir, f));
        }
    }

    public static void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    public static int countLines(String filename) throws IOException {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        boolean empty = true;

        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        }

        return (count == 0 && !empty) ? 1 : count;
    }

    public static ArrayList<File> getFiles(String dirPath) {
        ArrayList<File> content = new ArrayList<>();

        File folder = new File(dirPath);

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File f : folder.listFiles()) {
            content.add(f);

            if (f.isDirectory()) {
                content.addAll(getFiles(f.getAbsolutePath()));
            }
        }
        return content;
    }

    public static boolean pretendsToBeImage(File f) {
        if (f.isFile()) {
            System.out.println(f.getAbsolutePath());
            return Collections.toList(Constants.IMAGE_SUFFIXES).contains(FilenameUtils.getExtension(f.getPath().toLowerCase()));
        } else return false;
    }
}
