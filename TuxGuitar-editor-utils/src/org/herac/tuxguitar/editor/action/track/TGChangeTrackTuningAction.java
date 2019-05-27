package org.herac.tuxguitar.editor.action.track;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTrackTuningAction extends TGActionBase {
	
	public static final String NAME = "action.track.change-tuning";
	
	public static final String ATTRIBUTE_FRETS = "frets";
	public static final String ATTRIBUTE_OFFSET = "offset";
	public static final String ATTRIBUTE_LET_RING = "letRing";
	public static final String ATTRIBUTE_STRINGS = "strings";
	public static final String ATTRIBUTE_PROGRAM = "program";
	public static final String ATTRIBUTE_CLEF = "clef";
	public static final String ATTRIBUTE_TRANSPOSE_STRINGS = "transposeStrings";
	public static final String ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS = "transposeTryKeepString";
	public static final String ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS = "transposeApplyToChords";
	
	public TGChangeTrackTuningAction(TGContext context) {
		super(context, NAME);
	}
	
	@SuppressWarnings("unchecked")
	protected void processAction(TGActionContext context){
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		Integer frets = context.getAttribute(ATTRIBUTE_FRETS);
		Integer offset = context.getAttribute(ATTRIBUTE_OFFSET);
		Boolean letRing = context.getAttribute(ATTRIBUTE_LET_RING);
		List<TGString> strings = ((List<TGString>) context.getAttribute(ATTRIBUTE_STRINGS));
		Short program = context.getAttribute(ATTRIBUTE_PROGRAM);
		Integer clef = context.getAttribute(ATTRIBUTE_CLEF);
		if( track != null ) {
			TGSongManager songManager = getSongManager(context);
			if (program != null) {
			    List<TGTrack> channelTracks = songManager.getTracksConnectedToChannel(track.getSong(), track.getChannelId());
				TGChannel channel = songManager.getChannel(track.getSong(), track.getChannelId());
				if (channelTracks.size() == 1 && channel.getProgram() == TGChannel.DEFAULT_PROGRAM) {
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
					context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, program);
					tgActionManager.execute(TGUpdateChannelAction.NAME, context);
				}
			}
			if (clef != null) {
			    // do not set clef if more than one is set on this track
                Iterator<TGMeasure> iter = track.getMeasures();
                int firstClef = -1;
                while (iter.hasNext()) {
                	TGMeasure measure = iter.next();
                	if (firstClef == -1) {
                		firstClef = measure.getClef();
					} else if (measure.getClef() != firstClef) {
                		firstClef = -1;
                		break;
					}
				}
                if (firstClef != -1) {
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, songManager.getTrackManager().getFirstMeasure(track));
					context.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, clef);
					context.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, true);
					tgActionManager.execute(TGChangeClefAction.NAME, context);
				}
			}
			if( strings != null ){
				int[] transpositions = createTranspositions(track, strings);
				
				songManager.getTrackManager().changeInstrumentStrings(track, strings);
				
				Boolean transposeStrings = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_STRINGS));
				if( Boolean.TRUE.equals(transposeStrings) ){
					boolean transposeTryKeepString = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS));
					boolean transposeApplyToChords = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS));
					
					songManager.getTrackManager().transposeNotes(track, transpositions, transposeTryKeepString, transposeApplyToChords );
				}
			}
			if( frets != null ) {
				songManager.getTrackManager().changeFrets(track, frets);
			}
			if( offset != null ) {
				songManager.getTrackManager().changeOffset(track, offset);
			}
			if( letRing != null ) {
				songManager.getTrackManager().changeLetRing(track, letRing);
			}
		}
	}
	
	public int[] createTranspositions(TGTrack track, List<?> newStrings ){
		int[] transpositions = new int[ newStrings.size() ];
		
		TGString newString = null;
		TGString oldString = null;
		for( int index = 0; index < transpositions.length ; index ++ ){
			for( int i = 0; i < track.stringCount() ; i ++ ){
				TGString string = track.getString( i + 1 );
				if( string.getNumber() == (index + 1) ){
					oldString = string;
					break;
				}
			}
			for( int i = 0; i < newStrings.size() ; i ++ ){
				TGString string = (TGString)newStrings.get( i );
				if( string.getNumber() == (index + 1) ){
					newString = string;
					break;
				}
			}
			if( oldString != null && newString != null ){
				transpositions[ index ] = (oldString.getValue() - newString.getValue());
			}else{
				transpositions[ index ] = 0;
			}
			
			newString = null;
			oldString = null;
		}
		
		return transpositions;
	}
}
