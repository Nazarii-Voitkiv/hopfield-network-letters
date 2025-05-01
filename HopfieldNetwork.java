import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HopfieldNetwork {
    private final int rozmiar;
    private final double[][] wagi;
    
    public HopfieldNetwork(int rozmiar) {
        this.rozmiar = rozmiar;
        this.wagi = new double[rozmiar][rozmiar];
    }
    
    public void trenuj(int[][] wzorce) {
        if (wzorce == null || wzorce.length == 0) {
            throw new IllegalArgumentException("Brak wzorców do treningu");
        }
        
        zresetujWagi();
        
        for (int[] wzorzec : wzorce) {
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (i != j) {
                        wagi[i][j] += (double) wzorzec[i] * wzorzec[j] / rozmiar;
                    }
                }
            }
        }
    }
    
    public void zresetujWagi() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                wagi[i][j] = 0.0;
            }
        }
    }
    
    public int[] rozpoznaj(int[] wejscie, int maksIteracji) {
        if (wejscie.length != rozmiar) {
            throw new IllegalArgumentException("Wymiar wejścia musi być zgodny z rozmiarem sieci");
        }
        
        int[] stan = wejscie.clone();
        boolean zmieniono;
        int iteracje = 0;
        
        while (iteracje < maksIteracji) {
            zmieniono = aktualizujStanSieci(stan);
            
            if (!zmieniono) {
                break;
            }
            
            iteracje++;
        }
        
        return stan;
    }
    
    private boolean aktualizujStanSieci(int[] stan) {
        boolean zmieniono = false;
        int[] nowyStan = new int[stan.length];
        
        for (int i = 0; i < rozmiar; i++) {
            double aktywacja = obliczAktywacje(stan, i);
            nowyStan[i] = (aktywacja >= 0) ? 1 : -1;
            
            if (stan[i] != nowyStan[i]) {
                zmieniono = true;
            }
        }
        
        System.arraycopy(nowyStan, 0, stan, 0, stan.length);
        return zmieniono;
    }
    
    public double obliczAktywacje(int[] stan, int indeks) {
        double aktywacja = 0.0;
        for (int j = 0; j < rozmiar; j++) {
            if (j != indeks) {
                aktywacja += wagi[indeks][j] * stan[j];
            }
        }
        return aktywacja;
    }
    
    public int odlegloscHamminga(int[] a, int[] b) {
        int odleglosc = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                odleglosc++;
            }
        }
        return odleglosc;
    }
    
    public double obliczEnergie(int[] stan) {
        double energia = 0.0;
        
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (i != j) {
                    energia -= wagi[i][j] * stan[i] * stan[j] * 0.5;
                }
            }
        }
        
        return energia;
    }
    
    public List<int[]> rozpoznajZKrokami(int[] wejscie, int maksIteracji) {
        if (wejscie.length != rozmiar) {
            throw new IllegalArgumentException("Wymiar wejścia musi być zgodny z rozmiarem sieci");
        }
        
        List<int[]> stany = new ArrayList<>();
        int[] stan = wejscie.clone();
        
        stany.add(stan.clone());
        
        boolean zmieniono;
        int iteracje = 0;
        
        while (iteracje < maksIteracji) {
            zmieniono = aktualizujStanSieci(stan);
            stany.add(stan.clone());
            
            if (!zmieniono) {
                break;
            }
            
            iteracje++;
        }
        
        return stany;
    }
}
