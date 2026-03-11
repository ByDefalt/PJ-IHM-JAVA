package com.ubo.tp.message.utils;

import java.util.Map;

public class EmojiBinders {
    // URL stable vers Google Noto (via jsDelivr)
    private static final String NOTO_BASE_URL = "https://cdn.jsdelivr.net/gh/googlefonts/noto-emoji@main/png/128/emoji_u";

    private static final Map<String, EmojiData> EMOJIS = new java.util.LinkedHashMap<>();

    static {
        // Le format attendu par Google est très strict (minuscules, sans zéros inutiles ou avec suffixes spécifiques)
        EMOJIS.put(":smile:", new EmojiData("smile.png", "1f60a", "😊"));

        // CORRECTIONS :
        // Pour Noto, 'sad' et 'heart' nécessitent souvent le suffixe de variante fe0f
        EMOJIS.put(":sad:", new EmojiData("sad.png", "2639_fe0f", "☹️"));
        EMOJIS.put(":heart:", new EmojiData("heart.png", "2764_fe0f", "❤️"));

        EMOJIS.put(":thumbs_up:", new EmojiData("thumbs_up.png", "1f44d", "👍"));
        EMOJIS.put(":thumbs_down:", new EmojiData("thumbs_down.png", "1f44e", "👎"));
        EMOJIS.put(":laughing:", new EmojiData("laughing.png", "1f606", "😆"));
        EMOJIS.put(":crying:", new EmojiData("crying.png", "1f62d", "😭"));
        EMOJIS.put(":angry:", new EmojiData("angry.png", "1f620", "😠"));
        EMOJIS.put(":surprised:", new EmojiData("surprised.png", "1f632", "😲"));
    }

    private EmojiBinders() {}

    public static String[] getSupportedCodes() {
        return EMOJIS.keySet().toArray(new String[0]);
    }

    public static String getEmojiImageUrl(String code) {
        if (code == null) return null;
        EmojiData data = EMOJIS.get(code);
        if (data == null) return null;

        // 1. Vérification locale
        if (data.fileName != null) {
            java.net.URL res = EmojiBinders.class.getResource("/images/emoji/" + data.fileName);
            if (res != null) return res.toExternalForm();
        }

        // 2. Fallback Google Noto (Doit être en minuscules)
        if (data.hex != null) {
            return NOTO_BASE_URL + data.hex.toLowerCase() + ".png";
        }
        return null;
    }

    public static String replaceEmojiCodesUnicode(String text) {
        if (text == null || text.isEmpty()) return text;
        String out = text;
        for (Map.Entry<String, EmojiData> e : EMOJIS.entrySet()) {
            out = out.replace(e.getKey(), e.getValue().unicode);
        }
        return out;
    }

    private record EmojiData(String fileName, String hex, String unicode) {}
}