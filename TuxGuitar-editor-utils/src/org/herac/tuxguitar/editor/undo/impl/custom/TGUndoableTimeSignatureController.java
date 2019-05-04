package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTimeSignatureController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableTimeSignature.startUndo(context);
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		TGTimeSignature timeSignature = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE);
		TGMeasureHeader start = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		TGMeasureHeader end = actionContext.getAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_HEADER_END);
		boolean truncateOrExtend = actionContext.getAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_TRUNCATE_OR_EXTEND);
		
		return ((TGUndoableTimeSignature) undoableEdit).endUndo(timeSignature, start.getNumber(), end != null ? end.getNumber() : -1, truncateOrExtend);
	}
}
