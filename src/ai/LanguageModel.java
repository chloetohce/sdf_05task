package ai;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

public class LanguageModel {
    private Map<String, Map<String, Integer>> dist;
    private static float RANDOM_LIMIT = 0.1f;

    public LanguageModel() {
        this.dist = new HashMap<>();
    }

    public void buildModel(File f) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        /* 
         * Using ArrayList to store all the words because doing by each line read removes the last word on each line. 
         * Another possible implementation might to remove \n from the whole text and replace them with spaces, and replace periods
         * with \n. Would then treat each last word as the end of a sentence, so no words follow. 
         */
        List<String> allWords = new ArrayList<>(); 
        while ((line = br.readLine()) != null) {
            line = line.strip().
                replaceAll("[^\\sa-zA-Z0-9.]", " ").
                replace('.', ' ');
            String[] words = line.split("\\s+");
            allWords.addAll(Arrays.stream(words).toList());
        }

        for (int i = 0; i < allWords.size() - 1; i++) {
            String curr = allWords.get(i);
            String next = allWords.get(i + 1);
            Map<String, Integer> wordDist = this.dist.getOrDefault(curr, new HashMap<>());
            if (!wordDist.containsKey(next))
                wordDist.put(next, 0);

            wordDist.put(next, wordDist.get(next) + 1);
            this.dist.put(curr, wordDist);
        }
        br.close();
    }

    public void printModel() {
        for (Map.Entry<String, Map<String, Integer>> e : this.dist.entrySet()) {
            String word = e.getKey();
            Map<String, Integer> wordDist = e.getValue();
            System.out.println(word + "- ");

            for (Map.Entry<String, Integer> nextEle : wordDist.entrySet()) {
                String next = nextEle.getKey();
                Integer count = nextEle.getValue();
                System.out.println("    " + next + ": " + count);
            }
        }
    }

    private String exploit(String root) {
        if (!this.dist.containsKey(root))
            return "";
        Map<String, Integer> wordMap = this.dist.get(root);
        String next = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> map : wordMap.entrySet()) {
            String word = map.getKey();
            int count = map.getValue();
            if (count > maxCount) {
                maxCount = count;
                next = word;
            }
        }
        return next;
    }

    private String explore(String root) {
        if (!this.dist.containsKey(root))
            return "";
        Map<String, Integer> wordMap = this.dist.get(root);
        String next = "";
        Random rand = new SecureRandom();
        int randomNum = rand.nextInt(wordMap.size());
        int i = 0;
        for (String word : wordMap.keySet()) {
            if (i == randomNum) {
                next = word;
            }
            i++;
        }
        return next;
    }

    public void generateSentence(String root, int num) {
        Random rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(root + " ");
        int toAdd = num - 1;
        String prevWord = root;

        for (int i = 0; i <= toAdd; i++) {
            if (prevWord == "")
                break;

            float randomNum = rand.nextFloat();
            if (randomNum < RANDOM_LIMIT) {
                prevWord = explore(prevWord);
            } else {
                prevWord = exploit(prevWord);
            }
            sb.append(prevWord + " ");
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        Console cons = System.console();
        List<File> files = new ArrayList<>();
        String input = "/";
        while (!input.equals("")) {
            input = cons.readLine("Enter file: ");
            File f = new File(input);
            if (f.exists()) {
                files.add(f);
            } else if (!(f.exists() || input.equals(""))) {
                System.err.println("File does not exist. Please re-enter and try again.");
            }
        }

        LanguageModel lm = new LanguageModel();
        for (File file : files) {
            lm.buildModel(file);
        }

        //lm.printModel();

        String root = "I";
        while (!root.equals("")) {
            root = cons.readLine("Enter root word: ");
            if (root.equals(""))
                System.exit(-1);
            
            String num = cons.readLine("Number of words: ");
            
            lm.generateSentence(root, Integer.parseInt(num));
        }
        
    }
}
