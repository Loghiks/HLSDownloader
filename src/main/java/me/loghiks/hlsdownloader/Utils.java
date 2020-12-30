package me.loghiks.hlsdownloader;

import javax.swing.*;
import java.io.*;

public class Utils {

    public static void displayErrorPopup(String message) {

        JOptionPane.showMessageDialog(null, message, Main.APP_NAME + " - Error", JOptionPane.ERROR_MESSAGE);

    }

    public static void browseFile(File file) {

        try {
            Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
        } catch (IOException ignored) { }

    }

    public static void printStackTrace(File file, Throwable throwable) {

        try {

            PrintStream printStream = new PrintStream(new FileOutputStream(file), true);

            throwable.printStackTrace(printStream);

            printStream.close();

        }catch (IOException ignored) { }

    }

}
