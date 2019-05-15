package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

import java.util.List;

public interface TGController {
	
	public UIResourceFactory getResourceFactory();
	
	public TGResourceBuffer getResourceBuffer();
	
	public TGSongManager getSongManager();
	
	public TGSong getSong();
	
	public TGLayoutStyles getStyles();
	
	public List<TGTrack> getTrackSelection();

	public boolean isRunning(TGBeat beat);
	
	public boolean isRunning(TGMeasure measure);
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader);
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader);
}
