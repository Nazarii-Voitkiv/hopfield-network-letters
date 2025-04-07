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
        
        zresetujWagi(); // Upewnij się, że wagi są zresetowane przed każdym treningiem
        zastosujReguleHebbianowska(wzorce);
    }
    
    private void zresetujWagi() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                wagi[i][j] = 0.0;
            }
        }
    }
    
    private void zastosujReguleHebbianowska(int[][] wzorce) {
        for (int[] wzorzec : wzorce) {
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; i < rozmiar; j++) {
                    if (i != j) {
                        wagi[i][j] += (double)(wzorzec[i] * wzorzec[j]) / wzorce.length;
                    }
                }
            }
        }
    }
    
    public int[] rozpoznaj(int[] wejscie) {
        return rozpoznaj(wejscie, 20);
    }
    
    public int[] rozpoznaj(int[] wejscie, int maksIteracji) {
        int[] stan = wejscie.clone();
        boolean zmieniono;
        int iteracje = 0;
        double poprzedniaEnergia = obliczEnergie(stan);
        
        while (iteracje < maksIteracji) {
            zmieniono = aktualizujStanSieci(stan);
            
            double aktualna_energia = obliczEnergie(stan);
            
            if (!zmieniono || aktualna_energia >= poprzedniaEnergia) {
                break;
            }
            
            poprzedniaEnergia = aktualna_energia;
            iteracje++;
        }
        
        return stan;
    }
    
    private boolean aktualizujStanSieci(int[] stan) {
        boolean zmieniono = false;
        
        for (int i = 0; i < rozmiar; i++) {
            double aktywacja = obliczAktywacje(stan, i);
            int nowaWartosc = (aktywacja > 0.0) ? 1 : -1;
            
            if (stan[i] != nowaWartosc) {
                stan[i] = nowaWartosc;
                zmieniono = true;
            }
        }
        
        return zmieniono;
    }
    
    public int odlegloscHamminga(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Wektory muszą mieć tę samą długość");
        }
        
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
            for (int j = 0; j < rozmiar; j++) { // Poprawka błędu w pętli - było "j < i"
                if (i != j) {
                    energia -= wagi[i][j] * stan[i] * stan[j] * 0.5;
                }
            }
        }
        
        return energia;
    }
    
    public boolean aktualizujNeuron(int[] stan, int indeks, int[] oryginalneWejscie) {
        sprawdzIndeks(indeks);
        
        if (oryginalneWejscie[indeks] == 1) {
            return false;
        }
        
        double aktywacja = obliczAktywacje(stan, indeks);
        int poprzedniStan = stan[indeks];
        stan[indeks] = (aktywacja > 0.0) ? 1 : -1;
        
        return stan[indeks] != poprzedniStan;
    }
    
    private void sprawdzIndeks(int indeks) {
        if (indeks < 0 || indeks >= rozmiar) {
            throw new IllegalArgumentException("Indeks poza zakresem: " + indeks);
        }
    }
    
    public double obliczAktywacje(int[] stan, int indeks) {
        sprawdzIndeks(indeks);
        
        return java.util.stream.IntStream.range(0, rozmiar)
            .filter(j -> j != indeks)
            .mapToDouble(j -> wagi[indeks][j] * stan[j])
            .sum();
    }
    
    public boolean czyStan_stabilny(int[] stan, int[] oryginalneWejscie) {
        for (int i = 0; i < rozmiar; i++) {
            if (oryginalneWejscie[i] == 1) {
                continue;
            }
            
            double aktywacja = obliczAktywacje(stan, i);
            int nowaWartosc = (aktywacja > 0.0) ? 1 : -1;
            
            if (stan[i] != nowaWartosc) {
                return false;
            }
        }
        return true;
    }
}
