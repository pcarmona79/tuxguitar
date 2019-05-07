package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGDeleteNoteOrRestAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.delete-note-or-rest";
	
	public TGDeleteNoteOrRestAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		for (TGNote note : noteRange.getNotes()) {
			TGVoice voice = note.getVoice();
			TGBeat beat = voice.getBeat();
			TGMeasure measure = beat.getMeasure();
			TGString string = measure.getTrack().getString(note.getString());
            if( beat.isTextBeat() && beat.isRestBeat() ){
                songManager.getMeasureManager().removeText(beat);
            }
            else if( voice.isRestVoice() ){
                songManager.getMeasureManager().removeVoice(voice, true);
            }
            else {
                songManager.getMeasureManager().removeNote(measure, beat.getStart(), voice.getIndex(), string.getNumber());
            }
		}
	}
}
