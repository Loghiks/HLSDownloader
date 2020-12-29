package me.loghiks.hlsdownloader.gui;

import me.loghiks.hlsdownloader.Main;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Utils {

    public static void displayErrorPopup(String message) {

        JOptionPane.showMessageDialog(null, message, Main.APP_NAME + " - Error", JOptionPane.ERROR_MESSAGE);

    }

}
