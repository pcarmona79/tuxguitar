package org.herac.tuxguitar.app.transport;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.MidiTickUtil;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransport {

	private TGContext context;
	
	public TGTransport(TGContext context) {
		this.context = context;
	}
	
	public TGSongManager getSongManager(){
		return TGDocumentManager.getInstance(this.context).getSongManager();
	}
	
	public TGSong getSong(){
		return TGDocumentManager.getInstance(this.context).getSong();
	}
	
	public void gotoFirst(){
		gotoMeasure(getSongManager().getFirstMeasureHeader(getSong()),true);
	}
	
	public void gotoLast(){
		gotoMeasure(getSongManager().getLastMeasureHeader(getSong()),true) ;
	}
	
	public void gotoNext(){
		MidiPlayer player = TuxGuitar.getInstance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getNextMeasureHeader(getSong(), header),true);
		}
	}
	
	public void gotoPrevious(){
		MidiPlayer player = TuxGuitar.getInstance().getPlayer();
		long start = MidiTickUtil.getStart(player.getTickPosition());
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), start);
		if(header != null){
			if (!player.isRunning() && start != header.getStart()) {
				gotoMeasure(header, true);
			} else {
				gotoMeasure(getSongManager().getPrevMeasureHeader(getSong(), header), true);
			}
		}
	}
	
	public void gotoCaretPosition() {
		gotoMeasure(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().getHeader(), false);
	}
	
	public void gotoMeasure(TGMeasureHeader header){
		gotoMeasure(header,false);
	}
	
	public void gotoMeasure(TGMeasureHeader header,boolean moveCaret){
		if(header != null){
			TGMeasure playingMeasure = null;
			if( TuxGuitar.getInstance().getPlayer().isRunning() ){
				TuxGuitar.getInstance().getEditorCache().updatePlayMode();
				playingMeasure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			}
			if( playingMeasure == null || playingMeasure.getHeader().getNumber() != header.getNumber() ){
				TuxGuitar.getInstance().getPlayer().setTickPosition(MidiTickUtil.getTick(header.getStart()));
				if(moveCaret){
					Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
					caret.update(caret.getTrack().getNumber(), header.getStart(), caret.getStringNumber());
					TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().goToTickPosition();
					TuxGuitar.getInstance().updateCache(true);
				}
			}
		}
	}

	public void gotoBeat(TGBeat beat) {
	    if (beat != null) {
			TGBeat playingBeat = null;
			if( TuxGuitar.getInstance().getPlayer().isRunning() ){
				TuxGuitar.getInstance().getEditorCache().updatePlayMode();
				playingBeat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			}
			if( playingBeat == null || playingBeat != beat) {
				TuxGuitar.getInstance().getPlayer().setTickPosition(beat.getStart());
			}
		}
	}
	
	public void gotoPlayerPosition(){
		TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().goToTickPosition();
		TuxGuitar.getInstance().updateCache(true);
	}
	
	public void play(){
		MidiPlayer player = TuxGuitar.getInstance().getPlayer();
		if(!player.isRunning()){
			try{
				player.getMode().reset();
				player.play();
			}catch(MidiPlayerException e){
				TGErrorManager.getInstance(this.context).handleError(e);
			}
		}else{
			player.pause();
		}
	}
	
	public void stop(){
		MidiPlayer player = TuxGuitar.getInstance().getPlayer();
		if(!player.isRunning()){
			player.reset();
			this.gotoPlayerPosition();
		}else{
			player.reset();
		}
	}
	
	public static TGTransport getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTransport.class.getName(), new TGSingletonFactory<TGTransport>() {
			public TGTransport createInstance(TGContext context) {
				return new TGTransport(context);
			}
		});
	}
}
