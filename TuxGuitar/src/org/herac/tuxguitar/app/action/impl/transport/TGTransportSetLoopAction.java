package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportSetLoopAction extends TGActionBase {

	public static final String NAME = "action.transport.set-loop";

	public TGTransportSetLoopAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
        MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
        MidiPlayerMode pm = midiPlayer.getMode();
        pm.setLoop(!pm.isLoop());
	}
}
