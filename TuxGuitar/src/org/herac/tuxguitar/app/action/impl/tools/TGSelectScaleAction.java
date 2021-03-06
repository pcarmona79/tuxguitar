package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.util.TGContext;

public class TGSelectScaleAction extends TGActionBase{
	
	public static final String NAME = "action.tools.select-scale";
	
	public static final String ATTRIBUTE_SCALE_INFO = "scaleInfo";
	public static final String ATTRIBUTE_KEY = "scaleKey";
    public static final String ATTRIBUTE_SCALE = "scale";

    public TGSelectScaleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		ScaleManager scaleManager = ScaleManager.getInstance(getContext());
		TGScale scale = context.getAttribute(ATTRIBUTE_SCALE);
		if (scale != null) {
			scaleManager.setScale(scale);
		} else {
			ScaleInfo info = context.getAttribute(ATTRIBUTE_SCALE_INFO);
			Integer key = context.getAttribute(ATTRIBUTE_KEY);

			scaleManager.selectScale(info, key != null ? key : 0);
		}
	}
}
