package org.herac.tuxguitar.app.view.component.table;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.menu.impl.TrackMenu;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIResource;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTableViewer implements TGEventListener {
	
	private TGContext context;
	private UIScrollBarPanel composite;
	private UIScrollBar hScroll;
	private UIScrollBar vScroll;
	private UIColor[] backgrounds;
	private UIColor[] foregrounds;
	private TrackMenu menu;
	private TGTable table;
	private TGProcess redrawProcess;
	private TGProcess redrawPlayModeProcess;
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateMenuItemsProcess;
	private TGTableColorModel colorModel;
	private int selectedTrack;
	private int selectedMeasure;
	private int trackCount = 0;
	private boolean autoSizeEnabled;
	private boolean update;
	private boolean followScroll;
	private boolean resetTexts;
	private boolean resetColors;

	public TGTableViewer(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
		this.addListeners();
	}
	
	public void addListeners() {
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
	}
	
	public void init(UIContainer parent, boolean visible){
		this.composite = this.getUIFactory().createScrollBarPanel(parent, true, true, false);
		this.addColorModel();
		this.loadConfig();
		this.addLayout();
		this.addTable();
		this.addHScroll();
		this.addVScroll();
		this.updateVisibility(visible);
	}
	
	private void addLayout(){
		getControl().setLayout(new UIScrollBarPanelLayout(false, true, true, true, false, false));
	}
	
	private void addHScroll(){
		this.hScroll = getControl().getHScroll();
		this.hScroll.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTableViewer.this.redrawProcess.process();
			}
		});
	}
	
	private void addVScroll(){
		this.vScroll = getControl().getVScroll();
		this.vScroll.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTableViewer.this.getControl().layout();
			}
		});
	}
	
	private void addTable(){
		UIMouseUpListener listener = mouseFocusListener();
		this.table = new TGTable(this.context, this, getControl());
		this.table.getColumnNumber().getControl().addMouseUpListener(listener);
		this.table.getColumnButtons().getControl().addMouseUpListener(listener);
		this.table.getColumnName().getControl().addMouseUpListener(listener);
		this.table.getColumnInstrument().getControl().addMouseUpListener(listener);
		this.table.getColumnCanvas().getControl().addMouseUpListener(listener);

		this.table.getColumnCanvas().getControl().addResizeListener(new UIResizeListener() {
			public void onResize(UIResizeEvent event) {
				TGTableViewer.this.updateHScroll();
			}
		});
		this.fireUpdate(true);
		this.loadProperties();
		this.loadIcons();
	}
	
	private void addColorModel(){
		this.colorModel = new TGTableColorModel();
	}
	
	public void loadProperties() {
		this.table.loadProperties();
		this.loadMenuProperties();
	}
	
	public void loadIcons() {
	    this.table.loadIcons();
		this.loadMenuIcons();
	}
	
	public void fireUpdate(boolean newSong){
		this.update = true;
		if( newSong ){
			this.trackCount = 0;
		}
	}
	
	public void updateItems(){
		this.resetTexts = true;
		this.followScroll = true;
	}
	
	public void updateHScroll(){
	    TGTrack track = getEditor().getTablature().getCaret().getTrack();
	    if (track == null) {
	    	return;
		}
		int width = Math.round(track.countMeasures() * this.table.getRowHeight());
		int canvasWidth = Math.round(this.table.getColumnCanvas().getControl().getBounds().getWidth());
		this.hScroll.setIncrement(Math.round(this.table.getRowHeight()));
		this.hScroll.setMaximum(Math.max((width - canvasWidth), 0));
		this.hScroll.setThumb(canvasWidth);
	}
	
	public void updateVScroll(){
		this.vScroll.setIncrement(Math.round(this.table.getRowHeight()));
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public TGTable getTable(){
		return this.table;
	}
	
	public TGTableColorModel getColorModel() {
		return this.colorModel;
	}
	
	public int getHScrollSelection(){
		return this.hScroll.getValue();
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.getInstance().getTablatureEditor();
	}
	
	private String getInstrument(TGTrack track){
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
		TGChannel channel = TuxGuitar.getInstance().getSongManager().getChannel(song, track.getChannelId());
		if( channel != null ){
			return ( channel.getName() != null ? channel.getName() : new String() );
		}
		return new String();
	}
	
	private void updateTable(){
		if( this.update ){
			this.updateTableMenu();
			
			TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
			int count = song.countTracks();
			this.table.removeRowsAfter(count);
			for(int i = this.table.getRowCount(); i < count; i ++){
				this.table.createRow();
			}
			for(int i = 0; i < count; i ++){
				final TGTrack track = song.getTrack(i);
				final TGTableRow row = this.table.getRow(i);
				if(row != null){
					row.getControl().setPopupMenu((UIPopupMenu) this.menu.getMenu());
					//Number
					this.updateTableTextRow(row.getNumber(), track, Integer.toString(track.getNumber()));
					
					//Solo-Mute
					this.updateTableButtonsRow(row.getButtons(), track);
					
					//Name
					this.updateTableTextRow(row.getName(), track, track.getName());
					
					//Instrument
					this.updateTableTextRow(row.getInstrument(), track, getInstrument(track));

					row.setMouseUpListenerLabel(new UIMouseUpListener() {
						public void onMouseUp(UIMouseEvent event) {
							row.getPainter().setFocus();
						}
					});
					row.setMouseDownListenerLabel(new UIMouseDownListener() {
						public void onMouseDown(UIMouseEvent event) {
							if( track.getNumber() != getEditor().getTablature().getCaret().getTrack().getNumber() ){
								TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGGoToTrackAction.NAME);
								tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
								tgActionProcessor.process();
							}
						}
					});
					row.setMouseDoubleClickListenerLabel(new UIMouseDoubleClickListener() {
						public void onMouseDoubleClick(UIMouseEvent event) {
							new Thread(new Runnable() {
								public void run() {
									TGActionProcessor tgActionProcessor = new TGActionProcessor(TGTableViewer.this.context, TGGoToTrackAction.NAME);
									tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
									tgActionProcessor.setOnFinish(new Runnable() {
										public void run() {
											new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGOpenTrackPropertiesDialogAction.NAME).process();
										}
									});
									tgActionProcessor.process();
								}
							}).start();
						}
					});
					
					row.setMouseUpListenerCanvas(new UIMouseUpListener() {
						public void onMouseUp(UIMouseEvent event) {
							row.getPainter().setFocus();
						}
					});
					row.setMouseDownListenerCanvas(new UIMouseDownListener() {
						public void onMouseDown(UIMouseEvent event) {
							int index = (int)((event.getPosition().getX() + getHScrollSelection()) / getTable().getRowHeight());
							if( index >= 0 && index < track.countMeasures() ){
								TGMeasure measure = track.getMeasure(index);
								TGBeat beat = TuxGuitar.getInstance().getSongManager().getMeasureManager().getFirstBeat(measure.getBeats());
								if( beat != null ){
									TGActionProcessor tgActionProcessor = new TGActionProcessor(TGTableViewer.this.context, TGMoveToAction.NAME);
									tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
									tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
									tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
									tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(1));
									tgActionProcessor.process();
								}
							}
						}
					});
					
					row.setPaintListenerCanvas(new TGTableCanvasPainter(this,track));
				}
			}
			
			this.table.update();
			this.selectedTrack = 0;
			this.selectedMeasure = 0;
			this.updateVScroll();
			
			if(!this.resizeTable(count) && this.trackCount != count) {
				this.getControl().layout();
			}
			this.trackCount = count;
		}
		this.update = false;
	}
	
	private boolean resizeTable(int trackCount) {
		UIWindow uiWindow = TGWindow.getInstance(this.context).getWindow();
		if(!this.autoSizeEnabled ) {
			Float packedHeight = uiWindow.getLayout().get(this.getControl(), UITableLayout.PACKED_HEIGHT);
			if( packedHeight == null ) {
				uiWindow.getLayout().set(this.getControl(), UITableLayout.PACKED_HEIGHT, 150f);
				uiWindow.layout();
				return true;
			}
		}
		else if(this.trackCount != trackCount){
			uiWindow.getLayout().set(this.getControl(), UITableLayout.PACKED_HEIGHT, null);
			uiWindow.layout();
			return true;
		}
		return false;
	}
	
	private void updateTableRow(TGTableRowCell cell, TGTrack track) {
		cell.setData(TGTrack.class.getName(), track);
		cell.setMenu((UIPopupMenu) this.menu.getMenu());
	}
	
	private void updateTableTextRow(TGTableRowTextCell cell, TGTrack track, String label) {
		cell.setText(label);
		updateTableRow(cell, track);
	}
	
	private void updateTableButtonsRow(TGTableRowButtonsCell cell, TGTrack track) {
		cell.setSolo(track.isSolo());
		cell.setMute(track.isMute());
		cell.setVisible(track.isVisible());
		updateTableRow(cell, track);
	}

	private void updateTableMenu() {
		this.disposeMenu();
		this.createTrackMenu();
	}

	private void updateMenuItems() {
		if( this.menu != null && !this.menu.isDisposed() ) {
			this.menu.update();
		}
	}

	private void loadMenuProperties() {
		if( this.menu != null && !this.menu.isDisposed() ) {
			this.menu.loadProperties();
		}
	}

	private void loadMenuIcons() {
		if( this.menu != null && !this.menu.isDisposed() ) {
			this.menu.loadIcons();
		}
	}
	
	private void resetTextsValues(){
		int rows = this.table.getRowCount();
		for(int i = 0; i < rows; i ++){
			TGTableRow row = this.table.getRow(i);
			
			row.getNumber().setText(Integer.toString(((TGTrack)row.getNumber().getData(TGTrack.class.getName())).getNumber()));
			row.getButtons().setSolo(((TGTrack)row.getButtons().getData(TGTrack.class.getName())).isSolo());
			row.getButtons().setMute(((TGTrack)row.getButtons().getData(TGTrack.class.getName())).isMute());
			row.getButtons().setVisible(((TGTrack)row.getButtons().getData(TGTrack.class.getName())).isVisible());
			row.getName().setText(((TGTrack)row.getName().getData(TGTrack.class.getName())).getName());
			row.getInstrument().setText(getInstrument((TGTrack)row.getInstrument().getData(TGTrack.class.getName())));
		}
	}
	
	private List<UIResource> resetColors() {
		if( this.resetColors || this.backgrounds == null || this.foregrounds == null) {
			this.resetColors = false;
			
			List<UIResource> disposableResources = new ArrayList<UIResource>();
			if( this.backgrounds != null ) {
				for(UIColor uiColor : this.backgrounds) {
					disposableResources.add(uiColor);
				}
			}
			if( this.foregrounds != null ) {
				for(UIColor uiColor : this.foregrounds) {
					disposableResources.add(uiColor);
				}
			}
			
			this.backgrounds = this.colorModel.createBackgrounds(this.context);
			this.foregrounds = this.colorModel.createForegrounds(this.context);
			this.composite.setBgColor(this.backgrounds[4]);
		}
		return null;
	}
	
	private void redrawRows(int selectedTrack) {
		List<UIResource> disposableResources = this.resetColors();
		
		int rows = this.table.getRowCount();
		for(int i = 0; i < rows; i ++){
			TGTableRow row = this.table.getRow(i); 
			row.getPainter().redraw();
			if(this.selectedTrack != selectedTrack){
				row.setBgColor( this.backgrounds[ ((selectedTrack - 1) == i) ? 2: ( i % 2 ) ] );
				row.setFgColor( this.foregrounds[ ((selectedTrack - 1) == i) ? 2: ( i % 2 ) ] );
			}
		}
		
		if( disposableResources != null ) {
			this.disposeResources(disposableResources.toArray(new UIResource[disposableResources.size()]));
		}
	}
	
	public void redraw() {
		if(!isDisposed()){
			this.updateTable();
			int selectedTrack = getEditor().getTablature().getCaret().getTrack().getNumber();
			this.redrawRows(selectedTrack);
			this.selectedTrack = selectedTrack;
			this.selectedMeasure = 0;
			this.updateHScroll();
			
			if( this.resetTexts ){
				this.resetTextsValues();
				this.resetTexts = false;
			}
			if( this.followScroll ){
				this.followHorizontalScroll(getEditor().getTablature().getCaret().getMeasure().getNumber());
				this.followScroll = false;
			}
			getControl().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed()){
			TGMeasure measure =  TuxGuitar.getInstance().getEditorCache().getPlayMeasure();
			if(measure != null && measure.getTrack() != null){
				this.updateTable();
				int selectedTrack = measure.getTrack().getNumber();
				int selectedMeasure = measure.getNumber();
				if(this.selectedTrack != selectedTrack || this.selectedMeasure != selectedMeasure){
					this.redrawRows(selectedTrack);
					this.followHorizontalScroll(selectedMeasure);
					this.table.getColumnCanvas().getControl().redraw();
				}
				this.selectedTrack = selectedTrack;
				this.selectedMeasure = selectedMeasure;
			}
		}
	}
	
	private void followHorizontalScroll(int selectedMeasure){
		int hScrollSelection = this.hScroll.getValue();
		int hScrollThumb = this.hScroll.getThumb();
		
		float measureSize = this.table.getRowHeight();
		float measurePosition = ((selectedMeasure * measureSize) - measureSize);
		
		if((measurePosition - hScrollSelection) < 0 ){
			this.hScroll.setValue(Math.max(Math.round(measurePosition), 0));
		}
		else if((measurePosition + measureSize - hScrollSelection ) > hScrollThumb){
			this.hScroll.setValue(Math.max(Math.round(measurePosition + measureSize - hScrollThumb), 0));
		}
	}
	
	public void loadConfig() {
		this.autoSizeEnabled = TuxGuitar.getInstance().getConfig().getBooleanValue(TGConfigKeys.TABLE_AUTO_SIZE);
		this.colorModel.resetColors(this.context);
		this.resetColors = true;
		this.resetColors();
		this.trackCount = 0;
	}
	
	public UIScrollBarPanel getControl() {
		return this.composite;
	}
	
	public void createTrackMenu() {
		this.menu = new TrackMenu(this.getUIFactory().createPopupMenu(TGWindow.getInstance(this.context).getWindow()), false);
		this.menu.showItems();
		this.menu.update();
	}
	
	public void disposeColors() {
		this.disposeResources(this.backgrounds);
		this.disposeResources(this.foregrounds);
		this.backgrounds = null;
		this.foregrounds = null;
	}
	
	public void disposeMenu() {
		if( this.menu != null && !this.menu.isDisposed() ) {
			this.menu.dispose();
			this.menu = null;
		}
	}
	
	public void disposeResources(UIResource[] resources) {
		for(int i = 0; i < resources.length; i++){
			resources[i].dispose();
		}
	}
	
	public void dispose(){
		if(!this.isDisposed()){
			this.getControl().dispose();
			this.disposeMenu();
			this.disposeColors();
		}
	}
	
	public boolean isDisposed(){
		return (getControl() == null || getControl().isDisposed());
	}
	
	protected int getSelectedTrack(){
		return this.selectedTrack;
	}
	
	public void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redraw();
			}
		});
		this.redrawPlayModeProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redrawPlayingMode();
			}
		});
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		this.updateMenuItemsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				updateMenuItems();
			}
		});
	}
	
	private UIMouseUpListener mouseFocusListener(){
		return new UIMouseUpListener() {
			public void onMouseUp(UIMouseEvent event) {
				TGTable table = getTable();
				if(table != null){
					TGTableRow row = table.getRow( ( getSelectedTrack() - 1 ) );
					if( row != null ){
						row.getPainter().setFocus();
					}
				}
			}
		};
	}

	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redrawProcess.process();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayModeProcess.process();
		}
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItems();
			this.updateMenuItemsProcess.process();
		}else if( type == TGUpdateEvent.SONG_UPDATED ){
			this.fireUpdate( false );
		}else if( type == TGUpdateEvent.SONG_LOADED ){
			this.fireUpdate( true );
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
	}
	
	public static TGTableViewer getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTableViewer.class.getName(), new TGSingletonFactory<TGTableViewer>() {
			public TGTableViewer createInstance(TGContext context) {
				return new TGTableViewer(context);
			}
		});
	}

	public UIColor getBackgroundColor() {
		return this.backgrounds[4];
	}

	public UIColor getBorderColor() {
	    return this.backgrounds[5];
	}

	public UISize getTrimSize() {
        UISize outer = this.composite.getPackedSize();
		UISize inner = this.table.getControl().getPackedSize();
        outer.setWidth(outer.getWidth() - inner.getWidth());
		outer.setHeight(outer.getHeight() - inner.getHeight());
		return outer;
	}

	public void updateVisibility(boolean visible) {
        this.composite.setVisible(visible);
        TGWindow window = TGWindow.getInstance(context);
        window.getTableDivider().setVisible(visible);
        window.getWindow().layout();
	}

    public void toggleVisibility() {
	    this.updateVisibility(!this.isVisible());
    }

    public boolean isVisible() {
		return this.composite.isVisible();
	}
}
