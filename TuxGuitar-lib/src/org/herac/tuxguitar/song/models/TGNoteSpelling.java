package org.herac.tuxguitar.song.models;

public abstract class TGNoteSpelling {

	final public int ACCIDENTAL_NONE = 0;
	final public int ACCIDENTAL_SHARP = 1;
	final public int ACCIDENTAL_DOUBLESHARP = 2;
	final public int ACCIDENTAL_FLAT = -1;
	final public int ACCIDENTAL_DOUBLEFLAT = -2;
	
	// Spelling
	private int noteName; // C=0, B=6, -1 is undefined (default)
	private int accidental; // ACCIDENTAL_* values

	private int key_signature;
	private int[] roots;
	private int[] scale;
	
	// helper data
	private static int[] accidentals = { 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0 };
	private static int[] notes = { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6 };
	private static int[] semitones = { 0, 2, 4, 5, 7, 9, 11 };

	public TGNoteSpelling() {
		this.noteName = -1;
		this.accidental = 0;
		this.key_signature = -1;
	}

	public int fromString(String signature)
	{
		// ref initializeKey
		int val = 0;
		String[] keys = {"c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces"};
		for(int i = 0; i < keys.length; i++)
		{
			if (signature.toLowerCase() == keys[i])
			{
				val = i-7;
			}
		}
		
		return val;
	}
	
	private int initializeKey (int keysignature) {
		// keysignature 1 to 7 is number of sharps, 8 to 14 is (number of flats + 7)
		// rearrange so these are in order flat to sharp, with C = 7   "c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces"
		if (keysignature <= 7)
			keysignature = keysignature + 7;
		else
			keysignature = 14 - keysignature;
		
		if (this.key_signature != keysignature)
		{
			// one-time initialization in case the object is getting re-used
			roots = new int[15];
			// set the root note name for each key, to match keysignature
	
			int rootname = 0;
			for (int i = 0; i < roots.length; i++) {
				// for semitones:roots[i] = ( (i*5) + 11 ) % 12;
				roots[i] = rootname % 7;
				rootname += 4;
			}
	
			// scale represents the accidental of each note, C to B
			scale = new int[7];
			for (int i = 0; i < scale.length; i++) {
				scale[i] = ACCIDENTAL_FLAT; 
			}

			// raise the seventh until we get to the right key
			int offset = 3; // Fb -> F natural
			for ( int i = 1; i <= keysignature; i++) {
				scale[offset]++;
				offset = (offset + 4) % 7;
			}
			
			// update and return
			this.key_signature = keysignature;
		}
		return keysignature;
	}
	
	public void setSpellingFromKey(int value, int keysignature) {
		
		keysignature = initializeKey(keysignature);
		
		int key_notename = roots[keysignature];
		int key_semitone = semitones[key_notename] + scale[key_notename];
		// int key_accidental = scale[0];		
		// int interval = ((semitone + 12) - root) % 12; // distance in semitones
		int note_semitone = value % 12; // c to b in semitones
		
		//"A Mathematical Model Of Tonal Function", Robert T. Kelley, Lander University
		// http://www.robertkelleyphd.com/AMathematicalModelOfTonalFunction.pdf
		// NOTE: Mod function as described expects (-3 Mod 12 = 9) so I added an extra "+12" in here
		int note_notename = (7 * note_semitone-((((7 * ((note_semitone + 12 - key_semitone) % 12)+5) % 12)-5)+ (7 * key_semitone) - (12 * key_notename) )) / 12;

		this.setSpelling(note_notename,  scale[note_notename]);
	}

	public void setSpelling(int value) {
		int temp = value % 12;
		this.noteName = notes[temp];
		this.accidental = accidentals[temp];		
	}

	public void setSpelling(int noteName, int accidental) {
		this.noteName = noteName;
		this.accidental = accidental;
	}

	// Octave should represent MIDI octave, so setSpelling(0, 0, 4) would return 60
	public int setSpelling(int noteName, int accidental, int octave) {
		this.noteName = noteName;
		this.accidental = accidental;
		int value = (octave + 1) * 12;
		// find the first natural note this matches
		for (value = 0; value < notes.length; value++) {
			if ( noteName == notes[value] /* and accidental[value] == 0 */)
				break;
		}
		// adjust
		value += accidental;
		return value;
	}
	
	public int getNoteName() {
		return this.noteName;
	}

	public int getAccidental() {
		return this.accidental;
	}
	
	public String toLilyPondString() {
		String noteNames[] = { "c", "d", "e", "f", "g", "a", "b" };
		String result = "";
		if (noteName >= 0){
			result += noteNames[getNoteName()];
			switch(getAccidental())
			{
			case ACCIDENTAL_NONE:
				break;
			case ACCIDENTAL_SHARP:
				result += "is";
				break;
			case ACCIDENTAL_DOUBLESHARP:
				result += "isis";
				break;
			case ACCIDENTAL_FLAT:
				result += "es";
				break;
			case ACCIDENTAL_DOUBLEFLAT:
				result += "eses";
				break;
			}
			result += accidentals[getAccidental()+1];
		}
		return result;
	}
	
	public String toString() {
		String noteNames[] = { "C", "D", "E", "F", "G", "A", "B" };
		String accidentals[] =  { "b", "", "#" };
		String result = "";
		if (noteName >= 0){
			result += noteNames[getNoteName()];
			result += accidentals[getAccidental()+1];
		}
		return result;
	}
}
