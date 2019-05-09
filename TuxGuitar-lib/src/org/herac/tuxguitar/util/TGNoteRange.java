package org.herac.tuxguitar.util;

import org.herac.tuxguitar.song.helpers.TGBeatRangeNoteIterator;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;

import java.util.*;

/**
 * Created by tubus on 26.01.17.
 */
public class TGNoteRange {
	private List<TGNote> notes;
	private TreeSet<TGMeasure> measures = new TreeSet<TGMeasure>(Comparator.comparingLong(a -> a.getHeader().getNumber()));
	private TreeSet<TGBeat> beats = new TreeSet<TGBeat>(Comparator.comparingLong(TGBeat::getStart));

	public TGNoteRange(List<TGNote> notes) {
		this.notes = notes;
		for (TGNote note : notes) {
			TGBeat beat = note.getVoice().getBeat();
			beats.add(beat);
			measures.add(beat.getMeasure());
		}
	}

	public TGNoteRange(TGBeat start, TGBeat end, Collection<Integer> voices) {
		this(new TGBeatRangeNoteIterator(start, end, voices).toList());
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

	public boolean isEmpty() {
		return notes.isEmpty();
	}

	public TreeSet<TGMeasure> getMeasures() {
		return measures;
	}

	public TreeSet<TGBeat> getBeats() {
		return beats;
	}
}
