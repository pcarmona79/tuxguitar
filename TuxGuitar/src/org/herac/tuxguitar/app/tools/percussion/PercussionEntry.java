package org.herac.tuxguitar.app.tools.percussion;

import org.herac.tuxguitar.graphics.control.TGPercussionNote;

public class PercussionEntry {
    private String name;
    private int note;
    private int kind;
    private boolean shown;

    public PercussionEntry(String name, int note, int kind, boolean shown) {
        this.name = name;
        this.note = note;
        this.kind = kind;
        this.shown = shown;
    }

    public PercussionEntry(String name, TGPercussionNote note, boolean shown) {
        this(name, note.getNote(), note.getKind(), shown);
    }

    public PercussionEntry(PercussionEntry other) {
        this(other.getName(), other.getNote(), other.getKind(), other.isShown());
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
