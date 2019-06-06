package org.herac.tuxguitar.app.tools.percussion;

public class PercussionEntry {
    private String name;
    private int position;
    private int kind;
    private boolean shown;

    public PercussionEntry(String name, int position, int kind, boolean shown) {
        this.name = name;
        this.position = position;
        this.kind = kind;
        this.shown = shown;
    }

    public PercussionEntry(PercussionEntry other) {
        this(other.getName(), other.getPosition(), other.getKind(), other.isShown());
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
