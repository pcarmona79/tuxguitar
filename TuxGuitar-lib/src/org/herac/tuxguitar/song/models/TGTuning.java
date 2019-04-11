package org.herac.tuxguitar.song.models;

public class TGTuning {
	private String name;
	private int[] values;

	public TGTuning(String name, int[] values) {
		this.name = name;
		this.values = values;
	}
	public String getName() {
		return this.name;
	}
	public int[] getValues() {
		return this.values;
	}
}
