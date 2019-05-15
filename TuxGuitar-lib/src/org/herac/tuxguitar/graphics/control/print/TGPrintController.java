package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

import java.util.Collections;
import java.util.List;

public class TGPrintController implements TGController {
	
	private TGSong song;
	private TGSongManager songManager;
	private UIResourceFactory resourceFactory;
	private TGResourceBuffer resourceBuffer;
	private TGLayoutStyles styles;
	
	public TGPrintController(TGSong song, TGSongManager songManager, UIResourceFactory resourceFactory, TGLayoutStyles styles){
		this.song = song;
		this.songManager = songManager;
		this.styles = styles;
		this.resourceFactory = resourceFactory;
		this.resourceBuffer = new TGResourceBuffer();
	}
	
	public TGSong getSong() {
		return song;
	}

	public TGSongManager getSongManager() {
		return this.songManager;
	}
	
	public UIResourceFactory getResourceFactory(){
		return this.resourceFactory;
	}
	
	public TGResourceBuffer getResourceBuffer() {
		return this.resourceBuffer;
	}
	
	public TGLayoutStyles getStyles(){
		return this.styles;
	}
	
	public List<TGTrack> getTrackSelection() {
		return getSong().getTrackList();
	}

	public boolean isRunning(TGBeat beat) {
		return false;
	}
	
	public boolean isRunning(TGMeasure measure) {
		return false;
	}
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		return false;
	}
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		return false;
	}
}
