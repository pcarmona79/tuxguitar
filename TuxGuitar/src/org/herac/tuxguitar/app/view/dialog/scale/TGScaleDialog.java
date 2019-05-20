package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.util.ArrayList;
import java.util.List;

public class TGScaleDialog implements TGEventListener {
    private static final String[] INTERVAL_NAMES = new String[]{
            "P1", "m2", "M2", "m3", "M3", "P4", "°5", "P5", "m6", "M6", "m7", "M7"
    };
    private final TGSyncProcess loadPropertiesProcess;
    private final TGSyncProcess loadIconsProcess;
    private final ScaleManager scaleManager;

    private UIWindow dialog;
    private UIListBoxSelect<ScaleInfo> scaleSelect;
    private UIListBoxSelect<Integer> keySelect;
    private UILabel intervalLabel;
    private UIButton buttonAddPreset;
    private UIButton buttonRemovePreset;
    private TGContext context;
    private TGDialogButtons buttons;
    private List<UIToggleButton> intervalButtons;

    private TGScaleDialog(TGContext context) {
        this.context = context;
        this.loadPropertiesProcess = new TGSyncProcess(this.context, this::loadProperties);
        this.loadIconsProcess = new TGSyncProcess(this.context, this::loadIcons);
        this.scaleManager = ScaleManager.getInstance(context);
    }

