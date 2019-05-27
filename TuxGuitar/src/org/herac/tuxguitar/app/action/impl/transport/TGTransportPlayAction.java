package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportPlayAction extends TGActionBase {
	
	public static final String NAME = "action.transport.play";
	
	public TGTransportPlayAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		MidiPlayer player = MidiPlayer.getInstance(getContext());
		TGTransport transport = TGTransport.getInstance(getContext());
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();

		player.setSelection(-1, -1);
		if (!player.isRunning()) {
			transport.gotoMeasure(tablature.getCaret().getMeasure().getHeader(), true);
		}
		transport.play();
		if (!player.isRunning()) {
			transport.gotoMeasure(tablature.getCaret().getMeasure().getHeader(), true);
		}
	}
}