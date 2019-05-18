package org.herac.tuxguitar.app.view.dialog.piano;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.docked.TGDockedPlayingComponent;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.app.view.widgets.TGNoteToolbar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.util.TGContext;

public class TGPiano extends TGDockedPlayingComponent {
	
	private static final boolean TYPE_NOTES[] = new boolean[]{true,false,true,false,true,true,false,true,false,true,false,true};
	private static final int NATURAL_NOTES = 7;
	private static final int MAX_OCTAVES = 8;
	private static final int NATURAL_WIDTH = 15;
	private static final int SHARP_WIDTH = 8;
	private static final int NATURAL_HEIGHT = 60;
	private static final int SHARP_HEIGHT = 40;
	
	private TGContext context;
	private boolean changes;
	private TGPianoConfig config;
	private TGNoteToolbar toolbar;
	private UICanvas canvas;
	private UILabel scaleName;
	private UIButton scale;
	private TGBeat beat;
	private TGBeat externalBeat;
	private UIImage image;
	
	public TGPiano(TGContext context, UIContainer parent) {
		this.context = context;
		this.config = new TGPianoConfig(context);
		this.config.load();
		this.control = getUIFactory().createPanel(parent, false);
		this.initToolBar();
		this.initCanvas();
		this.createControlLayout();
		this.loadIcons();
		this.loadProperties();
		
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.canvas);
	}
	
	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolbar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(this.canvas, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(this.canvas, UITableLayout.PACKED_WIDTH, Float.valueOf(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)));
		uiLayout.set(this.canvas, UITableLayout.PACKED_HEIGHT, Float.valueOf(NATURAL_HEIGHT));
		
		this.control.setLayout(uiLayout);
	}
	
	private void initToolBar() {
		UIFactory factory = getUIFactory();

		this.toolbar =  new TGNoteToolbar(context, factory, this.control);

		// separator
		this.toolbar.createLeftSeparator(factory);

		// scale
		this.scale = factory.createButton(this.toolbar.getLeftComposite());
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.scale.addSelectionListener(new TGActionProcessorListener(this.context, TGOpenScaleDialogAction.NAME));
		this.toolbar.setLeftControlLayout(this.scale);

		// scale name
		this.scaleName = factory.createLabel(this.toolbar.getLeftComposite());
		this.toolbar.setLeftControlLayout(this.scaleName);

		this.toolbar.getSettings().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
	}

	private void loadScaleName() {
		int scaleKey = TuxGuitar.getInstance().getScaleManager().getSelectionKey();
		int scaleIndex = TuxGuitar.getInstance().getScaleManager().getSelectionIndex();
		String key = TuxGuitar.getInstance().getScaleManager().getKeyName( scaleKey );
		String name = TuxGuitar.getInstance().getScaleManager().getScaleName( scaleIndex );
		this.scaleName.setText( ( key != null && name != null ) ? ( key + " - " + name ) : "" );
	}
	
	private void initCanvas(){
		this.image = makePianoImage();
		this.canvas = getUIFactory().createCanvas(this.control, true);
		this.canvas.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGPianoPainterListener()));
		this.canvas.addMouseUpListener(new TGPianoMouseListener());
		this.canvas.setFocus();
	}
	
	/**
	 * Crea la imagen del piano
	 *
	 * @return
	 */
	private UIImage makePianoImage(){
		UIFactory factory = getUIFactory();
		float zoom = this.getZoom();
		UIImage image = factory.createImage((NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)) * zoom, NATURAL_HEIGHT * zoom);
		UIPainter painter = image.createPainter();

		int x = 0;
		int y = 0;
		painter.setBackground(this.config.getColorNatural());
		painter.initPath(UIPainter.PATH_FILL);
		painter.addRectangle(x * zoom,y * zoom,(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ) * zoom,NATURAL_HEIGHT * zoom);
		painter.closePath();
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				painter.setForeground(this.config.getColorNotNatural());
				painter.initPath();
				painter.setAntialias(false);
				painter.addRectangle(x * zoom,y * zoom,NATURAL_WIDTH * zoom,NATURAL_HEIGHT * zoom);
				painter.closePath();
				x += NATURAL_WIDTH;
			}else{
				painter.setBackground(this.config.getColorNotNatural());
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle((x - (SHARP_WIDTH / 2)) * zoom,y * zoom,SHARP_WIDTH * zoom,SHARP_HEIGHT * zoom);
				painter.closePath();
			}
		}
		paintScale(painter, zoom);
		
		painter.dispose();
		return image;
	}
	
	/**
	 * Pinta la nota a partir del indice
	 * 	 
	 * @param painter
	 * @param zoom
	 */
	private void paintScale(UIPainter painter, float zoom){
		painter.setBackground(this.config.getColorScale());
		painter.setForeground(this.config.getColorScale());
		int posX = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if(TuxGuitar.getInstance().getScaleManager().getScale().getNote(i)){
				if(TYPE_NOTES[i % TYPE_NOTES.length] ){
					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}
					
					int size = SHARP_WIDTH;
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle( (x + 1 + (((NATURAL_WIDTH - size) / 2))) * zoom ,(NATURAL_HEIGHT - size - (((NATURAL_WIDTH - size) / 2))) * zoom,size * zoom,size * zoom);
					painter.closePath();
				}else{
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle((posX + 1) * zoom, (SHARP_HEIGHT - SHARP_WIDTH + 1) * zoom,(SHARP_WIDTH - 2) * zoom,(SHARP_WIDTH - 2) * zoom);
					painter.closePath();
				}
			}
			
			posX += width;
		}
	}
	
	/**
	 * Pinta la nota a partir del indice
	 * 	 
	 * @param painter
	 * @param value
	 * @param zoom
	 */
	protected void paintNote(UIPainter painter,int value, float zoom){
		painter.setBackground(this.config.getColorNote());
		int posX = 0;
		int y = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if(i == value){
				if(TYPE_NOTES[i % TYPE_NOTES.length]){
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle((posX + 1) * zoom,(y + 1) * zoom,(width - 1) * zoom,SHARP_HEIGHT * zoom);
					
					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}
					painter.addRectangle((x + 1) * zoom,(y + SHARP_HEIGHT + 1) * zoom,(NATURAL_WIDTH - 1) * zoom,(NATURAL_HEIGHT - SHARP_HEIGHT - 1) * zoom);
					painter.closePath();
				}else{
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle((posX + 1) * zoom,(y + 1) * zoom,(width - 1) * zoom,(SHARP_HEIGHT - 1) * zoom);
					painter.closePath();
				}
				
			}
			
			posX += width;
		}
	}

	private float getZoom() {
		return this.control.getDeviceZoom() / 100f;
	}

	
	protected void paintEditor(UIPainter painter) {
		this.updateEditor();
		
		painter.drawImage(this.image, 0, 0);
		
		// pinto notas
		if( this.beat != null ){
			float zoom = this.getZoom();
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while(it.hasNext()){
					this.paintNote(painter, getRealNoteValue( it.next() ), zoom);
				}
			}
		}
	}
	
	/**
	 * Retorna el indice de la nota seleccionada
	 * 
	 * @param x
	 * @return
	 */
	private int getSelection(float x){
		float posX = 0;
		
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			float width = 0f;
			
			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}
			
			if( x >= posX && x < (posX + width)  ){
				return i;
			}
			
			posX += width;
		}
		return -1;
	}
	
	protected void hit(float x, float y) {
		int value = this.getSelection(x);
		
		if(!this.removeNote(value)) {
			this.addNote(value);
		}
	}
	
	private boolean removeNote(int value) {
		if(this.beat != null){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if( getRealNoteValue(note) == value ) {
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
						tgActionProcessor.process();
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean addNote(int value) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		
		List<TGString> strings = caret.getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;
				
				if(this.beat != null){
					for(int v = 0; v < this.beat.countVoices(); v ++){
						TGVoice voice = this.beat.getVoice( v );
						Iterator<TGNote> it = voice.getNotes().iterator();
						while (it.hasNext()) {
							TGNoteImpl note = (TGNoteImpl) it.next();
							if (note.getString() == string.getNumber()) {
								emptyString = false;
								break;
							}
						}
					}
				}
				if(emptyString){
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
					tgActionProcessor.process();
					
					return true;
				}
			}
		}
		return false;
	}
	
	protected int getRealNoteValue(TGNote note){
		TGVoice voice = note.getVoice();
		if( voice != null ){
			TGBeat beat = voice.getBeat();
			if( beat != null ){
				TGMeasure measure = beat.getMeasure();
				if( measure != null ){
					TGTrack track = measure.getTrack();
					if( track != null ){
						return ( note.getValue() + track.getString( note.getString() ).getValue() );
					}
				}
			}
		}
		// If note have no parents, uses current track strings.
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		TGTrack track = caret.getTrack();
		if( track != null ){
			return ( note.getValue() + track.getString( note.getString() ).getValue() );
		}
		return 0;
	}
	
	public boolean hasChanges(){
		return this.changes;
	}
	
	public void setChanges(boolean changes){
		this.changes = changes;
	}
	
	public void setExternalBeat(TGBeat externalBeat){
		this.externalBeat = externalBeat;
	}
	
	public TGBeat getExternalBeat(){
		return this.externalBeat;
	}
	
	protected void updateEditor(){
		if( isVisible() ){
			if( hasChanges() ){
				this.image.dispose();
				this.image = makePianoImage();
			}
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				this.beat = TuxGuitar.getInstance().getEditorCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TuxGuitar.getInstance().getEditorCache().getEditBeat();
			}
		}
	}
	
	public void redraw() {
		if(!this.isDisposed()){
			this.control.redraw();
			this.canvas.redraw();
			this.toolbar.update();
		}
	}
	
	public void redrawPlayingMode(){
		if(!this.isDisposed() ){
			this.canvas.redraw();
		}
	}
	
	public void dispose(){
		this.control.dispose();
		this.image.dispose();
		this.config.dispose();
	}
	
	public void loadProperties(){
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.toolbar.loadProperties();
		this.loadScaleName();
		this.control.layout();
	}
	
	public void loadIcons(){
		this.toolbar.loadIcons();
		this.control.layout();
	}
	
	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
		this.control.layout();
	}
	
	public void configure(){
		this.config.configure(TGWindow.getInstance(this.context).getWindow());
	}
	
	public void reloadFromConfig() {
		this.setChanges(true);
		this.redraw();
	}
	
	public UICanvas getCanvas() {
		return this.canvas;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	private class TGPianoMouseListener implements UIMouseUpListener {
		
		public TGPianoMouseListener(){
			super();
		}
		
		public void onMouseUp(UIMouseEvent event) {
			getCanvas().setFocus();
			if( event.getButton() == 1 ){
				if(!TuxGuitar.getInstance().getPlayer().isRunning() && !TGEditorManager.getInstance(TGPiano.this.context).isLocked()){
					if( getExternalBeat() == null ){
						hit(event.getPosition().getX(), event.getPosition().getY());
					}else{
						setExternalBeat( null );
						TuxGuitar.getInstance().updateCache(true);
					}
				}
			}else{
				new TGActionProcessor(TGPiano.this.context, TGGoRightAction.NAME).process();
			}
		}
	}
	
	private class TGPianoPainterListener implements TGBufferedPainterHandle {
		
		public TGPianoPainterListener(){
			super();
		}

		public void paintControl(UIPainter painter) {
			TGPiano.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGPiano.this.canvas;
		}
	}
}
