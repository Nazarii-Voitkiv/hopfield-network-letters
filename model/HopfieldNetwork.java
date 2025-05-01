package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HopfieldNetwork {
    private final int size;
    private final double[][] weights;
    private UpdateMode updateMode = UpdateMode.SYNCHRONOUS;
    
    public enum UpdateMode {
        SYNCHRONOUS, ASYNCHRONOUS
    }
    
    public HopfieldNetwork(int size) {
        this.size = size;
        this.weights = new double[size][size];
    }
    
    public void setUpdateMode(UpdateMode mode) {
        this.updateMode = mode;
    }
    
    public void train(int[][] patterns) {
        if (patterns == null || patterns.length == 0) {
            throw new IllegalArgumentException("Brak wzorców do treningu");
        }
        
        resetWeights();
        
        for (int[] pattern : patterns) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        weights[i][j] += (double) pattern[i] * pattern[j] / size;
                    }
                }
            }
        }
    }
    
    private void resetWeights() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                weights[i][j] = 0.0;
            }
        }
    }
    
    public int[] recognize(int[] input, int maxIterations) {
        int[] state = input.clone();
        boolean changed;
        int iterations = 0;
        
        while (iterations < maxIterations) {
            changed = updateNetworkState(state);
            
            if (!changed) {
                break;
            }
            
            iterations++;
        }
        
        return state;
    }
    
    public List<int[]> recognizeWithSteps(int[] input, int maxIterations) {
        List<int[]> states = new ArrayList<>();
        int[] state = input.clone();
        states.add(state.clone());
        
        boolean changed;
        int iterations = 0;
        
        while (iterations < maxIterations) {
            changed = updateNetworkState(state);
            states.add(state.clone());
            
            if (!changed) {
                break;
            }
            
            iterations++;
        }
        
        return states;
    }
    
    private boolean updateNetworkState(int[] state) {
        if (updateMode == UpdateMode.SYNCHRONOUS) {
            return updateSynchronous(state);
        } else {
            return updateAsynchronous(state);
        }
    }
    
    private boolean updateSynchronous(int[] state) {
        boolean changed = false;
        int[] newState = new int[state.length];
        
        for (int i = 0; i < size; i++) {
            double activation = calculateActivation(state, i);
            newState[i] = (activation >= 0) ? 1 : -1;
            
            if (state[i] != newState[i]) {
                changed = true;
            }
        }
        
        System.arraycopy(newState, 0, state, 0, state.length);
        return changed;
    }
    
    private boolean updateAsynchronous(int[] state) {
        boolean changed = false;
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int index = indices.remove(random.nextInt(indices.size()));
            double activation = calculateActivation(state, index);
            int newValue = (activation >= 0) ? 1 : -1;
            
            if (state[index] != newValue) {
                state[index] = newValue;
                changed = true;
            }
        }
        
        return changed;
    }
    
    public double calculateActivation(int[] state, int index) {
        double activation = 0.0;
        for (int j = 0; j < size; j++) {
            if (j != index) {
                activation += weights[index][j] * state[j];
            }
        }
        return activation;
    }
    
    public int hammingDistance(int[] a, int[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                distance++;
            }
        }
        return distance;
    }
    
    public double calculateEnergy(int[] state) {
        double energy = 0.0;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    energy -= weights[i][j] * state[i] * state[j] * 0.5;
                }
            }
        }
        
        return energy;
    }
}