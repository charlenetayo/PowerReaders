/**
 * Language
 * Stores one language: the name shown to the user (e.g. "Spanish")
 * and the short code the translation API understands (e.g. "es").
 */
public class Language {

    private final String name;
    private final String code;

    public Language(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    // The dropdown box (JComboBox) calls toString() to decide what text to show
    @Override
    public String toString() {
        return name;
    }
}