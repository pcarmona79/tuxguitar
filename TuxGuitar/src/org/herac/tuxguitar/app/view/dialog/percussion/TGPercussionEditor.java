package org.herac.tuxguitar.app.view.dialog.percussion;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPercussionKey;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.*;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TGPercussionEditor implements TGEventListener {

	private static int COLUMNS = 4;
	private static final MidiPercussionKey[] PERCUSSIONS = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
	private final TGContext context;
	private UIWindow dialog;
	private UIPanel composite;

	private Map<Integer, UIToggleButton> buttons;

	public TGPercussionEditor(TGContext context){
		this.context = context;
		this.buttons = new HashMap<>();
	}
	
	public void show(){
		UIFactory factory = getUIFactory();
		this.dialog = factory.createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setText(TuxGuitar.getProperty("percussion.editor"));
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				dispose();
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});

		this.composite = factory.createPanel(this.dialog, false);

		UITableLayout dialogLayout = new UITableLayout();
		this.dialog.setLayout(dialogLayout);
		dialogLayout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UITableLayout compositeLayout = new UITableLayout();
		this.composite.setLayout(compositeLayout);

		final int rows = (int) Math.ceil(PERCUSSIONS.length / ((double) COLUMNS));
		int x = 1;
		int y = 1;
		for (int i = 0; i < PERCUSSIONS.length; i++) {
			final MidiPercussionKey key = PERCUSSIONS[i];
			final UIToggleButton button = factory.createToggleButton(this.composite, true);
			compositeLayout.set(button, y, x, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 1f);
			button.setText(key.getValue() + " " + key.getName());
            button.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					buttonStateChanged(button.isSelected(), (event.getState() & UIKeyEvent.SHIFT) != 0, key.getValue());
				}
			});
			y++;
			if (y > rows) {
				x++;
				y = 1;
			}
			this.buttons.put(key.getValue(), button);
		}

		this.addListeners();

		this.dialog.computePackedSize(null, null);
		UISize minimumSize = this.dialog.getPackedSize();
		this.dialog.setMinimumSize(minimumSize);
		this.dialog.setBounds(new UIRectangle(minimumSize));
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);

	}

	public void addListeners(){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.composite);
		for (UIToggleButton button : buttons.values()) {
			TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(button);
		}
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener( this );
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener( this );
	}

	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void redraw(){
		if(!this.isDisposed()){
			this.updateButtons();
		}
	}
	private boolean canEdit() {
		TuxGuitar tg = TuxGuitar.getInstance();
		Tablature tablature = tg.getTablatureEditor().getTablature();
		TGTrack track = tablature.getCaret().getTrack();
		return !tg.getPlayer().isRunning() && tablature.getSongManager().isPercussionChannel(tablature.getSong(), track.getChannelId());
	}

	public void updateButtons() {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();

		boolean enabled = canEdit();
		for (UIToggleButton button : this.buttons.values()) {
			button.setEnabled(enabled);
			button.setSelected(false);
		}

        TGBeat beat = tablature.getCaret().getSelectedBeat();
        if(beat != null) {
            for (int v = 0; v < beat.countVoices(); v++) {
                TGVoice voice = beat.getVoice(v);
                for (TGNote note : voice.getNotes()) {
					UIToggleButton button = this.buttons.get(note.getValue());
					if (button != null) {
						button.setSelected(true);
					}
                }
            }
        }
	}

	private void buttonStateChanged(boolean selected, boolean shift, int value) {
		if(canEdit() && !TGEditorManager.getInstance(context).isLocked()) {
			if (selected) {
				if (shift) {
					addNote(value);
				} else {
					addNoteAtCaret(value);
				}
			} else {
				removeNote(value);
			}
		}
	}

	private boolean removeNote(int value) {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGBeat beat = tablature.getCaret().getSelectedBeat();
		boolean result = false;
		if(beat != null){
			for(int v = 0; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				for (TGNote note : voice.getNotes()) {
					if( note.getValue() == value) {
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
						tgActionProcessor.process();
						result = true;
					}
				}
			}
		}
		return result;
	}

	private void addNote(int value) {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGTrack track = tablature.getCaret().getTrack();
		TGBeat beat = tablature.getCaret().getSelectedBeat();
		Set<Integer> strings = new HashSet<>();
		for (int i = 0; i < track.stringCount(); i++) {
			strings.add(i + 1);
		}
		if(beat != null){
			for(int v = 0; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				for (TGNote note : voice.getNotes()) {
				    if (note.getValue() == value) {
				    	return;
					}
				    strings.remove(note.getString());
				}
			}
		}
		int stringId = strings.isEmpty() ? track.stringCount() : strings.iterator().next();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, value);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(stringId));
		tgActionProcessor.process();
	}

	private void addNoteAtCaret(int value) {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, value);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, tablature.getCaret().getSelectedString());
		tgActionProcessor.process();
	}

	public void loadIcons(){
		if(!this.isDisposed() ){
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.redraw();
		}
	}
	
	public void loadProperties() {
		if(!this.isDisposed() ){
			this.dialog.setText(TuxGuitar.getProperty("percussion.editor"));
			this.redraw();
		}
	}
	
	public void dispose(){
		if(!this.isDisposed()){
			this.buttons.clear();
			this.dialog.dispose();
		}
	}

	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL || type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redraw();
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
	
	public static TGPercussionEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPercussionEditor.class.getName(), new TGSingletonFactory<TGPercussionEditor>() {
			public TGPercussionEditor createInstance(TGContext context) {
				return new TGPercussionEditor(context);
			}
		});
	}

}
