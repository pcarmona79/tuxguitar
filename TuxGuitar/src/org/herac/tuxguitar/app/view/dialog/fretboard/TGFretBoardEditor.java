package org.herac.tuxguitar.app.view.dialog.fretboard;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import org.herac.tuxguitar.app.editor.TGRedrawEvent;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFretBoardEditor implements TGEventListener{
	
	private TGContext context;
	private TGFretBoard fretBoard;
	private boolean visible;
	
	public TGFretBoardEditor(TGContext context){
		this.context = context;
		this.appendListeners();
	}
	
	public void appendListeners() {
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
	}
	
	private TGFretBoard getFretBoard(){
		return this.fretBoard;
	}
	
	public void hideFretBoard(){
		this.visible = false;
		getFretBoard().setVisible(this.visible);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().removeBeatViewerListener(this);
		TuxGuitar.getInstance().updateShellFooter(0,0,0);
	}
	
	public void showFretBoard(){
		this.visible = true;
		getFretBoard().setVisible(this.visible);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().addBeatViewerListener(this);
		TuxGuitar.getInstance().updateShellFooter(getFretBoard().getHeight(), 730,520);
	}
	
	public void showFretBoard(Composite parent) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(100,0);
		
		this.fretBoard = new TGFretBoard(this.context, parent);
		this.fretBoard.setLayoutData(data);
	}
	
	public void dispose(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().dispose();
		}
	}
	
	public void redraw(){
		if( getFretBoard() != null && !getFretBoard().isDisposed() /*&& !TuxGuitar.getInstance().isLocked()*/){
			getFretBoard().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if( getFretBoard() != null && !getFretBoard().isDisposed() && !TuxGuitar.getInstance().isLocked()){
			getFretBoard().redrawPlayingMode();
		}
	}
	
	public boolean isVisible(){
		return (getFretBoard() != null && !getFretBoard().isDisposed() && this.visible);
	}
	
	public void loadProperties(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadProperties();
		}
	}
	
	public void loadIcons(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadIcons();
		}
	}
	
	public void loadScale(){
		if( getFretBoard() != null){
			getFretBoard().loadScale();
		}
	}
	
	public void showExternalBeat(TGBeat beat) {
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().setExternalBeat(beat);
		}
	}
	
	public void hideExternalBeat() {
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().setExternalBeat(null);
		}
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void processExternalBeatEvent(TGEvent event) {
		if( TGExternalBeatViewerEvent.ACTION_SHOW.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.showExternalBeat((TGBeat) event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_BEAT));
		}
		else if( TGExternalBeatViewerEvent.ACTION_HIDE.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.hideExternalBeat();
		}
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processRedrawEvent(event);
				}
				else if( TGExternalBeatViewerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processExternalBeatEvent(event);
				}
				else if( ScaleEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadScale();
				}
			}
		});
	}
	
	public static TGFretBoardEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFretBoardEditor.class.getName(), new TGSingletonFactory<TGFretBoardEditor>() {
			public TGFretBoardEditor createInstance(TGContext context) {
				return new TGFretBoardEditor(context);
			}
		});
	}
}
