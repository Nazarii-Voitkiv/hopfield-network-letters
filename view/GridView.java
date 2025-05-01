package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GridView extends JPanel {
    private static final int GRID_SIZE = 8;
    private static final int CELL_SIZE = 50;
    
    private final int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private final JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];
    private int[] lastPattern;
    private boolean isDrawing = false;
    private int currentDrawMode = 1;
    
    public GridView() {
        configurePanel();
        initializeGrid();
        createButtons();
        addDrawingHandlers();
    }
    
    private void configurePanel() {
        Dimension dimension = new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE, 0, 0));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
    
    private void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = -1;
            }
        }
    }
    
    private void createButtons() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = createGridButton();
                buttons[i][j].putClientProperty("row", i);
                buttons[i][j].putClientProperty("col", j);
                add(buttons[i][j]);
            }
        }
    }
    
    private JButton createGridButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setBorder(new LineBorder(Color.GRAY, 1));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setUI(new BasicButtonUI());
        return button;
    }
    
    private void addDrawingHandlers() {
        MouseHandler handler = new MouseHandler();
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j].addMouseListener(handler);
                buttons[i][j].addMouseMotionListener(handler);
            }
        }
    }
    
    private class MouseHandler extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mousePressed(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            int row = (int) button.getClientProperty("row");
            int col = (int) button.getClientProperty("col");
            
            currentDrawMode = (grid[row][col] == 1) ? -1 : 1;
            
            isDrawing = true;
            setCellState(row, col, currentDrawMode);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            isDrawing = false;
            saveCurrentPattern();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isDrawing) return;
            
            Point mousePosition;
            if (e.getSource() instanceof JButton) {
                mousePosition = SwingUtilities.convertPoint(
                    (Component) e.getSource(), e.getPoint(), GridView.this);
            } else {
                mousePosition = e.getPoint();
            }
            
            int row = mousePosition.y / CELL_SIZE;
            int col = mousePosition.x / CELL_SIZE;
            
            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                setCellState(row, col, currentDrawMode);
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            
        }
    }
    
    private void setCellState(int row, int col, int newState) {
        if (grid[row][col] != newState) {
            grid[row][col] = newState;
            updateButtonAppearance(buttons[row][col], newState);
        }
    }
    
    private void updateButtonAppearance(JButton button, int state) {
        button.setBackground(state == 1 ? Color.BLACK : Color.WHITE);
    }
    
    public int[] getGridAsArray() {
        int[] result = new int[GRID_SIZE * GRID_SIZE];
        int index = 0;
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                result[index++] = grid[i][j];
            }
        }
        
        return result;
    }
    
    public void setGridFromArray(int[] array) {
        checkArrayLength(array);
        
        int index = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = array[index];
                updateButtonAppearance(buttons[i][j], array[index]);
                index++;
            }
        }
    }
    
    private void checkArrayLength(int[] array) {
        if (array.length != GRID_SIZE * GRID_SIZE) {
            throw new IllegalArgumentException("Tablica musi mieć długość " + 
                (GRID_SIZE * GRID_SIZE));
        }
    }
    
    public void clearGrid() {
        initializeGrid();
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                updateButtonAppearance(buttons[i][j], grid[i][j]);
            }
        }
    }
    
    public void saveCurrentPattern() {
        lastPattern = getGridAsArray();
    }
    
    public void restoreLastPattern() {
        if (lastPattern != null) {
            setGridFromArray(lastPattern);
        }
    }
}
