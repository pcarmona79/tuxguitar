package org.herac.tuxguitar.io.ptb.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PTBeat implements PTComponent{
	
	private int staff;
	private int voice;
	private int multiBarRest;
	private int duration;
	private int enters;
	private int times;
	private boolean dotted;
	private boolean doubleDotted;
	private boolean vibrato;
	private boolean grace;
	private boolean arpeggioUp;
	private boolean arpeggioDown;
	private boolean palmMute;
	private boolean tap;
	private boolean letRing;
	private List<PTNote> notes;
	
	public PTBeat(int staff,int voice){
		this.staff = staff;
		this.voice = voice;
		this.notes = new ArrayList<PTNote>();
		this.multiBarRest = 1;
	}
	
	public int getStaff() {
		return this.staff;
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	public void addNote(PTNote note){
		this.notes.add(note);
	}
	
	public List<PTNote> getNotes(){
		return this.notes;
	}
	
	public boolean isGrace() {
		return this.grace;
	}
	
	public void setGrace(boolean grace) {
		this.grace = grace;
	}
	
	public boolean isVibrato() {
		return this.vibrato;
	}
	
	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
	}
	
	public int getMultiBarRest() {
		return this.multiBarRest;
	}
	
	public void setMultiBarRest(int multiBarRest) {
		this.multiBarRest = multiBarRest;
	}
	
	public boolean isDotted() {
		return this.dotted;
	}
	
	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}
	
	public boolean isDoubleDotted() {
		return this.doubleDotted;
	}
	
	public void setDoubleDotted(boolean doubleDotted) {
		this.doubleDotted = doubleDotted;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getEnters() {
		return this.enters;
	}
	
	public void setEnters(int enters) {
		this.enters = enters;
	}
	
	public int getTimes() {
		return this.times;
	}
	
	public void setTimes(int times) {
		this.times = times;
	}
	
	public boolean isArpeggioUp() {
		return this.arpeggioUp;
	}
	
	public void setArpeggioUp(boolean arpeggioUp) {
		this.arpeggioUp = arpeggioUp;
	}
	
	public boolean isArpeggioDown() {
		return this.arpeggioDown;
	}
	
	public void setArpeggioDown(boolean arpeggioDown) {
		this.arpeggioDown = arpeggioDown;
	}
	
	public boolean isPalmMute() {
		return this.palmMute;
	}
	
	public void setPalmMute(boolean palmMute) {
		this.palmMute = palmMute;
	}
	
	public boolean isTap() {
		return this.tap;
	}
	
	public void setTap(boolean tap) {
		this.tap = tap;
	}
	
	public boolean isLetRing() {
		return this.letRing;
	}
	
	public void setLetRing(boolean letRing) {
		this.letRing = letRing;
	}
	
	public PTComponent getClone(){
		PTBeat beat = new PTBeat( getStaff(), getVoice() );
		beat.setDuration( getDuration() );
		beat.setDotted( isDotted() );
		beat.setDoubleDotted( isDoubleDotted() );
		beat.setTimes( getTimes() );
		beat.setEnters( getEnters() );
		beat.setMultiBarRest( getMultiBarRest() );
		beat.setGrace( isGrace() );
		beat.setVibrato( isVibrato() );
		beat.setArpeggioUp( isArpeggioUp() );
		beat.setArpeggioDown( isArpeggioDown() );
		beat.setPalmMute( isPalmMute() );
		beat.setTap( isTap() );
		beat.setLetRing( isLetRing() );
		Iterator<PTNote> it = getNotes().iterator();
		while( it.hasNext() ){
			beat.addNote( ((PTNote)it.next()).getClone() );
		}
		return beat;
	}
}