package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGCutNoteAction extends TGActionBase{

	public static final String NAME = "action.note.cut";

	public TGCutNoteAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGCopyNoteAction.NAME, context);
		tgActionManager.execute(TGDeleteNoteOrRestAction.NAME, context);
	}
}
