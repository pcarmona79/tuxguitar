/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGNote {
	// MIDI value
	private int value;
	private int velocity;
	private int string;
	private boolean tiedNote;
	private TGNoteEffect effect;
	private TGVoice voice;
	private TGNoteSpelling spelling;
	
	public TGNote(TGFactory factory) {
		this.value = 0;
		this.spelling = factory.newNoteSpelling();
		this.velocity = TGVelocities.DEFAULT;
		this.string = 1;
		this.tiedNote = false;
		this.effect = factory.newEffect();
	}
	
	public void setSpelling(TGNoteSpelling spelling) {
		this.spelling = spelling;
	}

	public TGNoteSpelling getSpelling() {
		// if this has never been set, initialize with default setting
		if (spelling.getPitchNumber() < 0 && this.getVoice() != null)
		{
			try {
				int keySignature = this.getVoice().getBeat().getMeasure().getKeySignature();
				int midiValue = (this.getVoice().getBeat().getMeasure().getTrack().getString(this.getString()).getValue() + this.getValue()); 
				
				if (keySignature == 0)
					spelling.setSpelling(midiValue);
				else 
					spelling.setSpellingFromKey(midiValue,  keySignature);
			} catch(Exception e) {
				// do nothing
			}
		}
		return this.spelling;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getVelocity() {
		return this.velocity;
	}
	
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	
	public int getString() {
		return this.string;
	}
	
	public void setString(int string) {
		this.string = string;
	}
	
	public boolean isTiedNote() {
		return this.tiedNote;
	}
	
	public void setTiedNote(boolean tiedNote) {
		this.tiedNote = tiedNote;
	}
	
	public TGNoteEffect getEffect() {
		return this.effect;
	}
	
	public void setEffect(TGNoteEffect effect) {
		this.effect = effect;
	}
	
	public TGVoice getVoice() {
		return this.voice;
	}
	
	public void setVoice(TGVoice voice) {
		this.voice = voice;
	}
	
	public TGNote clone(TGFactory factory){
		TGNote note = factory.newNote();
		note.setValue(getValue());
		note.setVelocity(getVelocity());
		note.setString(getString());
		note.setTiedNote(isTiedNote());
		note.setEffect(getEffect().clone(factory));
		return note;
	}
}