package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.util.TGNoteRange;

import java.util.*;

/**
 * Created by tubus on 20.12.16.
 */
public class Selector {

	private final Tablature tablature;
	private TGBeat initial;
	private TGBeat start;
	private TGBeat end;
	private boolean active;

	public Selector(Tablature tablature) {
	    this.tablature = tablature;
	}

	public void initializeSelection(TGBeat beat) {
		initial = beat;
		start = beat;
		end = beat;
		active = false;
	}

	public void updateSelection(TGBeat beat) {
	    if (initial == null || beat == null) {
	    	initializeSelection(beat);
		} else {
	    	if (beat != initial) {
	    		active = true;
			}
			if (initial.getMeasure().getNumber() < beat.getMeasure().getNumber() || initialIsEarlierInTheSameMeasure(beat)) {
				start = initial;
				end = beat;
			} else {
				start = beat;
				end = initial;
			}
		}
	}

	public void clearSelection() {
		initializeSelection(null);
	}

	private boolean initialIsEarlierInTheSameMeasure(TGBeat beat) {
		return initial.getMeasure().getNumber() == beat.getMeasure().getNumber() && initial.getStart() < beat.getStart();
	}

	public TGBeat getInitialBeat() {
		return initial;
	}

	public TGBeat getStartBeat() {
		return start;
	}

	public TGBeat getEndBeat() {
		return end;
	}

	public boolean isActive() {
		return active;
	}

	public void paintSelectedArea(TGLayout viewLayout, UIPainter painter) {
		if (isActive()) {
			TGTrackImpl track = (TGTrackImpl) initial.getMeasure().getTrack();
			track.paintBeatSelection(viewLayout, painter, start, end);
		}
	}

	public TGNoteRange getNoteRange() {
		BeatRangeNoteIterator iterator = new BeatRangeNoteIterator(start, end);
		ArrayList<TGNote> selectedNotes = new ArrayList<TGNote>();
		while (iterator.hasNext()) {
			selectedNotes.add(iterator.next());
		}
		return new TGNoteRange(selectedNotes);
	}

	private static class BeatRangeNoteIterator implements Iterator<TGNote> {
		private Iterator<TGMeasure> measureIterator;
		private TGMeasure currentMeasure;

		private Iterator<TGBeat> beatIterator;
		private TGBeat currentBeat;
		private final TGBeat lastBeat;

		private int currentVoiceIndex;
		private TGVoice currentVoice;

		private Iterator<TGNote> noteIterator;

		BeatRangeNoteIterator(TGBeat start, TGBeat end) {
			lastBeat = end;

			measureIterator = start.getMeasure().getTrack().getMeasures();
			while (currentMeasure != start.getMeasure())
				currentMeasure = measureIterator.next();

			beatIterator = currentMeasure.getBeats().iterator();
			while (currentBeat != start)
				currentBeat = beatIterator.next();

			currentVoiceIndex = 0;
			updateVoice();
		}

		public boolean hasNext() {
			moveToActiveNoteIteratorOrEnd();
			return noteIterator.hasNext();
		}

		public TGNote next() {
			moveToActiveNoteIteratorOrEnd();
			return noteIterator.next();
		}

		private void moveToActiveNoteIteratorOrEnd() {

			while(!noteIterator.hasNext() && !isInTheEnd()) {
				currentVoiceIndex++;
				if (currentVoiceIndex < currentBeat.countVoices()) {
					updateVoice();
				}
				else if (beatIterator.hasNext()) {
					updateBeat();
				}
				else if (measureIterator.hasNext()) {
					updateMeasure();
				}
			}
		}

		private void updateMeasure() {
			currentMeasure = measureIterator.next();
			beatIterator = currentMeasure.getBeats().iterator();
			updateBeat();
		}

		private void updateBeat() {
			currentBeat = beatIterator.next();
			currentVoiceIndex = 0;
			updateVoice();
		}

		private void updateVoice() {
			currentVoice = currentBeat.getVoice(currentVoiceIndex);
			noteIterator = currentVoice.getNotes().iterator();
		}

		private boolean isInTheEnd() {
			return !noteIterator.hasNext() && currentBeat == lastBeat && currentVoiceIndex == (lastBeat.countVoices() - 1);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
