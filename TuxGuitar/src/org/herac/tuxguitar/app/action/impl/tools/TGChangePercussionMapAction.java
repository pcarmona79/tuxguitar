package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.tools.percussion.PercussionEntry;
import org.herac.tuxguitar.app.tools.percussion.PercussionManager;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGPercussionNote;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.util.TGContext;

public class TGChangePercussionMapAction extends TGActionBase{

	public static final String NAME = "action.tools.change-percussion-map";

	public static final String ATTRIBUTE_PERCUSSION_MAP = "percussionMap";
	public static final String ATTRIBUTE_SAVE = "save";

    public TGChangePercussionMapAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		PercussionManager percussionManager = PercussionManager.getInstance(getContext());
		PercussionEntry[] entries = context.getAttribute(ATTRIBUTE_PERCUSSION_MAP);
		boolean save = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_SAVE));

        percussionManager.setEntries(entries);
        if (save) {
			percussionManager.savePercussion();
		}
		TablatureEditor.getInstance(getContext()).getTablature().setPercussionMap(entries);
	}
}
