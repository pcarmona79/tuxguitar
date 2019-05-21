package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveChordAction;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;

import java.util.Iterator;
import java.util.List;

public class TGChordDialog {
	
	private UIWindow dialog;
	private TGViewContext context;
	private TGChordEditor editor;
	private TGChordSelector selector;
	private TGChordList list;
	private TGChordRecognizer recognizer;
	private TGCursorController cursorController;
	
	public TGChordDialog(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		final TGTrack track = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGVoice voice = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		
		final UIFactory uiFactory = this.getUIFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		
		TGChordStyleAdapter.appendColors(this.context.getContext());
		
		this.dialog = uiFactory.createWindow(uiParent, true, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("chord.editor"));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getCustomChordManager().write();
			}
		});
		
		UITableLayout topLayout = new UITableLayout();
		UIPanel topComposite = uiFactory.createPanel(this.dialog, false);
		topComposite.setLayout(topLayout);
		dialogLayout.set(topComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout bottomLayout = new UITableLayout();
		UIPanel bottomComposite = uiFactory.createPanel(this.dialog, false);  
		bottomComposite.setLayout(bottomLayout);
		dialogLayout.set(bottomComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		int[] tuning = findCurrentTuning(measure.getTrack());
		
		//---------------SELECTOR--------------------------------
		this.selector = new TGChordSelector(this, topComposite, tuning);
		topLayout.set(this.selector.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//---------------EDITOR--------------------------------
		this.editor = new TGChordEditor(this, topComposite, (short)tuning.length);
		this.editor.setCurrentTrack(measure.getTrack());
		topLayout.set(this.editor.getControl(), 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//---------------RECOGNIZER------------------------------------
		this.recognizer = new TGChordRecognizer(this, topComposite);
		topLayout.set(this.recognizer.getControl(), 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);
		
		//---------------CUSTOM CHORDS---------------------------------
		TGChordCustomList customList = new TGChordCustomList(this, topComposite);
		topLayout.set(customList.getControl(), 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//---------------LIST--------------------------------
		this.list = new TGChordList(this, bottomComposite, beat);
		bottomLayout.set(this.list.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					insertChord(track, measure, beat, voice, getEditor().getChord());
					getWindow().dispose();
				}),
				TGDialogButtons.clean(() -> {
					removeChord(measure, beat);
					getWindow().dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		// load the current chord
		this.editor.setChord(findCurrentChord(measure, beat.getStart()));

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public TGChordEditor getEditor() {
		return this.editor;
	}
	
	public TGChordSelector getSelector() {
		return this.selector;
	}
	
	public TGChordList getList() {
		return this.list;
	}
	
	public TGChordRecognizer getRecognizer() {
		return this.recognizer;
	}
	
	public boolean isDisposed(){
		return this.dialog.isDisposed();
	}
	
	public UIWindow getWindow(){
		return this.dialog;
	}
	
	public void loadCursor(UICursor cursor) {
		if(!this.dialog.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.getContext().getContext(), this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}
	
	public UIColor getColor(String colorId) {
		return TGColorManager.getInstance(this.context.getContext()).getColor(colorId);
	}
	
	private int[] findCurrentTuning(TGTrack track){
		int[] tuning = new int[track.stringCount()];
		Iterator<TGString> it = track.getStrings().iterator();
		while(it.hasNext()){
			TGString string = (TGString)it.next();
			tuning[(tuning.length - string.getNumber())] = string.getValue();
		}
		return tuning;
	}
	
	protected TGChord findCurrentChord(TGMeasure measure, long start){
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		TGChord chord = manager.getMeasureManager().getChord(measure, start);
		if( chord == null ){
			chord = manager.getFactory().newChord(measure.getTrack().stringCount());
			chord.setFirstFret(1);
			List<TGNote> notes = manager.getMeasureManager().getNotes(measure, start);
			if(!notes.isEmpty()){
				int maxValue = -1;
				int minValue = -1;
				
				//verifico el first fret
				Iterator<TGNote> it = notes.iterator();
				while(it.hasNext()){
					TGNote note = (TGNote)it.next(); 
					if( maxValue < 0 || maxValue < note.getValue()){
						maxValue = note.getValue();
					}
					if( note.getValue() > 0 && (minValue < 0 || minValue > note.getValue())){
						minValue = note.getValue();
					}
				}
				if(maxValue > TGChordImpl.MAX_FRETS  && minValue > 0){
					chord.setFirstFret((short)(minValue));
				}
				
				//agrego los valores
				it = notes.iterator();
				while(it.hasNext()){
					TGNote note = (TGNote)it.next();
					chord.addFretValue( ( note.getString() - 1) , note.getValue());
				}
			}
		}
		return chord;
	}
	
	public void insertChord(TGTrack track, TGMeasure measure, TGBeat beat, TGVoice voice, TGChord chord) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, voice);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.processOnNewThread();
	}
	
	public void removeChord(TGMeasure measure, TGBeat beat) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGRemoveChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.processOnNewThread();
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}
	
	public TGViewContext getContext() {
		return this.context;
	}
}