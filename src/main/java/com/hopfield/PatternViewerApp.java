import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PatternViewerApp extends JDialog { // Zmiana z JFrame na JDialog dla modalności
    private static final String[] OPCJE_LITER = {"M", "O", "N"};
    private static final int ROZMIAR_SIATKI = 8;
    private static final int ROZMIAR_KOMORKI = 40;
    
    private JComboBox<String> wybierakLiter;
    private JComboBox<String> wybierakWzorcow;
    private PanelWzorca panelWzorca;
    private JButton przyciskUsuwania;
    private JLabel etykietaStatusu;
    private Map<String, List<Path>> plikiWzorcow;
    
    public PatternViewerApp() {
        super((Frame)null, "Przeglądarka wzorców", true); // Jawnie ustawiamy jako modalny dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Zmiana z JFrame na JDialog
        setLayout(new BorderLayout(10, 10));
        
        inicjalizujKomponenty();
        utworzInterfejs();
        
        setSize(400, 550);
        setLocationRelativeTo(null);
    }
    
    private void inicjalizujKomponenty() {
        panelWzorca = new PanelWzorca();
        etykietaStatusu = new JLabel("Wybierz literę i wzorzec do wyświetlenia");
        wczytajPlikiWzorcow();
    }
    
    private void utworzInterfejs() {
        add(utworzPanelWyboru(), BorderLayout.NORTH);
        add(utworzKontenerWzorca(), BorderLayout.CENTER);
        add(utworzPanelStatusu(), BorderLayout.SOUTH);
        
        aktualizujWybierakWzorcow();
    }
    
    private JPanel utworzPanelWyboru() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        wybierakLiter = new JComboBox<>(OPCJE_LITER);
        wybierakLiter.addActionListener(e -> aktualizujWybierakWzorcow());
        
        wybierakWzorcow = new JComboBox<>();
        wybierakWzorcow.addActionListener(e -> wyswietlWybranyWzorzec());
        
        przyciskUsuwania = new JButton("Usuń wzorzec");
        przyciskUsuwania.addActionListener(e -> usunWybranyWzorzec());
        przyciskUsuwania.setEnabled(false);
        
        panel.add(new JLabel("Litera:"));
        panel.add(wybierakLiter);
        panel.add(new JLabel("Wzorzec:"));
        panel.add(wybierakWzorcow);
        panel.add(new JLabel(""));
        panel.add(przyciskUsuwania);
        
        return panel;
    }
    
    private JPanel utworzKontenerWzorca() {
        JPanel kontener = new JPanel(new BorderLayout());
        kontener.setBorder(BorderFactory.createTitledBorder("Podgląd wzorca"));
        kontener.add(panelWzorca, BorderLayout.CENTER);
        return kontener;
    }
    
    private JPanel utworzPanelStatusu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.add(etykietaStatusu, BorderLayout.CENTER);
        return panel;
    }
    
    private void wczytajPlikiWzorcow() {
        plikiWzorcow = new HashMap<>();
        for (String litera : OPCJE_LITER) {
            plikiWzorcow.put(litera, new ArrayList<>());
        }
        
        try {
            Path katalogWzorcow = Paths.get(PatternUtils.KATALOG_WZORCOW);
            if (Files.exists(katalogWzorcow)) {
                List<Path> wszystkiePliki = Files.list(katalogWzorcow).collect(Collectors.toList());
                
                for (String litera : OPCJE_LITER) {
                    String prefiks = litera + "_";
                    plikiWzorcow.put(litera, wszystkiePliki.stream()
                        .filter(sciezka -> {
                            String nazwaPliku = sciezka.getFileName().toString();
                            return nazwaPliku.startsWith(prefiks) && nazwaPliku.endsWith(".pat");
                        })
                        .sorted((p1, p2) -> {
                            int num1 = wyodrebnijNumerWzorca(p1.getFileName().toString());
                            int num2 = wyodrebnijNumerWzorca(p2.getFileName().toString());
                            return Integer.compare(num1, num2);
                        })
                        .collect(Collectors.toList()));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd podczas wczytywania plików wzorców: " + e.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int wyodrebnijNumerWzorca(String nazwaPliku) {
        try {
            int pozycjaPodkreslenia = nazwaPliku.indexOf('_');
            int pozycjaKropki = nazwaPliku.lastIndexOf('.');
            if (pozycjaPodkreslenia >= 0 && pozycjaKropki > pozycjaPodkreslenia) {
                String czescNumeryczna = nazwaPliku.substring(pozycjaPodkreslenia + 1, pozycjaKropki);
                return Integer.parseInt(czescNumeryczna);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    private void aktualizujWybierakWzorcow() {
        String wybranaLitera = (String) wybierakLiter.getSelectedItem();
        List<Path> pliki = plikiWzorcow.get(wybranaLitera);
        
        wybierakWzorcow.removeAllItems();
        
        if (pliki.isEmpty()) {
            wybierakWzorcow.addItem("Brak dostępnych wzorców");
            przyciskUsuwania.setEnabled(false);
            panelWzorca.wyczyscWzorzec();
            etykietaStatusu.setText("Nie znaleziono wzorców dla litery " + wybranaLitera);
        } else {
            pliki.forEach(sciezka -> wybierakWzorcow.addItem(sciezka.getFileName().toString()));
            przyciskUsuwania.setEnabled(true);
            wyswietlWybranyWzorzec();
        }
    }
    
    private void wyswietlWybranyWzorzec() {
        if (wybierakWzorcow.getItemCount() == 0 || 
            "Brak dostępnych wzorców".equals(wybierakWzorcow.getSelectedItem())) {
            return;
        }
        
        String wybranaLitera = (String) wybierakLiter.getSelectedItem();
        int wybranyIndeks = wybierakWzorcow.getSelectedIndex();
        
        if (wybranyIndeks >= 0 && wybranyIndeks < plikiWzorcow.get(wybranaLitera).size()) {
            Path sciezkaWzorca = plikiWzorcow.get(wybranaLitera).get(wybranyIndeks);
            
            try {
                int[] wzorzec = PatternUtils.wczytajWzorzecZPliku(sciezkaWzorca);
                panelWzorca.ustawWzorzec(wzorzec);
                etykietaStatusu.setText("Wyświetlany: " + sciezkaWzorca.getFileName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                   "Błąd podczas wczytywania wzorca: " + e.getMessage(),
                   "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void usunWybranyWzorzec() {
        if (wybierakWzorcow.getItemCount() == 0 || 
            "Brak dostępnych wzorców".equals(wybierakWzorcow.getSelectedItem())) {
            return;
        }
        
        String wybranaLitera = (String) wybierakLiter.getSelectedItem();
        int wybranyIndeks = wybierakWzorcow.getSelectedIndex();
        
        if (wybranyIndeks >= 0 && wybranyIndeks < plikiWzorcow.get(wybranaLitera).size()) {
            Path sciezkaWzorca = plikiWzorcow.get(wybranaLitera).get(wybranyIndeks);
            
            int potwierdzenie = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć ten wzorzec?\n" + sciezkaWzorca.getFileName(),
                "Potwierdź usunięcie", JOptionPane.YES_NO_OPTION);
            
            if (potwierdzenie == JOptionPane.YES_OPTION) {
                try {
                    Files.delete(sciezkaWzorca);
                    JOptionPane.showMessageDialog(this, "Wzorzec został pomyślnie usunięty.");
                    
                    wczytajPlikiWzorcow();
                    aktualizujWybierakWzorcow();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Błąd podczas usuwania wzorca: " + e.getMessage(),
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public void setModal(boolean modal) {
        super.setModal(modal);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            PatternViewerApp app = new PatternViewerApp();
            app.setModal(true);
            app.setVisible(true);
        });
    }
    
    private static class PanelWzorca extends JPanel {
        private final int[][] siatka = new int[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
        
        public PanelWzorca() {
            setPreferredSize(new Dimension(ROZMIAR_SIATKI * ROZMIAR_KOMORKI, ROZMIAR_SIATKI * ROZMIAR_KOMORKI));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            wyczyscWzorzec();
        }
        
        public void ustawWzorzec(int[] wzorzec) {
            if (wzorzec.length != ROZMIAR_SIATKI * ROZMIAR_SIATKI) {
                throw new IllegalArgumentException("Wzorzec musi mieć długość " + (ROZMIAR_SIATKI * ROZMIAR_SIATKI));
            }
            
            int indeks = 0;
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                    siatka[i][j] = wzorzec[indeks++];
                }
            }
            
            repaint();
        }
        
        public void wyczyscWzorzec() {
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; j < ROZMIAR_SIATKI; j++) { // NAPRAWIONO: j < ROZMIAR_SIATKI zamiast i < ROZMIAR_SIATKI // NAPRAWIONO: j < ROZMIAR_SIATKI zamiast i < ROZMIAR_SIATKI
                    siatka[i][j] = -1;
                }
            }
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (int i = 0; i < ROZMIAR_SIATKI; i++) {
                for (int j = 0; j < ROZMIAR_SIATKI; j++) { // NAPRAWIONO: j < ROZMIAR_SIATKI zamiast i < ROZMIAR_SIATKI // NAPRAWIONO: j < ROZMIAR_SIATKI zamiast i < ROZMIAR_SIATKI
                    g.setColor(siatka[i][j] == 1 ? Color.BLACK : Color.WHITE);
                    g.fillRect(j * ROZMIAR_KOMORKI, i * ROZMIAR_KOMORKI, ROZMIAR_KOMORKI, ROZMIAR_KOMORKI);
                    
                    g.setColor(Color.GRAY);
                    g.drawRect(j * ROZMIAR_KOMORKI, i * ROZMIAR_KOMORKI, ROZMIAR_KOMORKI, ROZMIAR_KOMORKI);
                }
            }
        }
    }
}
