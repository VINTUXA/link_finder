package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class LinkFinder extends RecursiveTask<Set<String>> {
    public static Set<String> uniqueUrls = new HashSet<>();
    public static TreeMap<String,List<String>> map = new TreeMap<>();
    private String url;
    private List<LinkFinder> tasks;
    private int tabLevel;

    public LinkFinder(String url, int tabLevel) {
        this.url = url;
        this.tasks = new ArrayList<>();
        this.tabLevel = tabLevel;
    }

    public static TreeMap<String, List<String>> getMap() {
        return map;
    }

    @Override
    protected Set<String> compute() {
        try {
            Thread.sleep(200);
            Document html = Jsoup.connect(url)
                    .timeout(1000000)
                    .header("Accept", "text/javascript")
                    .get();
            List<String> currentListOfUrls = new ArrayList<>();
            map.put(url, currentListOfUrls);
            for (Element el : html.select("a")){
                String currentUrl = el.attr("href").split("\\?")[0];
                if(checkRequirements(currentUrl)){
                    uniqueUrls.add(currentUrl);
                    LinkFinder task = new LinkFinder(currentUrl, tabLevel + 1);
                    task.fork();
                    tasks.add(task);
                    map.get(url).add(currentUrl);
                }
            }
            for (LinkFinder task : tasks){
                task.join();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return uniqueUrls;
    }

    private boolean checkRequirements(String url){
        return isItLinkChildOfRequiredUrl(url)
                && !uniqueUrls.contains(url);
    }

    private boolean isItLinkChildOfRequiredUrl(String url){
        return url.contains(this.url);
    }
}
