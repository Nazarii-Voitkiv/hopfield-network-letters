import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class GridPanel extends JPanel {
    private static final int ROZMIAR_SIATKI = 8;
    private static final int ROZMIAR_KOMORKI = 50;
    
    private int[][] siatka = new int[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
    private JButton[][] przyciski = new JButton[ROZMIAR_SIATKI][ROZMIAR_SIATKI];
    
    public GridPanel() {
        ustawWymiary();
        setLayout(new GridLayout(ROZMIAR_SIATKI, ROZMIAR_SIATKI, 0, 0));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        inicjalizujSiatke();
        utworzPrzyciskiSiatki();
    }
    
    private void ustawWymiary() {
        Dimension wymiar = new Dimension(ROZMIAR_SIATKI * ROZMIAR_KOMORKI, ROZMIAR_SIATKI * ROZMIAR_KOMORKI);
        setPreferredSize(wymiar);
        setMinimumSize(wymiar);
        setMaximumSize(wymiar);
    }
    
    private void utworzPrzyciskiSiatki() {
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
        
        przycisk.addActionListener(e -> {
            siatka[wiersz][kolumna] = siatka[wiersz][kolumna] == 1 ? -1 : 1;
            aktualizujWygladPrzycisku(przycisk, siatka[wiersz][kolumna]);
        });
        
        return przycisk;
    }
    
    private void aktualizujWygladPrzycisku(JButton przycisk, int stan) {
        przycisk.setBackground(stan == 1 ? Color.BLACK : Color.WHITE);
    }
    
    private void inicjalizujSiatke() {
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                siatka[i][j] = -1;
            }
        }
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
        if (tablica.length != ROZMIAR_SIATKI * ROZMIAR_SIATKI) {
            throw new IllegalArgumentException("Tablica musi mieć długość " + (ROZMIAR_SIATKI * ROZMIAR_SIATKI));
        }
        
        int indeks = 0;
        for (int i = 0; i < ROZMIAR_SIATKI; i++) {
            for (int j = 0; j < ROZMIAR_SIATKI; j++) {
                siatka[i][j] = tablica[indeks++];
                aktualizujWygladPrzycisku(przyciski[i][j], siatka[i][j]);
            }
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
}
