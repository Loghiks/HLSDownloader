package me.loghiks.hlsdownloader;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Utils {

    public static void displayErrorPopup(String message) {

        JOptionPane.showMessageDialog(null, message, Main.APP_NAME + " - Error", JOptionPane.ERROR_MESSAGE);

    }

    public static void browseFile(File file) {

        try {
            Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
        } catch (IOException ignored) { }

    }

    public static boolean printStackTrace(File file, Throwable throwable) {

        try {

            PrintStream printStream = new PrintStream(new FileOutputStream(file), true);

            throwable.printStackTrace(printStream);

            printStream.close();

            return true;

        }catch (IOException e) {

            return false;

        }

    }

}
