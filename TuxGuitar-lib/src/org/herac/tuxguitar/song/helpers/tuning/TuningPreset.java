package org.herac.tuxguitar.song.helpers.tuning;

public class TuningPreset {
    private TuningGroup parent;
    private String name;
    private int[] values;
    private short program;
    private int clef;
    private int frets;

    public TuningPreset(TuningGroup parent, String name, int[] values, short program, int clef, int frets) {
        this.parent = parent;
        this.name = name;
        this.values = values;
        this.program = program;
        this.clef = clef;
        this.frets = frets;
    }
    public TuningGroup getParent() {
        return this.parent;
    }
    public String getName() {
        return this.name;
    }
    public int[] getValues() {
        return this.values;
    }
    public short getProgram() {
        return this.program;
    }
    public int getClef() {
        return this.clef;
    }
    public int getFrets() {
        return this.frets;
    }
}
