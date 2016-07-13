package com.mysterionnh.tinker.tests.muffin;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Write a description of class InputDialog here.
 *
 * @author Niklas Halle
 * @version 1.0
 */
class InputDialog {
    private String _name = null;
    private String _age = null;
    private String _complete = "0";

    public void act() {
        String personalInformation[] = inputDialog();
        if (Objects.equals(personalInformation[2], "1")) {
            System.out.println(Arrays.toString(personalInformation));
        } else {
            System.out.println("---");
        }
    }

    // Gets name and age of User
    private String[] inputDialog() {
        String personalInformation[];

        // Den Namen des Nutzers hohlen
        _name = JOptionPane.showInputDialog("Hallo! Schön dich zu sehen.\nWie heißt du?");
        // _name = Greenfoot.ask("Hallo! Schön dich zu sehen.\nWie heißt du?");
        // Eingabe auf Fehler pruefen
        if (!isNameValide(_name)) {
            personalInformation = new String[]{_name, _age, _complete};
            return personalInformation;
        }

        _age = JOptionPane.showInputDialog("\"" + _name + "\"? Ein schöner Name.\nWie alt bist du?");

        // Eingabe auf Fehler pruefen
        if (!isAgeValide(_age)) {
            personalInformation = new String[]{_name, _age, _complete};
            return personalInformation;
        } else {
            _complete = "1";
            if (Integer.parseInt(_age) < 6) {
                logError("User is too young! (" + _age + ").\n");
                personalInformation = new String[]{_name, _age, _complete};
                return personalInformation;
            } else {
                if (Integer.parseInt(_age) > 99) {
                    logError("User is too old! (" + _age + ").\n");
                    personalInformation = new String[]{_name, _age, _complete};
                    return personalInformation;
                }
            }
        }
        personalInformation = new String[]{_name, _age, _complete};
        _name = null;
        _age = null;
        _complete = null;
        return personalInformation;
    }

    private boolean isAgeValide(String age) {
        if (age == null || age.isEmpty() || !("".equals(extractLetter(age)))) {
            _age = JOptionPane.showInputDialog("Dein Alter enth�lt Fehler. Bitte gib deinen Alter ein.\nStelle sicher das es nur Zahlen enth�hlt und du das Feld nicht leer l�sst.\nFalls du beenden m�chtest klicke jetzt auf \"Abbrechen\".");
            if (_age == null || _age.isEmpty() || !("".equals(extractLetter(_age)))) {
                logError("User was unable/didn't want to write his/her age.");
                _age = "NaN";
                return false;
            }
        }
        return true;
    }

    private boolean isNameValide(String name) {
        if (name == null || name.isEmpty() || !("".equals(extractNumber(name)))) {
            _name = JOptionPane.showInputDialog("Dein Name enth�lt Fehler. Bitte gib deinen Namen ein.\nStelle sicher das er keine Zahlen enth�hlt und du das Feld nicht leer l�sst.\nFalls du beenden m�chtest klicke jetzt auf \"Abbrechen\".");
            if (_name == null || _name.isEmpty() || !("".equals(extractNumber(_name)))) {
                logError("User was unable/didn't want to write his/her name.");
                _name = "---";
                return false;
            }
        }
        return true;
    }

    private String extractLetter(final String str) {
        //Ueberpruefen ob der String leer ist
        if (str == null || str.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        // String in eine Liste aus Chars umwandeln, gucken ob Buchstabe(n) darunter sind
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                sb.append(c);
                found = true;
            } else if (found) {
                // If we already found a letter before and this char is not a letter, stop looping
                break;
            }
        }
        // Erstes gefundene Zahlenfeld zurueck geben, wenn nichts gefunden, leer
        return sb.toString();
    }

    private String extractNumber(final String str) {
        //Ueberpruefen ob der String leer ist
        if (str == null || str.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        // String in eine Liste aus Chars umwandeln, gucken ob Zahl(en) darunter sind
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
                found = true;
            } else if (found) {
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }
        // Erstes gefundene Zahlenfeld zurueck geben, wenn nichts gefunden, leer
        return sb.toString();
    }

    // Schreibt eine Fehlermeldung mit ergaenzenden Informationen
    private void logError(String er) {
        System.err.println("ERROR > " + er + "\n");
    }
}
