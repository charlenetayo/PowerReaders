import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TextSplitter
 * The free translation API only accepts about 500 bytes per request.
 * This class cuts long text into smaller pieces (chunks) so each one fits.
 * It tries to cut at the end of a sentence so the translation stays natural.
 */
public class TextSplitter {

    // Splits the text into chunks that are each at most maxBytes long
    public static List<String> split(String text, int maxBytes) {
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        // Cut after . ! ? (English/Spanish), Chinese punctuation, or a new line
        String[] sentences = text.split("(?<=[.!?\u3002\uFF01\uFF1F\n])");

        for (String sentence : sentences) {
            if (byteLength(sentence) > maxBytes) {
                // This one sentence is too long by itself, so save what we have first...
                if (current.length() > 0) {
                    chunks.add(current.toString());
                    current.setLength(0);
                }
                // ...then cut the long sentence character by character
                chunks.addAll(cutByCharacters(sentence, maxBytes));

            } else if (byteLength(current.toString()) + byteLength(sentence) > maxBytes) {
                // Adding this sentence would go over the limit, so start a new chunk
                chunks.add(current.toString());
                current.setLength(0);
                current.append(sentence);

            } else {
                // It still fits, so add it to the current chunk
                current.append(sentence);
            }
        }

        if (current.toString().trim().length() > 0) {
            chunks.add(current.toString());
        }
        return chunks;
    }

    // Used only for extremely long sentences
    private static List<String> cutByCharacters(String sentence, int maxBytes) {
        List<String> pieces = new ArrayList<>();
        StringBuilder piece = new StringBuilder();

        for (char ch : sentence.toCharArray()) {
            piece.append(ch);
            // Leave a little room, because Chinese characters take 3 bytes each
            if (byteLength(piece.toString()) >= maxBytes - 4) {
                pieces.add(piece.toString());
                piece.setLength(0);
            }
        }
        if (piece.length() > 0) {
            pieces.add(piece.toString());
        }
        return pieces;
    }

    // The API limit is measured in bytes, not characters
    private static int byteLength(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }
}