package me.loghiks.hlsdownloader.cli;

public class CLIProgressBar {

    private static int maxValue;

    public static void initProgressBar(int max) {

        maxValue = max;
        System.out.println("Preparing to download...");

    }

    public static void updateProgressBar(int progress) {

        int percentage = progress * 100 / maxValue;

        String percentageStr = String.valueOf(percentage);

        while (percentageStr.length() < 3)
            percentageStr = " " + percentageStr;

        System.out.print("\rDownloading... " + percentageStr + "% [" + progressBar(percentage) + "] " + progress + "/" + maxValue);

    }

    private static String progressBar(int progress) {

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < 100; j++) {

            if(j <= progress) sb.append('=');
            else sb.append(' ');

        }

        return sb.toString();

    }

}
