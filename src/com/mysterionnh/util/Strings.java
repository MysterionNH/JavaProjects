package com.mysterionnh.util;

import com.mysterionnh.Constants;

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

        return "      " + revert(temp);
    }

    public static String leftPad(String str, int padding, char pad) {
        if (!(str.length() >= padding)) {
            for (int i = padding - (str.length()); i > -1; i--) {
                str = pad + str;
            }
        }
        return str;
    }

    public static String rightPad(String str, int padding, char pad) {
        for (int i = padding - (str.length()); i > -1; i--) {
            str = str + pad;
        }
        return str;
    }

    public static char isValidDecString(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return c;
        }
        return Constants.TRUEc;
    }

    public static char isValidHexString(String str) {
        for (char c : str.toCharArray()) {
            if (!Characters.isHexChar(c)) return c;
        }
        return Constants.TRUEc;
    }

    public static char isValidBinString(String str) {
        for (char c : str.toCharArray()) {
            if (c != '1' && c != '0') return c;
        }
        return Constants.TRUEc;
    }

    public static String toBinString(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
        }
        return result;
    }

    public static String toHexString(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += String.format("%02x", b).toUpperCase();
        }
        return result;
    }
}
