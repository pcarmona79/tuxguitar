package org.herac.tuxguitar.song.models;
/*
	"A Mathematical Model Of Tonal Function", Robert T. Kelley, Lander University
	http://www.robertkelleyphd.com/AMathematicalModelOfTonalFunction.pdf
	
	Any tonal pitch or pitch class may be represented as an ordered triple of integers. We
	shall arbitrarily assign the ordered triple (0, 0, 0) to the pitch C4, or "middle C". The first
	component of the ordered triple represents a measurement of distance in semitones away
	from C4. Thus, the first integer in the ordered triple of the pitch D4 is 2, and F#3 is -6. The
	second integer in the ordered triple represents a distance away from C4 measured in diatonic
	steps, without regard to chromatic inflection. Thus, the ordered triples for the pitches Db4,
	D4, and D#4 all have 1 as their second component, though their first components are 1, 2,
	and 3, respectively. In other words, all "C"s have second component 0; all "D"s have second
	component 1; all "E"s, 2, and so on through "B", which has second component 6. The first
	two components of the ordered triple thus completely encapsulate the information required
	to write a pitch in music notation, including how the pitch is spelled. *

	Brinkman (1986) uses this ordered pair notation for the computer representation of the diatonic 
	spelling of a pitch 
 */
public abstract class TGNoteSpelling {

	final public int ACCIDENTAL_NONE = 0;
	final public int ACCIDENTAL_SHARP = 1;
	final public int ACCIDENTAL_DOUBLESHARP = 2;
	final public int ACCIDENTAL_FLAT = -1;
	final public int ACCIDENTAL_DOUBLEFLAT = -2;
	
	// Spelling
	private int pitchNumber;   // C=0, B=6, -1 is undefined (default)
	private int accidental; // ACCIDENTAL_* values
	private int octave;     // octave 4 = C4 = midi note 60
	private int midiValue;

	private int keySignature; // translated
	private static int[] roots;
	private int[] scale;
	
	// helper data TODO: leverage TGMeasureImpl.ACCIDENTAL_SHARP_NOTES / ACCIDENTAL_FLAT_NOTES
	// when this logic is debugged
	private static int[] accidentals = { 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0 };
	private static int[] notes = { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6 };
	private static int[] semitones = { 0, 2, 4, 5, 7, 9, 11 };

	public TGNoteSpelling() {
		this.pitchNumber = -1; // undefined
		this.accidental = 0; // none
		this.keySignature = -1; // undefined
		this.octave = 0;

		if (roots == null)
		{
			// set the root note name for each key, to match keysignature
			// e.g. key signature 0 is Cb, 1 is Gb so root 0 is C, root 1 is G
			roots= new int[15];
			int rootname = 0;
			for (int i = 0; i < roots.length; i++) {
				roots[i] = rootname % 7;
				rootname += 4;
			}
		}
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
				// TODO fix this
				val = i-7;
			}
		}
		
		return val;
	}
	
	private int initializeKey (int keysignature) {
		// TuxGuitar keysignature 1 to 7 is number of sharps, 8 to 14 is (number of flats + 7)
		// rearrange so these are in order Cb=0, C=7, C#=14 
		if (keysignature <= 7)
			keysignature = keysignature + 7;
		else
			keysignature = 14 - keysignature;
		
		if (this.keySignature != keysignature)
		{
			// scale represents the accidental of each note, C to B, initialize as all flat
			scale = new int[7];
			for (int i = 0; i < scale.length; i++) {
				scale[i] = ACCIDENTAL_FLAT;
			}

			// raise the seventh until we get to the right key
			// starting with Fb -> F natural to go from Cb to Gb
			int offset = 3; 
			for ( int i = 1; i <= keysignature; i++) {
				scale[offset]++; // ACCIDENTAL_FLAT goes to ACCIDENTAL_NONE, which goes to ACCIDENTAL_SHARP
				offset = (offset + 4) % 7;
			}
			
			// update and return
			this.keySignature = keysignature;
		}
		return keysignature;
	}
	
	public void setSpellingFromKey(int midiValue, int keysignature) {
		
		// TuxGuitar key signature is translated to fit this logic
		keysignature = initializeKey(keysignature);
		
		int keyPitchNumber = roots[keysignature];
		//int keySemitone = semitones[keyPitchNumber] + scale[keyPitchNumber];
		int keySemitone = semitones[keyPitchNumber] + scale[keyPitchNumber];
		int newNoteSemitone = midiValue % 12; // c to b in semitones
		int t1 = 0; // TODO:
		
		// int key_accidental = scale[0];
		// int interval = ((semitone + 12) - root) % 12; // distance in semitones
		
		// NOTE: Mod function as described expects (-3 Mod 12 = 9) so I added an extra "+12" in here

		// Kelley: Because the ordered pair for the tonic, Bb, is (10, 6), we shall use the value
		// 10 in place of the variable t1, and the value 6 in place of the variable t2. Then the
		// value 1 replaces the variable a. After solving the equation using a little bit of
		// arithmetic, the result is the ordered pair (1, 1), meaning that the pitch class 1
		// is to be spelled as Db rather than C#

		// (7·a-((((7·((a-t1) mod 12)+5) mod 12)-5)+7·t1-12·t2)) / 12
		int newPitchNumber = (7*newNoteSemitone-((((7*((newNoteSemitone-t1) % 12)+5) % 12)-5)+7*t1-12*keyPitchNumber)) / 12;
		
		this.setSpelling(newPitchNumber,  scale[newPitchNumber]);
	}

	public void setSpelling(int value) {
		int temp = value % 12;
		this.pitchNumber = notes[temp];
		this.accidental = accidentals[temp];
		this.midiValue = value;
		this.octave = (value - temp)/12 - 1;
	}

	public void setSpelling(int pitchNumber, int accidental) {
		this.pitchNumber = pitchNumber;
		this.accidental = accidental;
	}

	// Octave should represent MIDI octave, so setSpelling(0, 0, 4) would return 60
	public int setSpelling(int pitchNumber, int accidental, int octave) {
		this.pitchNumber = pitchNumber;
		this.accidental = accidental;
		this.octave = octave;
		int value = (octave + 1) * 12;
		// find the first natural note this matches
		for (value = 0; value < notes.length; value++) {
			if ( pitchNumber == notes[value] /* and accidental[value] == 0 */)
				break;
		}
		// adjust
		value += accidental;
		return value;
	}
	
	public int getPitchNumber() {
		return this.pitchNumber;
	}

	public int getAccidental() {
		return this.accidental;
	}
	
	public String toLilyPondString() {
		String noteNames[] = { "c", "d", "e", "f", "g", "a", "b" };
		String result = "";
		if (pitchNumber >= 0){
			result += noteNames[getPitchNumber()];
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
		}
		return result;
	}
	
	public String toString() {
		String noteNames[] = { "C", "D", "E", "F", "G", "A", "B" };
		String accidentals[] =  { "b", "", "#" };
		String result = "";
		if (pitchNumber >= 0){
			result += noteNames[getPitchNumber()];
			result += accidentals[getAccidental()+1];
		}
		return result;
	}
}
