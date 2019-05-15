package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTrackVisibleAction extends TGActionBase {

	public static final String NAME = "action.gui.change-track-visible";

	public TGChangeTrackVisibleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		context.setAttribute(TGSetTrackVisibleAction.ATTRIBUTE_VISIBLE, !track.isVisible());
		TGActionManager.getInstance(getContext()).execute(TGSetTrackVisibleAction.NAME, context);
	}
}
