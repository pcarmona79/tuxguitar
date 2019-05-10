package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.util.NoSuchElementException;

public abstract class TGScale {
    public static final int NOTE_COUNT = 12;
    private int notes = 0;
    private int key = 0;

    public boolean getNote(int note) {
        return (this.notes & (1 << ((note + (NOTE_COUNT - this.key)) % NOTE_COUNT))) != 0;
    }

    public int getNotes() {
        return this.notes;
    }

    public void setNotes(int notes) {
        this.notes = notes & ((1 << NOTE_COUNT) - 1);

    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setNote(int note, boolean on) {
        if (note < 0 || note >= NOTE_COUNT) {
            throw new NoSuchElementException();
        }
        if (on) {
            this.notes |= (1 << note);
        } else {
            this.notes &= ~(1 << note);
        }
    }

    public void clear() {
        this.setKey(0);
        this.notes = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TGScale tgScale = (TGScale) o;
        return notes == tgScale.notes &&
                key == tgScale.key;
    }

    public TGScale clone(TGFactory factory) {
        TGScale scale = factory.newScale();
        scale.notes = notes;
        scale.setKey(this.key);
        return scale;
    }
}
