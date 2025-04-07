import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class GridPanel extends JPanel {
    private static final int ROZMIAR_SIATKI = 8;
    private static final int ROZMIAR_KOMORKI = 50;
    
    private final int[][] siatka = new int[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
    private final JButton[][] przyciski = new JButton[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
    private int[] ostatniWzorzec;
    private boolean rysowanie = false;  
    private int aktualnyTrybRysowania = 1;

    public GridPanel() {
        skonfigurujPanel();
        inicjalizujSiatke();
        utworzPrzyciski();
        dodajObslugeRysowania();
    }
    
    private void skonfigurujPanel() {
        Dimension wymiar = new Dimension(ROZMIAR_SIATKI * ROZMIAR_KOMORKI, ROZMIAR_SIATKI * ROZMIAR_KOMORKI);
        setPreferredSize(wymiar);
        setMinimumSize(wymiar);
        setMaximumSize(wymiar);
        setLayout(new GridLayout(ROZMIAR_SIATKI, ROZMIAR_SIATKI, 0, 0));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
    
    private void inicjalizujSiatke() {
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                siatka[i][j] = -1;
            }
        }
    }
    
    private void utworzPrzyciski() {
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                przyciski[i][j] = utworzPrzyciskSiatki(i, j);
                add(przyciski[i][j]);
            }
        }
    }
    
    private JButton utworzPrzyciskSiatki(int wiersz, int kolumna) {
        JButton przycisk = new JButton();
        
        Dimension wymiar = new Dimension(ROZMIAR_KOMORKI, ROZMIAR_KOMORKI);
        przycisk.setPreferredSize(wymiar);
        przycisk.setMinimumSize(wymiar);
        przycisk.setMaximumSize(wymiar);
        
        przycisk.setBorder(new LineBorder(Color.GRAY, 1));
        przycisk.setMargin(new Insets(0, 0, 0, 0));
        przycisk.setContentAreaFilled(true);
        przycisk.setFocusPainted(false);
        przycisk.setBackground(Color.WHITE);
        przycisk.setUI(new BasicButtonUI());
        
        return przycisk;
    }
    
    private void dodajObslugeRysowania() {
        MouseHandler handler = new MouseHandler();
        
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                final int wiersz = i;
                final int kolumna = j;
                
                JButton przycisk = przyciski[i][j];
                przycisk.addMouseListener(handler);
                przycisk.addMouseMotionListener(handler);
                
                przycisk.putClientProperty("wiersz", wiersz);
                przycisk.putClientProperty("kolumna", kolumna);
            }
        }
    }
    
    private class MouseHandler extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mousePressed(MouseEvent e) {
            JButton przycisk = (JButton) e.getSource();
            int wiersz = (int) przycisk.getClientProperty("wiersz");
            int kolumna = (int) przycisk.getClientProperty("kolumna");
            
            aktualnyTrybRysowania = (siatka[wiersz][kolumna] == 1) ? -1 : 1;
            
            rysowanie = true;
            zmienStanKomorki(wiersz, kolumna, aktualnyTrybRysowania);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            rysowanie = false;
            zapiszBiezacyWzorzec();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (!rysowanie) return;

            Point pozycjaMyszy;
            
            if (e.getSource() instanceof JButton) {
                pozycjaMyszy = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), GridPanel.this);
            } else {
                pozycjaMyszy = e.getPoint();
            }
            
            int wiersz = pozycjaMyszy.y / ROZMIAR_KOMORKI;
            int kolumna = pozycjaMyszy.x / ROZMIAR_KOMORKI;
            
            if (wiersz >= 0 && wiersz < ROZMIAR_SIATKI && kolumna >= 0 && kolumna < ROZMIAR_SIATKI) {
                zmienStanKomorki(wiersz, kolumna, aktualnyTrybRysowania);
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            // Nic nie robimy przy poruszaniu myszą bez wciśniętego przycisku
        }
    }
    
    private void zmienStanKomorki(int wiersz, int kolumna, int nowyStan) {
        if (siatka[wiersz][kolumna] != nowyStan) {
            siatka[wiersz][kolumna] = nowyStan;
            aktualizujWygladPrzycisku(przyciski[wiersz][kolumna], nowyStan);
        }
    }
    
    private void przelaczKomorke(int wiersz, int kolumna) {
        siatka[wiersz][kolumna] = siatka[wiersz][kolumna] == 1 ? -1 : 1;
    }
    
    private void aktualizujWygladPrzycisku(JButton przycisk, int stan) {
        przycisk.setBackground(stan == 1 ? Color.BLACK : Color.WHITE);
    }
    
    public int[] pobierzSiatkeJakoTablice() {
        int[] wynik = new int[ROZMIAR_SIATKI * ROZMIAR_SIATKI];
        int indeks = 0;
        
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                wynik[indeks++] = siatka[i][j];
            }
        }
        
        return wynik;
    }
    
    public void ustawSiatkeZTablicy(int[] tablica) {
        sprawdzDlugoscTablicy(tablica);
        
        int indeks = 0;
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                siatka[i][j] = tablica[indeks++];
                aktualizujWygladPrzycisku(przyciski[i][j], siatka[i][j]);
            }
        }
    }
    
    public void zachowajAktywnePiksele(int[] nowaTablica, int[] oryginalna) {
        sprawdzDlugoscTablicy(nowaTablica);
        sprawdzDlugoscTablicy(oryginalna);
        
        int[] wynik = nowaTablica.clone();
        
        for (int i = 0; i < wynik.length; i++) {
            if (oryginalna[i] == 1) {
                wynik[i] = 1;
            }
        }
        
        ustawSiatkeZTablicy(wynik);
    }
    
    private void sprawdzDlugoscTablicy(int[] tablica) {
        if (tablica.length != ROZMIAR_SIATKI * ROZMIAR_SIATKI) {
            throw new IllegalArgumentException("Tablica musi mieć długość " + (ROZMIAR_SIATKI * ROZMIAR_SIATKI));
        }
    }
    
    public void wyczyscSiatke() {
        inicjalizujSiatke();
        
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                aktualizujWygladPrzycisku(przyciski[i][j], siatka[i][j]);
            }
        }
    }

    public void zapiszBiezacyWzorzec() {
        ostatniWzorzec = pobierzSiatkeJakoTablice();
    }

    public void przywrocOstatniWzorzec() {
        if (ostatniWzorzec != null) {
            ustawSiatkeZTablicy(ostatniWzorzec);
        }
    }
}
