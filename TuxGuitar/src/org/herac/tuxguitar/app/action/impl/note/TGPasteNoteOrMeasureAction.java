package org.herac.tuxguitar.app.action.impl.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.clipboard.TGMeasurePasteDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGPasteNoteAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.util.TGContext;

public class TGPasteNoteOrMeasureAction extends TGActionBase{

	public static final String NAME = "action.gui.paste";

	public TGPasteNoteOrMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		TGClipboard clipboard = TGClipboard.getInstance(getContext());
		if (clipboard.getSegment() != null) {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMeasurePasteDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		} else if (clipboard.getBeats() != null) {
			TGActionManager.getInstance(getContext()).execute(TGPasteNoteAction.NAME, tgActionContext);
		}
	}
}
