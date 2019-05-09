package org.herac.tuxguitar.util;

import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class TGBeatRange {

    private List<TGBeat> beats;
    private TreeSet<TGMeasure> measures = new TreeSet<>(Comparator.comparingLong(a -> a.getHeader().getNumber()));

    public TGBeatRange(List<TGBeat> beats) {
        this.beats = beats;
        for (TGBeat beat : beats) {
            measures.add(beat.getMeasure());
        }
    }

    public boolean isEmpty() {
        return this.beats.isEmpty();
    }

    public List<TGBeat> getBeats() {
        return beats;
    }

    public TreeSet<TGMeasure> getMeasures() {
        return measures;
    }

    public static TGBeatRange single(TGBeat note) {
        return new TGBeatRange(Collections.singletonList(note));
    }

    public static TGBeatRange empty() {
        return new TGBeatRange(Collections.<TGBeat>emptyList());
    }
}
