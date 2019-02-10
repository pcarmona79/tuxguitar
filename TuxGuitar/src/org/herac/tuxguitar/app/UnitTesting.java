package org.herac.tuxguitar.app;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNoteSpelling;

public class UnitTesting {

	private static String[] tuxGuitarKeys = {
			// TuxGuitar order
			"c","g","d","a","e","b","fis","cis",//"gis","dis","ais","eis","bis"};
			"f","bes","ees","aes","des","ges","ces"};
	
	private static int[] results = {
			0, 1, 2, 3, 4, 5, 6, 7, -1, -2, -3, -4, -5, -6, -7
	};
	
	public static void main(String[] args){
		
		try {
			TGNoteSpelling spelling = new TGFactory().newNoteSpelling();
			for(int i = 0; i < tuxGuitarKeys.length; i++) {
				int keyValue = spelling.fromString(tuxGuitarKeys[i]);
				if (keyValue != results[i])
					throw new Exception();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}	
}
