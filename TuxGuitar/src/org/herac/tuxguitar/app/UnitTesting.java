package org.herac.tuxguitar.app;

import java.util.Objects;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNoteSpelling;

public class UnitTesting {

	private static String[] tuxGuitarKeys = {
			// TuxGuitar order
			"c","g","d","a","e","b","fis","cis",//"gis","dis","ais","eis","bis"};
			"f","bes","ees","aes","des","ges","ces"};
	
	private static int[] expectedResults = {
			0, 1, 2, 3, 4, 5, 6, 7, -1, -2, -3, -4, -5, -6, -7
	};
	
	private static String[] expectedNotes = {
			// gis is the result for key of C, likely because in A minor G# is the harmonic 7th
			// otherwise favor sharps with a key signature of sharps, flats for flats.
			"gis'", "gis'", "gis'", "gis'", "gis'", "gis'", "gis'", "gis'",
			"aes'", "aes'", "aes'", "aes'", "aes'", "aes'", "aes'"
	};
	
	public static void main(String[] args){
		boolean noException = true;
		
		try {
			// test set key from string
			TGNoteSpelling spelling = new TGFactory().newNoteSpelling();
			for(int i = 0; i < tuxGuitarKeys.length; i++) {
				int keyValue = spelling.fromString(tuxGuitarKeys[i]);
				if (keyValue != expectedResults[i])
					throw new Exception();
			}
			
			//
			int midiNote = 68; // Ab/B#
			for(int i = 1; i < tuxGuitarKeys.length; i++) {
				int keysignature = spelling.fromString(tuxGuitarKeys[i]);
				spelling.setSpellingFromKey(midiNote, keysignature);
				String result = spelling.toLilyPondString();
				if (!result.equals(expectedNotes[i]))
					throw new Exception();
			}
			
			// start over
			spelling.resetKey();
			spelling.setSpelling(60); // c
			spelling.setSpelling(66); // f#
			int key = spelling.guessKey();
			if (key != 1)
				throw new Exception();
			
		} catch (Throwable e) {
			e.printStackTrace();
			noException = false;
		}
		
		if (noException == false) {
			// failure
		}
		System.exit(0);
	}	
}
