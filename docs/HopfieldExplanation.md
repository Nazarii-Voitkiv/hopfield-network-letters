# Sieć Hopfielda - Zasada działania

## 1. Struktura sieci
- Sieć składa się z 64 neuronów (siatka 8×8)
- Każdy neuron połączony z każdym innym (ale nie z samym sobą)
- Wagi połączeń przechowywane w macierzy `double[][] wagi` o wymiarach 64×64

## 2. Reprezentacja wzorców
- Każda litera to wzorzec 64 pikseli (int[64])
- Wartości pikseli: 1 (czarny/aktywny) lub -1 (biały/nieaktywny)
- Wzorce przechowywane w `Map<String, List<int[]>> wzorce`

## 3. Uczenie sieci (metoda `trenuj()`)

```java
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
```

## 4. Rozpoznawanie wzorca (metoda `rozpoznaj()`)

```java
public int[] rozpoznaj(int[] wejscie, int maksIteracje) {
    int[] stan = wejscie.clone();
    
    while (iteracje < maksIteracje) {
        for (int i = 0; i < rozmiar; i++) {
            double suma = 0.0;
            for (int j = 0; j < rozmiar; j++) {
                if (i != j) {
                    suma += wagi[i][j] * stan[j];
                }
            }
            int nowaWartosc = (suma >= 0) ? 1 : -1;
            stan[i] = nowaWartosc;
        }
        
        if (!zmieniono || energia_wzrosla) break;
    }
    
    return stan;
}
```

## 5. Ocena rozpoznanego wzorca
- Porównanie wynikowego wzorca z każdym zapisanym wzorcem
- Wykorzystanie odległości Hamminga do określenia podobieństwa
- Sortowanie wyników według najmniejszej odległości

## 6. Interpretacja wyniku
- Wyświetlenie rozpoznanej litery
- Pokazanie dystansów Hamminga dla wszystkich liter
- Informacja o liczbie zmienionych pikseli
