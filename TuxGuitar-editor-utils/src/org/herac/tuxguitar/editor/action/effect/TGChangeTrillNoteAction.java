package org.herac.tuxguitar.editor.action.effect;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGChangeTrillNoteAction extends TGActionBase{
	
	public static final String NAME = "action.note.effect.change-trill";
	
	public static final String ATTRIBUTE_EFFECT = TGEffectTrill.class.getName();

	public TGChangeTrillNoteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGEffectTrill effect = ((TGEffectTrill) context.getAttribute(ATTRIBUTE_EFFECT));
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

		for (TGNote note : noteRange.getNotes()) {
			getSongManager(context).getMeasureManager().changeTrillNote(note, effect);
		}
	}
}
