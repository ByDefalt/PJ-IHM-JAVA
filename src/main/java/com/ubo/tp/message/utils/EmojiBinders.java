package com.ubo.tp.message.utils;

import java.util.Map;

public class EmojiBinders {
    // Centralised definitions for all supported emojis (name, twemoji hex code, unicode char)
    private static final Map<String, EmojiData> EMOJIS = new java.util.LinkedHashMap<>();

    static {
        // Utiliser les caractères emoji directement pour la lisibilité
        EMOJIS.put(":smile:", new EmojiData("smile.png", "1f60a", "😊"));
        EMOJIS.put(":sad:", new EmojiData("sad.png", "2639", "☹️"));
        EMOJIS.put(":heart:", new EmojiData("heart.png", "2764", "❤️"));
        EMOJIS.put(":thumbs_up:", new EmojiData("thumbs_up.png", "1f44d", "👍"));
        EMOJIS.put(":thumbs_down:", new EmojiData("thumbs_down.png", "1f44e", "👎"));
        EMOJIS.put(":laughing:", new EmojiData("laughing.png", "1f606", "😆"));
        EMOJIS.put(":crying:", new EmojiData("crying.png", "1f62d", "😭"));
        EMOJIS.put(":angry:", new EmojiData("angry.png", "1f620", "😠"));
        EMOJIS.put(":surprised:", new EmojiData("surprised.png", "1f632", "😲"));
    }

    private EmojiBinders() {
        /* This utility class should not be instantiated */
    }

    public static String[] getSupportedCodes() {
        return EMOJIS.keySet().toArray(new String[0]);
    }

    /**
     * Retourne l'URL d'une image emoji : d'abord ressource locale (/images/emoji/<name>.png),
     * sinon fallback vers Twemoji CDN.
     */
    public static String getEmojiImageUrl(String code) {
        if (code == null) return null;
        EmojiData data = EMOJIS.get(code);
        if (data == null) return null;
        if (data.fileName != null) {
            java.net.URL res = EmojiBinders.class.getResource("/images/emoji/" + data.fileName);
            if (res != null) return res.toExternalForm();
        }
        // fallback Twemoji
        if (data.hex != null) return "https://twemoji.maxcdn.com/v/latest/72x72/" + data.hex + ".png";
        return null;
    }

    /**
     * Remplace les codes emoji par des entités HTML contenant le caractère emoji
     * et un span qui demande une police emoji (pour JEditorPane HTML).
     */
    public static String replaceEmojiCodesHtml(String text) {
        if (text == null || text.isEmpty()) return text;
        String out = text;
        for (Map.Entry<String, EmojiData> e : EMOJIS.entrySet()) {
            String code = e.getKey();
            String uni = e.getValue().unicode;
            String span = "<span style=\"font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', 'Segoe UI Symbol';\">" + uni + "</span>";
            out = out.replace(code, span);
        }
        return out;
    }

    /**
     * Remplace les codes emoji par les caractères Unicode correspondants (pur texte).
     * Utilisé côté JavaFX/TextFlow.
     */
    public static String replaceEmojiCodesUnicode(String text) {
        if (text == null || text.isEmpty()) return text;
        String out = text;
        for (Map.Entry<String, EmojiData> e : EMOJIS.entrySet()) {
            out = out.replace(e.getKey(), e.getValue().unicode);
        }
        return out;
    }

    private record EmojiData(String fileName, String hex, String unicode) {
    }
}
