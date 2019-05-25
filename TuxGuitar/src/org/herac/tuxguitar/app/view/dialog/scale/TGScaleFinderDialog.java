package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TGScaleFinderDialog implements TGEventListener {

    private static final int MIN_SELECTION = 1;

    private final TGSyncProcess loadPropertiesProcess;
    private final TGSyncProcessLocked updateProcess;
    private final ScaleManager scaleManager;
    private AtomicBoolean killThread;
    private TGContext context;

    private UIWindow dialog;
    private UIListBoxSelect<Result> results;
    private UICheckBox useSelection;
    private UILabel fromLabel;
    private UISpinner fromSpinner;
    private UILabel toLabel;
    private UISpinner toSpinner;
    private UILabel confidenceLabel;
    private UISpinner confidenceSpinner;

    private static class Result {
        public int key;
        public ScaleInfo info;
        public float confidence;

        public Result(int key, ScaleInfo info, float confidence) {
            this.key = key;
            this.info = info;
            this.confidence = confidence;
        }
    };

    private TGScaleFinderDialog(TGContext context) {
        this.context = context;
        this.loadPropertiesProcess = new TGSyncProcess(this.context, this::loadProperties);
        this.updateProcess = new TGSyncProcessLocked(this.context, this::update);
        this.scaleManager = ScaleManager.getInstance(context);
    }

    public static TGScaleFinderDialog getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, TGScaleFinderDialog.class.getName(), TGScaleFinderDialog::new);
    }

    public void show(final TGViewContext viewContext) {
        this.context = viewContext.getContext();
        final UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
        final UIWindow uiParent = viewContext.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
        final UITableLayout dialogLayout = new UITableLayout();
        this.dialog = uiFactory.createWindow(uiParent, false, true);

        this.addListeners();
        this.dialog.addDisposeListener(event -> removeListeners());

        dialog.setLayout(dialogLayout);

        // ----------------------------------------------------------------------
        UITableLayout compositeLayout = new UITableLayout();
        UIPanel composite = uiFactory.createPanel(dialog, false);
        composite.setLayout(compositeLayout);
        dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

        this.results = uiFactory.createListBoxSelect(composite);
        this.results.addSelectionListener(event -> selectScale(this.results.getSelectedValue()));
        compositeLayout.set(results, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
        compositeLayout.set(results, UITableLayout.PACKED_HEIGHT, 200f);

        UIPanel form = uiFactory.createPanel(composite, false);
        compositeLayout.set(form, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
        UITableLayout formLayout = new UITableLayout(0f);
        form.setLayout(formLayout);

        this.confidenceLabel = uiFactory.createLabel(form);
        formLayout.set(confidenceLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        this.confidenceSpinner = uiFactory.createSpinner(form);
        this.confidenceSpinner.setMinimum(1);
        this.confidenceSpinner.setMaximum(100);
        this.confidenceSpinner.setValue(90);
        this.confidenceSpinner.addSelectionListener(event -> update());
        formLayout.set(confidenceSpinner, 2, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, false, 1, 1, 180f, null, null);

        this.useSelection = uiFactory.createCheckBox(form);
        this.useSelection.addSelectionListener(event -> update());
        this.useSelection.setSelected(true);
        formLayout.set(this.useSelection, 3, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        this.fromLabel = uiFactory.createLabel(form);
        formLayout.set(fromLabel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        this.fromSpinner = uiFactory.createSpinner(form);
        this.fromSpinner.addSelectionListener(event -> {
            int fromSelection = fromSpinner.getValue();

            if(fromSelection > toSpinner.getValue()){
                toSpinner.setIgnoreEvents(true);
                toSpinner.setValue(fromSelection);
                toSpinner.setIgnoreEvents(false);
            }
            update();
        });
        formLayout.set(fromSpinner, 4, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false, 1, 1, 180f, null, null);

        this.toLabel = uiFactory.createLabel(form);
        formLayout.set(toLabel, 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        this.toSpinner = uiFactory.createSpinner(form);
        this.toSpinner.addSelectionListener(event -> {
            int toSelection = toSpinner.getValue();

            if(toSelection < fromSpinner.getValue()){
                fromSpinner.setIgnoreEvents(true);
                fromSpinner.setValue(toSelection);
                fromSpinner.setIgnoreEvents(false);
            }
            update();
        });
        formLayout.set(toSpinner, 5, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false, 1, 1, 180f, null, null);

        this.loadProperties();
        this.update();

        final TGBeatRange beats = TablatureEditor.getInstance(this.context).getTablature().getCurrentBeatRange();
        this.fromSpinner.setValue(beats.firstMeasure().getNumber());
        this.toSpinner.setValue(beats.lastMeasure().getNumber());

        TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
    }

    private void selectScale(Result result) {
        TGActionProcessor.process(context, TGSelectScaleAction.NAME,
                TGSelectScaleAction.ATTRIBUTE_SCALE_INFO, result.info,
                TGSelectScaleAction.ATTRIBUTE_KEY, result.key);
    }

    public void update() {
        final Tablature tablature = TablatureEditor.getInstance(this.context).getTablature();
        final TGTrackManager trackManager = tablature.getSongManager().getTrackManager();
        final TGMeasureManager measureManager = tablature.getSongManager().getMeasureManager();
        final Caret caret = tablature.getCaret();
        final TGTrack track = caret.getTrack();
        final int measureCount = track.countMeasures();
        final boolean selectorActive = tablature.getSelector().isActive();

        this.useSelection.setEnabled(selectorActive);

        this.fromSpinner.setMinimum(MIN_SELECTION);
        this.fromSpinner.setMaximum(measureCount);
        this.fromSpinner.setEnabled(!selectorActive || !this.useSelection.isSelected());

        this.toSpinner.setMinimum(MIN_SELECTION);
        this.toSpinner.setMaximum(measureCount);
        this.toSpinner.setEnabled(!selectorActive || !this.useSelection.isSelected());

        final TGNoteRange notes;
        if (selectorActive && this.useSelection.isSelected()) {
            notes = tablature.getCurrentNoteRange();
        } else {
            int voice = caret.getVoice();
            TGBeat start = measureManager.getFirstBeat(trackManager.getMeasure(track, this.fromSpinner.getValue()).getBeats());
            TGBeat end = measureManager.getLastBeat(trackManager.getMeasure(track, this.toSpinner.getValue()).getBeats());
            notes = new TGNoteRange(start, end, Collections.singletonList(voice));
        }

        final float confidence = this.confidenceSpinner.getValue() / 100f;
        final TGFactory factory = tablature.getSongManager().getFactory();
        final UIApplication application = TGApplication.getInstance(this.context).getApplication();
        if (this.killThread != null) {
            this.killThread.set(true);
        }
        this.killThread = new AtomicBoolean(false);
        final AtomicBoolean killSignal = this.killThread;
        TGThreadManager.getInstance(this.context).start(() -> {
            final List<Result> scales = searchScales(scaleManager, factory, track, notes.getNotes(), confidence, killSignal);
            application.runInUiThread(() -> {
                if (this.killThread == killSignal && !killSignal.get()) {
                    this.results.removeItems();
                    for (Result result : scales) {
                        this.results.addItem(new UISelectItem<>(this.createResultLabel(result), result));
                    }
                }
            });
        });
    }


    private String createResultLabel(Result result) {
        return String.format("%s %s (%d%%)", scaleManager.getKeyName(result.key), result.info.getName(), (int) Math.floor(result.confidence * 100f));
    }

    private static List<Result> searchScales(ScaleManager scaleManager, TGFactory factory, TGTrack track, List<TGNote> notes, float confidence, AtomicBoolean killSignal) {
        final int noteCount = notes.size();
        final int threshold = (int) Math.ceil(confidence * noteCount);
        final int needed = noteCount - threshold;

        final List<Result> results = new ArrayList<>();
        final List<ScaleInfo> scales = scaleManager.getScales();

        if (noteCount == 0) {
            return results;
        }
        for (ScaleInfo info : scales) {
            if (info.getKeys() == 0xfff) {
                continue;
            }
            for (int key = 0; key < TGScale.NOTE_COUNT; key++) {
                if (killSignal.get()) {
                    return new ArrayList<>();
                }
                TGScale scale = factory.newScale();
                scale.setKey(key);
                scale.setNotes(info.getKeys());

                int matches = 0;
                for (int i = 0; i < noteCount; i++) {
                    TGNote note = notes.get(i);
                    int value = track.getOffset() + note.getValue() + track.getStrings().get(note.getString() - 1).getValue();
                    if (scale.getNote(value)) {
                        matches++;
                    }
                    int x = i - needed;
                    if (x >= 0 && matches < x) {
                        break;
                    }
                }
                if (matches >= threshold) {
                    results.add(new Result(key, info, matches / ((float) noteCount)));
                }
            }
        }
        results.sort((a, b) -> Float.compare(b.confidence, a.confidence));
        return results;
    }

    public void loadProperties() {
        this.dialog.setText(TuxGuitar.getProperty("scale.finder"));
        this.confidenceLabel.setText(TuxGuitar.getProperty("scale.finder.confidence-threshold"));
        this.useSelection.setText(TuxGuitar.getProperty("scale.finder.use-selection"));
        this.fromLabel.setText(TuxGuitar.getProperty("edit.from"));
        this.toLabel.setText(TuxGuitar.getProperty("edit.to"));
    }

    public boolean isDisposed() {
        return this.dialog == null || this.dialog.isDisposed();
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.dialog.dispose();
        }
    }

    private void addListeners() {
        TuxGuitar.getInstance().getLanguageManager().addLoader(this);
        TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
    }

    private void removeListeners() {
        TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
        TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
    }

    public void processEvent(final TGEvent event) {
        if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.loadPropertiesProcess.process();
        } else if (TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
            int type = event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE);
            if (type == TGUpdateEvent.SONG_LOADED || type == TGUpdateEvent.SONG_UPDATED || type == TGUpdateEvent.SELECTION) {
                this.updateProcess.process();
            }
        }
    }
}
