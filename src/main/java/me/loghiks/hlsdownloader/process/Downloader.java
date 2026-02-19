package me.loghiks.hlsdownloader.process;

import me.loghiks.hlsdownloader.Main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

public class Downloader extends Thread {

    private File file;
    private String url;

    private final File output;

    private Consumer<File> whenDone;
    private Consumer<Throwable> errorConsumer;

    private Consumer<Integer> initConsumer;
    private Consumer<Integer> progressConsumer;

    public Downloader(File file, File output) {
        this(output);

        this.file = file;
    }

    public Downloader(String url, File output) {
        this(output);

        this.url = url;

    }

    private Downloader(File output) {
        super(Main.APP_NAME + " - Downloader Thread");

        this.output = output;

    }

    public void whenDone(Consumer<File> out) {

        this.whenDone = out;

    }

    public void onError(Consumer<Throwable> errorConsumer) {

        this.errorConsumer = errorConsumer;

    }

    public void onInit(Consumer<Integer> initConsumer) {

        this.initConsumer = initConsumer;

    }

    public void onProgress(Consumer<Integer> progressConsumer) {

        this.progressConsumer = progressConsumer;

    }

    @Override
    public void run() {

        try {

            List<String> urls = file != null ? HLSParser.parseFile(file) : HLSParser.parseUrl(url);

            download(urls);

        } catch (Throwable e) {

            if(errorConsumer != null) errorConsumer.accept(e);

        }

    }

    private void download(List<String> urls) throws IOException {

        if(initConsumer != null) initConsumer.accept(urls.size());

        output.getParentFile().mkdirs();
        output.createNewFile();

        BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(output.toPath()));

        int index = 1;
        for (String url : urls) {

            try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                }

                out.flush();

            }

            if(initConsumer != null && progressConsumer != null)
                progressConsumer.accept(index);

            index++;
        }

        out.close();


        if(whenDone != null) whenDone.accept(output);

    }

}
