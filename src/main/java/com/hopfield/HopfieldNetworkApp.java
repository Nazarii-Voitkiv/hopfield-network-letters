import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Timer;

public class HopfieldNetworkApp extends JFrame {
    private static final int OPOZNIENIE_ANIMACJI = 80;
    
    private final GridPanel panelSiatki;
    private final HopfieldNetwork siec;
    private Map<String, List<int[]>> wzorce;
    private final JLabel etykietaWyniku;
    private final JLabel etykietaStatystyk;
    
    private JButton przyciskUzupelniania;
    private Timer czasomierzAnimacji;
    private boolean animacjaTrwa = false;
    private boolean siecWytrenowana = false;
    
    public HopfieldNetworkApp() {
        super("Sieć Hopfielda - Rozpoznawanie Liter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setMinimumSize(new Dimension(500, 700));
        
        panelSiatki = new GridPanel();
        siec = new HopfieldNetwork(64);
        
        etykietaWyniku = new JLabel();
        etykietaWyniku.setFont(new Font("Arial", Font.PLAIN, 14));
        
        etykietaStatystyk = new JLabel("", JLabel.CENTER);
        etykietaStatystyk.setFont(new Font("Arial", Font.BOLD, 16));
        etykietaStatystyk.setForeground(Color.BLACK);
        etykietaStatystyk.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        
        wzorce = new HashMap<>();
        wzorce.put("M", new ArrayList<>());
        wzorce.put("O", new ArrayList<>());
        wzorce.put("N", new ArrayList<>());
        
        wczytajWzorce();
        aktualizujStatystyki();
        
        utworzInterfejs();
        
        setSize(500, 900);
        setResizable(false);
        setLocationRelativeTo(null);
        
        aktualizujStanPrzyciskow();
    }
    
    private void utworzInterfejs() {
        add(utworzPanelSiatki(), BorderLayout.CENTER);
        add(utworzPanelPrzyciskow(), BorderLayout.SOUTH);
        add(utworzPanelInformacyjny(), BorderLayout.NORTH);
    }
    
    private JPanel utworzPanelPrzyciskow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        
        JButton zapiszM = utworzPrzycisk("Zapisz M");
        JButton zapiszO = utworzPrzycisk("Zapisz O");
        JButton zapiszN = utworzPrzycisk("Zapisz N");
        JButton przegladajWzorce = utworzPrzycisk("Przeglądaj wzorce");
        JButton trenuj = utworzPrzycisk("Trenuj");
        przyciskUzupelniania = utworzPrzycisk("Uzupełnij");
        JButton wyczysc = utworzPrzycisk("Wyczyść");
        
        zapiszM.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery M");
        zapiszO.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery O");
        zapiszN.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery N");
        przegladajWzorce.setToolTipText("Otwiera okno do przeglądania i zarządzania zapisanymi wzorcami");
        trenuj.setToolTipText("Trenuje sieć neuronową na podstawie zapisanych wzorców");
        przyciskUzupelniania.setToolTipText("Uzupełnia częściowo narysowany wzorzec do pełnej litery");
        przyciskUzupelniania.setEnabled(false);
        wyczysc.setToolTipText("Czyści siatkę rysowania");
        
        zapiszM.addActionListener(e -> zapiszWzorzec("M"));
        zapiszO.addActionListener(e -> zapiszWzorzec("O"));
        zapiszN.addActionListener(e -> zapiszWzorzec("N"));
        trenuj.addActionListener(e -> trenujSiec());
        przyciskUzupelniania.addActionListener(e -> uzupelnijWzorzec());
        wyczysc.addActionListener(e -> {
            panelSiatki.wyczyscSiatke();
            etykietaWyniku.setText("Wynik: Brak");
            zatrzymajAnimacje();
        });
        
        przegladajWzorce.addActionListener(e -> {
            try {
                PatternViewerApp przeglad = new PatternViewerApp();
                przeglad.setLocationRelativeTo(this);
                przeglad.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Błąd podczas otwierania przeglądarki wzorców: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridy = 0;
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(zapiszM, gbc);
        
        gbc.gridx = 1;
        panel.add(zapiszO, gbc);
        
        gbc.gridx = 2;
        panel.add(zapiszN, gbc);
        
        gbc.gridx = 3;
        panel.add(wyczysc, gbc);

        gbc.gridy = 1;
        
        gbc.gridx = 0;
        panel.add(trenuj, gbc);
        
        gbc.gridx = 1;
        panel.add(przyciskUzupelniania, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        
        przegladajWzorce.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(przegladajWzorce, gbc);
        
        return panel;
    }
    
    private JPanel utworzPanelInformacyjny() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(400, 200));
        
        JScrollPane przewijacz = new JScrollPane(etykietaWyniku);
        przewijacz.setBorder(null);
        przewijacz.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel panelEtykiet = new JPanel(new BorderLayout());
        panelEtykiet.add(przewijacz, BorderLayout.CENTER);
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
            PatternUtils.zapiszWzorzec(wzorzec, litera);
            wzorce.get(litera).add(wzorzec);
            
            JOptionPane.showMessageDialog(this, "Wzorzec " + litera + " został zapisany pomyślnie!");
            aktualizujStatystyki();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Błąd podczas zapisywania wzorca: " + ex.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void wczytajWzorce() {
        try {
            wzorce = PatternUtils.wczytajWszystkieWzorce();
            aktualizujStatystyki();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Błąd podczas wczytywania wzorców: " + e.getMessage(),
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void trenujSiec() {
        List<int[]> wzorceTreningowe = wzorce.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        
        if (wzorceTreningowe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak wzorców do treningu!");
            return;
        }
        
        if (wzorceTreningowe.size() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Zaleca się posiadanie co najmniej jednego wzorca dla każdej litery (M, O, N).",
                "Uwaga", JOptionPane.WARNING_MESSAGE);
        }
        
        siec.trenuj(wzorceTreningowe.toArray(new int[0][]));
        siecWytrenowana = true; 
        aktualizujStanPrzyciskow(); 
        
        JOptionPane.showMessageDialog(this, 
            "Sieć wytrenowana na " + wzorceTreningowe.size() + " wzorcach!");
    }
    
    private void uzupelnijWzorzec() {
        if (!siecWytrenowana) {
            JOptionPane.showMessageDialog(this, 
                "Najpierw wytrenuj sieć przyciskiem \"Trenuj\".",
                "Sieć niewytrenowana", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (wzorcePuste()) {
            JOptionPane.showMessageDialog(this, 
                "Brak wzorców do rozpoznawania. Zapisz wzorce i wytrenuj sieć.",
                "Brak wzorców", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (animacjaTrwa) {
            zatrzymajAnimacje();
            return;
        }
        
        int[] biezacyWzorzec = panelSiatki.pobierzSiatkeJakoTablice();
        if (!maAktywnePiksele(biezacyWzorzec)) {
            JOptionPane.showMessageDialog(this, 
                "Narysuj fragment litery przed uzupełnianiem!",
                "Pusty wzorzec", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int[] oryginalnyWzorzec = biezacyWzorzec.clone();
        panelSiatki.zapiszBiezacyWzorzec();
        
        DopasowanieWzorca najlepszeDopasowanie = znajdzNajlepiejDopasowanyWzorzec(oryginalnyWzorzec);
        
        if (najlepszeDopasowanie != null) {
            rozpocznijAnimowaneUzupelnianie(oryginalnyWzorzec, najlepszeDopasowanie);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Nie znaleziono pasującego wzorca.\nŻaden z zapisanych wzorców nie zawiera wszystkich narysowanych pikseli.",
                "Brak dopasowania", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private DopasowanieWzorca znajdzNajlepiejDopasowanyWzorzec(int[] wzorzecUzytkownika) {
        DopasowanieWzorca najlepszeDopasowanie = null;
        int minPikseliDoDodania = Integer.MAX_VALUE;
        
        for (String litera : wzorce.keySet()) {
            for (int[] wzorzecBazowy : wzorce.get(litera)) {
                if (wszystkiePikseleZgodne(wzorzecUzytkownika, wzorzecBazowy)) {
                    int pikseleDoDodania = policzPikseleDoDodania(wzorzecUzytkownika, wzorzecBazowy);
                    if (pikseleDoDodania < minPikseliDoDodania) {
                        minPikseliDoDodania = pikseleDoDodania;
                        najlepszeDopasowanie = new DopasowanieWzorca(litera, wzorzecBazowy, pikseleDoDodania);
                    }
                }
            }
        }
        
        return najlepszeDopasowanie;
    }
    
    private boolean wszystkiePikseleZgodne(int[] wzorzecUzytkownika, int[] wzorzecBazowy) {
        for (int i = 0; i < wzorzecUzytkownika.length; i++) {
            if (wzorzecUzytkownika[i] == 1 && wzorzecBazowy[i] == -1) {
                return false;
            }
        }
        return true;
    }
    
    private int policzPikseleDoDodania(int[] wzorzecUzytkownika, int[] wzorzecBazowy) {
        int licznik = 0;
        for (int i = 0; i < wzorzecBazowy.length; i++) {
            if (wzorzecBazowy[i] == 1 && wzorzecUzytkownika[i] == -1) {
                licznik++;
            }
        }
        return licznik;
    }
    
    private void rozpocznijAnimowaneUzupelnianie(int[] oryginalnyWzorzec, DopasowanieWzorca dopasowanie) {
        przyciskUzupelniania.setText("Zatrzymaj");
        animacjaTrwa = true;
        
        List<Integer> pikseleDoDodania = new ArrayList<>();
        for (int i = 0; i < oryginalnyWzorzec.length; i++) {
            if (dopasowanie.wzorzec[i] == 1 && oryginalnyWzorzec[i] == -1) {
                pikseleDoDodania.add(i);
            }
        }
        
        Collections.shuffle(pikseleDoDodania);
        
        final int[] aktualnyWzorzec = oryginalnyWzorzec.clone();
        
        etykietaWyniku.setText(String.format("<html><div style='width:380px'><b>Uzupełnianie wzorca litery %s...</b><br>" +
                "Pozostałe piksele: %d</div></html>", dopasowanie.litera, pikseleDoDodania.size()));
        
        czasomierzAnimacji = new Timer(OPOZNIENIE_ANIMACJI, new AnimatorUzupelnianiaWzorca(
                aktualnyWzorzec, oryginalnyWzorzec, pikseleDoDodania, dopasowanie.litera));
        
        czasomierzAnimacji.start();
    }
    
    private class AnimatorUzupelnianiaWzorca implements ActionListener {
        private final int[] aktualnyWzorzec;
        private final int[] oryginalnyWzorzec;
        private final List<Integer> pikseleDoDodania;
        private final String litera;
        private int indeksPiksela = 0;
        
        public AnimatorUzupelnianiaWzorca(int[] aktualnyWzorzec, int[] oryginalnyWzorzec, 
                                      List<Integer> pikseleDoDodania, String litera) {
            this.aktualnyWzorzec = aktualnyWzorzec;
            this.oryginalnyWzorzec = oryginalnyWzorzec;
            this.pikseleDoDodania = pikseleDoDodania;
            this.litera = litera;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (indeksPiksela >= pikseleDoDodania.size()) {
                zatrzymajAnimacje();
                zakonczUzupelnianie(aktualnyWzorzec, oryginalnyWzorzec, litera);
                return;
            }
            
            int pikselDoZmiany = pikseleDoDodania.get(indeksPiksela++);
            aktualnyWzorzec[pikselDoZmiany] = 1;
            
            panelSiatki.ustawSiatkeZTablicy(aktualnyWzorzec);
            
            int pozostalo = pikseleDoDodania.size() - indeksPiksela;
            etykietaWyniku.setText(String.format("<html><div style='width:380px'><b>Uzupełnianie wzorca litery %s...</b><br>" +
                    "Pozostałe piksele: %d<br>" +
                    "Dodano piksel w: (%d,%d)</div></html>", 
                    litera, pozostalo, pikselDoZmiany / 8, pikselDoZmiany % 8));
        }
    }
    
    private void zatrzymajAnimacje() {
        if (czasomierzAnimacji != null && czasomierzAnimacji.isRunning()) {
            czasomierzAnimacji.stop();
        }
        animacjaTrwa = false;
        przyciskUzupelniania.setText("Uzupełnij");
    }
    
    private void zakonczUzupelnianie(int[] finalnyWzorzec, int[] oryginalnyWzorzec, String rozpoznanaLitera) {
        for (int i = 0; i < finalnyWzorzec.length; i++) {
            if (oryginalnyWzorzec[i] == 1) {
                finalnyWzorzec[i] = 1;
            }
        }
        
        panelSiatki.ustawSiatkeZTablicy(finalnyWzorzec);
        
        StringBuilder tekstWyniku = new StringBuilder("<html><div style='width:380px'>");
        tekstWyniku.append("<h3>WYNIK DOPASOWANIA WZORCA</h3>");
        tekstWyniku.append("Rozpoznana litera: <b style='font-size:18px;'>").append(rozpoznanaLitera).append("</b><br><br>");
        tekstWyniku.append("Wzorzec został uzupełniony na podstawie dokładnego dopasowania.<br><br>");
        tekstWyniku.append("<span style='color:green;'><b>Uzupełnianie zakończone!</b></span>");
        tekstWyniku.append("</div></html>");
        
        etykietaWyniku.setText(tekstWyniku.toString());
    }
    
    private boolean wzorcePuste() {
        return wzorce.values().stream().anyMatch(List::isEmpty);
    }
    
    private boolean maAktywnePiksele(int[] wzorzec) {
        for (int piksel : wzorzec) {
            if (piksel == 1) return true;
        }
        return false;
    }
    
    private void aktualizujStatystyki() {
        int liczbaM = PatternUtils.policzWzorce("M");
        int liczbaO = PatternUtils.policzWzorce("O");
        int liczbaN = PatternUtils.policzWzorce("N");
        
        etykietaStatystyk.setText(String.format(
            "<html><div style='text-align: center;'>Zapisane wzorce: " +
            "<b>M=%d</b>, <b>O=%d</b>, <b>N=%d</b>" +
            "</div></html>", 
            liczbaM, liczbaO, liczbaN
        ));
    }
    
    private void aktualizujStanPrzyciskow() {
        przyciskUzupelniania.setEnabled(siecWytrenowana && !wzorcePuste());
        
        if (!siecWytrenowana) {
            etykietaWyniku.setText("<html>Aby rozpocząć rozpoznawanie wzorców:<br>" +
                "1. Zapisz kilka wzorców liter<br>" +
                "2. Kliknij przycisk \"Trenuj\"<br>" +
                "3. Narysuj fragment litery i kliknij \"Uzupełnij\"</html>");
        }
    }
    
    private static class DopasowanieWzorca {
        final String litera;
        final int[] wzorzec;
        final int pikseleDoDodania;
        
        DopasowanieWzorca(String litera, int[] wzorzec, int pikseleDoDodania) {
            this.litera = litera;
            this.wzorzec = wzorzec;
            this.pikseleDoDodania = pikseleDoDodania;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        
        SwingUtilities.invokeLater(() -> new HopfieldNetworkApp().setVisible(true));
    }
}
