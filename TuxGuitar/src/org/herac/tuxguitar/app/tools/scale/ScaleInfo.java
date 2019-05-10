package org.herac.tuxguitar.app.tools.scale;

public class ScaleInfo {

	private String name;
	private int keys;
	
	public ScaleInfo(String name, int keys) {
		this.name = name;
		this.keys = keys;
	}
	
	public int getKeys() {
		return this.keys;
	}
	
	public String getName() {
		return this.name;
	}
}
