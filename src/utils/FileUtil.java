package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static boolean fileExists(String filePath) {
        if (filePath == null) return false;
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }

    public static String[] readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return new String[0];
        }
        return lines.toArray(new String[0]);
    }

    public static void writeAllLines(String filePath, String[] lines) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}