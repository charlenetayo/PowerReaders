import javax.swing.SwingUtilities;

/**
 * Main
 * The starting point of the program. It just opens the home screen.
 */
public class Main {

    public static void main(String[] args) {
        // Swing windows should always be created on the "Event Dispatch Thread"
        SwingUtilities.invokeLater(() -> {
            HomeFrame home = new HomeFrame();
            home.setVisible(true);
        });
    }
}