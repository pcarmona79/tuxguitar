package org.herac.tuxguitar.editor.action.note;

import java.util.Iterator;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGBeatGroup;
import org.herac.tuxguitar.graphics.control.TGVoiceImpl;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGSetVoiceDownAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.voice-down";
	
	public TGSetVoiceDownAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		for (TGNote note : noteRange.getNotes()) {
			TGVoiceImpl voice = (TGVoiceImpl) note.getVoice();
			if (voice != null) {
				TGBeatGroup group = voice.getBeatGroup();
				if (!voice.isEmpty() && !voice.isRestVoice() && group != null) {
					Iterator<?> it = group.getVoices().iterator();
					while (it.hasNext()) {
						TGVoice current = (TGVoice) it.next();
						getSongManager(context).getMeasureManager().changeVoiceDirection(current, TGVoice.DIRECTION_DOWN);
					}
				}
			}
		}
	}
}
