package me.loghiks.hlsdownloader.process;

import me.loghiks.hlsdownloader.gui.HLSFrame;
import me.loghiks.hlsdownloader.gui.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Downloader extends Thread {

    private static final OkHttpClient client = new OkHttpClient();

    private final File m3u8, output;
    private Consumer<File> whenDone;

    public Downloader(File m3u8, File output) {
        this.m3u8 = m3u8;
        this.output = output;
    }

    public void whenDone(Consumer<File> out) {

        this.whenDone = out;

    }

    @Override
    public void run() {

        List<String> urls;

        try {
            urls = extractUrls();
        } catch (FileNotFoundException e) {
            Utils.displayErrorPopup("HLS file can't be read !");
            System.exit(74);
            return;
        }

        try {
            download(urls);
        } catch (IOException e) {
            Utils.displayErrorPopup("Unable to download the video !");
            System.exit(75);
        }

    }

    private void download(List<String> urls) throws IOException {

        HLSFrame.INSTANCE.initProgressBar(urls.size());

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

            HLSFrame.INSTANCE.updateProgressBar(index);

            index++;
        }

        sink.close();

        if(whenDone != null) whenDone.accept(output);

    }

    private List<String> extractUrls() throws FileNotFoundException {

        List<String> urls = new ArrayList<>();

        Scanner scanner = new Scanner(new FileInputStream(m3u8));

        String line;

        while ((line = scanner.nextLine()) != null) {

            if(line.startsWith("#EXTINF")) {

                String url = scanner.nextLine();

                if(url != null) urls.add(url);
                else break;

            }

            if(line.startsWith("#EXT-X-ENDLIST")) break;

        }

        scanner.close();

        return urls;

    }

}
