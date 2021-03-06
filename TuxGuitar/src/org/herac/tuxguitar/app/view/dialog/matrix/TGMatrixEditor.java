package org.herac.tuxguitar.app.view.dialog.matrix;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGNoteToolbar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.player.base.MidiPercussionKey;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseEnterListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseExitListener;
import org.herac.tuxguitar.ui.event.UIMouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.*;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMatrixEditor implements TGEventListener {
	
	private static final float DEFAULT_WIDTH = 640f;
	private static final float DEFAULT_HEIGHT = 480f;
	private static final float MINIMUM_HEIGHT = 200f;

	private static final int BORDER_HEIGHT = 20;
	private static final int SCROLL_INCREMENT = 50;
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_MATRIX);
	private static final MidiPercussionKey[] PERCUSSIONS = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
	private static final int[] DIVISIONS = new int[] {1,2,3,4,6,8,16};
	
	private TGContext context;
	private TGMatrixConfig config;
	private UIWindow dialog;
	private UIPanel composite;
	private TGNoteToolbar toolbar;
	private UIScrollBarPanel canvasPanel;
	private UICanvas editor;
	private UIRectangle clientArea;
	private UIImage buffer;
	private BufferDisposer bufferDisposer;
	private UILabel gridsLabel;
	private float width;
	private float height;
	private float bufferWidth;
	private float bufferHeight;
	private float timeWidth;
	private float lineHeight;
	private float leftSpacing;
	private int minNote;
	private int maxNote;
	private int duration;
	private int selection;
	private int grids;
	
	public TGMatrixEditor(TGContext context){
		this.context = context;
		this.grids = this.loadGrids();
	}
	
	public void show(){
		this.config = new TGMatrixConfig(this.context);
		this.config.load();
		
		this.dialog = getUIFactory().createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setBounds(new UIRectangle(new UISize(DEFAULT_WIDTH, DEFAULT_HEIGHT)));
		this.dialog.addDisposeListener(new DisposeListenerImpl());
		this.bufferDisposer = new BufferDisposer();
		
		this.composite = getUIFactory().createPanel(this.dialog, false);
		
		this.initToolBar();
		this.initEditor();
		this.createWindowLayout();
		this.createControlLayout();
		this.loadIcons();
		this.addListeners();

		this.dialog.computePackedSize(null, null);
		UISize minimumSize = this.dialog.getPackedSize();
		minimumSize.setHeight(MINIMUM_HEIGHT);
		this.dialog.setMinimumSize(minimumSize);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}
	
	public void createWindowLayout() {
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.dialog.setLayout(uiLayout);
	}
	
	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolbar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.canvasPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.composite.setLayout(uiLayout);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.editor);
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener( this );
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener( this );
	}
	
	private void initToolBar() {
		UIFactory factory = getUIFactory();
		this.toolbar =  new TGNoteToolbar(context, factory, this.composite);

		this.toolbar.createLeftSeparator(factory);

		// grids
		this.gridsLabel = factory.createLabel(this.toolbar.getLeftComposite());
		this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
		this.toolbar.setLeftControlLayout(this.gridsLabel);

		final UIDropDownSelect<Integer> divisionsCombo = factory.createDropDownSelect(this.toolbar.getLeftComposite());
		for(int i = 0; i < DIVISIONS.length; i ++){
			divisionsCombo.addItem(new UISelectItem<Integer>(Integer.toString(DIVISIONS[i]), DIVISIONS[i]));
		}
		divisionsCombo.setSelectedValue(this.grids > 0 ? this.grids : null);
		divisionsCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer grids = divisionsCombo.getSelectedValue();
				if( grids != null ){
					setGrids(grids);
				}
			}
		});
		this.toolbar.setLeftControlLayout(divisionsCombo);
		this.toolbar.getSettings().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
	}

	public void initEditor(){
		TGMatrixMouseListener mouseListener = new TGMatrixMouseListener();
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout uiLayout = new UITableLayout(0f);
		
		this.canvasPanel = uiFactory.createScrollBarPanel(this.composite, true, true, true);
		this.canvasPanel.setLayout(uiLayout);
		
		this.selection = -1;
		this.editor = uiFactory.createCanvas(this.canvasPanel, false);
		this.editor.setFocus();
		this.editor.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGMatrixPainterListener()));
		this.editor.addMouseUpListener(mouseListener);
		this.editor.addMouseEnterListener(mouseListener);
		this.editor.addMouseExitListener(mouseListener);
		this.editor.addMouseMoveListener(mouseListener);
		
		this.canvasPanel.getHScroll().setIncrement(SCROLL_INCREMENT);
		this.canvasPanel.getHScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				redraw();
			}
		});

		this.canvasPanel.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.canvasPanel.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				redraw();
			}
		});
		
		uiLayout.set(this.editor, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f); 
	}
	
	private void updateScroll(){
		if( this.clientArea != null ){
			UIScrollBar vBar = this.canvasPanel.getVScroll();
			UIScrollBar hBar = this.canvasPanel.getHScroll();
			vBar.setMaximum(Math.max(Math.round(this.height - this.clientArea.getHeight()), 0));
			vBar.setThumb(Math.round(this.clientArea.getHeight()));
			
			hBar.setMaximum(Math.max(Math.round(this.width - this.clientArea.getWidth()), 0));
			hBar.setThumb(Math.round(this.clientArea.getWidth()));
		}
	}
	
	private int getValueAt(float y){
		if(this.clientArea == null || (y - BORDER_HEIGHT) < 0 || y + BORDER_HEIGHT > this.clientArea.getHeight()){
			return -1;
		}
		int scroll = this.canvasPanel.getVScroll().getValue();
		int value = (this.maxNote -  ((int)(  (y + scroll - BORDER_HEIGHT)  / this.lineHeight)) );
		return value;
	}
	
	private long getStartAt(float x){
		TGMeasure measure = getMeasure();
		float posX = (x + this.canvasPanel.getHScroll().getValue());
		long start =(long) (measure.getStart() + (((posX - this.leftSpacing) * measure.getLength()) / (this.timeWidth * measure.getTimeSignature().getNumerator())));
		return start;
	}
	
	private void paintEditor(UIPainter painter) {
		this.clientArea = this.editor.getBounds();
		
		if( this.clientArea != null ){
			float zoom = this.dialog.getDeviceZoom() / 100f;
			UIImage buffer = getBuffer(zoom);
			
			this.width = this.bufferWidth;
			this.height = (this.bufferHeight + (BORDER_HEIGHT *2));
			
			this.updateScroll();
			int scrollX = this.canvasPanel.getHScroll().getValue();
			int scrollY = this.canvasPanel.getVScroll().getValue();
			
			painter.drawImage(buffer, -scrollX * zoom,(BORDER_HEIGHT - scrollY) * zoom);
			this.paintMeasure(painter,(-scrollX), (BORDER_HEIGHT - scrollY), zoom);
			this.paintSelection(painter, (-scrollX), (BORDER_HEIGHT - scrollY), zoom);
			this.paintBorders(painter,(-scrollX),0, zoom);
			this.paintPosition(painter,(-scrollX),0, zoom);
		}
	}
	
	private UIImage getBuffer(float zoom){
		if( this.clientArea != null ){
			this.bufferDisposer.update(this.clientArea.getWidth() * zoom, this.clientArea.getHeight() * zoom);
			if(this.buffer == null || this.buffer.isDisposed()){
				UIFactory uiFactory = getUIFactory();
				
				String[] names = null;
				TGMeasure measure = getMeasure();
				this.maxNote = 0;
				this.minNote = 127;
				if( TuxGuitar.getInstance().getSongManager().isPercussionChannel(getCaret().getSong(), measure.getTrack().getChannelId()) ){
					names = new String[PERCUSSIONS.length];
					for(int i = 0; i < names.length;i ++){
						this.minNote = Math.min(this.minNote,PERCUSSIONS[i].getValue());
						this.maxNote = Math.max(this.maxNote,PERCUSSIONS[i].getValue());
						names[i] = PERCUSSIONS[names.length - i -1].getName();
					}
				}else{
					for(int sNumber = 1; sNumber <= measure.getTrack().stringCount();sNumber ++){
						TGString string = measure.getTrack().getString(sNumber);
						this.minNote = Math.min(this.minNote,string.getValue());
						this.maxNote = Math.max(this.maxNote,(string.getValue() + 20));
					}
					names = new String[this.maxNote - this.minNote + 1];
					for(int i = 0; i < names.length;i ++){
						names[i] = (NOTE_NAMES[ (this.maxNote - i) % 12] + ((this.maxNote - i) / 12 ) );
					}
				}
				
				float minimumNameWidth = 110;
				float minimumNameHeight = 1;
				UIImage auxImage = uiFactory.createImage(1f, 1f);
				UIPainter auxPainter = auxImage.createPainter();
				UIFont font = this.config.getFont();
				UIFont scaledFont = this.getUIFactory().createFont(font.getName(), font.getHeight() * zoom, font.isBold(), font.isItalic());
				auxPainter.setFont(scaledFont);
				for(int i = 0; i < names.length;i ++){
					float fmWidth = auxPainter.getFMWidth(names[i]) / zoom;
					if( fmWidth > minimumNameWidth ){
						minimumNameWidth = fmWidth;
					}
					float fmHeight = auxPainter.getFMHeight() / zoom;
					if( fmHeight > minimumNameHeight ){
						minimumNameHeight = fmHeight;
					}
				}
				auxPainter.dispose();
				auxImage.dispose();
				
				int cols = measure.getTimeSignature().getNumerator();
				int rows = (this.maxNote - this.minNote);
				
				this.leftSpacing = minimumNameWidth + 10;
				this.lineHeight = Math.max(minimumNameHeight,( (this.clientArea.getHeight() - (BORDER_HEIGHT * 2.0f))/ (rows + 1.0f)));
				this.timeWidth = Math.max((10 * (TGDuration.SIXTY_FOURTH / measure.getTimeSignature().getDenominator().getValue())),( (this.clientArea.getWidth() - this.leftSpacing) / cols)  );
				this.bufferWidth = this.leftSpacing + (this.timeWidth * cols);
				this.bufferHeight = (this.lineHeight * (rows + 1));
				this.buffer = uiFactory.createImage(this.bufferWidth * zoom, this.bufferHeight * zoom);
				
				UIPainter painter = this.buffer.createPainter();
				painter.setFont(scaledFont);
				painter.setForeground(this.config.getColorForeground());
				
				for(int i = 0; i <= rows; i++){
					painter.setBackground(this.config.getColorLine( i % 2 ));
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(0 ,(i * this.lineHeight) * zoom,this.bufferWidth * zoom, this.lineHeight * zoom);
					painter.closePath();
					painter.drawString(names[i],5 * zoom, ((i * this.lineHeight) + (this.lineHeight / 2f) + painter.getFMMiddleLine() / zoom) * zoom);
				}
				for(int i = 0; i < cols; i ++){
					float colX = this.leftSpacing + (i * this.timeWidth);
					float divisionWidth = ( this.timeWidth / this.grids );
					for( int j = 0; j < this.grids; j ++ ){
						if( j == 0 ){
							painter.setLineStyleSolid();
						}else{
							painter.setLineStyleDot();
						}
						painter.initPath();
						painter.setAntialias(false);
						painter.moveTo((colX + (j * divisionWidth)) * zoom ,0);
						painter.lineTo((colX + (j * divisionWidth)) * zoom ,this.bufferHeight * zoom);
						painter.closePath();
					}
				}
				scaledFont.dispose();
				painter.dispose();
			}
		}
		return this.buffer;
	}
	
	private void paintMeasure(UIPainter painter,float fromX, float fromY, float zoom){
		if( this.clientArea != null ){
			TGMeasure measure = getMeasure();
			if(measure != null){
				Iterator<TGBeat> it = measure.getBeats().iterator();
				while(it.hasNext()){
					TGBeat beat = (TGBeat)it.next();
					paintBeat(painter, measure, beat, fromX, fromY, zoom);
				}
			}
		}
	}
	
	private void paintBeat(UIPainter painter,TGMeasure measure,TGBeat beat,float fromX, float fromY, float zoom){
		if( this.clientArea != null ){
			float minimumY = BORDER_HEIGHT;
			float maximumY = (this.clientArea.getHeight() - BORDER_HEIGHT);
			
			for( int v = 0; v < beat.countVoices(); v ++ ){
				TGVoice voice = beat.getVoice(v);
				for( int i = 0 ; i < voice.countNotes() ; i ++){
					TGNoteImpl note = (TGNoteImpl)voice.getNote(i);
					float x1 = (fromX + this.leftSpacing + (((beat.getStart() - measure.getStart()) * (this.timeWidth * measure.getTimeSignature().getNumerator())) / measure.getLength()) + 1);
					float y1 = (fromY + (((this.maxNote - this.minNote) - (note.getRealValue() - this.minNote)) * this.lineHeight) + 1 );
					float x2 = (x1 + ((voice.getDuration().getTime() * this.timeWidth) / measure.getTimeSignature().getDenominator().getTime()) - 2 );
					float y2 = (y1 + this.lineHeight - 2 );
					
					if( y1 >= maximumY || y2 <= minimumY){
						continue;
					}
					
					y1 = ( y1 < minimumY ? minimumY : y1 );
					y2 = ( y2 > maximumY ? maximumY : y2 );
					
					if((x2 - x1) > 0 && (y2 - y1) > 0){
						painter.setBackground( (note.getBeatImpl().isPlaying(TuxGuitar.getInstance().getTablatureEditor().getTablature().getViewLayout()) ? this.config.getColorPlay():this.config.getColorNote()));
						painter.initPath(UIPainter.PATH_FILL);
						painter.setAntialias(false);
						painter.addRectangle(x1 * zoom, y1 * zoom, (x2 - x1) * zoom, (y2 - y1) * zoom);
						painter.closePath();
					}
				}
			}
		}
	}
	
	private void paintBorders(UIPainter painter,float fromX, float fromY, float zoom){
		if( this.clientArea != null ){
			painter.setBackground(this.config.getColorBorder());
			painter.initPath(UIPainter.PATH_FILL);
			painter.setAntialias(false);
			painter.addRectangle(fromX * zoom, fromY * zoom, this.bufferWidth * zoom, BORDER_HEIGHT * zoom);
			painter.addRectangle(fromX * zoom, (fromY + (this.clientArea.getHeight() - BORDER_HEIGHT)) * zoom, this.bufferWidth  * zoom, BORDER_HEIGHT * zoom);
			painter.closePath();
			
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(fromX * zoom, fromY * zoom, this.width * zoom, this.clientArea.getHeight() * zoom);
			painter.closePath();
		}
	}
	
	private void paintPosition(UIPainter painter,float fromX, float fromY, float zoom){
		if( this.clientArea != null && !TuxGuitar.getInstance().getPlayer().isRunning()){
			Caret caret = getCaret();
			TGMeasure measure = getMeasure();
			TGBeat beat = caret.getSelectedBeat();
			if(beat != null){
				float x = (((beat.getStart() - measure.getStart()) * (this.timeWidth * measure.getTimeSignature().getNumerator())) / measure.getLength());
				float width = (beat.getVoice(caret.getVoice()).getDuration().getTime() * this.timeWidth) / measure.getTimeSignature().getDenominator().getTime();
				float height = BORDER_HEIGHT * zoom;
				painter.setBackground(this.config.getColorPosition());
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle((fromX + (this.leftSpacing + x)) * zoom,  fromY * zoom, width, height);
				painter.closePath();
				
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle((fromX + (this.leftSpacing + x)) * zoom, (fromY + (this.clientArea.getHeight() - BORDER_HEIGHT)) * zoom, width, height);
				painter.closePath();
			}
		}
	}
	
	private void paintSelection(UIPainter painter, float fromX, float fromY, float zoom){
		if( this.clientArea != null && !TuxGuitar.getInstance().getPlayer().isRunning()){
			if( this.selection >= this.minNote && this.selection <= this.maxNote){
				float x = fromX * zoom;
				float y = (fromY + ((this.maxNote - this.selection) * this.lineHeight)) * zoom;
				float width = this.bufferWidth * zoom;
				float height = this.lineHeight * zoom;
				
				painter.setAlpha(100);
				painter.setBackground(this.config.getColorLine(2));
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x,y,width,height);
				painter.closePath();
				painter.setAlpha(255);
			}
		}
	}
	
	private void updateSelection(float y){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			int previousSelection = this.selection;
			this.selection = getValueAt(y);
			
			if( this.selection != previousSelection ){
				this.redraw();
			}
		}
	}
	
	private void hit(float x, float y){
		if(!TuxGuitar.getInstance().getPlayer().isRunning()){
			int value = getValueAt(y);
			long start = getStartAt(x);
			Caret caret = getCaret();
			TGMeasure measure = getMeasure();
			TGSongManager songManager = TGDocumentManager.getInstance(this.context).getSongManager();
			TGVoice voice = songManager.getMeasureManager().getVoiceIn(measure, start, caret.getVoice());
			
			if( value >= this.minNote && value <= this.maxNote ){
				if( start >= measure.getStart() && voice != null ){
					if(!removeNote(voice.getBeat(), value)){
						addNote(voice.getBeat(), start, value);
					}
				}else{
					play(value);
				}
			}
			else if( voice != null ){
				moveTo(voice.getBeat());
			}
		}
	}
	
	private boolean removeNote(TGBeat beat,int value) {
		TGMeasure measure = getMeasure();
		
		for(int v = 0; v < beat.countVoices(); v ++){
			TGVoice voice = beat.getVoice( v );
			Iterator<TGNote> it = voice.getNotes().iterator();
			while (it.hasNext()) {
				TGNoteImpl note = (TGNoteImpl) it.next();
				if( note.getRealValue() == value ) {
					TGString string = measure.getTrack().getString(note.getString());
					
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
					tgActionProcessor.process();
					
					this.moveTo(beat, string);
					
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean addNote(TGBeat beat, long start, int value) {
		if( beat != null ){
			TGMeasure measure = getMeasure();
			
			List<TGString> strings = measure.getTrack().getStrings();
			for(int i = 0;i < strings.size();i ++){
				TGString string = (TGString)strings.get(i);
				if(value >= string.getValue()){
					boolean emptyString = true;
					
					for(int v = 0; v < beat.countVoices(); v ++){
						TGVoice voice = beat.getVoice( v );
						Iterator<TGNote> it = voice.getNotes().iterator();
						while (it.hasNext()) {
							TGNoteImpl note = (TGNoteImpl) it.next();
							if (note.getString() == string.getNumber()) {
								emptyString = false;
								break;
							}
						}
					}
					if( emptyString ){
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, start);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
						tgActionProcessor.process();
						
						this.moveTo(beat, string);
						
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void moveTo(TGBeat beat) {
		this.moveTo(beat, null);
	}
	
	private void moveTo(TGBeat beat, TGString string) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGMoveToAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		if( string != null ) {
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		}
		tgActionProcessor.process();
	}
	
	private void play(final int value){
		new Thread(new Runnable() {
			public void run() {
				TGTrack tgTrack = getMeasure().getTrack();
				TGChannel tgChannel = TuxGuitar.getInstance().getSongManager().getChannel(tgTrack.getSong(), tgTrack.getChannelId());
				if( tgChannel != null ){
					int volume = TGChannel.DEFAULT_VOLUME;
					int balance = TGChannel.DEFAULT_BALANCE;
					int chorus = tgChannel.getChorus();
					int reverb = tgChannel.getReverb();
					int phaser = tgChannel.getPhaser();
					int tremolo = tgChannel.getTremolo();
					int channel = tgChannel.getChannelId();
					int program = tgChannel.getProgram();
					int bank = tgChannel.getBank();
					int[][] beat = new int[][]{ new int[]{ (tgTrack.getOffset() + value) , TGVelocities.DEFAULT } };
					TuxGuitar.getInstance().getPlayer().playBeat(channel,bank,program, volume, balance,chorus,reverb,phaser,tremolo,beat);
				}
			}
		}).start();
	}
	
	private int loadGrids(){
		int grids = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.MATRIX_GRIDS);
		// check if is valid value
		for(int i = 0 ; i < DIVISIONS.length ; i ++ ){
			if(grids == DIVISIONS[i]){
				return grids;
			}
		}
		return DIVISIONS[1];
	}
	
	private void setGrids(int grids){
		this.grids = grids;
		this.disposeBuffer();
		this.redraw();
	}
	
	public int getGrids(){
		return this.grids;
	}
	
	private TGMeasure getMeasure(){
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			TGMeasure measure = TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			if(measure != null){
				return measure;
			}
		}
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
	}
	
	private Caret getCaret(){
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void redraw(){
		if(!isDisposed()){
			this.editor.redraw();
			this.toolbar.update();
		}
	}
	
	public void redrawPlayingMode() {
		this.redraw();
	}
	
	private void configure(){
		this.config.configure(this.dialog);
	}
	
	public void reloadFromConfig(){
		this.disposeBuffer();
		this.redraw();
	}
	
	private void layout(){
		if(!this.isDisposed() ){
			this.composite.layout();
		}
	}
	
	public void loadIcons(){
		if(!this.isDisposed() ){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.toolbar.loadIcons();;
			this.layout();
			this.redraw();
		}
	}
	
	public void loadProperties() {
		if(!this.isDisposed() ){
			this.dialog.setText(TuxGuitar.getProperty("matrix.editor"));
			this.gridsLabel.setText(TuxGuitar.getProperty("matrix.grids"));
			this.toolbar.loadProperties();
			this.disposeBuffer();
			this.layout();
			this.redraw();
		}
	}
	
	public void dispose(){
		if(!this.isDisposed()){
			this.dialog.dispose();
		}
	}
	
	private void disposeBuffer(){
		if( this.buffer != null && !this.buffer.isDisposed()){
			this.buffer.dispose();
			this.buffer = null;
		}
	}
	
	private void disposeAll() {
		this.disposeBuffer();
		this.config.dispose();
	}
	
	private UICanvas getEditor() {
		return this.editor;
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}

	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processRedrawEvent(event);
				}
			}
		});
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public static TGMatrixEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMatrixEditor.class.getName(), new TGSingletonFactory<TGMatrixEditor>() {
			public TGMatrixEditor createInstance(TGContext context) {
				return new TGMatrixEditor(context);
			}
		});
	}
	
	private class BufferDisposer {
		private int numerator;
		private int denominator;
		private int track;
		private boolean percussion;
		
		private float width;
		private float height;
		
		public void update(float width, float height){
			TGMeasure measure = getMeasure();
			int track = measure.getTrack().getNumber();
			int numerator = measure.getTimeSignature().getNumerator();
			int denominator = measure.getTimeSignature().getDenominator().getValue();
			boolean percussion = TuxGuitar.getInstance().getSongManager().isPercussionChannel(measure.getTrack().getSong(), measure.getTrack().getChannelId());
			if( width != this.width || height != this.height || this.track != track || this.numerator != numerator || this.denominator != denominator || this.percussion != percussion ){
				disposeBuffer();
			}
			this.track = track;
			this.numerator = numerator;
			this.denominator = denominator;
			this.percussion = percussion;
			this.width = width;
			this.height = height;
		}
	}
	
	private class DisposeListenerImpl implements UIDisposeListener {
		
		public void onDispose(UIDisposeEvent event) {
			TGMatrixEditor.this.disposeAll();
			TGMatrixEditor.this.removeListeners();
			TuxGuitar.getInstance().updateCache(true);
		}
	}
	
	private class TGMatrixMouseListener implements UIMouseUpListener, UIMouseEnterListener, UIMouseExitListener, UIMouseMoveListener {
		
		public TGMatrixMouseListener(){
			super();
		}
		
		public void onMouseUp(UIMouseEvent event) {
			getEditor().setFocus();
			if( event.getButton() == 1 ){
				if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
					hit(event.getPosition().getX(), event.getPosition().getY());
				}
			}
		}
		
		public void onMouseMove(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(event.getPosition().getY());
			}
		}
		
		public void onMouseExit(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				updateSelection(-1);
			}
		}
		
		public void onMouseEnter(UIMouseEvent event) {
			if(!TGEditorManager.getInstance(TGMatrixEditor.this.context).isLocked()){
				redraw();
			}
		}
	}
	
	private class TGMatrixPainterListener implements TGBufferedPainterHandle {
		
		public TGMatrixPainterListener(){
			super();
		}

		public void paintControl(UIPainter painter) {
			TGMatrixEditor.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGMatrixEditor.this.editor;
		}
	}
}
