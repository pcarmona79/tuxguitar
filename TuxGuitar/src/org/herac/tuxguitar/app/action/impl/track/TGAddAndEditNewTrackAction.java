package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.util.TGContext;

public class TGAddAndEditNewTrackAction extends TGActionBase{

	public static final String NAME = "action.gui.add-new-track";

	public TGAddAndEditNewTrackAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		TGActionManager.getInstance(getContext()).execute(TGAddNewTrackAction.NAME, tgActionContext);
		TGActionManager.getInstance(getContext()).execute(TGOpenTrackPropertiesDialogAction.NAME, tgActionContext);
	}
}