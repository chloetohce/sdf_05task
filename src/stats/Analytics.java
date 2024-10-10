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

            storeData.put(appData[1], app);
        }
        br.close();
    }

    private static float validRating(String f) {
        return f.equals("NaN") ? 0.0f : Float.parseFloat(f);
    }

    private Map<String, Map<String, Float>> sortCategory() {
        // Create a new Map with categories as keys and list the apps with their respective ratings 
        // within the nested map. 
        Map<String, Map<String, Float>> categories = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> e : storeData.entrySet()) {
            String appName = e.getKey();
            Map<String, String> data = e.getValue();
            String category = data.get("Category");
            float rating = validRating(data.get("Rating"));

            Map<String, Float> update = new HashMap<>();
            if (categories.containsKey(category)) {
                update = categories.get(category);
            }
            update.put(appName, rating);
            categories.put(category, update);
        }
        return categories;
    }

    // public Map<String, String> getHighest() {
    // }

    public static void main(String[] args) throws IOException {
        String f = "data" + File.separator + "googleplaystore.csv";
        if (args.length > 0) {
            f = args[0];
        }
        Analytics a = new Analytics();
        a.getData(new File(f));
        Map<String, Float> test = a.sortCategory().get("ART_AND_DESIGN");
        for (Map.Entry<String, Float> e : a.sortCategory().get("ART_AND_DESIGN").entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
    }
}