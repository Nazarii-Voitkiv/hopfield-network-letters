# Hopfield Network – Rozpoznawanie Liter (M, O, N)

## 🧠 Opis projektu
Aplikacja desktopowa (Java + Swing) realizująca klasyczną sieć Hopfielda do rozpoznawania i autouzupełniania liter M, O, N rysowanych na siatce 8×8. System uczy się na podstawie przykładów i potrafi uzupełniać częściowo narysowane litery.

---

## 📚 Teoretyczne podstawy

Sieć Hopfielda to rekurencyjna, w pełni połączona sieć neuronowa, działająca jako autoasocjacyjna pamięć. Każdy zapisany wzorzec staje się tzw. atraktorem – stabilnym stanem, do którego sieć dąży.

### 🔁 Algorytm uczenia (reguła Hebba)

Dla wag między neuronami:

Wᵢⱼ = (1 / N) * Σₛ (Vᵢˢ * Vⱼˢ),  gdzie i ≠ j

- `N` – liczba neuronów (64 dla siatki 8×8)
- `Vᵢˢ` – stan i-tego neuronu w s-tym wzorcu
- Wagi są symetryczne i bez samozwrotnych połączeń

### ⚙️ Proces rozpoznawania

1. Inicjalizacja stanu sieci częściowym lub zniekształconym wzorcem
2. Iteracyjne uaktualnianie stanu każdego neuronu:

hᵢ = Σⱼ Wᵢⱼ * Vⱼ
Vᵢ = sgn(hᵢ)

3. Proces kończy się, gdy sieć osiągnie stan stabilny (brak zmian) lub przekroczy limit iteracji

### 🔋 Funkcja energii
Sieć dąży do lokalnego minimum energii:

E = -0.5 * Σᵢ Σⱼ Wᵢⱼ * Vᵢ * Vⱼ

---

## 📦 Architektura projektu

Projekt oparty na wzorcu **MVC**:

- **Model**:
  - `HopfieldNetwork.java` – logika uczenia i rozpoznawania
  - `PatternManager.java` – zarządzanie zapisanymi wzorcami

- **View**:
  - `MainView.java` – główne GUI aplikacji
  - `GridView.java` – komponent siatki do rysowania
  - `PatternViewerDialog.java` – podgląd zapisanych wzorców

- **Controller**:
  - `HopfieldController.java` – zarządza interakcją między modelem a widokiem

---

## ⚙️ Funkcjonalność

- Rysowanie liter M, O, N na siatce 8×8
- Zapisywanie wielu wzorców na literę
- Automatyczne trenowanie sieci po dodaniu nowego wzorca
- Uzupełnianie niepełnych rysunków
- Wybór trybu aktualizacji: synchroniczny lub asynchroniczny
- Wyświetlanie energii sieci podczas iteracji
- Obsługa animacji procesu rozpoznawania

---

## 📈 Pojemność sieci

Zgodnie z teorią, sieć Hopfielda może stabilnie przechowywać:

P_max ≈ 0.138 × N

Dla N = 64 (8×8 siatka):  
**P_max ≈ 8–9 wzorców łącznie**

Przekroczenie tej liczby może prowadzić do:
- zafałszowanych wspomnień (ang. spurious states)
- błędnego rozpoznawania

---

## ▶️ Jak uruchomić

1. Uruchom `Main.java`
2. Narysuj literę lub jej fragment
3. Kliknij **"Uzupełnij"**, aby aktywować sieć
4. Obserwuj proces rozpoznawania i wynik

---

## ➕ Dodawanie nowych wzorców

1. Narysuj literę M, O lub N
2. Kliknij przycisk: `"Zapisz M"`, `"Zapisz O"` lub `"Zapisz N"`
3. Sieć automatycznie się przeuczy

---

## 📁 Informacje techniczne

- Maksymalna liczba iteracji: konfigurowalna (`MAX_ITERACJI_HOPFIELD`)
- Wzorce przechowywane w plikach tekstowych (`wzorce/`)
- Wartości wzorca: 64 linii z wartościami `1` lub `-1`
- Obsługa zarówno synchronicznego, jak i asynchronicznego trybu pracy

---

## 🛠 Przyszłe ulepszenia

- Obsługa większej liczby liter
- Eksport/import wzorców do JSON lub CSV
- Przełącznik trybu rozpoznawania w GUI
- Testowanie odporności na szum

---

## 📄 Licencja

Projekt edukacyjny. Może być używany i modyfikowany do celów naukowych lub demonstracyjnych.