import controller.HopfieldController;
import model.HopfieldNetwork;
import view.MainView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            HopfieldController controller = new HopfieldController();
            controller.setMaxIterations(1);
            controller.setUpdateMode(HopfieldNetwork.UpdateMode.SYNCHRONOUS);
            
            MainView view = new MainView(controller);
            controller.setMainView(view);
            view.setVisible(true);
        });
    }
}
