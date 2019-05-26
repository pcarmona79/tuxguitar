package org.herac.tuxguitar.io.gpx.score;

public class GPXBeat {

	private int id;
	private int rhythmId;
	private int[] noteIds;
	private String dynamic;
	private boolean slapped;
	private boolean popped;
	private boolean vibrato;
	private String brush;    // up stroke, down stroke
 	private int brushDuration;
	private int[] tremolo; // 1/8, 1/4, etc.
	private String fading;
	private String text;
	private String grace;
	private Integer chordId;

	private boolean whammyBarEnabled;
	private Integer whammyBarOriginValue;
	private Integer whammyBarMiddleValue;
	private Integer whammyBarDestinationValue;
	private Integer whammyBarOriginOffset;
	private Integer whammyBarMiddleOffset1;
	private Integer whammyBarMiddleOffset2;
	private Integer whammyBarDestinationOffset;

	public GPXBeat() {
		this.slapped = false;
		this.popped = false;
		this.tremolo = null;
		this.brush = new String();
		this.brushDuration = 0;
		this.text = new String();
		this.tremolo = null;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRhythmId() {
		return this.rhythmId;
	}

	public void setRhythmId(int rhythmId) {
		this.rhythmId = rhythmId;
	}

	public int[] getNoteIds() {
		return this.noteIds;
	}

	public void setNoteIds(int[] noteIds) {
		this.noteIds = noteIds;
	}

	public String getDynamic() {
		return this.dynamic;
	}

	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isSlapped() {
		return slapped;
	}

	public void setSlapped(boolean slapped) {
		this.slapped = slapped;
	}

	public boolean isPopped() {
		return popped;
	}

	public void setPopped(boolean popped) {
		this.popped = popped;
	}

	public String getBrush() {
		return brush;
	}

	public void setBrush(String brush) {
		this.brush = brush;
	}

	public int getBrushDuration() {
		return brushDuration;
	}

	public void setBrushDuration(int brushDuration) {
		this.brushDuration = brushDuration;
	}

	public int[] getTremolo() {
		return tremolo;
	}

	public void setTremolo(int[] tremolo) {
		this.tremolo = tremolo;
	}
	
	public boolean isVibrato() {
		return vibrato;
	}

	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
	}

	public String getFading() {
		return fading;
	}

	public void setFading(String fading) {
		this.fading = fading;
	}

	public String getGrace() {
		return grace;
	}

	public void setGrace(String grace) {
		this.grace = grace;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getChordId() {
		return chordId;
	}

	public void setChordId(Integer chordId) {
		this.chordId = chordId;
	}

	public boolean isWhammyBarEnabled() {
		return whammyBarEnabled;
	}

	public void setWhammyBarEnabled(boolean whammyBarEnabled) {
		this.whammyBarEnabled = whammyBarEnabled;
	}

	public Integer getWhammyBarOriginValue() {
		return whammyBarOriginValue;
	}

	public void setWhammyBarOriginValue(Integer whammyBarOriginValue) {
		this.whammyBarOriginValue = whammyBarOriginValue;
	}

	public Integer getWhammyBarMiddleValue() {
		return whammyBarMiddleValue;
	}

	public void setWhammyBarMiddleValue(Integer whammyBarMiddleValue) {
		this.whammyBarMiddleValue = whammyBarMiddleValue;
	}

	public Integer getWhammyBarDestinationValue() {
		return whammyBarDestinationValue;
	}

	public void setWhammyBarDestinationValue(Integer whammyBarDestinationValue) {
		this.whammyBarDestinationValue = whammyBarDestinationValue;
	}

	public Integer getWhammyBarOriginOffset() {
		return whammyBarOriginOffset;
	}

	public void setWhammyBarOriginOffset(Integer whammyBarOriginOffset) {
		this.whammyBarOriginOffset = whammyBarOriginOffset;
	}

	public Integer getWhammyBarMiddleOffset1() {
		return whammyBarMiddleOffset1;
	}

	public void setWhammyBarMiddleOffset1(Integer whammyBarMiddleOffset1) {
		this.whammyBarMiddleOffset1 = whammyBarMiddleOffset1;
	}

	public Integer getWhammyBarMiddleOffset2() {
		return whammyBarMiddleOffset2;
	}

	public void setWhammyBarMiddleOffset2(Integer whammyBarMiddleOffset2) {
		this.whammyBarMiddleOffset2 = whammyBarMiddleOffset2;
	}

	public Integer getWhammyBarDestinationOffset() {
		return whammyBarDestinationOffset;
	}

	public void setWhammyBarDestinationOffset(Integer whammyBarDestinationOffset) {
		this.whammyBarDestinationOffset = whammyBarDestinationOffset;
	}
}
