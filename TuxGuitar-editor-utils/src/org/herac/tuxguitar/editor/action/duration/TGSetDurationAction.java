package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGSetDurationAction extends TGActionBase {
	
	public static final String NAME = "action.note.duration.set-duration";
	
	public TGSetDurationAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGDuration duration = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);
		TGSongManager songManager = getSongManager(context);
		TGMeasureManager measureManager = songManager.getMeasureManager();
		TGFactory factory = songManager.getFactory();

		if (!noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				TGVoice voice = note.getVoice();
				TGBeat beat = voice.getBeat();
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		} else {
			TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
			for (TGBeat beat : beats.getBeats()) {
				TGMeasure measure = beat.getMeasure();
				measureManager.changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
			}
		}
	}
}
