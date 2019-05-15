package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeShowAllTracksAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-multitrack";
	
	public TGChangeShowAllTracksAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		int visibleCount = getSongManager(context).countVisibleTracks(song);
		TGTrackManager trackManager = getSongManager(context).getTrackManager();
		if (visibleCount != song.countTracks()) {
			for (int i = 0; i < song.countTracks(); i++) {
				trackManager.changeVisible(song.getTrack(i), true);
			}
		} else {
			TGTrack selectedTrack = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			getSongManager(context).showSingleTrack(song, selectedTrack);
		}
	}
}
