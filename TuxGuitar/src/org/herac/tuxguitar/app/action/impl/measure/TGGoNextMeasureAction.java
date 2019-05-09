package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

import java.util.List;

public class TGGoNextMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.go-next";
	
	public TGGoNextMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){ 
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoNext();
		}
		else{
			Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				tablature.getSelector().clearSelection();
			}
			Caret caret = tablature.getCaret();
			TGTrackImpl track = caret.getTrack();
			TGMeasure measure = getSongManager(context).getTrackManager().getNextMeasure(caret.getMeasure());
			if( track != null && measure != null ){
				caret.update(track.getNumber(), measure.getStart(), caret.getSelectedString().getNumber());
			} else {
			    // move to last beat if already at end
				List<TGBeat> beats = caret.getMeasure().getBeats();
				if (!beats.isEmpty()) {
					TGBeat beat = getSongManager(context).getMeasureManager().getLastBeat(beats);
					caret.moveTo(track, caret.getMeasure(), beat, caret.getSelectedString().getNumber());
				}
			}
		}
	}
}
