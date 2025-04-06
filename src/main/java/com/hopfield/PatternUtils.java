import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class PatternUtils {
    public static final String PATTERNS_DIR = "wzorce";
    
    static {
        try {
            Path dirPath = Paths.get(PATTERNS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
        } catch (IOException e) {
            System.err.println("Nie udało się utworzyć katalogu wzorców: " + e.getMessage());
        }
    }
    
    public static void savePattern(int[] wzorzec, String litera) throws IOException {
        int nastIndeks = pobierzNastepnyIndeks(litera);
        String nazwaPliku = litera + "_" + nastIndeks + ".pat";
        
        Path sciezkaPliku = Paths.get(PATTERNS_DIR, nazwaPliku);
        try (BufferedWriter writer = Files.newBufferedWriter(sciezkaPliku)) {
            for (int wartosc : wzorzec) {
                writer.write(String.valueOf(wartosc));
                writer.newLine();
            }
        }
    }
    
    private static int pobierzNastepnyIndeks(String litera) throws IOException {
        List<Path> istniejaceWzorce = Files.list(Paths.get(PATTERNS_DIR))
            .filter(path -> path.getFileName().toString().startsWith(litera + "_"))
            .filter(path -> path.getFileName().toString().endsWith(".pat"))
            .collect(Collectors.toList());
        
        return istniejaceWzorce.size() + 1;
    }
    
    public static List<int[]> loadAllPatterns(String litera) throws IOException {
        List<int[]> wzorce = new ArrayList<>();
        
        try (DirectoryStream<Path> strumien = Files.newDirectoryStream(
                Paths.get(PATTERNS_DIR), litera + "_*.pat")) {
            for (Path sciezka : strumien) {
                int[] wzorzec = wczytajWzorzecZPliku(sciezka);
                if (wzorzec != null) {
                    wzorce.add(wzorzec);
                }
            }
        }
        
        return wzorce;
    }
    
    public static Map<String, List<int[]>> loadAllPatterns() throws IOException {
        Map<String, List<int[]>> wszystkieWzorce = new HashMap<>();
        
        wszystkieWzorce.put("M", loadAllPatterns("M"));
        wszystkieWzorce.put("O", loadAllPatterns("O"));
        wszystkieWzorce.put("N", loadAllPatterns("N"));
        
        return wszystkieWzorce;
    }
    
    public static int[] loadPatternFromFile(Path sciezkaPliku) throws IOException {
        return wczytajWzorzecZPliku(sciezkaPliku);
    }
    
    private static int[] wczytajWzorzecZPliku(Path sciezkaPliku) throws IOException {
        if (!Files.exists(sciezkaPliku)) {
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(sciezkaPliku)) {
            int[] wzorzec = new int[64];
            for (int i = 0; i < wzorzec.length; i++) {
                String linia = reader.readLine();
                if (linia == null) {
                    throw new IOException("Plik wzorca jest niekompletny: " + sciezkaPliku);
                }
                wzorzec[i] = Integer.parseInt(linia);
            }
            return wzorzec;
        }
    }
    
    public static boolean patternsExist(String litera) {
        try {
            return Files.list(Paths.get(PATTERNS_DIR))
                .anyMatch(path -> path.getFileName().toString().startsWith(litera + "_") &&
                                 path.getFileName().toString().endsWith(".pat"));
        } catch (IOException e) {
            return false;
        }
    }
    
    public static int countPatterns(String litera) {
        try {
            return (int) Files.list(Paths.get(PATTERNS_DIR))
                .filter(path -> path.getFileName().toString().startsWith(litera + "_"))
                .filter(path -> path.getFileName().toString().endsWith(".pat"))
                .count();
        } catch (IOException e) {
            return 0;
        }
    }
}
