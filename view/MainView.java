package view;

import controller.HopfieldController;
import model.HopfieldNetwork.UpdateMode;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private final GridView gridView;
    private final JLabel resultLabel;
    private final JLabel statsLabel;
    private JButton completeButton;
    private final HopfieldController controller;
    
    public MainView(HopfieldController controller) {
        super("Sieć Hopfielda - Rozpoznawanie Liter");
        this.controller = controller;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setMinimumSize(new Dimension(500, 700));
        
        gridView = new GridView();
        
        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        statsLabel = new JLabel("", JLabel.CENTER);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statsLabel.setForeground(Color.BLACK);
        statsLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        
        createInterface();
        
        setSize(500, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        
        updatePatternStats();
        resultLabel.setText("<html>Sieć gotowa do pracy.<br>Narysuj fragment litery</html>");
    }
    
    private void createInterface() {
        add(createGridPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createInfoPanel(), BorderLayout.NORTH);
    }
    
    private JPanel createGridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel centerPanel = new JPanel();
        centerPanel.add(gridView);
        
        panel.add(Box.createVerticalGlue());
        panel.add(centerPanel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        
        JButton saveM = createButton("Zapisz M");
        JButton saveO = createButton("Zapisz O");
        JButton saveN = createButton("Zapisz N");
        JButton viewPatterns = createButton("Przeglądaj wzorce");
        completeButton = createButton("Uzupełnij");
        JButton clear = createButton("Wyczyść");
        
        saveM.addActionListener(e -> savePattern("M"));
        saveO.addActionListener(e -> savePattern("O"));
        saveN.addActionListener(e -> savePattern("N"));
        viewPatterns.addActionListener(e -> openPatternViewer());
        completeButton.addActionListener(e -> completePattern());
        clear.addActionListener(e -> clearGrid());
        
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(saveM, gbc);
        
        gbc.gridx = 1;
        panel.add(saveO, gbc);
        
        gbc.gridx = 2;
        panel.add(saveN, gbc);
        
        gbc.gridx = 3;
        panel.add(clear, gbc);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        
        completeButton.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(completeButton, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        panel.add(viewPatterns, gbc);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(400, 150));
        
        JScrollPane scrollPane = new JScrollPane(resultLabel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(scrollPane, BorderLayout.CENTER);
        labelPanel.add(statsLabel, BorderLayout.SOUTH);
        
        panel.add(labelPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setMargin(new Insets(10, 15, 10, 15));
        return button;
    }
    
    private void savePattern(String letter) {
        controller.savePattern(gridView.getGridAsArray(), letter);
        updatePatternStats();
    }
    
    private void openPatternViewer() {
        controller.showPatternViewer(this);
    }
    
    private void completePattern() {
        if (controller.isAnimationRunning()) {
            controller.stopAnimation();
            completeButton.setText("Uzupełnij");
            return;
        }
        
        int[] inputPattern = gridView.getGridAsArray();
        gridView.saveCurrentPattern();
        
        completeButton.setText("Zatrzymaj");
        controller.startRecognition(inputPattern, gridView);
    }
    
    private void clearGrid() {
        gridView.clearGrid();
        resultLabel.setText("Wynik: Brak");
        controller.stopAnimation();
    }
    
    public void updatePatternStats() {
        int countM = controller.countPatterns("M");
        int countO = controller.countPatterns("O");
        int countN = controller.countPatterns("N");
        
        statsLabel.setText(String.format(
            "<html><div style='text-align: center;'>Wzorce: " +
            "<b>M=%d</b>, <b>O=%d</b>, <b>N=%d</b>" +
            "</div></html>", 
            countM, countO, countN
        ));
    }
    
    public void setResultText(String text) {
        resultLabel.setText(text);
    }
    
    public void setCompleteButtonText(String text) {
        completeButton.setText(text);
    }
    
    public void setCompleteButtonEnabled(boolean enabled) {
        completeButton.setEnabled(enabled);
    }
    
    public GridView getGridView() {
        return gridView;
    }
}