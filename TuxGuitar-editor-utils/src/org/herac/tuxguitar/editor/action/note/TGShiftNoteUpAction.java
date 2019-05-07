package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGShiftNoteUpAction extends TGActionBase {
	
	public static final String NAME = "action.note.general.shift-up";
	
	public TGShiftNoteUpAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGNote caretNote = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		for (TGNote note : noteRange.getNotes()) {
			TGBeat beat = note.getVoice().getBeat();
			TGMeasure measure = beat.getMeasure();
			int nextString = getSongManager(context).getMeasureManager().shiftNoteUp(measure, beat.getStart(), note.getString());
			if (caretNote == note && nextString > 0){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, measure.getTrack().getString(nextString));
			}
		}
		context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
	}
}
