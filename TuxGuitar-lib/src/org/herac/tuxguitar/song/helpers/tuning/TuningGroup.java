package org.herac.tuxguitar.song.helpers.tuning;

import java.util.List;

import org.herac.tuxguitar.song.models.TGTuning;

public class TuningGroup {
	
	private String name;
	private List<TGTuning> tunings;
	private List<TuningGroup> groups;
	
	public TuningGroup(String name, List<TGTuning> tunings, List<TuningGroup> groups) {
		this.name = name;
		this.tunings = tunings;
		this.groups = groups;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<TGTuning> getTunings() {
		return this.tunings;
	}
	
	public List<TuningGroup> getGroups() {
		return this.groups;
	}
}
