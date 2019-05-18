package org.herac.tuxguitar.app.view.dialog.percussion;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.percussion.PercussionEntry;
import org.herac.tuxguitar.app.tools.percussion.PercussionManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGNoteToolbar;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
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

    private final TGContext context;
    private final TGPercussionConfig config;
    private UIWindow dialog;
    private UIPanel composite;
    private UITableLayout compositeLayout;

    private Map<Integer, UIToggleButton> buttons;
    private TGNoteToolbar toolbar;

    public TGPercussionEditor(TGContext context) {
        this.context = context;
        this.config = new TGPercussionConfig(context);
        this.buttons = new HashMap<>();
    }

    public static TGPercussionEditor getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, TGPercussionEditor.class.getName(), new TGSingletonFactory<TGPercussionEditor>() {
            public TGPercussionEditor createInstance(TGContext context) {
                return new TGPercussionEditor(context);
            }
        });
    }

    public void show() {
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

        UITableLayout dialogLayout = new UITableLayout();
        this.dialog.setLayout(dialogLayout);

        this.toolbar = new TGNoteToolbar(context, factory, this.dialog);
        this.toolbar.getSettings().addSelectionListener(new UISelectionListener() {
            public void onSelect(UISelectionEvent event) {
                configure();
            }
        });
        dialogLayout.set(this.toolbar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

        this.composite = factory.createPanel(this.dialog, false);
        dialogLayout.set(this.composite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

        this.compositeLayout = new UITableLayout();
        this.composite.setLayout(this.compositeLayout);

        this.loadIcons();
        this.loadProperties();
        this.createButtons();
        this.addListeners();

        this.dialog.setBounds(new UIRectangle(this.dialog.getPackedSize()));
        TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);

    }

    public void update(PercussionEntry[] entries) {
        if (!this.isDisposed()) {
            this.createButtons(entries);
            this.composite.layout();
            this.redraw();
        }
    }

    private void createButtons() {
        PercussionEntry[] entries = PercussionManager.getInstance(context).getEntries();
        createButtons(entries);
    }

    private void createButtons(PercussionEntry[] entries) {
        for (UIToggleButton button : this.buttons.values()) {
            button.dispose();
            this.compositeLayout.removeControlAttributes(button);
        }
        this.buttons.clear();

        UIFactory factory = getUIFactory();
        int shownEntries = 0;
        for (PercussionEntry entry : entries) {
            if (entry.isShown()) {
                shownEntries++;
            }
        }

        final int rows = (int) Math.ceil(shownEntries / ((double) COLUMNS));
        int x = 1;
        int y = 1;
        for (int i = 0; i < entries.length; i++) {
            final PercussionEntry entry = entries[i];
            if (entry.isShown()) {
                final int value = i;
                final UIToggleButton button = factory.createToggleButton(this.composite, true);
                this.compositeLayout.set(button, y, x, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 1f);
                button.setText(value + " " + entry.getName());
                button.addSelectionListener(new UISelectionListener() {
                    public void onSelect(UISelectionEvent event) {
                        buttonStateChanged(button.isSelected(), (event.getState() & UIKeyEvent.SHIFT) != 0, value);
                    }
                });
                y++;
                if (y > rows) {
                    x++;
                    y = 1;
                }
                this.buttons.put(value, button);
            }
        }
        this.dialog.computePackedSize(null, null);
        UISize minimumSize = this.dialog.getPackedSize();
        this.dialog.setMinimumSize(minimumSize);
    }

    private void addListeners() {
        TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.composite);
        for (UIToggleButton button : buttons.values()) {
            TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(button);
        }
        TuxGuitar.getInstance().getSkinManager().addLoader(this);
        TuxGuitar.getInstance().getLanguageManager().addLoader(this);
        TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
    }

    private void removeListeners() {
        TuxGuitar.getInstance().getSkinManager().removeLoader(this);
        TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
        TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
    }

    public boolean isDisposed() {
        return (this.dialog == null || this.dialog.isDisposed());
    }

    public void redraw() {
        if (!this.isDisposed()) {
            this.updateButtons();
            this.toolbar.update();
        }
    }

    private boolean canEdit() {
        TuxGuitar tg = TuxGuitar.getInstance();
        Tablature tablature = tg.getTablatureEditor().getTablature();
        TGTrack track = tablature.getCaret().getTrack();
        return !tg.getPlayer().isRunning() && tablature.getSongManager().isPercussionChannel(tablature.getSong(), track.getChannelId());
    }

    private void updateButtons() {
        Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();

        boolean enabled = canEdit();
        for (UIToggleButton button : this.buttons.values()) {
            button.setEnabled(enabled);
            button.setSelected(false);
        }

        TGBeat beat = tablature.getCaret().getSelectedBeat();
        if (beat != null) {
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
        if (canEdit() && !TGEditorManager.getInstance(context).isLocked()) {
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

    private void removeNote(int value) {
        Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
        TGBeat beat = tablature.getCaret().getSelectedBeat();
        if (beat != null) {
            for (int v = 0; v < beat.countVoices(); v++) {
                TGVoice voice = beat.getVoice(v);
                for (TGNote note : voice.getNotes()) {
                    if (note.getValue() == value) {
                        TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
                        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
                        tgActionProcessor.process();
                    }
                }
            }
        }
    }

    private void addNote(int value) {
        Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
        TGTrack track = tablature.getCaret().getTrack();
        TGBeat beat = tablature.getCaret().getSelectedBeat();
        Set<Integer> strings = new HashSet<>();
        for (int i = 0; i < track.stringCount(); i++) {
            strings.add(i + 1);
        }
        if (beat != null) {
            for (int v = 0; v < beat.countVoices(); v++) {
                TGVoice voice = beat.getVoice(v);
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

    public void loadIcons() {
        if (!this.isDisposed()) {
            this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
            this.toolbar.loadIcons();
            this.redraw();
        }
    }

    public void loadProperties() {
        if (!this.isDisposed()) {
            this.dialog.setText(TuxGuitar.getProperty("percussion.editor"));
            this.toolbar.loadProperties();
            this.redraw();
        }
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.buttons.clear();
            this.dialog.dispose();
        }
    }

    private void processRedrawEvent(TGEvent event) {
        int type = event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE);
        if (type == TGRedrawEvent.NORMAL || type == TGRedrawEvent.PLAYING_NEW_BEAT) {
            this.redraw();
        }
    }

    public void processEvent(final TGEvent event) {
        TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
            public void run() {
                if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
                    loadIcons();
                } else if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
                    loadProperties();
                } else if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
                    processRedrawEvent(event);
                }
            }
        });
    }

    public void configure() {
        this.config.configure(TGWindow.getInstance(this.context).getWindow());
    }

    public UIFactory getUIFactory() {
        return TGApplication.getInstance(this.context).getFactory();
    }

}
