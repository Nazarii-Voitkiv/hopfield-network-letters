import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PatternViewerApp extends JFrame {
    private JComboBox<String> wybórLitery;
    private JComboBox<String> wybórWzorca;
    private PatternPanel panelWzorca;
    private JButton przyciskUsun;
    private JLabel etykietaStatusu;
    private Map<String, List<Path>> plikiWzorców;
    
    public PatternViewerApp() {
        setTitle("Przeglądarka Wzorców");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        inicjalizujKomponenty();
        stwórzUI();
        
        setSize(400, 550);
        setLocationRelativeTo(null);
    }
    
    private void inicjalizujKomponenty() {
        panelWzorca = new PatternPanel();
        etykietaStatusu = new JLabel("Wybierz literę i wzorzec do wyświetlenia");
        inicjalizujPlikiWzorcow();
    }
    
    private void stwórzUI() {
        add(stwórzPanelWyboru(), BorderLayout.NORTH);
        add(stwórzKontenerWzorca(), BorderLayout.CENTER);
        add(stwórzPanelStatusu(), BorderLayout.SOUTH);
        
        aktualizujWybórWzorca();
    }
    
    private JPanel stwórzPanelWyboru() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        wybórLitery = new JComboBox<>(new String[]{"M", "O", "N"});
        wybórLitery.addActionListener(e -> aktualizujWybórWzorca());
        
        wybórWzorca = new JComboBox<>();
        wybórWzorca.addActionListener(e -> wyświetlWybranyWzorzec());
        
        przyciskUsun = new JButton("Usuń Wzorzec");
        przyciskUsun.addActionListener(e -> usuńWybranyWzorzec());
        przyciskUsun.setEnabled(false);
        
        panel.add(new JLabel("Litera:"));
        panel.add(wybórLitery);
        panel.add(new JLabel("Wzorzec:"));
        panel.add(wybórWzorca);
        panel.add(new JLabel(""));
        panel.add(przyciskUsun);
        
        return panel;
    }
    
    private JPanel stwórzKontenerWzorca() {
        JPanel kontener = new JPanel(new BorderLayout());
        kontener.setBorder(BorderFactory.createTitledBorder("Widok Wzorca"));
        kontener.add(panelWzorca, BorderLayout.CENTER);
        return kontener;
    }
    
    private JPanel stwórzPanelStatusu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.add(etykietaStatusu, BorderLayout.CENTER);
        return panel;
    }
    
    private void inicjalizujPlikiWzorcow() {
        plikiWzorców = new HashMap<>();
        plikiWzorców.put("M", new ArrayList<>());
        plikiWzorców.put("O", new ArrayList<>());
        plikiWzorców.put("N", new ArrayList<>());
        
        try {
            Path katalogWzorców = Paths.get(PatternUtils.PATTERNS_DIR);
            if (Files.exists(katalogWzorców)) {
                List<Path> wszystkiePliki = Files.list(katalogWzorców).collect(Collectors.toList());
                
                for (Path plik : wszystkiePliki) {
                    String nazwaPliku = plik.getFileName().toString();
                    for (String litera : plikiWzorców.keySet()) {
                        if (nazwaPliku.startsWith(litera + "_") && nazwaPliku.endsWith(".pat")) {
                            plikiWzorców.get(litera).add(plik);
                        }
                    }
                }
                
                for (List<Path> pliki : plikiWzorców.values()) {
                    pliki.sort((p1, p2) -> {
                        int num1 = wyodrębnijNumerWzorca(p1.getFileName().toString());
                        int num2 = wyodrębnijNumerWzorca(p2.getFileName().toString());
                        return Integer.compare(num1, num2);
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd wczytywania plików wzorców: " + e.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int wyodrębnijNumerWzorca(String nazwaPliku) {
        try {
            int pozycjaPodkreślnika = nazwaPliku.indexOf('_');
            int pozycjaKropki = nazwaPliku.lastIndexOf('.');
            if (pozycjaPodkreślnika >= 0 && pozycjaKropki > pozycjaPodkreślnika) {
                String częśćLiczbowa = nazwaPliku.substring(pozycjaPodkreślnika + 1, pozycjaKropki);
                return Integer.parseInt(częśćLiczbowa);
            }
        } catch (Exception e) {
            // Ignoruj błędy parsowania
        }
        return 0;
    }
    
    private void aktualizujWybórWzorca() {
        String wybranaLitera = (String) wybórLitery.getSelectedItem();
        List<Path> pliki = plikiWzorców.get(wybranaLitera);
        
        wybórWzorca.removeAllItems();
        
        if (pliki.isEmpty()) {
            wybórWzorca.addItem("Brak dostępnych wzorców");
            przyciskUsun.setEnabled(false);
            panelWzorca.wyczyśćWzorzec();
            etykietaStatusu.setText("Nie znaleziono wzorców dla litery " + wybranaLitera);
        } else {
            for (Path plik : pliki) {
                wybórWzorca.addItem(plik.getFileName().toString());
            }
            przyciskUsun.setEnabled(true);
            wyświetlWybranyWzorzec();
        }
    }
    
    private void wyświetlWybranyWzorzec() {
        if (wybórWzorca.getItemCount() == 0 || 
            "Brak dostępnych wzorców".equals(wybórWzorca.getSelectedItem())) {
            return;
        }
        
        String wybranaLitera = (String) wybórLitery.getSelectedItem();
        int wybranyIndeks = wybórWzorca.getSelectedIndex();
        
        if (wybranyIndeks >= 0 && wybranyIndeks < plikiWzorców.get(wybranaLitera).size()) {
            Path ścieżkaWzorca = plikiWzorców.get(wybranaLitera).get(wybranyIndeks);
            
            try {
                int[] wzorzec = PatternUtils.loadPatternFromFile(ścieżkaWzorca);
                panelWzorca.ustawWzorzec(wzorzec);
                etykietaStatusu.setText("Wyświetlanie: " + ścieżkaWzorca.getFileName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                   "Błąd wczytywania wzorca: " + e.getMessage(),
                   "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void usuńWybranyWzorzec() {
        if (wybórWzorca.getItemCount() == 0 || 
            "Brak dostępnych wzorców".equals(wybórWzorca.getSelectedItem())) {
            return;
        }
        
        String wybranaLitera = (String) wybórLitery.getSelectedItem();
        int wybranyIndeks = wybórWzorca.getSelectedIndex();
        
        if (wybranyIndeks >= 0 && wybranyIndeks < plikiWzorców.get(wybranaLitera).size()) {
            Path ścieżkaWzorca = plikiWzorców.get(wybranaLitera).get(wybranyIndeks);
            
            int potwierdzenie = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć ten wzorzec?\n" + ścieżkaWzorca.getFileName(),
                "Potwierdź Usunięcie", JOptionPane.YES_NO_OPTION);
            
            if (potwierdzenie == JOptionPane.YES_OPTION) {
                try {
                    Files.delete(ścieżkaWzorca);
                    JOptionPane.showMessageDialog(this, "Wzorzec został pomyślnie usunięty.");
                    
                    inicjalizujPlikiWzorcow();
                    aktualizujWybórWzorca();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Błąd podczas usuwania wzorca: " + e.getMessage(),
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Ignoruj błędy wyglądu
            }
            new PatternViewerApp().setVisible(true);
        });
    }
    
    private class PatternPanel extends JPanel {
        private static final int ROZMIAR_SIATKI = 8;
        private static final int ROZMIAR_KOMORKI = 40;
        private int[][] siatka = new int[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
        
        public PatternPanel() {
            setPreferredSize(new Dimension(ROZMIAR_SIATKI * ROZMIAR_KOMORKI, 
                                           ROZMIAR_SIATKI * ROZMIAR_KOMORKI));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            wyczyśćWzorzec();
        }
        
        public void ustawWzorzec(int[] wzorzec) {
            if (wzorzec.length != ROZMIAR_SIATKI * ROZMIAR_SIATKI) {
                throw new IllegalArgumentException("Wzorzec musi mieć długość " 
                                                + (ROZMIAR_SIATKI * ROZMIAR_SIATKI));
            }
            
            int indeks = 0;
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                    siatka[i][j] = wzorzec[indeks++];
                }
            }
            
            repaint();
        }
        
        public void wyczyśćWzorzec() {
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                    siatka[i][j] = -1;
                }
            }
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; i < ROZMIAR_SIATKI; j++) {
                    g.setColor(siatka[i][j] == 1 ? Color.BLACK : Color.WHITE);
                    g.fillRect(j * ROZMIAR_KOMORKI, i * ROZMIAR_KOMORKI, 
                               ROZMIAR_KOMORKI, ROZMIAR_KOMORKI);
                    
                    g.setColor(Color.GRAY);
                    g.drawRect(j * ROZMIAR_KOMORKI, i * ROZMIAR_KOMORKI, 
                               ROZMIAR_KOMORKI, ROZMIAR_KOMORKI);
                }
            }
        }
    }
}
