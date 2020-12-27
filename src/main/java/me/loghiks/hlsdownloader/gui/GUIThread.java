package me.loghiks.hlsdownloader.gui;

import me.loghiks.hlsdownloader.Main;

import javax.swing.*;

public class GUIThread extends Thread {

    private GUIThread() {
        super(Main.APP_NAME + " GUI Thread");

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ignored) { }

    }

    @Override
    public void run() {

        HLSFrame frame = new HLSFrame();
        frame.setVisible(true);

    }

    public static void startThread() {

        new GUIThread().start();

    }

}
