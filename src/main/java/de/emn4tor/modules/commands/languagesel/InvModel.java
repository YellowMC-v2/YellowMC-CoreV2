package de.emn4tor.modules.commands.languagesel;

import java.util.Locale;

public class InvModel {
    private final int id;
    private final Locale locale;
    private final String glyph;

    public InvModel(int id, Locale locale, String glyph) {
        this.id = id;
        this.locale = locale;
        this.glyph = glyph;
    }

    public int getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getGlyph() {
        return glyph;
    }
}