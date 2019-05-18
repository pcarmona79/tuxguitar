package org.herac.tuxguitar.app.view.dialog.percussion;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGChangePercussionMapAction;
import org.herac.tuxguitar.app.tools.percussion.PercussionEntry;
import org.herac.tuxguitar.app.tools.percussion.PercussionManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.dialog.helper.TGOkCancelDefaults;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.graphics.control.TGPercussionNote;
import org.herac.tuxguitar.player.base.MidiPercussionKey;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGPercussionConfig {

    private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_PERCUSSION);
    private static final int MAX_OCTAVES = 10;
    private static final int MAX_NOTES = 12;

    private static final MidiPercussionKey[] PERCUSSIONS = TuxGuitar.getInstance().getPlayer().getPercussionKeys();
    private static final KindSelect[] KIND_NOTES = new KindSelect[]{
            new KindSelect("percussion.kind.note", TGPercussionNote.KIND_NOTE),
            new KindSelect("percussion.kind.cymbal", TGPercussionNote.KIND_CYMBAL),
            new KindSelect("percussion.kind.diamond", TGPercussionNote.KIND_DIAMOND),
            new KindSelect("percussion.kind.triangle-up", TGPercussionNote.KIND_TRIANGLE_UP),
            new KindSelect("percussion.kind.triangle-down", TGPercussionNote.KIND_TRIANGLE_DOWN),
            new KindSelect("percussion.kind.square", TGPercussionNote.KIND_SQUARE)
    };
    private static final KindSelect[] KIND_ACCENTS = new KindSelect[]{
            new KindSelect("percussion.kind.open", TGPercussionNote.KIND_OPEN),
            new KindSelect("percussion.kind.closed", TGPercussionNote.KIND_CLOSED),
            new KindSelect("percussion.kind.circled", TGPercussionNote.KIND_CIRCLED)
    };

    private TGContext context;
    private UITable<PercussionEntry> table;
    private PercussionEntry[] mapping;
    private UITextField nameEntry;
    private UIDropDownSelect<Integer> noteSelect;
    private UICheckBox shown;
    private List<UIRadioButton> kindNotes;
    private List<UICheckBox> kindAccents;

    public TGPercussionConfig(TGContext context) {
        this.context = context;
        this.kindNotes = new ArrayList<>();
        this.kindAccents = new ArrayList<>();
        this.mapping = new PercussionEntry[TGPercussionNote.NOTE_COUNT];
    }

    private void load() {
        PercussionEntry[] stored = PercussionManager.getInstance(context).getEntries();
        for (int i = 0; i < TGPercussionNote.NOTE_COUNT; i++) {
            this.mapping[i] = new PercussionEntry(stored[i]);
        }
    }

    private void defaults() {
        for (int i = 0; i < TGPercussionNote.NOTE_COUNT; i++) {
            TGPercussionNote note = TGPercussionNote.DEFAULT_MAPPING[i];
            this.mapping[i] = new PercussionEntry("", note.getNote(), note.getKind(), false);
        }
        for (MidiPercussionKey key : PERCUSSIONS) {
            this.mapping[key.getValue()].setName(key.getName());
            this.mapping[key.getValue()].setShown(true);
        }
    }

    private void save(boolean saveToDisk) {
        TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangePercussionMapAction.NAME);
        tgActionProcessor.setAttribute(TGChangePercussionMapAction.ATTRIBUTE_PERCUSSION_MAP, this.mapping);
        tgActionProcessor.setAttribute(TGChangePercussionMapAction.ATTRIBUTE_SAVE, saveToDisk);
        tgActionProcessor.process();
        TGPercussionEditor.getInstance(context).update(this.mapping);
    }

    private void displayItem(PercussionEntry entry) {
        this.noteSelect.setSelectedValue(isNatural(entry.getNote()) ? entry.getNote() : entry.getNote() - 1);
        this.shown.setSelected(entry.isShown());
        setKind(entry.getKind());
        this.nameEntry.setText(entry.getName());
    }

    private int getKind() {
        int kind = 0;
        for (int i = 0; i < kindNotes.size(); i++) {
            if (kindNotes.get(i).isSelected()) {
                kind |= KIND_NOTES[i].flag;
            }
        }
        for (int i = 0; i < kindAccents.size(); i++) {
            if (kindAccents.get(i).isSelected()) {
                kind |= KIND_ACCENTS[i].flag;
            }
        }
        return kind;
    }

    private void setKind(int kind) {
        for (int i = 0; i < kindNotes.size(); i++) {
            kindNotes.get(i).setSelected((kind & KIND_NOTES[i].flag) != 0);
        }
        for (int i = 0; i < kindAccents.size(); i++) {
            kindAccents.get(i).setSelected((kind & KIND_ACCENTS[i].flag) != 0);
        }
    }

    private void entryEdited() {
        this.updateEntry(this.table.getSelectedValue());
    }

    private void updateEntry(PercussionEntry entry) {
        entry.setName(this.nameEntry.getText());
        entry.setNote(this.noteSelect.getSelectedValue());
        entry.setShown(this.shown.isSelected());
        entry.setKind(getKind());
        UITableItem<PercussionEntry> item = this.table.getSelectedItem();
        item.setText(1, entry.getName());
        this.table.updateItem(item);
    }

    private void addTableItems() {
        int selected = this.table.getSelectedIndex();
        this.table.removeItems();
        for (int i = 0; i < this.mapping.length; i++) {
            PercussionEntry entry = this.mapping[i];
            UITableItem<PercussionEntry> item = new UITableItem<>(entry);
            item.setText(0, Integer.toString(i));
            item.setText(1, entry.getName());
            table.addItem(item);
        }
        if (selected == -1) {
            this.table.setSelectedValue(this.mapping[PERCUSSIONS[0].getValue()]);
        } else {
            this.table.setSelectedItem(this.table.getItem(selected));
        }
        this.displayItem(table.getSelectedValue());
    }

    public UIFactory getUIFactory() {
        return TGApplication.getInstance(this.context).getFactory();
    }

    public void configure(UIWindow parent) {
        this.load();

        final UIFactory factory = getUIFactory();
        final UITableLayout windowLayout = new UITableLayout();
        final UIWindow window = factory.createWindow(parent, true, true);
        window.setLayout(windowLayout);
        window.setText(TuxGuitar.getProperty("percussion.settings"));

        // ----------------------------------------------------------------------
        UITableLayout groupLayout = new UITableLayout();
        UIPanel group = factory.createPanel(window, false);
        group.setLayout(groupLayout);
        windowLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);

        table = factory.createTable(group, true);
        table.setColumns(2);
        table.setColumnName(0, "#");
        table.setColumnName(1, TuxGuitar.getProperty("name"));
        table.addSelectionListener(event -> displayItem(table.getSelectedValue()));
        groupLayout.set(table, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
        groupLayout.set(table, UITableLayout.PACKED_HEIGHT, 200f);

        UITableLayout formLayout = new UITableLayout();
        UIPanel form = factory.createPanel(group, false);
        form.setLayout(formLayout);
        groupLayout.set(form, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

        UILabel nameLabel = factory.createLabel(form);
        nameLabel.setText(TuxGuitar.getProperty("name"));
        formLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        this.nameEntry = factory.createTextField(form);
        formLayout.set(this.nameEntry, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
        this.nameEntry.addModifyListener(event -> entryEdited());

        UILabel noteLabel = factory.createLabel(form);
        noteLabel.setText(TuxGuitar.getProperty("percussion.score-note"));
        formLayout.set(noteLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

        this.noteSelect = factory.createDropDownSelect(form);
        String[] tuningTexts = getValueLabels();
        for (int value = 0; value < tuningTexts.length; value++) {
            if (isNatural(value)) {
                this.noteSelect.addItem(new UISelectItem<>(tuningTexts[value], value));
            }
        }
        formLayout.set(this.noteSelect, 2, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false, 1, 1, 80f, null, 2f);
        this.noteSelect.addSelectionListener(event -> entryEdited());

        this.shown = factory.createCheckBox(form);
        this.shown.setText(TuxGuitar.getProperty("percussion.show-in-editor"));
        formLayout.set(this.shown, 3, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, false);
        this.shown.addSelectionListener(event -> entryEdited());

        UILabel kindLabel = factory.createLabel(form);
        kindLabel.setText(TuxGuitar.getProperty("percussion.display-as"));
        formLayout.set(kindLabel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_TOP, false, false);

        UITableLayout displayLayout = new UITableLayout(0f);
        UIPanel display = factory.createPanel(form, false);
        display.setLayout(displayLayout);
        formLayout.set(display, 4, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_TOP, true, true, 1, 1, null, null, 0f);

        kindNotes.clear();
        int row = 1;
        for (KindSelect kind : KIND_NOTES) {
            UIRadioButton button = factory.createRadioButton(display);
            button.setText(TuxGuitar.getProperty(kind.property));
            button.addSelectionListener(event -> {
                entryEdited();
            });
            displayLayout.set(button, row++, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
            kindNotes.add(button);
        }

        kindAccents.clear();
        row = 1;
        for (KindSelect kind : KIND_ACCENTS) {
            UICheckBox button = factory.createCheckBox(display);
            button.setText(TuxGuitar.getProperty(kind.property));
            button.addSelectionListener(event -> entryEdited());
            displayLayout.set(button, row++, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
            kindAccents.add(button);
        }

        // ------------------BUTTONS--------------------------
        TGOkCancelDefaults okCancelDefaults = new TGOkCancelDefaults(context, factory, window,
                () -> {
                    save(true);
                    window.dispose();
                },
                window::dispose,
                () -> {
                    defaults();
                    addTableItems();
                });
        windowLayout.set(okCancelDefaults.getControl(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

        this.addTableItems();

        TGDialogUtil.openDialog(window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
        window.computePackedSize(null, null);
        UISize minimumSize = window.getPackedSize();
        window.setMinimumSize(minimumSize);
    }

    private boolean isNatural(int value) {
        value = value % 12;
        return value == 0 || value == 2 || value == 4 || value == 5 || value == 7 || value == 9 || value == 11;
    }

    private String[] getValueLabels() {
        String[] valueNames = new String[MAX_NOTES * MAX_OCTAVES];
        for (int i = 0; i < valueNames.length; i++) {
            valueNames[i] = this.getValueLabel(i, true);
        }
        return valueNames;
    }

    private String getValueLabel(Integer value, boolean octave) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(NOTE_NAMES[value % NOTE_NAMES.length]);

            if (octave) {
                sb.append(value / MAX_NOTES);
            }
        }
        return sb.toString();
    }

    private static class KindSelect {
        public String property;
        public int flag;

        private KindSelect(String property, int flag) {
            this.property = property;
            this.flag = flag;
        }
    }
}
