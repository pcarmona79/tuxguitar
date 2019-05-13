package org.herac.tuxguitar.song.helpers.tuning;

public class TuningPreset {
    private TuningGroup parent;
    private String name;
    private int[] values;
    private int program;

    public TuningPreset(TuningGroup parent, String name, int[] values, int program) {
        this.parent = parent;
        this.name = name;
        this.values = values;
        this.program = program;
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
    public int getProgram() {
        return this.program;
    }
}
