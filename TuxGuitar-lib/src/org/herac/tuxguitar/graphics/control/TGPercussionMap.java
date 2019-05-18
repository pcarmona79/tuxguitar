package org.herac.tuxguitar.graphics.control;

/**
 * The goal of this class is to provide a configurable interface to drum
 * rendering mapping of rendering flags and notes.
 * 
 * @author simpoir@gmail.com
 */
public class TGPercussionMap {
	static final int MAX_NOTES = 90;
	
	static final int MAP_VALUE = 0;
	static final int MAP_KIND  = 1;
	static final int MAP_LEN   = 2;

    public static final int KIND_CYMBAL = 1; // X note
    public static final int KIND_NOTE   = 2; // round and black
    public static final int KIND_OPEN   = 4; // small o above
    public static final int KIND_CLOSED = 8; // small + above
	
    // note#, [render note#, render flags]
    private int[][] mapping = new int[MAX_NOTES][MAP_LEN];
    
    // TODO discuss this (and refactor)
    // singleton. I'm not sure if it's the best behaviour, as it would
    // be nicer to have per song drum mapping, but for now global config it is.
	private static TGPercussionMap instance;
	
	// The note to return when mapping is undefined
	private int defaultNote;
    
    /**
     * Fetches the current drum mapping or create one from default settings.
     * 
     * As there is currently no loading or saving of this, it always returns
     * the default hardcoded mapping.
     * @return an existing TGDrumMap or a new if none exists
     */
    public static TGPercussionMap getCurrentDrumMap() {
    	if (null == instance) {
    		instance = new TGPercussionMap();
    	}
    	return instance;
    }
    public TGPercussionMap() {
    	
    	loadDefaultMapping();
	}
    
    // TODO support dumping as pref and reloading drum maps

    private void loadDefaultMapping() {
    	defaultNote = 0;
    	
    	// see http://en.wikipedia.org/wiki/Percussion_notation
    	mapping[35] = new int[] {53, KIND_NOTE}; // accoustic bass drum
    	mapping[36] = new int[] {53, KIND_NOTE}; // bass drum
    	mapping[38] = new int[] {60, KIND_NOTE}; // accoustic snare
    	mapping[40] = new int[] {60, KIND_NOTE}; // electric snare
    	mapping[42] = new int[] {68, KIND_CYMBAL|KIND_CLOSED}; // closed high hat
    	mapping[46] = new int[] {68, KIND_CYMBAL|KIND_OPEN}; // open high hat
    	mapping[49] = new int[] {69, KIND_CYMBAL}; // crash cymbal
    	mapping[57] = new int[] {69, KIND_CYMBAL}; // crash cymbal 2
    	mapping[51] = new int[] {66, KIND_CYMBAL}; // ride cymbal
    	mapping[59] = new int[] {66, KIND_CYMBAL}; // ride cymbal 2
    	mapping[55] = new int[] {71, KIND_CYMBAL}; // splash cymbal
    	mapping[41] = new int[] {58, KIND_NOTE}; // low floor tom
    	mapping[43] = new int[] {58, KIND_NOTE}; // high floor tom
    	mapping[45] = new int[] {62, KIND_NOTE}; // low tom
    	mapping[48] = new int[] {62, KIND_NOTE}; // hi med tom
    	mapping[50] = new int[] {64, KIND_NOTE}; // high tom
    }

    /**
     * Returns the drum mapped equivalent of value, or value if not defined.
     * @param value a note value to be transposed
     * @return the numeric value of the transposed note.
     */
	public int transposeDrum(int value) {
		// FIXME if mapping undefined return the default position
		int[] transposed = mapping[value];
		
		return (transposed != null? transposed[MAP_VALUE]: defaultNote);
	}
	public int getRenderType(int value) {
		int[] transposed = mapping[value];
		
		return (transposed != null? transposed[MAP_KIND]: KIND_NOTE);
	}
}

