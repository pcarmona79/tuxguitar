package org.herac.tuxguitar.app.view.dialog.track;

public class TGTrackTuningPresetModel {

	private String name;
	private TGTrackTuningModel[] values;
	private int program;
	private TGTrackTuningGroupEntryModel entry;

	public TGTrackTuningPresetModel() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TGTrackTuningModel[] getValues() {
		return this.values;
	}

	public void setValues(TGTrackTuningModel[] values) {
		this.values = values;
	}

	public TGTrackTuningGroupEntryModel getEntry() { return entry; }

	public void setEntry(TGTrackTuningGroupEntryModel entry) { this.entry = entry; }

	public int getProgram() {
		return program;
	}

	public void setProgram(int program) {
		this.program = program;
	}
}
