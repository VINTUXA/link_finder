package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String URL = "https://nopaper.ru";
    private static final String pathToOutFile = "src/main/java/org/example/out/result.txt";
    static Set<String> usedUrls = new HashSet<>();
    public static void main(String[] args) throws IOException {

        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool service = new ForkJoinPool(cores);
        service.invoke(new LinkFinder(URL));
        printMapToFile(pathToOutFile, LinkFinder.getMap());
    }


    public static void printMapToFile(String path,  Map<String, List<String>> map) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                if(!usedUrls.contains(key)){
                    writer.println(key);
                    usedUrls.add(key);
                }
                printNestedUrlsToFile(writer, values, 1, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printNestedUrlsToFile(PrintWriter writer, List<String> urls, int tabLevel, Map<String,List<String>> map) {
        for (String url : urls) {
            if(usedUrls.contains(url)) continue;
            StringBuilder spaces = new StringBuilder();
            for (int i = 0; i < tabLevel; i++) {
                spaces.append("    ");
            }
            writer.println(spaces.toString() + url);
            usedUrls.add(url);
            if (map.containsKey(url)) {
                printNestedUrlsToFile(writer, map.get(url), tabLevel + 1, map);
            }
        }
    }
}