package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGChangeVelocityAction extends TGActionBase {
	
	public static final String NAME = "action.note.general.velocity";
	
	public TGChangeVelocityAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Integer velocity = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);
		if( velocity != null ){
			TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().changeVelocity(velocity, note);
			}

			if (!noteRange.getNotes().isEmpty()) {
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		}
	}
}
