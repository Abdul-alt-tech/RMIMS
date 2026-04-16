package com.nalex.rmims.gui;

import javax.swing.*;
import java.awt.*;

public class UITheme {
    public static final Color PRIMARY = new Color(52, 152, 219); // blue
    public static final Color SUCCESS = new Color(46, 204, 113); // green
    public static final Color DANGER = new Color(231, 76, 60);   // red
    public static final Color ACCENT = new Color(155, 89, 182);   // purple
    public static final Color NEUTRAL = new Color(240, 240, 240); // light gray
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 13);

    public static void styleButton(JButton btn, String variant) {
        // Prefer an emoji-capable font at the same size/style when available
        btn.setFont(getEmojiCapableFont(BUTTON_FONT.getSize2D(), BUTTON_FONT.getStyle()));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(btn.getPreferredSize());

        switch (variant == null ? "primary" : variant.toLowerCase()) {
            case "success":
                btn.setBackground(SUCCESS);
                btn.setForeground(TEXT_ON_PRIMARY);
                break;
            case "danger":
                btn.setBackground(DANGER);
                btn.setForeground(TEXT_ON_PRIMARY);
                break;
            case "accent":
                btn.setBackground(ACCENT);
                btn.setForeground(TEXT_ON_PRIMARY);
                break;
            case "neutral":
                btn.setBackground(NEUTRAL);
                btn.setForeground(Color.DARK_GRAY);
                btn.setBorderPainted(true);
                break;
            default:
                btn.setBackground(PRIMARY);
                btn.setForeground(TEXT_ON_PRIMARY);
                break;
        }
    }

    private static Font getEmojiCapableFont(float size, int style) {
        String os = System.getProperty("os.name").toLowerCase();
        Font candidate = null;

        if (os.contains("win")) {
            candidate = new Font("Segoe UI Emoji", style, (int) size);
        } else if (os.contains("mac")) {
            candidate = new Font("Apple Color Emoji", style, (int) size);
        } else {
            candidate = new Font("Noto Color Emoji", style, (int) size);
        }

        if (candidate != null && candidate.canDisplayUpTo("📤") == -1) {
            return candidate.deriveFont(style, size);
        }

        return BUTTON_FONT.deriveFont(style, size);
    }

    /**
     * Public helper to obtain an emoji-capable font at the requested size.
     * Falls back to the theme button font when no emoji font is available.
     */
    public static Font emojiFont(float size) {
        return getEmojiCapableFont(size, Font.PLAIN);
    }
}
