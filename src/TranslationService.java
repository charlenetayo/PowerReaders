import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TranslationService
 * This class does the actual translating. It has NO GUI code.
 * It sends the text to the MyMemory API over the internet
 * and returns the translation in the language the user picked.
 *
 * If you ever want to switch to Google Translate or Azure,
 * this is the only file you need to change.
 */
public class TranslationService {

    private static final String API_URL = "https://api.mymemory.translated.net/get";
    private static final int MAX_BYTES_PER_REQUEST = 400; // API limit is about 500

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Translates text from any source language into any target language.
     * Long text is split into chunks, translated one by one, then joined back together.
     */
    public String translate(String text, Language source, Language target) throws Exception {
        StringBuilder result = new StringBuilder();

        for (String chunk : TextSplitter.split(text, MAX_BYTES_PER_REQUEST)) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(translateChunk(chunk, source.getCode(), target.getCode()));
        }
        return result.toString();
    }

    // Sends ONE small piece of text to the API
    private String translateChunk(String text, String sourceCode, String targetCode) throws Exception {
        // Build the web address, e.g. ...get?q=Hello&langpair=en|es
        String url = API_URL
                + "?q=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&langpair=" + URLEncoder.encode(sourceCode + "|" + targetCode, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Server returned HTTP " + response.statusCode());
        }
        return readTranslation(response.body());
    }

    /**
     * The API answers in JSON, which looks like:
     * {"responseData":{"translatedText":"Kumusta"}, "responseStatus":200, ...}
     * We pull out the "translatedText" part. (No extra JSON library needed.)
     */
    private String readTranslation(String json) {
        // First check that the API did not report an error
        Matcher status = Pattern.compile("\"responseStatus\"\\s*:\\s*\"?(\\d+)").matcher(json);
        if (status.find() && !status.group(1).equals("200")) {
            Matcher details = Pattern
                    .compile("\"responseDetails\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
                    .matcher(json);
            String message = details.find() ? unescapeJson(details.group(1)) : "Unknown API error";
            throw new RuntimeException(message);
        }

        // Then get the translated text
        Matcher translated = Pattern
                .compile("\"translatedText\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
                .matcher(json);
        if (!translated.find()) {
            throw new RuntimeException("Could not read the translation from the server response.");
        }
        return unescapeJson(translated.group(1));
    }

    // JSON writes special characters as codes (like \n or \u00e9). This turns them back.
    private String unescapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) {
                sb.append(c);
                continue;
            }
            char next = s.charAt(++i);
            switch (next) {
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u':
                    if (i + 4 < s.length()) {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                    break;
                default: sb.append(next); // handles \" and \\ and \/
            }
        }
        return sb.toString();
    }
}