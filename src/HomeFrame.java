import javax.swing.*;
import java.awt.*;

/**
 * HomeFrame
 * The home (welcome) screen of the program.
 * It has three buttons: Start Translating, About, and Exit.
 * "Start Translating" opens the TranslatorWindow.
 */
public class HomeFrame extends JFrame {

    private final JButton startBtn = new JButton("Start Translating");
    private final JButton aboutBtn = new JButton("About");
    private final JButton exitBtn = new JButton("Exit");

    public HomeFrame() {
        super("Language Translator - Home");
        buildUI();
        setupButtonActions();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 380);
        setResizable(false);
        setLocationRelativeTo(null); // center on screen
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Language Translator");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("English \u2022 Spanish \u2022 Chinese \u2022 Tagalog");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Make all three buttons the same size and centered
        styleButton(startBtn);
        styleButton(aboutBtn);
        styleButton(exitBtn);

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));
        panel.add(startBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(aboutBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(exitBtn);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(220, 42);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
    }

    private void setupButtonActions() {
        // Open the translator and hide this home screen
        startBtn.addActionListener(e -> {
            TranslatorWindow translator = new TranslatorWindow(this);
            translator.setVisible(true);
            setVisible(false);
        });

        aboutBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Language Translator\n\n"
                        + "Translates text between English, Spanish, Chinese, and Tagalog.\n"
                        + "Powered by the MyMemory Translation API.\n"
                        + "An internet connection is required.",
                "About", JOptionPane.INFORMATION_MESSAGE));

        exitBtn.addActionListener(e -> System.exit(0));
    }
}