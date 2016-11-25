package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

/**
 * Created by tubus on 20.12.16.
 */
public class TGSetSelectionBeginingAction extends TGActionBase {

	public static final String NAME = "action.selector.set-selection-begining";

	public TGSetSelectionBeginingAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		EditorKit editorKit = TablatureEditor.getInstance(getContext()).getTablature().getEditorKit();

		if (editorKit.fillSelection(context)) {
			TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
			selector.initializeSelection(beat);
		}
	}
}
