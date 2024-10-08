package ai;

import java.io.*;
import java.util.*;

public class WordDistribution {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String fName = "data/sample.txt";
        if (args.length > 0) {
            fName = args[0];
        }

        File f = new File(fName);
        if (!f.exists()) {
            System.err.println("File does not exist. Please try again.");
            System.exit(-1);
        }

        Long fileSizeTemp = f.length();
        int fileSize = fileSizeTemp.intValue();

        StringBuilder sb = new StringBuilder(fileSize);

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        char[] buffer = new char[4 * 1024];
        while (br.read(buffer) != -1) {
            sb.append(buffer);
        }

        String raw = sb.toString().replace("\n", " ");
        StringBuilder alphanumericSB = new StringBuilder(fileSize);
        String alphanumeric = "";

        // Replace anything that is not alphanumeric or a full stop
        String valid = "abcdefghijklmnopqrstuvwxyz1234567890. ";
        for (char c : raw.toCharArray()) {
            if (Arrays.binarySearch(valid.toCharArray(), c) != -1) {
                alphanumericSB.append(c);
            }
        }

        System.out.println(raw);
        String[] arr = raw.split("\\s+");
        System.out.println(Arrays.toString(arr));
    }
}
