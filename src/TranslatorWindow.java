import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * TranslatorWindow
 * This class is ONLY the window (GUI): the dropdowns, text boxes, and buttons.
 * When the user clicks Translate, it asks TranslationService to do the work.
 * The user can translate between any two of the four languages.
 */
public class TranslatorWindow extends JFrame {

    // All the languages the user can choose from (for both "From" and "To")
    private static final Language[] LANGUAGES = {
            new Language("English", "en"),
            new Language("Spanish", "es"),
            new Language("Chinese (Simplified)", "zh-CN"),
            new Language("Tagalog", "tl")
    };

    // The "worker" that does the translating
    private final TranslationService service = new TranslationService();

    // GUI components
    private final JComboBox<Language> sourceBox = new JComboBox<>(LANGUAGES);
    private final JComboBox<Language> targetBox = new JComboBox<>(LANGUAGES);
    private final JButton swapBtn = new JButton("Swap \u21C4");
    private final JTextArea inputArea = new JTextArea(8, 40);
    private final JTextArea outputArea = new JTextArea(8, 40);
    private final JButton translateBtn = new JButton("Translate");
    private final JButton copyBtn = new JButton("Copy Result");
    private final JButton clearBtn = new JButton("Clear");
    private final JButton backBtn = new JButton("Back to Home");
    private final JLabel statusLabel = new JLabel("Ready");

    // The home screen, so we can return to it
    private final HomeFrame home;

    public TranslatorWindow(HomeFrame home) {
        super("Language Translator");
        this.home = home;

        // Start with English -> Tagalog selected
        sourceBox.setSelectedIndex(0);
        targetBox.setSelectedIndex(3);

        setupTextAreas();
        layoutComponents();
        setupButtonActions();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(640, 520));
        setLocationRelativeTo(null); // center on screen
    }

    // Step 1: style the two text boxes
    private void setupTextAreas() {
        // "SansSerif" falls back to a font that can show Chinese characters
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        inputArea.setFont(font);
        outputArea.setFont(font);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        outputArea.setEditable(false); // the user can't type in the result box
        outputArea.setBackground(new Color(245, 245, 245));
    }

    // Step 2: place everything inside the window
    private void layoutComponents() {
        // Top: From dropdown, Swap button, To dropdown
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(new JLabel("From:"));
        topPanel.add(sourceBox);
        topPanel.add(swapBtn);
        topPanel.add(new JLabel("To:"));
        topPanel.add(targetBox);

        // Middle: input box on top, output box below
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Enter text"));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Translation"));

        centerPanel.add(inputScroll);
        centerPanel.add(outputScroll);

        // Bottom: buttons and status message
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(translateBtn);
        buttonPanel.add(copyBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        // Put the three panels into the window
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Step 3: tell each button what to do when clicked
    private void setupButtonActions() {
        translateBtn.addActionListener(e -> onTranslateClicked());
        swapBtn.addActionListener(e -> onSwapClicked());
        copyBtn.addActionListener(e -> onCopyClicked());
        clearBtn.addActionListener(e -> onClearClicked());
        backBtn.addActionListener(e -> {
            dispose();               // close the translator window
            home.setVisible(true);   // show the home screen again
        });
    }

    private void onTranslateClicked() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to translate.",
                    "No input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Language source = (Language) sourceBox.getSelectedItem();
        Language target = (Language) targetBox.getSelectedItem();

        // Translating a language into itself makes no sense
        if (source.getCode().equals(target.getCode())) {
            JOptionPane.showMessageDialog(this,
                    "Please choose two different languages.",
                    "Same language", JOptionPane.WARNING_MESSAGE);
            return;
        }

        translateBtn.setEnabled(false);
        statusLabel.setText("Translating " + source + " \u2192 " + target + "...");
        outputArea.setText("");

        // SwingWorker runs the translation in the background
        // so the window does not freeze while waiting for the internet.
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return service.translate(text, source, target);
            }

            @Override
            protected void done() {
                try {
                    outputArea.setText(get());
                    statusLabel.setText("Done.");
                } catch (Exception ex) {
                    Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
                    statusLabel.setText("Error.");
                    JOptionPane.showMessageDialog(TranslatorWindow.this,
                            "Translation failed:\n" + cause.getMessage()
                                    + "\n\nCheck your internet connection and try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    translateBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    // Swaps the two languages, and moves the result into the input box
    private void onSwapClicked() {
        Object oldSource = sourceBox.getSelectedItem();
        sourceBox.setSelectedItem(targetBox.getSelectedItem());
        targetBox.setSelectedItem(oldSource);

        String result = outputArea.getText();
        if (!result.isEmpty()) {
            inputArea.setText(result);
            outputArea.setText("");
        }
        statusLabel.setText("Ready");
    }

    private void onCopyClicked() {
        String result = outputArea.getText();
        if (!result.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(result), null);
            statusLabel.setText("Copied to clipboard.");
        }
    }

    private void onClearClicked() {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("Ready");
    }
}