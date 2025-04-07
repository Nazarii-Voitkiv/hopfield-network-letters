import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class PatternUtils {
    public static final String KATALOG_WZORCOW = "wzorce";
    private static final int ROZMIAR_WZORCA = 64;
    
    static {
        utworzKatalogWzorcowJesliPotrzeba();
    }
    
    private static void utworzKatalogWzorcowJesliPotrzeba() {
        try {
            Path sciezkaKatalogu = Paths.get(KATALOG_WZORCOW);
            if (!Files.exists(sciezkaKatalogu)) {
                Files.createDirectory(sciezkaKatalogu);
            }
        } catch (IOException e) {
            System.err.println("Nie udało się utworzyć katalogu wzorców: " + e.getMessage());
        }
    }
    
    public static void zapiszWzorzec(int[] wzorzec, String litera) throws IOException {
        int nastepnyIndeks = pobierzNastepnyIndeksWzorca(litera);
        String nazwaPliku = litera + "_" + nastepnyIndeks + ".pat";
        
        Path sciezkaPliku = Paths.get(KATALOG_WZORCOW, nazwaPliku);
        try (BufferedWriter writer = Files.newBufferedWriter(sciezkaPliku)) {
            for (int wartosc : wzorzec) {
                writer.write(String.valueOf(wartosc));
                writer.newLine();
            }
        }
    }
    
    private static int pobierzNastepnyIndeksWzorca(String litera) throws IOException {
        List<Path> istniejaceWzorce = Files.list(Paths.get(KATALOG_WZORCOW))
            .filter(sciezka -> sciezka.getFileName().toString().startsWith(litera + "_"))
            .filter(sciezka -> sciezka.getFileName().toString().endsWith(".pat"))
            .collect(Collectors.toList());
        
        return istniejaceWzorce.size() + 1;
    }
    
    public static List<int[]> wczytajWszystkieWzorce(String litera) throws IOException {
        try (DirectoryStream<Path> strumien = Files.newDirectoryStream(
                Paths.get(KATALOG_WZORCOW), litera + "_*.pat")) {
            
            List<int[]> wzorce = new ArrayList<>();
            for (Path sciezka : strumien) {
                int[] wzorzec = wczytajWzorzecZPliku(sciezka);
                if (wzorzec != null) {
                    wzorce.add(wzorzec);
                }
            }
            return wzorce;
        }
    }
    
    public static Map<String, List<int[]>> wczytajWszystkieWzorce() throws IOException {
        Map<String, List<int[]>> wszystkieWzorce = new HashMap<>();
        
        wszystkieWzorce.put("M", wczytajWszystkieWzorce("M"));
        wszystkieWzorce.put("O", wczytajWszystkieWzorce("O"));
        wszystkieWzorce.put("N", wczytajWszystkieWzorce("N"));
        
        return wszystkieWzorce;
    }
    
    public static int[] wczytajWzorzecZPliku(Path sciezkaPliku) throws IOException {
        if (!Files.exists(sciezkaPliku)) {
            return null;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(sciezkaPliku)) {
            int[] wzorzec = new int[ROZMIAR_WZORCA];
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
    
    public static boolean wzorceIstnieja(String litera) {
        try {
            return Files.list(Paths.get(KATALOG_WZORCOW))
                .anyMatch(sciezka -> sciezka.getFileName().toString().startsWith(litera + "_") &&
                                 sciezka.getFileName().toString().endsWith(".pat"));
        } catch (IOException e) {
            return false;
        }
    }
    
    public static int policzWzorce(String litera) {
        try {
            return (int) Files.list(Paths.get(KATALOG_WZORCOW))
                .filter(sciezka -> sciezka.getFileName().toString().startsWith(litera + "_"))
                .filter(sciezka -> sciezka.getFileName().toString().endsWith(".pat"))
                .count();
        } catch (IOException e) {
            return 0;
        }
    }
}
