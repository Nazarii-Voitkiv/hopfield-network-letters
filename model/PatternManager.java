package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternManager {
    private static final String PATTERN_DIRECTORY = "wzorce";
    private static final int PATTERN_SIZE = 64;
    private Map<String, List<int[]>> patterns;
    
    public PatternManager() {
        patterns = new HashMap<>();
        patterns.put("M", new ArrayList<>());
        patterns.put("O", new ArrayList<>());
        patterns.put("N", new ArrayList<>());
        
        createPatternDirectoryIfNeeded();
        loadAllPatterns();
    }
    
    private void createPatternDirectoryIfNeeded() {
        try {
            Path directoryPath = Paths.get(PATTERN_DIRECTORY);
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void savePattern(int[] pattern, String letter) throws IOException {
        int nextIndex = getNextPatternIndex(letter);
        String fileName = letter + "_" + nextIndex + ".pat";
        
        Path filePath = Paths.get(PATTERN_DIRECTORY, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (int value : pattern) {
                writer.write(String.valueOf(value));
                writer.newLine();
            }
        }
        
        patterns.get(letter).add(pattern);
    }
    
    private int getNextPatternIndex(String letter) throws IOException {
        List<Path> existingPatterns = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                Paths.get(PATTERN_DIRECTORY), letter + "_*.pat")) {
            stream.forEach(existingPatterns::add);
        }
        return existingPatterns.size() + 1;
    }
    
    public Map<String, List<int[]>> getPatterns() {
        return patterns;
    }
    
    public List<int[]> getAllPatterns() {
        List<int[]> allPatterns = new ArrayList<>();
        for (List<int[]> letterPatterns : patterns.values()) {
            allPatterns.addAll(letterPatterns);
        }
        return allPatterns;
    }
    
    public int countPatterns(String letter) {
        return patterns.get(letter).size();
    }
    
    private void loadAllPatterns() {
        try {
            for (String letter : patterns.keySet()) {
                loadPatternsForLetter(letter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPatternsForLetter(String letter) throws IOException {
        List<int[]> letterPatterns = new ArrayList<>();
        Path directoryPath = Paths.get(PATTERN_DIRECTORY);
        
        if (Files.exists(directoryPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                    directoryPath, letter + "_*.pat")) {
                
                for (Path filePath : stream) {
                    int[] pattern = readPatternFromFile(filePath);
                    if (pattern != null) {
                        letterPatterns.add(pattern);
                    }
                }
            }
        }
        
        patterns.put(letter, letterPatterns);
    }
    
    private int[] readPatternFromFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            int[] pattern = new int[PATTERN_SIZE];
            for (int i = 0; i < pattern.length; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Niepełny plik wzorca: " + filePath);
                }
                pattern[i] = Integer.parseInt(line);
            }
            return pattern;
        }
    }
    
    public String findClosestLetter(int[] pattern) {
        String closestLetter = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (Map.Entry<String, List<int[]>> entry : patterns.entrySet()) {
            for (int[] trainingPattern : entry.getValue()) {
                int distance = calculateHammingDistance(pattern, trainingPattern);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestLetter = entry.getKey();
                }
            }
        }
        
        return closestLetter;
    }
    
    private int calculateHammingDistance(int[] a, int[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                distance++;
            }
        }
        return distance;
    }
}