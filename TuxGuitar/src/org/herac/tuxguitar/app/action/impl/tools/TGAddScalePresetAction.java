package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGAddScalePresetAction extends TGActionBase{

	public static final String NAME = "action.tools.add-scale-preset";

    public static final String ATTRIBUTE_SCALE_INFO = "scaleInfo";

    public TGAddScalePresetAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		ScaleManager scaleManager = ScaleManager.getInstance(getContext());
		ScaleInfo info = context.getAttribute(ATTRIBUTE_SCALE_INFO);

		scaleManager.addCustomScale(info);

		context.setAttribute(TGSelectScaleAction.ATTRIBUTE_VISIBLE, !track.isVisible());
		TGActionManager.getInstance(getContext()).execute(TGSelectScaleAction.NAME, context);
	}
}
