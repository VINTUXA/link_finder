package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String URL = "https://www.youtube.com";
    private static final String pathToOutFile = "src/main/java/org/example/out/result.txt";
    public static void main(String[] args) throws IOException {

        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool service = new ForkJoinPool(cores);
        service.invoke(new LinkFinder(URL, 0));
        Map<String,List<String>> map = service.invoke(new LinkFinder(URL, 0));
        printMapToFile(pathToOutFile, map);

    }


    public static void printMapToFile(String path,  Map<String, List<String>> map) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                writer.println(key);
                printNestedUrlsToFile(writer, values, 1, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printNestedUrlsToFile(PrintWriter writer, List<String> urls, int tabLevel, Map<String,List<String>> map) {
        for (String url : urls) {
            StringBuilder spaces = new StringBuilder();
            for (int i = 0; i < tabLevel; i++) {
                spaces.append("    ");
            }
            writer.println(spaces.toString() + url);
            if (map.containsKey(url)) {
                printNestedUrlsToFile(writer, map.get(url), tabLevel + 1, map);
            }
        }
    }
}