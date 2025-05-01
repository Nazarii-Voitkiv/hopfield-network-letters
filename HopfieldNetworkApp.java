import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.Timer;

public class HopfieldNetworkApp extends JFrame {
    private static final int MAX_ITERACJI_HOPFIELD = 1;
    private static int OPOZNIENIE_ANIMACJI = 80;
    
    private final GridPanel panelSiatki;
    private HopfieldNetwork siec;
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
        trenujSiec();
        
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
        przyciskUzupelniania = utworzPrzycisk("Uzupełnij");
        JButton wyczysc = utworzPrzycisk("Wyczyść");
        
        zapiszM.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery M");
        zapiszO.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery O");
        zapiszN.setToolTipText("Zapisuje bieżący rysunek jako wzorzec litery N");
        przegladajWzorce.setToolTipText("Otwiera okno do przeglądania i zarządzania zapisanymi wzorcami");
        przyciskUzupelniania.setToolTipText("Uzupełnia częściowo narysowany wzorzec do pełnej litery");
        wyczysc.setToolTipText("Czyści siatkę rysowania");
        
        zapiszM.addActionListener(e -> zapiszWzorzec("M"));
        zapiszO.addActionListener(e -> zapiszWzorzec("O"));
        zapiszN.addActionListener(e -> zapiszWzorzec("N"));
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
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        
        przyciskUzupelniania.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(przyciskUzupelniania, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        
        przegladajWzorce.setFont(new Font("Arial", Font.BOLD, 14));
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
            
            aktualizujStatystyki();
            trenujSiec();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void wczytajWzorce() {
        try {
            wzorce = PatternUtils.wczytajWszystkieWzorce();
            aktualizujStatystyki();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void trenujSiec() {
        List<int[]> wzorceTreningowe = new ArrayList<>();
        for (List<int[]> letterSamples : wzorce.values()) {
            wzorceTreningowe.addAll(letterSamples);
        }
        
        if (!wzorceTreningowe.isEmpty()) {
            siec = new HopfieldNetwork(64);
            siec.trenuj(wzorceTreningowe.toArray(new int[0][]));
            siecWytrenowana = true;
            
            System.out.println("Wytrenowano sieć na " + wzorceTreningowe.size() + " wzorcach");
            etykietaWyniku.setText("<html>Sieć gotowa do pracy.<br>Narysuj fragment litery</html>");
        }
    }
    
    private void uzupelnijWzorzec() {
        if (animacjaTrwa) {
            zatrzymajAnimacje();
            return;
        }
        
        int[] inputPattern = panelSiatki.pobierzSiatkeJakoTablice();
        panelSiatki.zapiszBiezacyWzorzec();
        
        rozpocznijAnimowaneRozpoznawanie(inputPattern.clone());
    }
    
    private void rozpocznijAnimowaneRozpoznawanie(int[] poczatkowyWzorzec) {
        przyciskUzupelniania.setText("Zatrzymaj");
        przyciskUzupelniania.setEnabled(true);
        animacjaTrwa = true;
        
        final int[] wzorzecPoczatkowy = poczatkowyWzorzec.clone();
        
        List<int[]> krokaAnimacji = siec.rozpoznajZKrokami(wzorzecPoczatkowy.clone(), MAX_ITERACJI_HOPFIELD);
        
        System.out.println("Початок анімації, кількість кроків: " + krokaAnimacji.size());
        etykietaWyniku.setText("<html><div style='width:380px'>" +
                "<b>Анімація мережі Хопфілда...</b><br>" +
                "Крок: 0/" + krokaAnimacji.size() + "</div></html>");
        
        OPOZNIENIE_ANIMACJI = 300;
        
        czasomierzAnimacji = new Timer(OPOZNIENIE_ANIMACJI, 
                new AnimatorRozpoznawaniaHopfield(wzorzecPoczatkowy, krokaAnimacji));
        czasomierzAnimacji.start();
    }
    
    private class AnimatorRozpoznawaniaHopfield implements ActionListener {
        private final int[] poczatkowyWzorzec;
        private final List<int[]> stanyPosrednie;
        private int indeksStanu = 0;
        
        public AnimatorRozpoznawaniaHopfield(int[] poczatkowyWzorzec, List<int[]> stanyPosrednie) {
            this.poczatkowyWzorzec = poczatkowyWzorzec;
            this.stanyPosrednie = stanyPosrednie;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (indeksStanu >= stanyPosrednie.size()) {
                zatrzymajAnimacje();
                zakonczRozpoznawanie();
                przyciskUzupelniania.setEnabled(true);
                return;
            }
            
            int[] biezacyStan = stanyPosrednie.get(indeksStanu);
            panelSiatki.ustawSiatkeZTablicy(biezacyStan);
            
            etykietaWyniku.setText(String.format("<html><div style='width:380px'>" +
                    "<b>Przetwarzanie sieci Hopfielda</b><br>" +
                    "Iteracja: %d/%d<br>" +
                    "Energia sieci: %.2f</div></html>", 
                    indeksStanu + 1, stanyPosrednie.size(), 
                    siec.obliczEnergie(biezacyStan)));
            
            indeksStanu++;
        }
        
        private void zakonczRozpoznawanie() {
            StringBuilder tekstWyniku = new StringBuilder("<html><div style='width:380px'>");
            tekstWyniku.append("<h3>WYNIK SIECI HOPFIELDA</h3>");
            
            String najblizszaLitera = znajdzNajblizszaLitere(stanyPosrednie.get(stanyPosrednie.size() - 1));
            if (najblizszaLitera != null) {
                tekstWyniku.append("Najbliższa litera: <b style='font-size:18px;'>")
                          .append(najblizszaLitera).append("</b><br><br>");
            }
            
            tekstWyniku.append("Liczba iteracji: ").append(stanyPosrednie.size()).append("<br>");
            tekstWyniku.append("Energia końcowa: ")
                     .append(String.format("%.2f", siec.obliczEnergie(stanyPosrednie.get(stanyPosrednie.size() - 1))))
                     .append("<br><br>");
            tekstWyniku.append("<span style='color:green;'><b>Przetwarzanie zakończone!</b></span>");
            tekstWyniku.append("</div></html>");
            
            etykietaWyniku.setText(tekstWyniku.toString());
        }
        
        private String znajdzNajblizszaLitere(int[] wzorzec) {
            String najblizszaLitera = null;
            int najmniejszaOdleglosc = Integer.MAX_VALUE;
            
            for (Map.Entry<String, List<int[]>> entry : wzorce.entrySet()) {
                for (int[] wzorzecTreningowy : entry.getValue()) {
                    int odleglosc = siec.odlegloscHamminga(wzorzec, wzorzecTreningowy);
                    if (odleglosc < najmniejszaOdleglosc) {
                        najmniejszaOdleglosc = odleglosc;
                        najblizszaLitera = entry.getKey();
                    }
                }
            }
            
            return najblizszaLitera;
        }
    }
    
    private void zatrzymajAnimacje() {
        if (czasomierzAnimacji != null && czasomierzAnimacji.isRunning()) {
            czasomierzAnimacji.stop();
        }
        animacjaTrwa = false;
        przyciskUzupelniania.setText("Uzupełnij");
        przyciskUzupelniania.setEnabled(true);
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
        przyciskUzupelniania.setEnabled(true);
        
        if (!siecWytrenowana) {
            etykietaWyniku.setText("<html>Sieć uczy się automatycznie przy uruchomieniu programu " +
                "i po dodaniu nowych wzorców.<br>" +
                "Narysuj kształt i naciśnij 'Uzupełnij', aby uruchomić sieć Hopfielda</html>");
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
