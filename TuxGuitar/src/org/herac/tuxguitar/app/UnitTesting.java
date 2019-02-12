package org.herac.tuxguitar.app;

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
			"b","b","b","b","b","b","b"
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
			int midiNote = 59; // B below middle C
			for(int i = 0; i < tuxGuitarKeys.length; i++) {
				int keysignature = spelling.fromString(tuxGuitarKeys[i]);
				spelling.setSpellingFromKey(midiNote, keysignature);
				String result = spelling.toLilyPondString();
				if (result != expectedNotes[i])
					throw new Exception();
			}
			
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
