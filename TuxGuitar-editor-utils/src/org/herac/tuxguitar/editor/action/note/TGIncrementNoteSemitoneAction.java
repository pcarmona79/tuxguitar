package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGIncrementNoteSemitoneAction extends TGActionBase {
	
	public static final String NAME = "action.note.general.increment-semitone";
	
	public TGIncrementNoteSemitoneAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		for (TGNote note : noteRange.getNotes()) {
			TGBeat beat = note.getVoice().getBeat();
			TGMeasure measure = beat.getMeasure();
			TGSongManager songManager = getSongManager(context);

			if (songManager.getMeasureManager().moveSemitoneUp(measure, beat.getStart(), note.getString())) {
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}
}
