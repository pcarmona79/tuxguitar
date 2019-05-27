package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.helpers.tuning.TuningGroup;
import org.herac.tuxguitar.song.helpers.tuning.TuningManager;
import org.herac.tuxguitar.song.helpers.tuning.TuningPreset;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

import java.io.File;
import java.util.*;

public class TGTrackTuningPresetSelect {
    private final UIFactory factory;
    private final ArrayList<UIDropDownSelect<TGTrackTuningGroupEntryModel>> selects;
    private final UIPanel panel;
    private final UITableLayout layout;
    private final TGContext context;
    private final TuningManager tuningManager;
    private final UIWindow parentDialog;
    private final UISelectItem<TGTrackTuningGroupEntryModel> customItem;
    private final UISelectItem<TGTrackTuningGroupEntryModel> unsavedItem;
    private final ArrayList<SelectionListener> listeners;
    private final TGTrackTuningGroupModel root;
    private TreeSet<TGTrackTuningPresetModel> presets;
    private TGTrackTuningPresetModel current;
    private TGTrackTuningPresetModel temporary;
    private UIPanel buttonPanel;
    private UIButton buttonAdd;
    private UIButton buttonRemove;

    public TGTrackTuningPresetSelect(TGContext context, UIFactory factory, UIWindow parentDialog, UIContainer parent) {
        this.context = context;
        this.factory = factory;
        this.parentDialog = parentDialog;
        this.listeners = new ArrayList<>();
        this.presets = new TreeSet<>(TGTrackTuningPresetModel::compare);
        this.temporary = new TGTrackTuningPresetModel();
        this.temporary.setProgram(TGChannel.DEFAULT_PROGRAM);
        this.temporary.setClef(TGMeasure.CLEF_TREBLE);
        this.temporary.setFrets(TGTrack.DEFAULT_FRETS);

        this.layout = new UITableLayout(0f);
        this.panel = factory.createPanel(parent, false);
        this.panel.setLayout(this.layout);

        this.tuningManager = TuxGuitar.getInstance().getTuningManager();
        this.tuningManager.loadCustomTunings(getUserFileName());
        int treeDepth = this.tuningManager.getTreeDepth();
        this.root = createModels(this.tuningManager.getBuiltinTunings());
        TGTrackTuningGroupModel customGroup = createModels(this.tuningManager.getCustomTunings());
        TGTrackTuningGroupEntryModel customEntry = new TGTrackTuningGroupEntryModel();
        customEntry.setGroup(customGroup);
        customGroup.setEntry(customEntry);

        this.customItem = new UISelectItem<>(TuxGuitar.getProperty("tuning.preset.custom"), customGroup.getEntry());
        this.unsavedItem = new UISelectItem<>(TuxGuitar.getProperty("tuning.preset.unsaved"));

        TGTrackTuningGroupModel leaf = this.root;
        this.selects = new ArrayList<>(treeDepth);
        for (int i = 0; i < treeDepth; i++) {
            this.selects.add(null); // add placeholder
            createSelect(i, leaf);

            TGTrackTuningGroupModel nextLeaf = null;
            for (TGTrackTuningGroupEntryModel entry : leaf.getChildren()) {
                if (entry.getGroup() != null) {
                    nextLeaf = entry.getGroup();
                    break;
                }
            }
            leaf = nextLeaf;
        }
    }

    private static TGTrackTuningPresetModel createTuningPreset(TuningPreset tuning) {
        int[] values = tuning.getValues();
        TGTrackTuningModel[] models = new TGTrackTuningModel[values.length];
        for(int i = 0 ; i < models.length; i ++) {
            models[i] = new TGTrackTuningModel();
            models[i].setValue(values[i]);
        }
        TGTrackTuningPresetModel preset = new TGTrackTuningPresetModel();
        preset.setName(tuning.getName());
        preset.setValues(models);
        preset.setProgram(tuning.getProgram());
        preset.setClef(tuning.getClef());
        preset.setFrets(tuning.getFrets());
        return preset;
    }

