package org.herac.tuxguitar.app.view.dialog.track;

public class TGTrackTuningPresetModel {

	private String name;
	private TGTrackTuningModel[] values;
	private short program;
	private int clef;
	private TGTrackTuningGroupEntryModel entry;

	public TGTrackTuningPresetModel() {
		super();
	}

	public TGTrackTuningPresetModel(TGTrackTuningPresetModel other) {
		this.setName(other.getName());
		this.values = new TGTrackTuningModel[other.getValues().length];
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = new TGTrackTuningModel();
			this.values[i].setValue(other.getValues()[i].getValue());
		}
		this.setProgram(other.getProgram());
		this.setClef(other.getClef());
		this.setEntry(other.entry);
	}

	public static int compare(TGTrackTuningPresetModel a, TGTrackTuningPresetModel b) {
		int lengthCompare = Integer.compare(a.values.length, b.values.length);
		if (lengthCompare != 0) {
			return lengthCompare;
		}
		for (int i = 0; i < a.values.length; i++) {
			int valueCompare = Integer.compare(a.values[i].getValue(), b.values[i].getValue());
			if (valueCompare != 0) {
				return valueCompare;
			}
		}
		return 0;
	}

	public int getClef() {
		return clef;
	}

	public void setClef(int clef) {
		this.clef = clef;
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

	public short getProgram() {
		return program;
	}

	public void setProgram(short program) {
		this.program = program;
	}
}
