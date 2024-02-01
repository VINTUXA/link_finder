package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class LinkFinder extends RecursiveTask<Set<String>> {
    public static Map<String,List<String>> map = new TreeMap<>();
    private String url;
    public LinkFinder(String url) {
        this.url = url;
    }
    public static Map<String, List<String>> getMap() {
        return map;
    }

    @Override
    protected Set<String>compute() {
        Set<String> currentUrlSet = new HashSet<>();
        try {
            Thread.sleep(200);
            Document html = getHtml(url);
            currentUrlSet.add(url);

            //map for output file
            List<String> currentListOfUrls = new ArrayList<>();
            map.put(url, currentListOfUrls);

            // find all child links
            for (Element el : html.select("a[href]")){
                String currentUrl = el.attr("href").replaceAll("\\/+$","");

                if (currentUrl.startsWith(url) && !currentUrlSet.contains(currentUrl)){
                    map.get(url).add(currentUrl);

                    // fork task for each child link
                    LinkFinder task = new LinkFinder(currentUrl);
                    task.fork();
                    currentUrlSet.addAll(task.join());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return currentUrlSet;
    }

    private Document getHtml(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(1000000)
                .header("Accept", "text/javascript")
                .get();
    }
}
