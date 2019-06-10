package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportPlaySelectionAction extends TGActionBase {

	public static final String NAME = "action.transport.play-selection";

	public TGTransportPlaySelectionAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTransport transport = TGTransport.getInstance(getContext());
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		MidiPlayer player = MidiPlayer.getInstance(getContext());
		Selector selector = tablature.getSelector();
		if (!player.isRunning()) {
			if (selector.isActive()) {
				TGBeat endBeat = selector.getEndBeat();
				TGDuration duration = tablature.getSongManager().getMeasureManager().getMinimumDuration(endBeat);
				player.setSelection(selector.getStartBeat().getStart(), endBeat.getStart() + duration.getTime());
				transport.gotoBeat(selector.getStartBeat());
			} else {
				player.setSelection(-1, -1);
				transport.gotoBeat(tablature.getCaret().getSelectedBeat());
			}
		}
		transport.play();
		if (!player.isRunning()) {
			tablature.getCaret().goToTickPosition();
		}
	}
}