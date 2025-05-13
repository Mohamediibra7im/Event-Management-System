import ui.LoginFrame;
import javax.swing.SwingUtilities;

public class EventManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
