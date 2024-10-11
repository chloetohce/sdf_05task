package stats;

import java.io.*;
import java.util.*;

public class Analytics {
    Map<String, Map<String, String>> storeData;

    public Analytics() {
        this.storeData = new HashMap<>();
    }

    public void getData(File f) throws IOException {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        /*
         * Columns: 
         * App, Category, Rating, Reviews, Size, Installs, Type, Price, Content Rating
         * Genres, LastUpdate, CurrentVer, AndroidVer
         */
        while ((line = br.readLine()) != null) {
            String[] appData = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            Map<String, String> app = new HashMap<>();
            
            for (int i = 1; i < appData.length; i++) {
                app.put(columns[i], appData[i].replace("\"", ""));
            }

            storeData.put(appData[0].replace("\"", ""), app);
        }
        br.close();
    }

    private static float validRating(Float f) {
        return f.equals(Float.parseFloat("NaN")) ? 0.0f : f;
    }

    private Map<String, Map<String, Float>> sortCategory() {
        // Create a new Map with categories as keys and list the apps with their respective ratings 
        // within the nested map. 
        Map<String, Map<String, Float>> categories = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> e : storeData.entrySet()) {
            String appName = e.getKey();
            Map<String, String> data = e.getValue();
            String category = data.get("Category");
            float rating = Float.parseFloat(data.get("Rating"));

            Map<String, Float> update = new HashMap<>();
            if (categories.containsKey(category)) {
                update = categories.get(category);
            }
            update.put(appName, rating);
            categories.put(category, update);
        }
        return categories;
    }

    public Map<String, String> getHighest() {
        Map<String, Map<String, Float>> sortedByCat = this.sortCategory();
        Set<String> categories = sortedByCat.keySet();
        Map<String, String> highest = new HashMap<>();
        for (String cat : categories) {
            Map<String, Float> subCatApps = sortedByCat.get(cat);
            highest.put(cat, "");
            for (Map.Entry<String, Float> app : subCatApps.entrySet()) {
                float currHighRating = subCatApps.getOrDefault(highest.get(cat), 0.0f);
                String appName = app.getKey();
                float appRating = app.getValue();
                if (appRating > currHighRating) {
                    highest.put(cat, appName);
                }
            }
        }
        return highest;
    }

    public Map<String, String> getLowest() {
        Map<String, Map<String, Float>> sortedByCat = this.sortCategory();
        Set<String> categories = sortedByCat.keySet();
        Map<String, String> lowest = new HashMap<>();
        for (String cat : categories) {
            Map<String, Float> subCatApps = sortedByCat.get(cat);
            lowest.put(cat, "");
            for (Map.Entry<String, Float> app : subCatApps.entrySet()) {
                float currHighRating = subCatApps.getOrDefault(lowest.get(cat), 5.0f);
                String appName = app.getKey();
                float appRating = app.getValue();
                if (appRating < currHighRating) {
                    lowest.put(cat, appName);
                }
            }
        }
        return lowest;
    }

    public Map<String, Float> getAverage() {
        Map<String, Map<String, Float>> sortedByCat = this.sortCategory();
        Set<String> categories = sortedByCat.keySet();
        Map<String, Float> averages = new HashMap<>();

        for (String cat : categories) {
            Map<String, Float> subCatApps = sortedByCat.get(cat);
            float avg = 0.0f;
            int count = 0;

            for (Float rating : subCatApps.values()) {
                count++;
                avg += validRating(rating); //Decided to count NaN values as 0, treated as unrated
            }
            avg = avg / count;
            averages.put(cat, avg);
        }
        return averages;
    }

    public static void main(String[] args) throws IOException {
        String f = "data" + File.separator + "googleplaystore.csv";
        if (args.length > 0) {
            f = args[0];
        }
        Analytics a = new Analytics();
        a.getData(new File(f));
        Map<String, String> highest = a.getHighest();
        System.out.println("================= HIGHEST =================");
        for (Map.Entry<String, String> e : highest.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        System.out.println();
        Map<String, String> lowest = a.getLowest();
        System.out.println("================= LOWEST =================");
        for (Map.Entry<String, String> e : lowest.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        System.out.println();
        Map<String, Float> averages = a.getAverage();
        System.out.println("================= AVERAGES =================");
        for (Map.Entry<String, Float> e : averages.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }


    }
}