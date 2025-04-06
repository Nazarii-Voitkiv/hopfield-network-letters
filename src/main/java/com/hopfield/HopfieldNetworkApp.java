import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class HopfieldNetworkApp extends JFrame {
    private GridPanel panelSiatki;
    private HopfieldNetwork siec;
    private Map<String, List<int[]>> wzorce;
    private JLabel etykietaWyniku;
    private JLabel etykietaStatystyk;
    
    public HopfieldNetworkApp() {
        setTitle("Sieć Hopfielda - Rozpoznawanie Liter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setMinimumSize(new Dimension(450, 700));
        
        inicjalizujKomponenty();
        utworzInterfejs();
        
        setSize(500, 900);
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    private void inicjalizujKomponenty() {
        panelSiatki = new GridPanel();
        siec = new HopfieldNetwork(64);
        
        etykietaWyniku = new JLabel();
        etykietaWyniku.setFont(new Font("Arial", Font.PLAIN, 14));
        
        etykietaStatystyk = new JLabel("", JLabel.CENTER);
        etykietaStatystyk.setFont(new Font("Arial", Font.PLAIN, 12));
        
        wzorce = new HashMap<>();
        wzorce.put("M", new ArrayList<>());
        wzorce.put("O", new ArrayList<>());
        wzorce.put("N", new ArrayList<>());
        
        wczytajWzorce();
        aktualizujStatystyki();
    }
    
    private void utworzInterfejs() {
        add(utworzPanelSiatki(), BorderLayout.CENTER);
        add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);
        add(utworzPanelInformacyjny(), BorderLayout.NORTH);
    }
    
    private JPanel utworzPanelPrzyciskow() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton btnZapiszM = utworzPrzycisk("Zapisz M");
        JButton btnZapiszO = utworzPrzycisk("Zapisz O");
        JButton btnZapiszN = utworzPrzycisk("Zapisz N");
        JButton btnPrzegladajWzorce = utworzPrzycisk("Przegląd Wzorców");
        JButton btnUcz = utworzPrzycisk("Ucz");
        JButton btnTest = utworzPrzycisk("Test");
        JButton btnWyczysc = utworzPrzycisk("Wyczyść");
        
        btnZapiszM.addActionListener(e -> zapiszWzorzec("M"));
        btnZapiszO.addActionListener(e -> zapiszWzorzec("O"));
        btnZapiszN.addActionListener(e -> zapiszWzorzec("N"));
        btnUcz.addActionListener(e -> trenujSiec());
        btnTest.addActionListener(e -> rozpoznajWzorzec());
        btnWyczysc.addActionListener(e -> {
            panelSiatki.wyczyscSiatke();
            etykietaWyniku.setText("Wynik: Brak");
        });
        btnPrzegladajWzorce.addActionListener(e -> {
            new PatternViewerApp().setVisible(true);
        });
        
        panel.add(btnZapiszM);
        panel.add(btnZapiszO);
        panel.add(btnZapiszN);
        panel.add(btnPrzegladajWzorce);
        panel.add(btnUcz);
        panel.add(btnTest);
        panel.add(btnWyczysc);
        
        return panel;
    }
    
    private JPanel utworzPanelInformacyjny() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(400, 200));
        
        JScrollPane scrollPane = new JScrollPane(etykietaWyniku);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel panelEtykiet = new JPanel(new BorderLayout());
        panelEtykiet.add(scrollPane, BorderLayout.CENTER);
        panelEtykiet.add(etykietaStatystyk, BorderLayout.SOUTH);
        
        panel.add(panelEtykiet, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel utworzPanelSiatki() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel panelCentrujacy = new JPanel();
        panelCentrujacy.add(panelSiatki);
        
        panel.add(Box.createVerticalGlue());
        panel.add(panelCentrujacy);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton utworzPrzycisk(String tekst) {
        JButton przycisk = new JButton(tekst);
        przycisk.setFont(new Font("Arial", Font.BOLD, 14));
        przycisk.setFocusPainted(false);
        przycisk.setMargin(new Insets(10, 15, 10, 15));
        return przycisk;
    }
    
    private void zapiszWzorzec(String litera) {
        try {
            int[] wzorzec = panelSiatki.pobierzSiatkeJakoTablice();
            PatternUtils.savePattern(wzorzec, litera);
            wzorce.get(litera).add(wzorzec);
            
            JOptionPane.showMessageDialog(this, "Wzorzec " + litera + " został zapisany!");
            aktualizujStatystyki();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Błąd podczas zapisywania wzorca: " + ex.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void wczytajWzorce() {
        try {
            wzorce = PatternUtils.loadAllPatterns();
            aktualizujStatystyki();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd podczas wczytywania wzorców: " + e.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void trenujSiec() {
        List<int[]> wzorceTreningowe = new ArrayList<>();
        for (List<int[]> listaWzorcow : wzorce.values()) {
            wzorceTreningowe.addAll(listaWzorcow);
        }
        
        if (wzorceTreningowe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak wzorców do nauki!");
            return;
        }
        
        if (wzorceTreningowe.size() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Zaleca się posiadanie przynajmniej po jednym wzorcu dla każdej litery (M, O, N).",
                "Uwaga", JOptionPane.WARNING_MESSAGE);
        }
        
        siec.train(wzorceTreningowe.toArray(new int[0][]));
        JOptionPane.showMessageDialog(this, 
            "Sieć nauczona " + wzorceTreningowe.size() + " wzorcami!");
    }
    
    private void rozpoznajWzorzec() {
        int[] biezacyWzorzec = panelSiatki.pobierzSiatkeJakoTablice();
        
        if (czyBrakWzorcow()) {
            JOptionPane.showMessageDialog(this, 
                "Najpierw zapisz co najmniej po jednym wzorcu dla każdej litery (M, O, N) i naciśnij przycisk \"Ucz\".",
                "Brak wzorców", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!czyMaAktywnePiksele(biezacyWzorzec)) {
            JOptionPane.showMessageDialog(this, 
                "Narysuj coś przed testowaniem!",
                "Pusty wzorzec", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Map<String, Integer> odleglosci = new HashMap<>();
        Map<String, int[]> najlepszeWzorce = new HashMap<>();
        
        for (String litera : wzorce.keySet()) {
            WynikPorownania wynik = znajdzNajblizszyWzorzec(biezacyWzorzec, wzorce.get(litera));
            odleglosci.put(litera, wynik.odleglosc);
            najlepszeWzorce.put(litera, wynik.wzorzec);
        }
        
        String rozpoznanaLitera = znajdzNajblizszyKlucz(odleglosci);
        int[] najlepszyWzorzec = najlepszeWzorce.get(rozpoznanaLitera);
        
        panelSiatki.ustawSiatkeZTablicy(najlepszyWzorzec);
        
        wyswietlWyniki(biezacyWzorzec, najlepszyWzorzec, rozpoznanaLitera, odleglosci);
    }
    
    private WynikPorownania znajdzNajblizszyWzorzec(int[] wzorzec, List<int[]> wzorceDoPorowniania) {
        int minOdleglosc = Integer.MAX_VALUE;
        int[] najlepszyWzorzec = null;
        
        for (int[] wzorzecOdniesienia : wzorceDoPorowniania) {
            int odleglosc = siec.hammingDistance(wzorzec, wzorzecOdniesienia);
            if (odleglosc < minOdleglosc) {
                minOdleglosc = odleglosc;
                najlepszyWzorzec = wzorzecOdniesienia;
            }
        }
        
        return new WynikPorownania(najlepszyWzorzec, minOdleglosc);
    }
    
    private void wyswietlWyniki(int[] oryginalnyWzorzec, int[] najlepszyWzorzec, 
                            String rozpoznanaLitera, Map<String, Integer> odleglosci) {
        List<Map.Entry<String, Integer>> posortowaneOdleglosci = 
            new ArrayList<>(odleglosci.entrySet());
        posortowaneOdleglosci.sort(Map.Entry.comparingByValue());
        
        int zmienionePiksele = liczbaZmienionychPikseli(oryginalnyWzorzec, najlepszyWzorzec);
        int dystans = odleglosci.get(rozpoznanaLitera);
        
        StringBuilder tekstWyniku = new StringBuilder("<html>");
        tekstWyniku.append("<div style='width:380px'>");
        
        tekstWyniku.append("<h3>WYNIK PORÓWNANIA</h3>");
        tekstWyniku.append("Rozpoznana litera: <b style='font-size:18px;'>").append(rozpoznanaLitera).append("</b><br><br>");
        
        tekstWyniku.append("<b>Ranking podobieństwa:</b><br>");
        for (Map.Entry<String, Integer> wpis : posortowaneOdleglosci) {
            tekstWyniku.append("- ").append(wpis.getKey())
                     .append(" (dystans = ").append(wpis.getValue()).append(")<br>");
        }
        
        tekstWyniku.append("<br>Zmienione piksele: ").append(zmienionePiksele);
        
        if (dystans <= 5) {
            tekstWyniku.append("<br><br><span style='color:green;'>Wysokie podobieństwo do wzorca</span>");
        } else if (dystans <= 10) {
            tekstWyniku.append("<br><br><span style='color:orange;'>Umiarkowane podobieństwo do wzorca</span>");
        } else {
            tekstWyniku.append("<br><br><span style='color:red;'>Niskie podobieństwo - niepewne rozpoznanie</span>");
        }
        
        tekstWyniku.append("</div></html>");
        etykietaWyniku.setText(tekstWyniku.toString());
    }
    
    private boolean czyBrakWzorcow() {
        for (String litera : wzorce.keySet()) {
            if (wzorce.get(litera).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean czyMaAktywnePiksele(int[] wzorzec) {
        for (int piksel : wzorzec) {
            if (piksel == 1) return true;
        }
        return false;
    }
    
    private String znajdzNajblizszyKlucz(Map<String, Integer> mapa) {
        String najlepszyKlucz = null;
        int najmnOdleglosc = Integer.MAX_VALUE;
        
        for (Map.Entry<String, Integer> wpis : mapa.entrySet()) {
            if (wpis.getValue() < najmnOdleglosc) {
                najmnOdleglosc = wpis.getValue();
                najlepszyKlucz = wpis.getKey();
            }
        }
        
        return najlepszyKlucz;
    }
    
    private int liczbaZmienionychPikseli(int[] przed, int[] po) {
        int licznik = 0;
        for (int i = 0; i < przed.length; i++) {
            if (przed[i] != po[i]) licznik++;
        }
        return licznik;
    }
    
    private void aktualizujStatystyki() {
        int liczbaM = PatternUtils.countPatterns("M");
        int liczbaO = PatternUtils.countPatterns("O");
        int liczbaN = PatternUtils.countPatterns("N");
        
        etykietaStatystyk.setText("Zapisane wzorce: M=" + liczbaM + ", O=" + liczbaO + ", N=" + liczbaN);
    }
    
    private static class WynikPorownania {
        final int[] wzorzec;
        final int odleglosc;
        
        WynikPorownania(int[] wzorzec, int odleglosc) {
            this.wzorzec = wzorzec;
            this.odleglosc = odleglosc;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorowanie błędów wyglądu
        }
        
        SwingUtilities.invokeLater(() -> new HopfieldNetworkApp().setVisible(true));
    }
}
