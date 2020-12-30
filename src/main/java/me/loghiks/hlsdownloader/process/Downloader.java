package me.loghiks.hlsdownloader.process;

import me.loghiks.hlsdownloader.Main;
import me.loghiks.hlsdownloader.gui.HLSFrame;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class Downloader extends Thread {

    private static final OkHttpClient client = new OkHttpClient();

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

        BufferedSink sink = Okio.buffer(Okio.sink(output));

        int index = 1;
        for (String url : urls) {

            Request request = new Request.Builder().url(url).build();

            Response response = client.newCall(request).execute();

            ResponseBody body = response.body();

            if(body != null) {

                sink.writeAll(body.source());
                sink.flush();

                body.close();

            }

            if(initConsumer != null && progressConsumer != null)
                progressConsumer.accept(index);

            index++;
        }

        sink.close();

        if(whenDone != null) whenDone.accept(output);

    }

}
