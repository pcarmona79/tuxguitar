package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGSetTrackVisibleAction extends TGActionBase {

	public static final String NAME = "action.gui.set-track-visible";

	public static final String ATTRIBUTE_VISIBLE = "visible";

	public TGSetTrackVisibleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		boolean visible = Boolean.TRUE.equals(context.getAttribute(ATTRIBUTE_VISIBLE));
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGSongManager songManager = getSongManager(context);
		songManager.getTrackManager().changeVisible(track, visible);
		TGTrack currentTrack = TablatureEditor.getInstance(getContext()).getTablature().getCaret().getTrack();
		if (songManager.countVisibleTracks(track.getSong()) == 0) {
			currentTrack.setVisible(true);
		}
		if (!currentTrack.isVisible()) {
			TGTrack newTrack = songManager.getVisibleTracks(track.getSong()).get(0);
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, newTrack);
			TGActionManager.getInstance(getContext()).execute(TGGoToTrackAction.NAME, context);
		}
	}
}