    static private int getIndex(TGTrackTuningPresetModel preset) {
        int index = -1;
        List<TGTrackTuningGroupEntryModel> children = preset.getEntry().getParent().getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getPreset() == preset) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new AssertionError("current preset doesn't exist in custom presets");
        }
        return index;
    }

    static private String getUserFileName() {
        return TGFileUtils.PATH_USER_CONFIG + File.separator + "tunings.xml";
    }

    private void createSelect(int index, TGTrackTuningGroupModel group) {
        UIDropDownSelect<TGTrackTuningGroupEntryModel> select = factory.createDropDownSelect(this.panel);
        this.selects.set(index, select);
        this.layout.set(select, 1+index, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
        if (group != null) {
            this.populateSelect(select, group);
        }
        select.addSelectionListener(event -> presetSelected(select));
    }

    private TGTrackTuningGroupModel createModels(TuningGroup group) {
        if (group == null) {
            return null;
        }
        TGTrackTuningGroupModel model = new TGTrackTuningGroupModel();
        model.setName(group.getName());
        List<TGTrackTuningGroupEntryModel> entries = new ArrayList<>(group.getGroups().size() + group.getTunings().size());
        for (TuningGroup subGroup : group.getGroups()) {
            TGTrackTuningGroupEntryModel entry = new TGTrackTuningGroupEntryModel();
            TGTrackTuningGroupModel subGroupModel = createModels(subGroup);
            subGroupModel.setEntry(entry);
            entry.setGroup(subGroupModel);
            entry.setParent(model);
            entries.add(entry);
        }
        for (TuningPreset tuning : group.getTunings()) {
            TGTrackTuningGroupEntryModel entry = new TGTrackTuningGroupEntryModel();
            TGTrackTuningPresetModel preset = createTuningPreset(tuning);
            preset.setEntry(entry);
            entry.setPreset(preset);
            entry.setParent(model);
            entries.add(entry);
            this.presets.add(preset);
        }
        model.setChildren(entries);
        return model;
    }

    private void populateSelect(UIDropDownSelect<TGTrackTuningGroupEntryModel> select, TGTrackTuningGroupModel group) {
        if (select == null) {
            return;
        }
        select.setIgnoreEvents(true);
        select.removeItems();
        if (group != null) {
            if (group == this.root) {
                select.addItem(this.customItem);
            }
            TGTrackTuningGroupModel customGroup = this.customItem.getValue().getGroup();
            if (group == customGroup) {
                select.addItem(this.unsavedItem);
                if (group.getChildren().isEmpty()) {
                    select.setSelectedItem(this.unsavedItem);
                }
            }
            for (TGTrackTuningGroupEntryModel entry : group.getChildren()) {
                boolean wasEmpty = select.getItemCount() == (group.getEntry() == null || group == customGroup ? 1 : 0);

                String name = "";
                if (entry.getGroup() != null) {
                    name = entry.getGroup().getName();
                } else if (entry.getPreset() != null) {
                    name = this.createLabel(entry.getPreset());
                }
                select.addItem(new UISelectItem<>(name, entry));
                if (wasEmpty) {
                    select.setSelectedValue(entry);
                    if (entry.getPreset() != null) {
                        this.current = entry.getPreset();
                    }
                }
            }
        }
        select.setEnabled(select.getItemCount() > 0);
        select.setIgnoreEvents(false);
    }

    private String createLabel(TGTrackTuningPresetModel tuningPreset) {
        StringBuilder label = new StringBuilder();
        label.append(tuningPreset.getName()).append(" - ");
        TGTrackTuningModel[] values = tuningPreset.getValues();
        for(int i = 0 ; i < values.length; i ++) {
            if( i > 0 ) {
                label.append(" ");
            }
            label.append(TGTrackTuningDialog.getValueLabel(values[values.length - i - 1].getValue()));
        }
        return label.toString();
    }

    private void createButtons() {
        this.buttonPanel = factory.createPanel(this.panel, false);
        UITableLayout buttonLayout = new UITableLayout(0f);
        this.buttonPanel.setLayout(buttonLayout);

        this.buttonAdd = this.factory.createButton(this.buttonPanel);
        this.buttonAdd.addSelectionListener(event -> openAddPresetDialog());
        buttonLayout.set(this.buttonAdd, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 2f);

        this.buttonRemove = this.factory.createButton(this.buttonPanel);
        this.buttonRemove.setImage(TGIconManager.getInstance(this.context).getListRemove());
        this.buttonRemove.setToolTipText(TuxGuitar.getProperty("remove"));
        buttonLayout.set(this.buttonRemove, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 2f);
        this.buttonRemove.addSelectionListener(event -> openRemovePresetDialog());

        this.layout.set(this.buttonPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
        this.panel.layout();
    }

    private void openAddPresetDialog() {
        final UITableLayout dialogLayout = new UITableLayout();
        final UIWindow nameDialog = factory.createWindow(this.parentDialog, true, true);
        nameDialog.setText(TuxGuitar.getProperty("tuning.add-preset"));
        nameDialog.setLayout(dialogLayout);

        final UIPanel form = factory.createPanel(nameDialog, false);
        final UITableLayout formLayout = new UITableLayout();
        form.setLayout(formLayout);

        final UILabel nameLabel = factory.createLabel(form);
        nameLabel.setText(TuxGuitar.getProperty("name"));
        formLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        final UITextField nameEntry = factory.createTextField(form);
        if (this.current != null) {
            nameEntry.setText(this.current.getName());
        }
        formLayout.set(nameEntry, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

        UILabel programLabel = factory.createLabel(form);
        programLabel.setText(TuxGuitar.getProperty("instrument.program"));
        formLayout.set(programLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        final UIDropDownSelect<Short> program = factory.createDropDownSelect(form);
        MidiInstrument[] instruments = MidiPlayer.getInstance(this.context).getInstruments();
        if (instruments != null) {
            int count = instruments.length;
            if (count > 128) {
                count = 128;
            }
            for (short i = 0; i < count; i++) {
                program.addItem(new UISelectItem<>(instruments[i].getName(), i));
            }
        }
        formLayout.set(program, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
        if (this.current != null) {
            program.setSelectedValue(this.current.getProgram());
        } else {
            program.setSelectedValue(this.temporary.getProgram());
        }

        UILabel clefLabel = factory.createLabel(form);
        clefLabel.setText(TuxGuitar.getProperty("composition.clef"));
        formLayout.set(clefLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        final UIDropDownSelect<Integer> clefs = factory.createDropDownSelect(form);
        clefs.addItem(new UISelectItem<>(TuxGuitar.getProperty("composition.clef.treble"), TGMeasure.CLEF_TREBLE));
        clefs.addItem(new UISelectItem<>(TuxGuitar.getProperty("composition.clef.bass"), TGMeasure.CLEF_BASS));
        clefs.addItem(new UISelectItem<>(TuxGuitar.getProperty("composition.clef.tenor"), TGMeasure.CLEF_TENOR));
        clefs.addItem(new UISelectItem<>(TuxGuitar.getProperty("composition.clef.alto"), TGMeasure.CLEF_ALTO));
        formLayout.set(clefs, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
        if (this.current != null) {
            clefs.setSelectedValue(this.current.getClef());
        } else {
            clefs.setSelectedValue(this.temporary.getClef());
        }

        UILabel fretsLabel = factory.createLabel(form);
        fretsLabel.setText(TuxGuitar.getProperty("tuning.frets"));
        formLayout.set(fretsLabel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);


        UISpinner fretsSpinner = factory.createSpinner(form);
        fretsSpinner.setMinimum(TGTrack.MIN_FRETS);
        fretsSpinner.setMaximum(TGTrack.MAX_FRETS);
        if (this.current != null) {
            fretsSpinner.setValue(this.temporary.getFrets());
        } else {
            fretsSpinner.setValue(this.temporary.getFrets());
        }
        formLayout.set(clefs, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 80f, null, 0f);

        dialogLayout.set(form, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);

        TGDialogButtons buttons = new TGDialogButtons(factory, nameDialog,
                TGDialogButtons.ok(() -> {
                    addPreset(nameEntry.getText(), program.getSelectedValue(), clefs.getSelectedValue(), fretsSpinner.getValue());
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

    private TuningPreset newPresetFromTemporary() {
        int[] values = new int[this.temporary.getValues().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.temporary.getValues()[i].getValue();
        }
        return new TuningPreset(this.tuningManager.getCustomTunings(), this.temporary.getName(), values, this.temporary.getProgram(), this.temporary.getClef(), this.temporary.getFrets());
    }


    private void addPreset(String name, short program, int clef, int frets) {
        TuningGroup custom = this.tuningManager.getCustomTunings();
        this.temporary.setName(name);
        this.temporary.setProgram(program);
        this.temporary.setClef(clef);
        this.temporary.setFrets(frets);
        if (this.current != null) {
            this.presets.remove(this.current);
            int index = getIndex(this.current);
            this.temporary.setValues(this.current.getValues());
            custom.getTunings().set(index, newPresetFromTemporary());
            TGTrackTuningGroupEntryModel entry = this.current.getEntry();
            this.current = new TGTrackTuningPresetModel(this.temporary);
            this.current.setEntry(entry);
            entry.setPreset(this.current);
            this.presets.add(this.current);
        } else {
            TuningPreset preset = newPresetFromTemporary();
            custom.getTunings().add(preset);
            TGTrackTuningGroupEntryModel entry = new TGTrackTuningGroupEntryModel();
            this.current = createTuningPreset(preset);
            this.current.setEntry(entry);
            entry.setPreset(this.current);
            entry.setParent(this.customItem.getValue().getGroup());
            this.customItem.getValue().getGroup().getChildren().add(entry);
            this.presets.add(this.current);
        }
        this.tuningManager.saveCustomTunings(getUserFileName());

        this.populateSelect(this.selects.get(1), this.customItem.getValue().getGroup());
        this.setSelectedTuning(Arrays.asList(this.temporary.getValues()));
    }

    private void removePreset() {
        TuningGroup custom = this.tuningManager.getCustomTunings();
        if (this.current != null) {
            int index = getIndex(this.current);
            custom.getTunings().remove(index);
            this.presets.remove(this.current);
            this.current.getEntry().getParent().getChildren().remove(index);
            this.tuningManager.saveCustomTunings(getUserFileName());
            this.populateSelect(this.selects.get(1), this.customItem.getValue().getGroup());
            this.setSelectedTuning(Arrays.asList(this.temporary.getValues()));
        }
    }

    private void updateButtons() {
        if (this.buttonPanel == null) {
            return;
        }
        if (this.current == null) {
            this.buttonAdd.setImage(TGIconManager.getInstance(this.context).getListAdd());
            this.buttonAdd.setToolTipText(TuxGuitar.getProperty("add"));
        } else {
            this.buttonAdd.setImage(TGIconManager.getInstance(this.context).getListEdit());
            this.buttonAdd.setToolTipText(TuxGuitar.getProperty("edit"));
        }
        this.buttonRemove.setEnabled(this.current != null);
    }

    private boolean isCustom(TGTrackTuningGroupEntryModel model) {
        if (model == null) {
            return true;
        }
        while (model != null) {
            if (model == this.customItem.getValue()) {
                return true;
            }
            model = model.getParent().getEntry();
        }
        return false;
    }

    private void update(TGTrackTuningGroupEntryModel model) {
        final boolean isCustom = isCustom(model);
        if (isCustom && this.buttonPanel == null) {
            UIDropDownSelect<TGTrackTuningGroupEntryModel> hideSelect = this.selects.get(2);
            if (hideSelect != null) {
                this.layout.removeControlAttributes(hideSelect);
                hideSelect.dispose();
                this.selects.set(2, null);

                this.createButtons();
                for (int i = 3; i < this.selects.size(); i++) {
                    this.selects.get(i).setVisible(false);
                }
            }
        } else if (!isCustom && this.buttonPanel != null){
            this.buttonPanel.dispose();
            this.layout.removeControlAttributes(this.buttonPanel);
            this.buttonPanel = null;
            this.createSelect(2, null);
            for (int i = 3; i < this.selects.size(); i++) {
                this.selects.get(i).setVisible(true);
            }
            this.panel.layout();
        }
    }

    private void presetSelected(UIDropDownSelect<TGTrackTuningGroupEntryModel> select) {
        this.current = null;
        TGTrackTuningGroupEntryModel model = select.getSelectedValue();
        this.update(model);
        // need to loop one past in order to get the preset
        for (UIDropDownSelect<TGTrackTuningGroupEntryModel> s: this.selects) {
            if (s != null) {
                s.setIgnoreEvents(true);
            }
        }
        for (int i = this.selects.indexOf(select) + 1; i < this.selects.size() + 1; i++) {
            if( model == null ) {
                if (i > 0 && i < this.selects.size()) {
                    UIDropDownSelect<TGTrackTuningGroupEntryModel> child = this.selects.get(i);
                    if (child == null) {
                        break;
                    }
                    this.populateSelect(child, null);
                }
            } else {
                if (model.getPreset() != null) {
                    for (SelectionListener listener : this.listeners) {
                        listener.onSelect(model.getPreset());
                    }
                    this.current = model.getPreset();
                    model = null;
                } else if (model.getGroup() != null && i < this.selects.size()) {
                    UIDropDownSelect<TGTrackTuningGroupEntryModel> child = this.selects.get(i);
                    if (child == null) {
                        break;
                    }
                    this.populateSelect(child, model.getGroup());
                    List<TGTrackTuningGroupEntryModel> entries = model.getGroup().getChildren();
                    if (entries.size() > 0) {
                        model = entries.get(0);
                    } else {
                        model = null;
                    }
                }
            }
        }
        for (UIDropDownSelect<TGTrackTuningGroupEntryModel> s: this.selects) {
            if (s != null) {
                s.setIgnoreEvents(false);
            }
        }
        this.updateButtons();
    }

    TGTrackTuningPresetModel setSelectedTuning(List<TGTrackTuningModel> tuning) {
        TGTrackTuningPresetModel selection = null;
        if (tuning != null) {
                TGTrackTuningModel[] models = new TGTrackTuningModel[tuning.size()];
            for (int i = 0; i < models.length; i++) {
                models[i] = tuning.get(i);
            }
            this.temporary.setValues(models);
            selection = this.presets.floor(this.temporary);
        }

        if (selection != null && TGTrackTuningPresetModel.compare(selection, this.temporary) != 0) {
            selection = null;
        }
        // special case for when different presets have the same tuning
        if (selection != null && this.current != null && TGTrackTuningPresetModel.compare(selection, this.current) == 0) {
            return selection;
        }
        TGTrackTuningGroupEntryModel selectionEntry = selection == null ? null : selection.getEntry();
        this.update(selectionEntry);
        if (selection == null || isCustom(selection.getEntry())) {
            int depth = 0;
            for (UIDropDownSelect<TGTrackTuningGroupEntryModel> select : this.selects) {
                if (depth == 0) {
                    select.setIgnoreEvents(true);
                    select.setSelectedItem(this.customItem);
                    select.setIgnoreEvents(false);
                } else if (depth == 1) {
                    this.populateSelect(select, this.customItem.getValue().getGroup());
                    select.setIgnoreEvents(true);
                    select.setSelectedValue(selectionEntry);
                    select.setIgnoreEvents(false);
                } else {
                    this.populateSelect(select, null);

                }
                depth++;
            }
            this.current = selection;
        } else {
            List<TGTrackTuningGroupModel> path = new ArrayList<>();
            TGTrackTuningGroupModel leaf = selection.getEntry().getParent();
            while (leaf != null) {
                path.add(0, leaf);
                if (leaf.getEntry() != null) {
                    leaf = leaf.getEntry().getParent();
                } else {
                    leaf = null;
                }
            }
            int depth = 0;
            for (UIDropDownSelect<TGTrackTuningGroupEntryModel> select : this.selects) {
                if (depth < path.size()) {
                    this.populateSelect(select, path.get(depth));
                    select.setIgnoreEvents(true);
                    TGTrackTuningGroupEntryModel entry = depth == path.size() - 1 ? selection.getEntry() : path.get(depth + 1).getEntry();
                    select.setSelectedValue(entry);
                    if (entry.getPreset() != null) {
                        this.current = entry.getPreset();
                    }
                    select.setIgnoreEvents(false);
                } else {
                    this.populateSelect(select, null);
                }
                depth++;
            }
        }
        this.updateButtons();
        return selection;
    }

    public void addListener(SelectionListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(SelectionListener listener) {
        this.listeners.remove(listener);
    }

    public UIPanel getControl() {
        return this.panel;
    }

    public interface SelectionListener {
        void onSelect(TGTrackTuningPresetModel preset);
    }
}
