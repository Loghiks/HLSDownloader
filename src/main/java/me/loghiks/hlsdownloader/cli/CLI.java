package me.loghiks.hlsdownloader.cli;

import me.loghiks.hlsdownloader.Main;
import me.loghiks.hlsdownloader.Utils;
import me.loghiks.hlsdownloader.process.Downloader;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CLI {

    public static void parseArguments(String[] argsArray) {

        List<String> args = Arrays.asList(argsArray);

        /*___________________________________ HELP ___________________________________*/

        if(args.contains(CommandArgument.HELP_ARG.argument)) {

            System.out.println("--------------- " + Main.APP_NAME + " - Help menu" + " ---------------");

            for (CommandArgument arg : CommandArgument.ALL_ARGUMENTS)
                System.out.println(arg.argument + " " + arg.parameter + " -> " + arg.description);


            return;
        }

        /*___________________________________ ERROR ___________________________________*/

        boolean printErrors = args.contains(CommandArgument.ERROR_ARG.argument);

        /*___________________________________ INPUT MODE ___________________________________*/

        String fileArg = CommandArgument.FILE_ARG.argument;
        String urlArg = CommandArgument.URL_ARG.argument;

        boolean fileMode = args.contains(fileArg);
        boolean urlMode = args.contains(urlArg);

        if(fileMode && urlMode) {

            System.out.println("Both " + fileArg + " and " + urlArg + " can NOT be used together !");

            return;
        }

        if(!fileMode && !urlMode) {

            System.out.println("One of " + fileArg + " and " + urlArg + " argument is needed !");

            return;
        }

        String pathOrUrl;

        if(fileMode) {

            if(args.indexOf(fileArg) != args.lastIndexOf(fileArg)) {

                System.out.println("You can specify only one file !");

                return;

            }

            pathOrUrl = args.get(args.indexOf(fileArg) + 1);

        }else {

            if(args.indexOf(urlArg) != args.lastIndexOf(urlArg)) {

                System.out.println("You can specify only one url !");

                return;

            }

            pathOrUrl = args.get(args.indexOf(urlArg) + 1);

        }

        /*___________________________________ OUTPUT FILE ___________________________________*/

        String outArg = CommandArgument.OUT_ARG.argument;

        if(!args.contains(outArg)) {

            System.out.println(outArg + " is needed !");

            return;

        }

        if(args.indexOf(outArg) != args.lastIndexOf(outArg)) {

            System.out.println("You can specify only one output file !");

            return;

        }

        String outPath = args.get(args.indexOf(outArg) + 1);

        if(!outArg.endsWith("\\.mp4")) outPath = outPath.concat(".mp4");

        File outputFile = new File(outPath);

        /*___________________________________ PROCESS ___________________________________*/

        Downloader downloader = fileMode ?
                new Downloader(new File(pathOrUrl), outputFile) :
                new Downloader(pathOrUrl, outputFile);

        downloader.onInit(CLIProgressBar::initProgressBar);
        downloader.onProgress(CLIProgressBar::updateProgressBar);

        downloader.onError(throwable -> {

            System.err.println("\nAn error has occurred while downloading the video: " + throwable.getMessage());

            if(printErrors) {

                File file = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), Main.APP_NAME + "_error_" + System.currentTimeMillis() + ".txt");

                boolean success = Utils.printStackTrace(file, throwable);

                if(success) System.out.println("Error stack trace can be found at : " + file.getAbsolutePath());
                else System.err.println("Unable to print stacktrace into " + file.getAbsolutePath());

            }

        });

        downloader.whenDone(outFile -> {

            System.out.println("\nThe video has been downloaded to " + outFile.getAbsolutePath());

            Utils.browseFile(outFile);

        });

        downloader.start();

    }

}