    public static TGScaleDialog getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, TGScaleDialog.class.getName(), TGScaleDialog::new);
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

        intervalButtons = new ArrayList<>();

        // ----------------------------------------------------------------------
        UITableLayout compositeLayout = new UITableLayout();
        UIPanel composite = uiFactory.createPanel(dialog, false);
        composite.setLayout(compositeLayout);
        dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

        this.keySelect = uiFactory.createListBoxSelect(composite);
        String[] keyNames = scaleManager.getKeyNames();
        for (int i = 0; i < keyNames.length; i++) {
            keySelect.addItem(new UISelectItem<>(keyNames[i], i));
        }
        keySelect.setSelectedValue(scaleManager.getSelectionKey());
        compositeLayout.set(keySelect, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
        compositeLayout.set(keySelect, UITableLayout.PACKED_HEIGHT, 200f);

        this.scaleSelect = uiFactory.createListBoxSelect(composite);
        scaleSelect.addItem(new UISelectItem<>(TuxGuitar.getProperty("scale.custom")));
        for (ScaleInfo scale : scaleManager.getScales()) {
            scaleSelect.addItem(new UISelectItem<>(scale.getName(), scale));
        }
        scaleSelect.setSelectedValue(scaleManager.getSelection());

        keySelect.addSelectionListener(event -> selectScale(scaleSelect.getSelectedValue(), keySelect.getSelectedValue()));
        scaleSelect.addSelectionListener(event -> scaleSelected());

        compositeLayout.set(scaleSelect, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);
        compositeLayout.set(scaleSelect, UITableLayout.PACKED_HEIGHT, 200f);

        //------------------INTERVALS--------------------------
        this.intervalLabel = uiFactory.createLabel(composite);
        compositeLayout.set(intervalLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        UITableLayout intervalLayout = new UITableLayout();
        UIPanel intervals = uiFactory.createPanel(composite, false);
        intervals.setLayout(intervalLayout);
        compositeLayout.set(intervals, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

        int col = 0;
        for (String name : INTERVAL_NAMES) {
            final UIToggleButton button = uiFactory.createToggleButton(intervals, false);
            button.setText(name);
            intervalButtons.add(button);
            final int index = col;
            button.addSelectionListener(event -> intervalSelected(index, button));
            intervalLayout.set(button, 1, ++col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, null);
        }

        UITableLayout presetLayout = new UITableLayout();
        UIPanel presetGroup = uiFactory.createPanel(composite, false);
        presetGroup.setLayout(presetLayout);
        compositeLayout.set(presetGroup, 2, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);

        this.buttonAddPreset = uiFactory.createButton(presetGroup);
        buttonAddPreset.addSelectionListener(event -> openAddPresetDialog());
        presetLayout.set(buttonAddPreset, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

        this.buttonRemovePreset = uiFactory.createButton(presetGroup);
        buttonRemovePreset.addSelectionListener(event -> openRemovePresetDialog());
        presetLayout.set(buttonRemovePreset, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

        //------------------BUTTONS--------------------------
        this.buttons = new TGDialogButtons(uiFactory, dialog, TGDialogButtons.close(dialog::dispose));
        dialogLayout.set(this.buttons.getControl(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

        this.loadProperties();
        this.loadIcons();
        this.updateIntervalButtons();
        this.updatePresetButtons();
        TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
    }

    public void loadProperties() {
        this.scaleSelect.setIndex(0, TuxGuitar.getProperty("scale.custom"));
        this.dialog.setText(TuxGuitar.getProperty("scale.list"));
        this.buttons.loadProperties();
        this.buttonAddPreset.setToolTipText(TuxGuitar.getProperty("scale.add-preset"));
        this.buttonRemovePreset.setToolTipText(TuxGuitar.getProperty("scale.remove-preset"));
        this.intervalLabel.setText(TuxGuitar.getProperty("scale.intervals"));
    }

    public void loadIcons() {

        if (scaleManager.isCustomScale(this.scaleSelect.getSelectedValue())) {
            this.buttonAddPreset.setImage(TuxGuitar.getInstance().getIconManager().getListEdit());
        } else {
            this.buttonAddPreset.setImage(TuxGuitar.getInstance().getIconManager().getListAdd());
        }
        this.buttonRemovePreset.setImage(TuxGuitar.getInstance().getIconManager().getListRemove());
    }

    private void selectScale(TGScale scale) {
        TGActionProcessor.process(context, TGSelectScaleAction.NAME,
                TGSelectScaleAction.ATTRIBUTE_SCALE, scale);
    }

    private void selectScale(ScaleInfo info, Integer key) {
        TGActionProcessor.process(context, TGSelectScaleAction.NAME,
                TGSelectScaleAction.ATTRIBUTE_SCALE_INFO, info,
                TGSelectScaleAction.ATTRIBUTE_KEY, key);
    }

    private void scaleSelected() {
        selectScale(scaleSelect.getSelectedValue(), keySelect.getSelectedValue());
        updateIntervalButtons();
        updatePresetButtons();
    }

    private void updateIntervalButtons() {
        ScaleInfo info = scaleSelect.getSelectedValue();
        TGScale scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
        scale.setNotes(info == null ? 0 : info.getKeys());
        for (int i = 0; i < intervalButtons.size(); i++) {
            intervalButtons.get(i).setSelected(scale.getNote(i));
        }
    }

    private void intervalSelected(int index, UIToggleButton button) {
        TGScale scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
        scale.setNotes(scaleManager.getScale().getNotes());
        scale.setKey(scaleManager.getScale().getKey());
        scale.setNote(index, button.isSelected());
        scaleSelect.setSelectedValue(scaleManager.getScaleInfo(scale));
        selectScale(scale);
        updatePresetButtons();
    }

    private void addPreset(String name) {
        ScaleInfo last = this.scaleSelect.getSelectedValue();
        ScaleInfo created = scaleManager.addCustomScale(name);
        if (created != null) {
            scaleManager.saveCustomScales();
            if (last != null) {
                this.scaleSelect.setItem(this.scaleSelect.getSelectedItem(), created.getName());
            } else {
                this.scaleSelect.addItem(new UISelectItem<>(created.getName(), created));
                scaleSelect.setSelectedValue(created);
            }
            selectScale(scaleManager.getScale());
            updatePresetButtons();
        }
    }

    private void removePreset() {
        ScaleInfo info = this.scaleSelect.getSelectedValue();
        if (scaleManager.removeCustomScale(info)) {
            scaleManager.saveCustomScales();
            this.scaleSelect.removeValue(info);
            this.scaleSelect.setSelectedValue(scaleManager.getSelection());
            updateIntervalButtons();
            updatePresetButtons();
        }
    }

    private void updatePresetButtons() {
        ScaleInfo info = this.scaleSelect.getSelectedValue();
        boolean isCustomScale = scaleManager.isCustomScale(info);
        boolean scaleEmpty = true;
        for (UIToggleButton button : this.intervalButtons) {
            if (button.isSelected()) {
                scaleEmpty = false;
                break;
            }
        }
        this.buttonAddPreset.setEnabled(!scaleEmpty && (info == null || isCustomScale));
        this.buttonRemovePreset.setEnabled(isCustomScale);
        this.loadIcons();
    }

    public boolean isDisposed() {
        return this.dialog == null || this.dialog.isDisposed();
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.dialog.dispose();
        }
    }

    private void openAddPresetDialog() {
        final UIFactory factory = TGApplication.getInstance(context).getFactory();

        final UITableLayout dialogLayout = new UITableLayout();
        final UIWindow nameDialog = factory.createWindow(this.dialog, true, true);
        nameDialog.setText(TuxGuitar.getProperty("scale.add-preset"));
        nameDialog.setLayout(dialogLayout);

        final UIPanel form = factory.createPanel(nameDialog, false);
        final UITableLayout formLayout = new UITableLayout();
        form.setLayout(formLayout);

        final UILabel nameLabel = factory.createLabel(form);
        nameLabel.setText(TuxGuitar.getProperty("name"));
        formLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        final UITextField nameEntry = factory.createTextField(form);
        ScaleInfo current = this.scaleSelect.getSelectedValue();
        if (current != null) {
            nameEntry.setText(current.getName());
        }
        formLayout.set(nameEntry, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

        dialogLayout.set(form, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);

        TGDialogButtons buttons = new TGDialogButtons(factory, nameDialog,
                TGDialogButtons.ok(() -> {
                    addPreset(nameEntry.getText());
                    nameDialog.dispose();
                }),
                TGDialogButtons.cancel(nameDialog::dispose));

        buttons.getButton(0).setEnabled(!nameEntry.getText().isEmpty());
        nameEntry.addModifyListener(event -> buttons.getButton(0).setEnabled(!nameEntry.getText().isEmpty()));
        dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

        TGDialogUtil.openDialog(nameDialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
    }

    private void openRemovePresetDialog() {
        TGActionProcessor.process(context, TGOpenViewAction.NAME,
                TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController(),
                TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("are-you-sure"),
                TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO,
                TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO,
                TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, (Runnable) this::removePreset);
    }

    private void addListeners(){
        TuxGuitar.getInstance().getSkinManager().addLoader(this);
        TuxGuitar.getInstance().getLanguageManager().addLoader(this);
    }

    private void removeListeners(){
        TuxGuitar.getInstance().getSkinManager().removeLoader(this);
        TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
    }

    public void processEvent(final TGEvent event) {
        if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.loadIconsProcess.process();
        } else if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.loadPropertiesProcess.process();
        }
    }
}
