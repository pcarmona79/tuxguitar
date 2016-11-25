package org.herac.tuxguitar.util;

import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by tubus on 26.01.17.
 */
public class TGNoteRange {
	private List<TGNote> notes;
	private LinkedHashSet<TGMeasure> measures = new LinkedHashSet<TGMeasure>();

	public TGNoteRange(List<TGNote> notes) {
		this.notes = notes;
		for (TGNote note : notes) {
			measures.add(note.getVoice().getBeat().getMeasure());
		}
	}

	public static TGNoteRange single(TGNote note) {
		return new TGNoteRange(Collections.singletonList(note));
	}

	public static TGNoteRange empty() {
		return new TGNoteRange(Collections.<TGNote>emptyList());
	}

	public List<TGNote> getNotes() {
		return notes;
	}

	public LinkedHashSet<TGMeasure> getMeasures() {
		return measures;
	}
}
