public class HopfieldNetwork {
    private int rozmiar;
    private double[][] wagi;
    
    public HopfieldNetwork(int rozmiar) {
        this.rozmiar = rozmiar;
        this.wagi = new double[rozmiar][rozmiar];
    }
    
    public void train(int[][] wzorce) {
        resetWagi();
        zastosujReguleHebbianowska(wzorce);
    }
    
    private void resetWagi() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                wagi[i][j] = 0.0;
            }
        }
    }
    
    private void zastosujReguleHebbianowska(int[][] wzorce) {
        for (int[] wzorzec : wzorce) {
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (i != j) {
                        wagi[i][j] += (double)(wzorzec[i] * wzorzec[j]) / wzorce.length;
                    }
                }
            }
        }
    }
    
    public int[] recall(int[] wejscie, int maxIteracje) {
        int[] stan = wejscie.clone();
        boolean zmieniono;
        int iteracje = 0;
        double poprzedniaEnergia = obliczEnergie(stan);
        
        while (iteracje < maxIteracje) {
            zmieniono = false;
            
            for (int i = 0; i < rozmiar; i++) {
                double suma = 0.0;
                for (int j = 0; j < rozmiar; j++) {
                    if (i != j) {
                        suma += wagi[i][j] * stan[j];
                    }
                }
                
                int nowaWartosc = (suma > 0.0) ? 1 : -1;
                
                if (stan[i] != nowaWartosc) {
                    stan[i] = nowaWartosc;
                    zmieniono = true;
                }
            }
            
            double obecnaEnergia = obliczEnergie(stan);
            
            if (!zmieniono || obecnaEnergia >= poprzedniaEnergia) {
                break;
            }
            
            poprzedniaEnergia = obecnaEnergia;
            iteracje++;
        }
        
        return stan;
    }
    
    public int[] recall(int[] wejscie) {
        return recall(wejscie, 20);
    }
    
    public int hammingDistance(int[] a, int[] b) {
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
            for (int j = 0; j < rozmiar; j++) {
                if (i != j) {
                    energia -= wagi[i][j] * stan[i] * stan[j] * 0.5;
                }
            }
        }
        
        return energia;
    }
}
