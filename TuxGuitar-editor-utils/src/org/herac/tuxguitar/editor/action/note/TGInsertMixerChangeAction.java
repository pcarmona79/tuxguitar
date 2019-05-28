package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMixerChange;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.util.TGContext;

public class TGInsertMixerChangeAction extends TGActionBase {

	public static final String NAME = "action.beat.general.insert-mixer-change";

	public static final String ATTRIBUTE_MIXER_CHANGE = "mixerChange";

	public TGInsertMixerChangeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGMixerChange mixer = context.getAttribute(ATTRIBUTE_MIXER_CHANGE);
		
		if( beat != null && mixer != null ){
			TGSongManager tgSongManager = getSongManager(context);

			tgSongManager.getMeasureManager().addMixerChange(beat, mixer);
		}
	}
}
