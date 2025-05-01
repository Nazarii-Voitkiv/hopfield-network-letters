package view;

import model.PatternManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PatternViewerDialog extends JDialog {
    private static final String[] LETTER_OPTIONS = {"M", "O", "N"};
    private static final int GRID_SIZE = 8;
    private static final int CELL_SIZE = 40;
    
    private final PatternManager patternManager;
    private JComboBox<String> letterSelector;
    private JComboBox<String> patternSelector;
    private PatternPanel patternPanel;
    private JButton deleteButton;
    
    public PatternViewerDialog(JFrame parent, PatternManager patternManager) {
        super(parent, "Przeglądarka wzorców", true);
        this.patternManager = patternManager;
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        createInterface();
        
        setSize(400, 550);
        updatePatternSelector();
    }
    
    private void initializeComponents() {
        letterSelector = new JComboBox<>(LETTER_OPTIONS);
        patternSelector = new JComboBox<>();
        patternPanel = new PatternPanel();
        deleteButton = new JButton("Usuń wzorzec");
        
        letterSelector.addActionListener(e -> updatePatternSelector());
        patternSelector.addActionListener(e -> displaySelectedPattern());
        deleteButton.addActionListener(e -> deleteSelectedPattern());
        deleteButton.setEnabled(false);
    }
    
    private void createInterface() {
        add(createSelectorPanel(), BorderLayout.NORTH);
        add(createPatternViewPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createSelectorPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Litera:"));
        panel.add(letterSelector);
        panel.add(new JLabel("Wzorzec:"));
        panel.add(patternSelector);
        
        return panel;
    }
    
    private JPanel createPatternViewPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder("Podgląd wzorca"));
        container.add(patternPanel, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(deleteButton);
        return panel;
    }
    
    private void updatePatternSelector() {
        String selectedLetter = (String) letterSelector.getSelectedItem();
        patternSelector.removeAllItems();
        
        if (selectedLetter == null) return;
        
        int count = patternManager.countPatterns(selectedLetter);
        if (count == 0) {
            patternSelector.addItem("Brak dostępnych wzorców");
            deleteButton.setEnabled(false);
            patternPanel.clearPattern();
        } else {
            for (int i = 1; i <= count; i++) {
                patternSelector.addItem(selectedLetter + "_" + i);
            }
            deleteButton.setEnabled(true);
            displaySelectedPattern();
        }
    }
    
    private void displaySelectedPattern() {
        String selectedItem = (String) patternSelector.getSelectedItem();
        if (selectedItem == null || selectedItem.equals("Brak dostępnych wzorców")) {
            patternPanel.clearPattern();
            return;
        }
        
        String selectedLetter = (String) letterSelector.getSelectedItem();
        int index = patternSelector.getSelectedIndex();
        
        if (selectedLetter != null && index >= 0 && 
            index < patternManager.getPatterns().get(selectedLetter).size()) {
            int[] pattern = patternManager.getPatterns().get(selectedLetter).get(index);
            patternPanel.setPattern(pattern);
        }
    }
    
    private void deleteSelectedPattern() {
        // Реализация удаления шаблона (опционально)
        JOptionPane.showMessageDialog(this, 
            "Функция удаления пока не реализована.", 
            "Информация", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static class PatternPanel extends JPanel {
        private int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        
        public PatternPanel() {
            setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
            clearPattern();
        }
        
        public void setPattern(int[] pattern) {
            if (pattern.length != GRID_SIZE * GRID_SIZE) {
                throw new IllegalArgumentException("Nieprawidłowy rozmiar wzorca");
            }
            
            int index = 0;
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    grid[i][j] = pattern[index++];
                }
            }
            
            repaint();
        }
        
        public void clearPattern() {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    grid[i][j] = -1;
                }
            }
            
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    g.setColor(grid[i][j] == 1 ? Color.BLACK : Color.WHITE);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    g.setColor(Color.GRAY);
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}
