package me.loghiks.hlsdownloader.gui;

import me.loghiks.hlsdownloader.Main;
import me.loghiks.hlsdownloader.process.Downloader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class HLSFrame extends JFrame {

    public static HLSFrame INSTANCE;

                                                    //TODO
    private static final String[] TYPES = {"File", /*"Url"*/};

    private final JButton selectButton = new JButton("Select File");
    private final JComboBox<String> inputTypeChooser = new JComboBox<>(TYPES);
    private final JTextField inputField = new JTextField();
    private final JTextField outputField = new JTextField();
    private final JButton downloadButton = new JButton("Download");
    private final JProgressBar progressBar = new JProgressBar();

    private File selectedFile;
    private File outputFile;

    public HLSFrame() throws HeadlessException {

        INSTANCE = this;

        this.setTitle(Main.APP_NAME);
        this.setSize(400, 260);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setupSelectButton();
        setupTypeChooser();
        setupInputTextField();
        setupOutputTextField();
        setupDownloadButton();
        setupProgressBar();

    }

    private void setupSelectButton() {

        selectButton.setBounds(260, 10, 100, 30);
        selectButton.setFocusable(false);
        selectButton.setFont(selectButton.getFont().deriveFont(15f));

        selectButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("HLS file (.m3u8)", "m3u8");
                fileChooser.setFileFilter(filter);

                int returnVal = fileChooser.showOpenDialog(HLSFrame.this);

                if(returnVal == JFileChooser.APPROVE_OPTION) {

                    selectedFile = fileChooser.getSelectedFile();
                    inputField.setText(selectedFile.getAbsolutePath());

                    outputFile = new File(fileChooser.getFileSystemView().getDefaultDirectory(),
                            selectedFile.getName().split("\\.")[0] + ".mp4");

                    outputField.setText(outputFile.getAbsolutePath());

                }

            }

        });

        this.add(selectButton);

    }

    private void setupTypeChooser() {

        inputTypeChooser.setBounds(150, 10, 100, 30);
        inputTypeChooser.setFocusable(false);
        inputTypeChooser.setFont(inputTypeChooser.getFont().deriveFont(15f));

        inputTypeChooser.addActionListener(e -> {

            selectedFile = null;
            outputFile = null;
            inputField.setText("");
            outputField.setText("");

            boolean flag = inputTypeChooser.getSelectedIndex() == 0;

            selectButton.setVisible(flag);
            inputField.setEnabled(!flag);

        });

        this.add(inputTypeChooser);

    }

    private void setupInputTextField() {

        JLabel label = new JLabel("Input: ");
        label.setBounds(13, 20, 100, 30);
        label.setFont(label.getFont().deriveFont(15f));

        this.add(label);


        inputField.setBounds(10, 50, 350, 30);
        inputField.setEnabled(false);

        inputField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                if(inputField.getText().isEmpty() || inputTypeChooser.getSelectedIndex() != 1) return;

                outputFile = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "video-" + System.currentTimeMillis() + ".mp4");

                outputField.setText(outputFile.getAbsolutePath());

            }

        });

        this.add(inputField);

    }

    private void setupOutputTextField() {

        JLabel label = new JLabel("Output: ");
        label.setBounds(13, 90, 100, 30);
        label.setFont(label.getFont().deriveFont(15f));

        this.add(label);


        outputField.setBounds(10, 120, 350, 30);

        outputField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                if(inputField.getText().isEmpty() || outputField.getText().isEmpty()) return;

                //Not the best way
                outputFile = new File(outputField.getText());

            }

        });

        this.add(outputField);

    }

    private void setupDownloadButton() {

        downloadButton.setBounds(260, 170, 100, 30);
        downloadButton.setFocusable(false);
        downloadButton.setFont(downloadButton.getFont().deriveFont(15f));

        downloadButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                downloadButton.setEnabled(false);

                //TODO: add checks and url support

                inputTypeChooser.setEnabled(false);
                selectButton.setEnabled(false);
                inputField.setEnabled(false);
                outputField.setEnabled(false);

                Downloader downloader = new Downloader(selectedFile, outputFile);

                downloader.start();

            }

        });

        this.add(downloadButton);


    }

    private void setupProgressBar() {

        progressBar.setBounds(10, 170, 240, 30);
        progressBar.setForeground(new Color(39, 174, 96));

        this.add(progressBar);

    }

    public void initProgressBar(int max) {

        progressBar.setStringPainted(true);
        progressBar.setMaximum(max);

    }

    public void updateProgressBar(int progress) {

        int percentage = progress * 100 / progressBar.getMaximum();

        progressBar.setValue(progress);
        progressBar.setString("Downloading... " + percentage + "% (" + progress + "/" + progressBar.getMaximum() + ")");

    }

}
