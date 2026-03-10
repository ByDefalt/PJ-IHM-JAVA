package com.ubo.tp.message.utils;

public class EmojiBinders {
    // Liste des codes supportés
    private static final String[] CODES = new String[]{
            ":smile:", ":sad:", ":heart:", ":thumbs_up:", ":thumbs_down:",
            ":laughing:", ":crying:", ":angry:", ":surprised:"
    };

    // Mapping code -> twemoji hex
    private static final java.util.Map<String, String> TWEMOJI_HEX = new java.util.HashMap<>();
    static {
        TWEMOJI_HEX.put(":smile:", "1f60a");
        TWEMOJI_HEX.put(":sad:", "2639");
        TWEMOJI_HEX.put(":heart:", "2764");
        TWEMOJI_HEX.put(":thumbs_up:", "1f44d");
        TWEMOJI_HEX.put(":thumbs_down:", "1f44e");
        TWEMOJI_HEX.put(":laughing:", "1f606");
        TWEMOJI_HEX.put(":crying:", "1f62d");
        TWEMOJI_HEX.put(":angry:", "1f620");
        TWEMOJI_HEX.put(":surprised:", "1f632");
    }

    public static String[] getSupportedCodes() { return CODES.clone(); }

    /**
     * Retourne l'URL d'une image emoji : d'abord ressource locale (/images/emoji/<name>.png),
     * sinon fallback vers Twemoji CDN.
     */
    public static String getEmojiImageUrl(String code) {
        if (code == null) return null;
        String key = code;
        String name = null;
        switch (key) {
            case ":smile:": name = "smile.png"; break;
            case ":sad:": name = "sad.png"; break;
            case ":heart:": name = "heart.png"; break;
            case ":thumbs_up:": name = "thumbs_up.png"; break;
            case ":thumbs_down:": name = "thumbs_down.png"; break;
            case ":laughing:": name = "laughing.png"; break;
            case ":crying:": name = "crying.png"; break;
            case ":angry:": name = "angry.png"; break;
            case ":surprised:": name = "surprised.png"; break;
            default: name = null; break;
        }
        if (name != null) {
            java.net.URL res = EmojiBinders.class.getResource("/images/emoji/" + name);
            if (res != null) return res.toExternalForm();
        }
        // fallback Twemoji
        String hex = TWEMOJI_HEX.get(code);
        if (hex != null) return "https://twemoji.maxcdn.com/v/latest/72x72/" + hex + ".png";
        return null;
    }

    /**
     * Remplace les codes emoji par des entités HTML contenant le caractère emoji
     * et un span qui demande une police emoji (pour JEditorPane HTML).
     */
    public static String replaceEmojiCodesHtml(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.replace(":smile:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDE0A</span>")
                   .replace(":sad:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\u2639\uFE0F</span>")
                   .replace(":heart:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\u2764\uFE0F</span>")
                   .replace(":thumbs_up:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDC4D</span>")
                   .replace(":thumbs_down:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDC4E</span>")
                   .replace(":laughing:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDE06</span>")
                   .replace(":crying:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDE2D</span>")
                   .replace(":angry:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDE20</span>")
                   .replace(":surprised:", "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">\uD83D\uDE32</span>");
    }

    /**
     * Remplace les codes emoji par les caractères Unicode correspondants (pur texte).
     * Utilisé côté JavaFX/TextFlow.
     */
    public static String replaceEmojiCodesUnicode(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.replace(":smile:", "\uD83D\uDE0A")
                   .replace(":sad:", "\u2639\uFE0F")
                   .replace(":heart:", "\u2764\uFE0F")
                   .replace(":thumbs_up:", "\uD83D\uDC4D")
                   .replace(":thumbs_down:", "\uD83D\uDC4E")
                   .replace(":laughing:", "\uD83D\uDE06")
                   .replace(":crying:", "\uD83D\uDE2D")
                   .replace(":angry:", "\uD83D\uDE20")
                   .replace(":surprised:", "\uD83D\uDE32");
    }

    /**
     * Ancienne méthode (conservée pour compatibilité) : renvoie HTML par défaut.
     */
    @Deprecated
    public static String replaceEmojiCodes(String text) {
        return replaceEmojiCodesHtml(text);
    }
}
