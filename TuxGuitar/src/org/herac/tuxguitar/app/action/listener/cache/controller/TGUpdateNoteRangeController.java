package org.herac.tuxguitar.app.action.listener.cache.controller;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

/**
 * Created by tubus on 26.01.17.
 */
public class TGUpdateNoteRangeController extends TGUpdateItemsController {

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		TGNoteRange noteRange = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);

		for (TGMeasure measure : noteRange.getMeasures()) {
			this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(measure.getNumber());
		}
		super.update(context, actionContext);
	}
}
