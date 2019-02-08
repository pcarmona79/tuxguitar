package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.graphics.control.TGMeasureImpl;

/*
	"A Mathematical Model Of Tonal Function", Robert T. Kelley, Lander University
	http://www.robertkelleyphd.com/AMathematicalModelOfTonalFunction.pdf

	The following notes from Kelley describe the numerical representation:
	
	"Any tonal pitch or pitch class may be represented as an ordered triple of integers. We
	shall arbitrarily assign the ordered triple (0, 0, 0) to the pitch C4, or "middle C". The first
	component of the ordered triple represents a measurement of distance in semitones away
	from C4. Thus, the first integer in the ordered triple of the pitch D4 is 2, and F#3 is -6. The
	second integer in the ordered triple represents a distance away from C4 measured in diatonic
	steps, without regard to chromatic inflection. Thus, the ordered triples for the pitches Db4,
	D4, and D#4 all have 1 as their second component, though their first components are 1, 2,
	and 3, respectively. In other words, all "C"s have second component 0; all "D"s have second
	component 1; all "E"s, 2, and so on through "B", which has second component 6. The first
	two components of the ordered triple thus completely encapsulate the information required
	to write a pitch in music notation, including how the pitch is spelled. *"

	Brinkman (1986) uses this ordered pair notation for the computer representation of the diatonic 
	spelling of a pitch 

	That did not seem to work.  Replacing with ideas from MuseScore,GPL v2
	https://github.com/musescore/MuseScore/blob/master/libmscore/pitchspelling.cpp
	https://musescore.org/en/plugin-development/tonal-pitch-class-enum
	
	With the comment:
    This file contains the implementation of an pitch spelling
    algorithmus from Emilios Cambouropoulos as published in:
    "Automatic Pitch Spelling: From Numbers to Sharps and Flats"
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
	
	// when this logic is debugged
	private static int[] accidentals = { 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0 };
	private static int[] semitones = { 0, 2, 4, 5, 7, 9, 11 };

	private static String[] keys = {
			// TuxGuitar order
			//"c","g","d","a","e","b","fis","cis",//"gis","dis","ais","eis","bis"};
			//"f","bes","ees","aes","des","ges","ces"};
			// this order / MuseScore, note G# and later won't be used
			"ces","ges","des","aes","ees","bes","f",
			"c","g","d","a","e","b","fis",
			"cis","gis","dis","ais","eis","bis"
	};
	
	public TGNoteSpelling() {
		this.pitchNumber = -1; // undefined
		this.accidental = 0; // none
		this.keySignature = -1; // undefined
		this.octave = -1;
		this.midiValue = -1;

		if (roots == null)
		{
			// set the root note name for each key, to match keysignature
			// aka diatonic distance from C
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
		// handle signatures not valid for TuxGuitar
		switch(signature)
		{
		case "fes":
			signature = "e";
			break;
		case "gis":
			signature = "aes";
			break;
		case "dis":
			signature = "ees";
			break;
		case "ais":
			signature = "ges";
			break;
		case "eis":
			signature = "f";
			break;
		case "bis":
			signature = "c";
			break;
		}
		
		int keysignature = 0; // default: C major
		for(int i = 0; i < keys.length; i++)
		{
			if (signature.equalsIgnoreCase(keys[i]))
			{
				keysignature = i - 7;
				break;
			}
		}
		
		initializeKey(keysignature);
		return keysignature; // return translated value
	}
	
	private int initializeKey (int keysignature) {
		// TuxGuitar keysignature 1 to 7 is number of sharps, 8 to 14 is (number of flats + 7)
		// rearrange so these are in order Cb=-7, C=0, C#=7
		// Tuxguitar leaves out G#, D#, A#, E#, B#, and Fb 
		// but you can do enharmonic spellings to get all of them
		if (keysignature <= 7)
			keysignature = keysignature + 7;
		else
			keysignature = 14 - keysignature;
		
		keysignature -= 7; // MuseScore
		
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
			for ( int i = -7; i < keysignature; i++) {
				scale[offset]++; // ACCIDENTAL_FLAT goes to ACCIDENTAL_NONE, which goes to ACCIDENTAL_SHARP
				offset = (offset + 4) % 7;
			}
			
			// update and return
			this.keySignature = keysignature;
		}
		return keysignature;
	}
	
	// MuseScore:
	// enum class Prefer : char { FLATS=8, NEAREST=11, SHARPS=13 };
	// int pitch2tpc(int pitch, Key key, Prefer prefer)
    // {
	//	return (pitch * 7 + 26 - (int(prefer) + int(key))) % 12 + (int(prefer) + int(key));
	// }
	// int tpc2step(int tpc)
	//     {
	//     // 14 - C
	//     // 15 % 7 = 1
    //                                            f  c  g  d  a  e  b
	//     static const int steps[STEP_DELTA_OCTAVE] = { 3, 0, 4, 1, 5, 2, 6 };
    // 	TODO: optimize -TCP_MIN
	//     return steps[(tpc-Tpc::TPC_MIN) % STEP_DELTA_OCTAVE];
    //	without a table, could also be rendered as:
    //    return ((tpc-Tpc::TPC_MIN) * STEP_DELTA_TPC) / STEP_DELTA_OCTAVE + TPC_FIRST_STEP;
	//     }
	
	public void setSpellingFromKey(int midiValue, int keysignature/*, int prefer*/) {
		
		int prefer = 11; // Prefer.NEAREST
		final int steps[] = { 3, 0, 4, 1, 5, 2, 6 };
		
		// TuxGuitar key signature is translated to fit this logic
		keysignature = initializeKey(keysignature);
	
		int newNoteSemitone = midiValue % 12;
		// octave s/b -1, but TG stores the sounding note, not written
		int thisOctave = (midiValue - newNoteSemitone)/12; 
		int tpc = (midiValue * 7 + 26 - (prefer + keysignature)) % 12 + (prefer + keysignature);
		int newPitchNumber = steps[(tpc+1) % 7];
		this.setSpelling(newPitchNumber,  scale[newPitchNumber], thisOctave);
	}

	public void setSpelling(int midiValue) {
		int temp = midiValue % 12;
		
		int[] notes;
		if (this.keySignature >= 0) // translated above
			notes = TGMeasureImpl.ACCIDENTAL_SHARP_NOTES;
		else
			notes = TGMeasureImpl.ACCIDENTAL_FLAT_NOTES;
		
		this.pitchNumber = notes[temp];
		this.accidental = accidentals[temp];
		this.midiValue = midiValue;
		this.octave = (midiValue - temp)/12 - 1;
	}

	// Octave should represent MIDI octave, so setSpelling(0, 0, 4) would return 60
	public int setSpelling(int pitchNumber, int accidental, int octave) {
		this.pitchNumber = pitchNumber;
		this.accidental = accidental;
		this.octave = octave;
		
		int[] notes;
		if (this.keySignature >= 0) // translated above
			notes = TGMeasureImpl.ACCIDENTAL_SHARP_NOTES;
		else
			notes = TGMeasureImpl.ACCIDENTAL_FLAT_NOTES;

		// TODO: calculate midi value.  Is that what this is supposed to do?
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
	
	// TODO: move this into lilypond output, "fromSpelling()" or something like that.
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
		} else {
			// TODO: fallback logic, similar to getLilypondKey()
		}
		
		if (this.midiValue>= 0)
		{
			// logic from getLilypondKey()
			for(int i = 4; i < (this.midiValue / 12); i ++){
				result += ("'");
			}
			for(int i = (this.midiValue / 12); i < 4; i ++){
				result += (",");
			}
		}
		else if(this.octave >= 0)
		{
			int t = this.octave;
			while (t > 4) {
				result += ("'");
				t--;
			}
			while (t < 4) {
				result += (",");
				t++;
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
