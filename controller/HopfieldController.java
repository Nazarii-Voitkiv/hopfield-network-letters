package controller;

import model.HopfieldNetwork;
import model.PatternManager;
import view.GridView;
import view.MainView;
import view.PatternViewerDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class HopfieldController {
    private static final int DEFAULT_MAX_ITERATIONS = 10;
    private static final int ANIMATION_DELAY = 300;
    
    private final HopfieldNetwork network;
    private final PatternManager patternManager;
    private MainView mainView;
    private Timer animationTimer;
    private boolean animationRunning = false;
    private int maxIterations = DEFAULT_MAX_ITERATIONS;
    
    public HopfieldController() {
        network = new HopfieldNetwork(64);
        patternManager = new PatternManager();
        trainNetwork();
    }
    
    public void setMainView(MainView view) {
        this.mainView = view;
    }
    
    public void setMaxIterations(int iterations) {
        this.maxIterations = iterations;
    }
    
    public void setUpdateMode(HopfieldNetwork.UpdateMode mode) {
        network.setUpdateMode(mode);
    }
    
    private void trainNetwork() {
        List<int[]> trainingPatterns = patternManager.getAllPatterns();
        if (!trainingPatterns.isEmpty()) {
            network.train(trainingPatterns.toArray(new int[0][]));
        }
    }
    
    public void savePattern(int[] pattern, String letter) {
        try {
            patternManager.savePattern(pattern, letter);
            trainNetwork();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                mainView, 
                "Błąd podczas zapisu wzorca: " + e.getMessage(), 
                "Błąd", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public void startRecognition(int[] initialPattern, GridView gridView) {
        if (animationRunning) {
            stopAnimation();
            return;
        }
        
        animationRunning = true;
        
        List<int[]> animationSteps = network.recognizeWithSteps(
            initialPattern.clone(), maxIterations);
        
        AnimationHandler handler = new AnimationHandler(animationSteps, gridView);
        animationTimer = new Timer(ANIMATION_DELAY, handler);
        animationTimer.start();
        
        mainView.setResultText("<html><div style='width:380px'>" +
                               "<b>Animacja sieci Hopfielda...</b><br>" +
                               "Krok: 0/" + animationSteps.size() + "</div></html>");
    }
    
    public void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        animationRunning = false;
        mainView.setCompleteButtonText("Uzupełnij");
    }
    
    public boolean isAnimationRunning() {
        return animationRunning;
    }
    
    public int countPatterns(String letter) {
        return patternManager.countPatterns(letter);
    }
    
    public void showPatternViewer(JFrame parent) {
        PatternViewerDialog dialog = new PatternViewerDialog(parent, patternManager);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        mainView.updatePatternStats();
        trainNetwork();
    }
    
    private class AnimationHandler implements ActionListener {
        private final List<int[]> animationSteps;
        private final GridView gridView;
        private int currentStep = 0;
        
        public AnimationHandler(List<int[]> steps, GridView gridView) {
            this.animationSteps = steps;
            this.gridView = gridView;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentStep >= animationSteps.size()) {
                stopAnimation();
                showRecognitionResult();
                return;
            }
            
            int[] currentState = animationSteps.get(currentStep);
            gridView.setGridFromArray(currentState);
            
            mainView.setResultText(String.format("<html><div style='width:380px'>" +
                    "<b>Przetwarzanie sieci Hopfielda</b><br>" +
                    "Iteracja: %d/%d<br>" +
                    "Energia: %.2f</div></html>", 
                    currentStep + 1, animationSteps.size(),
                    network.calculateEnergy(currentState)));
            
            currentStep++;
        }
        
        private void showRecognitionResult() {
            int[] finalState = animationSteps.get(animationSteps.size() - 1);
            String closestLetter = patternManager.findClosestLetter(finalState);
            
            StringBuilder resultText = new StringBuilder("<html><div style='width:380px'>");
            resultText.append("<h3>WYNIK SIECI HOPFIELDA</h3>");
            
            if (closestLetter != null) {
                resultText.append("Rozpoznana litera: <b style='font-size:18px;'>")
                         .append(closestLetter).append("</b><br><br>");
            }
            
            resultText.append("Liczba iteracji: ").append(animationSteps.size()).append("<br>");
            resultText.append("Energia końcowa: ")
                    .append(String.format("%.2f", network.calculateEnergy(finalState)))
                    .append("<br><br>");
            resultText.append("<span style='color:green;'><b>Przetwarzanie zakończone</b></span>");
            resultText.append("</div></html>");
            
            mainView.setResultText(resultText.toString());
        }
    }
}
