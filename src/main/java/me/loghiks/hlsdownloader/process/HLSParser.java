package me.loghiks.hlsdownloader.process;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class HLSParser {

    private static final String START_KEY = "#EXTM3U";
    private static final String END_KEY = "#EXT-X-ENDLIST";
    private static final String SEGMENT_KEY = "#EXTINF";

    public static List<String> parseFile(File file) throws IOException {

        if(file == null || !file.exists() || !file.isFile())
            throw new NullPointerException("The file do NOT exist !");

        InputStream inputStream = new FileInputStream(file);

        List<String> urls = readSegmentsUrls(inputStream);

        if(urls.isEmpty())
            throw new IllegalStateException("No URL(s) found in the provided file !");

        return urls;

    }

    public static List<String> parseUrl(String urlStr) throws IOException {

        if(urlStr == null || urlStr.trim().isEmpty())
            throw new NullPointerException("The url is null !");

        URL url = new URL(urlStr);

        InputStream inputStream = url.openStream();

        if(inputStream == null)
            throw new NullPointerException("Unable to read the url content !");

        List<String> urls = readSegmentsUrls(inputStream, urlStr.substring(0, urlStr.lastIndexOf('/')+1));

        if(urls.isEmpty())
            throw new IllegalStateException("No URL(s) found in the provided file !");

        return urls;

    }

    private static List<String> readSegmentsUrls(InputStream inputStream, String... partialUrl) throws IOException {

        List<String> lines = readAllLines(inputStream);

        if(lines.isEmpty())
            throw new IllegalStateException("The file is empty");

        if(!lines.get(0).equals(START_KEY))
            throw new IllegalArgumentException("The file must start with " + START_KEY);

        if(!lines.contains(END_KEY))
            throw new IllegalArgumentException("The file must end with " + END_KEY);

        List<String> result = new ArrayList<>();

        // Checks can be improved
        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            if(line.startsWith(SEGMENT_KEY)) {

                String[] split = line.split(",");

                if(split.length == 1 || split[1].trim().isEmpty()) {

                    String nextLine = lines.get(++i).trim();

                    if(nextLine.startsWith(SEGMENT_KEY)) {

                        i--;
                        continue;

                    }

                    split = nextLine.split(",");

                    if(split.length == 2) nextLine = split[1].trim();

                    if(!nextLine.startsWith("http")) {

                        if(partialUrl.length > 0) nextLine = partialUrl[0] + nextLine;
                        else throw new IllegalArgumentException("The url doesn't start with 'http' and there is no partial url provided !");

                    }

                    result.add(nextLine);

                }

            }

        }

        return result;

    }

    private static List<String> readAllLines(InputStream inputStream) throws IOException {

        List<String> result = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        while ((line = bufferedReader.readLine()) != null)
            result.add(line);

        bufferedReader.close();

        return result;

    }

}
