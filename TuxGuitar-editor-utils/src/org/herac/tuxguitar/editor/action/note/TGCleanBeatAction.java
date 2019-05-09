package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGCleanBeatAction extends TGActionBase {
	
	public static final String NAME = "action.note.general.clean-beat";
	
	public TGCleanBeatAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		for (TGBeat beat : beats.getBeats()) {
			getSongManager(context).getMeasureManager().cleanBeat(beat);
		}
	}
}
