package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.note.TGPasteNoteAction;

public class TGNotePasteDialog extends TGPasteDialog {

	public TGNotePasteDialog() {
		super(TGPasteNoteAction.NAME);
	}

	@Override
	protected String getReplaceText() {
		return TuxGuitar.getProperty("edit.paste.replace-note");
	}

	@Override
	protected String getInsertText() {
		return TuxGuitar.getProperty("edit.paste.insert-note");
	}

	@Override
	protected String getTitle() {
		return TuxGuitar.getProperty("edit.paste");
	}
}
