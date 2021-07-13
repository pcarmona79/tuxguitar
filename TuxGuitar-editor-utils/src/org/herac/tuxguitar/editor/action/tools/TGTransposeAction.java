package org.herac.tuxguitar.editor.action.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.song.helpers.TGStoredBeatList;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.util.TGBeatRange;


import java.util.List;



public class TGTransposeAction extends TGActionBase {
	
	public static final String NAME = "action.tools.transpose-notes";
	
	public static final String ATTRIBUTE_TRANSPOSITION = "transposition";
	public static final String ATTRIBUTE_TRY_KEEP_STRING = "tryKeepString";
	public static final String ATTRIBUTE_APPLY_TO_CHORDS = "applyToChords";
	public static final String ATTRIBUTE_APPLY_TO_ALL_TRACKS = "applyToAllTracks";
	public static final String ATTRIBUTE_APPLY_TO_ALL_MEASURES = "applyToAllMeasures";
	public static final String ATTRIBUTE_APPLY_TO_SELECTION = "applyToSelection";
	
	public TGTransposeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGTrack contextTrack = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGMeasure contextMeasure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		Integer transposition = context.getAttribute(ATTRIBUTE_TRANSPOSITION);
		Boolean tryKeepString = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRY_KEEP_STRING));
		Boolean applyToChords = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_CHORDS));
		Boolean applyToAllTracks = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_ALL_TRACKS));
		Boolean applyToAllMeasures = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_ALL_MEASURES));
		Boolean applyToSelection = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_APPLY_TO_SELECTION));
		
		if( applyToAllMeasures ){
			if( applyToAllTracks ){
				// all measures of all tracks
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					transposeTrack(songManager, song, song.getTrack( i ) , transposition , tryKeepString , applyToChords);
				}
			} else {
				// all measures of current track
				transposeTrack(songManager, song, contextTrack, transposition , tryKeepString , applyToChords);
			}
		} else if ( applyToSelection ) {
			// transpose beat range of selected track
			// add selected beat range to clipboard
			TGBeatRange beatsSelection = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
			if (!beatsSelection.isEmpty()) {
				TGClipboard.getInstance(this.getContext()).setData(new TGStoredBeatList(beatsSelection.getBeats(), getSongManager(context).getFactory()));
			}
			if ( !beatsSelection.isEmpty() ) {
			 	// make it happen
				transposeBeats(context, beatsSelection.getBeats() );
			}			
		} else {
			if( applyToAllTracks ){
				// all tracks of current measure
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					TGTrack track = song.getTrack( i );
					TGMeasure measure = songManager.getTrackManager().getMeasure(track, contextMeasure.getNumber() );
					if( measure != null ){
						transposeMeasure(songManager, song, measure, transposition , tryKeepString , applyToChords);
					}
				}
			} else {
				// current measure of current track
				transposeMeasure(songManager, song, contextMeasure, transposition , tryKeepString , applyToChords);
			}
		}
	}
	
	public void transposeMeasure(TGSongManager songManager, TGSong song, TGMeasure measure, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !songManager.isPercussionChannel(song, measure.getTrack().getChannelId()) ){
			songManager.getMeasureManager().transposeNotes( measure , transposition , tryKeepString , applyToChords , -1 );
		}
	}
	
	public void transposeTrack(TGSongManager songManager, TGSong song, TGTrack track, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !songManager.isPercussionChannel(song, track.getChannelId()) ){
			songManager.getTrackManager().transposeNotes( track , transposition , tryKeepString , applyToChords, -1 );
		}
	}

	private void transposeBeats(TGActionContext context, List<TGBeat> beats) {
		TGMeasureManager measureManager = this.getSongManager(context).getMeasureManager();
		TGMeasure measure = beats.get(0).getMeasure();

		Integer transposition = context.getAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION);
		if (transposition != null && transposition != 0) {
            Boolean tryKeepString = Boolean.TRUE.equals(context.getAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING));
            boolean applyToChords = Boolean.TRUE.equals(context.getAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS));
            List<TGString> strings = measureManager.getSortedStringsByValue(measure.getTrack(), ( transposition > 0 ? 1 : -1 ) ) ;
            for (TGBeat beat : beats) {
                measureManager.transposeNotes( beat , strings, transposition , tryKeepString , applyToChords , -1 );
            }
		}
		measureManager.removeNotesAfterString(measure, measure.getTrack().stringCount());
	}

}