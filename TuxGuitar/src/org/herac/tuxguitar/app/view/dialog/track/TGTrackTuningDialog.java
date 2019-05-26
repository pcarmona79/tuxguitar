package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;

import java.util.ArrayList;
import java.util.List;

public class TGTrackTuningDialog {
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	private TGViewContext context;
	private UIWindow dialog;

	private List<TGTrackTuningModel> initialTuning;
	private List<TGTrackTuningModel> tuning;
	private UITable<TGTrackTuningModel> tuningTable;
	private UICheckBox stringTransposition;
	private UICheckBox stringTranspositionTryKeepString;
	private UICheckBox stringTranspositionApplyToChords;
	private UISpinner offsetSpinner;
	private UICheckBox letRing;
	private UIButton buttonEdit;
	private UIButton buttonDelete;
	private UIButton buttonMoveUp;
	private UIButton buttonMoveDown;
	private TGTrackTuningPresetSelect presets;
	private short program = -1;
	private int clef = -1;

	TGTrackTuningDialog(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		TGSongManager songManager = this.findSongManager();
		TGTrack track = this.findTrack();
		
		if(!songManager.isPercussionChannel(track.getSong(), track.getChannelId())) {
			this.tuning = getTuningFromTrack(track);
			this.initialTuning = getTuningFromTrack(track);

			UIFactory factory = this.getUIFactory();
			UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			UITableLayout dialogLayout = new UITableLayout();
			
			this.dialog = factory.createWindow(parent, true, true);
			this.dialog.setLayout(dialogLayout);
			this.dialog.setText(TuxGuitar.getProperty("tuning"));
			
			UITableLayout leftPanelLayout = new UITableLayout();
			UIPanel leftPanel = factory.createPanel(this.dialog, false);
			leftPanel.setLayout(leftPanelLayout);
			dialogLayout.set(leftPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UITableLayout rightPanelLayout = new UITableLayout();
			UIPanel rightPanel = factory.createPanel(this.dialog, false);
			rightPanel.setLayout(rightPanelLayout);
			dialogLayout.set(rightPanel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);

			this.initTuningTable(leftPanel);
			
			this.initTuningOptions(rightPanel, track);
			
			this.initButtons(dialogLayout);

			this.updateTuningControls();

			TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}

	private void initTuningTable(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.presets = new TGTrackTuningPresetSelect(this.context.getContext(), factory, dialog, panel);
		panelLayout.set(this.presets.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		this.presets.addListener(this::updateTuningFromPreset);

		this.tuningTable = factory.createTable(panel, true);
		this.tuningTable.setColumns(2);
		this.tuningTable.setColumnName(0, TuxGuitar.getProperty("tuning.label"));
		this.tuningTable.setColumnName(1, TuxGuitar.getProperty("tuning.value"));
		this.tuningTable.addMouseDoubleClickListener(event -> onEditTuningModel());
		panelLayout.set(this.tuningTable, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		panelLayout.set(this.tuningTable, UITableLayout.PACKED_WIDTH, 250f);
		panelLayout.set(this.tuningTable, UITableLayout.PACKED_HEIGHT, 200f);

		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttonsPanel = factory.createPanel(panel, false);
		buttonsPanel.setLayout(buttonsLayout);
		panelLayout.set(buttonsPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		
		UIButton buttonAdd = factory.createButton(buttonsPanel);
		buttonAdd.setImage(TGIconManager.getInstance(this.context.getContext()).getListAdd());
		buttonAdd.setToolTipText(TuxGuitar.getProperty("add"));
		buttonAdd.addSelectionListener(event -> onAddTuningModel());

		buttonEdit = factory.createButton(buttonsPanel);
		buttonEdit.setImage(TGIconManager.getInstance(this.context.getContext()).getListEdit());
		buttonEdit.setToolTipText(TuxGuitar.getProperty("edit"));
		buttonEdit.addSelectionListener(event -> onEditTuningModel());

		buttonMoveUp = factory.createButton(buttonsPanel);
		buttonMoveUp.setImage(TGIconManager.getInstance(this.context.getContext()).getArrowUp());
		buttonMoveUp.setToolTipText(TuxGuitar.getProperty("move-up"));
		buttonMoveUp.addSelectionListener(event -> moveString(-1));

		buttonMoveDown = factory.createButton(buttonsPanel);
		buttonMoveDown.setImage(TGIconManager.getInstance(this.context.getContext()).getArrowDown());
		buttonMoveDown.setToolTipText(TuxGuitar.getProperty("move-down"));
		buttonMoveDown.addSelectionListener(event -> moveString(1));

		buttonDelete = factory.createButton(buttonsPanel);
		buttonDelete.setImage(TGIconManager.getInstance(this.context.getContext()).getListRemove());
		buttonDelete.setToolTipText(TuxGuitar.getProperty("remove"));
		buttonDelete.addSelectionListener(event -> onRemoveTuningModel());

		this.tuningTable.addSelectionListener(event -> updateButtons());

		buttonsLayout.set(buttonAdd, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonDelete, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonMoveUp, 1, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		buttonsLayout.set(buttonMoveDown, 1, 4, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonEdit, 1, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
	}

	private void initTuningOptions(UILayoutContainer parent, TGTrack track) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout topLayout = new UITableLayout(0f);
		UIPanel top = factory.createPanel(panel, false);
		top.setLayout(topLayout);
		panelLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true, 1, 1, null, null, 0f);
		
		UITableLayout bottomLayout = new UITableLayout(0f);
		UIPanel bottom = factory.createPanel(panel, false);
		bottom.setLayout(bottomLayout);
		panelLayout.set(bottom, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true, 1, 1, null, null, 0f);
		
		//---------------------------------OFFSET--------------------------------
		UILabel offsetLabel = factory.createLabel(top);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		topLayout.set(offsetLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		
		this.offsetSpinner = factory.createSpinner(top);
		this.offsetSpinner.setMinimum(TGTrack.MIN_OFFSET);
		this.offsetSpinner.setMaximum(TGTrack.MAX_OFFSET);
		this.offsetSpinner.setValue(track.getOffset());
		topLayout.set(this.offsetSpinner, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.letRing = factory.createCheckBox(top);
		this.letRing.setText(TuxGuitar.getProperty("track.letring-throughout"));
		this.letRing.setSelected(track.isLetRing());
		topLayout.set(this.letRing, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		//---------------------------------OPTIONS----------------------------------
		this.stringTransposition = factory.createCheckBox(bottom);
		this.stringTransposition.setText(TuxGuitar.getProperty("tuning.strings.transpose"));
		this.stringTransposition.setSelected( true );
		bottomLayout.set(this.stringTransposition, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTranspositionApplyToChords = factory.createCheckBox(bottom);
		this.stringTranspositionApplyToChords.setText(TuxGuitar.getProperty("tuning.strings.transpose.apply-to-chords"));
		this.stringTranspositionApplyToChords.setSelected( true );
		bottomLayout.set(this.stringTranspositionApplyToChords, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTranspositionTryKeepString = factory.createCheckBox(bottom);
		this.stringTranspositionTryKeepString.setText(TuxGuitar.getProperty("tuning.strings.transpose.try-keep-strings"));
		this.stringTranspositionTryKeepString.setSelected( true );
		bottomLayout.set(this.stringTranspositionTryKeepString, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTransposition.addSelectionListener(event -> {
			UICheckBox stringTransposition = TGTrackTuningDialog.this.stringTransposition;
			UICheckBox stringTranspositionApplyToChords = TGTrackTuningDialog.this.stringTranspositionApplyToChords;
			UICheckBox stringTranspositionTryKeepString = TGTrackTuningDialog.this.stringTranspositionTryKeepString;
			stringTranspositionApplyToChords.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
			stringTranspositionTryKeepString.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
		});
	}
	
	private void initButtons(UITableLayout layout) {
		UIFactory factory = this.getUIFactory();

        TGDialogButtons buttons = new TGDialogButtons(factory, dialog,
                TGDialogButtons.ok(() -> {
					if( updateTrackTuning() ) {
						dialog.dispose();
					}
                }), TGDialogButtons.cancel(dialog::dispose));

        layout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false, 1, 2);
	}
	
	private void onAddTuningModel() {
		new TGTrackTuningChooserDialog(this).select(this::addTuningModel);
	}
	
	private void onEditTuningModel() {
		final TGTrackTuningModel editingModel = this.tuningTable.getSelectedValue();
		if( editingModel != null ) {
			new TGTrackTuningChooserDialog(this).select(model -> {
				editingModel.setValue(model.getValue());
				updateTuningControls();
			}, editingModel);
		}
	}
	
	private void onRemoveTuningModel() {
		TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		if( model != null ) {
			removeTuningModel(model);
		}
	}

	private void moveString(int delta) {
		final TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		if (model != null) {
		    int index = this.tuning.indexOf(model);
		    this.tuning.remove(index);
		    this.tuning.add(index + delta, model);
			this.updateTuningControls();
		}
	}

	private static boolean areTuningsEqual(List<TGTrackTuningModel> a, List<TGTrackTuningModel> b) {
		if(a.size() == b.size()) {
			for(int i = 0 ; i < a.size(); i ++) {
				if(!a.get(i).getValue().equals(b.get(i).getValue())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private void updateTuningTable() {
		TGTrackTuningModel selection = this.tuningTable.getSelectedValue();
		
		this.tuningTable.removeItems();
		for(TGTrackTuningModel model : this.tuning) {
			UITableItem<TGTrackTuningModel> item = new UITableItem<>(model);
			item.setText(0, getValueLabel(model.getValue()));
			item.setText(1, getValueLabel(model.getValue(), true));
			
			this.tuningTable.addItem(item);
		}
		
		if( selection != null ) {
			this.tuningTable.setSelectedValue(selection);
		}
	}

	private void updateButtons() {
		TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		int index = model != null ? this.tuning.indexOf(model) : -1;
		buttonEdit.setEnabled(model != null);
		buttonDelete.setEnabled(model != null);
		buttonMoveUp.setEnabled(model != null && index > 0);
		buttonMoveDown.setEnabled(model != null && index < this.tuning.size() - 1);

		boolean isDefault = areTuningsEqual(this.tuning, this.initialTuning);
		stringTransposition.setEnabled(!isDefault);
		stringTranspositionApplyToChords.setEnabled(!isDefault);
		stringTranspositionTryKeepString.setEnabled(!isDefault);
	}

	private void clearTuningExtras() {
		this.program = -1;
		this.clef = -1;
	}
	
	private void updateTuningControls() {
		this.updateTuningTable();
		if (this.presets.setSelectedTuning(this.tuning) == null) {
			this.clearTuningExtras();
		}
		this.updateButtons();
	}
	
	private static List<TGTrackTuningModel> getTuningFromTrack(TGTrack track) {
		List<TGTrackTuningModel> tuning = new ArrayList<>();
		for(int i = 0; i < track.stringCount(); i ++) {
			TGString string = track.getString(i + 1);
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(string.getValue());
			tuning.add(model);
		}
		return tuning;
	}
	
	private void addTuningModel(TGTrackTuningModel model) {
		this.tuning.add(model);
		this.updateTuningControls();
	}
	
	private void removeTuningModel(TGTrackTuningModel model) {
		if( this.tuning.remove(model)) {
			this.updateTuningControls();
		}
	}

	private void updateTuningModels(List<TGTrackTuningModel> models) {
		this.tuning.clear();
		if( this.tuning.addAll(models)) {
			this.updateTuningControls();
		}
	}
	
	private void updateTuningFromPreset(TGTrackTuningPresetModel preset) {
		List<TGTrackTuningModel> models = new ArrayList<>();
		for(TGTrackTuningModel presetModel : preset.getValues()) {
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(presetModel.getValue());
			models.add(model);
		}
		this.program = preset.getProgram();
		this.clef = preset.getClef();
		this.updateTuningModels(models);
	}
	
	private boolean updateTrackTuning() {
		final TGSongManager songManager = this.findSongManager();
		final TGSong song = this.findSong();
		final TGTrack track = this.findTrack();
		
		final List<TGString> strings = new ArrayList<>();
		for(int i = 0; i < this.tuning.size(); i ++) {
			strings.add(TGSongManager.newString(findSongManager().getFactory(),(i + 1), this.tuning.get(i).getValue()));
		}
		
		final int offset = ((songManager.isPercussionChannel(song, track.getChannelId())) ? 0 : this.offsetSpinner.getValue());
		final boolean letRing = ((songManager.isPercussionChannel(song, track.getChannelId())) ? false : this.letRing.isSelected());
		final boolean offsetChanges = offset != track.getOffset();
		final boolean letRingChanges = letRing != track.isLetRing();
		final boolean tuningChanges = hasTuningChanges(track, strings);
		final boolean transposeStrings = shouldTransposeStrings(track, track.getChannelId());
		final boolean transposeApplyToChords = (transposeStrings && this.stringTranspositionApplyToChords.isSelected());
		final boolean transposeTryKeepString = (transposeStrings && this.stringTranspositionTryKeepString.isSelected());
		
		if( this.validateTrackTuning(strings)) {
			if( tuningChanges || offsetChanges || this.program != -1 || this.clef != -1 ){
				TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGChangeTrackTuningAction.NAME);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);

				if (this.program != -1) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_PROGRAM, this.program);
				}
				if (this.clef != -1) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_CLEF, this.clef);
				}
				if( tuningChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_STRINGS, transposeStrings);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS, transposeTryKeepString);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS, transposeApplyToChords);
				}
				if( offsetChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, offset);
				}
				if( letRingChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_LET_RING, letRing);
				}
				tgActionProcessor.process();
			}
			return true;
		}
		return false;
	}
	
	private boolean validateTrackTuning(List<TGString> strings) {
		if( strings.size() < TGTrack.MIN_STRINGS || strings.size() > TGTrack.MAX_STRINGS ) {
			TGMessageDialogUtil.errorMessage(this.getContext().getContext(), this.dialog, TuxGuitar.getProperty("tuning.strings.range-error", new String[] {Integer.toString(TGTrack.MIN_STRINGS), Integer.toString(TGTrack.MAX_STRINGS)}));
			
			return false;
		}
		return true;
	}
	
	private boolean shouldTransposeStrings(TGTrack track, int selectedChannelId){
		if( this.stringTransposition.isSelected()){
			boolean percussionChannelNew = findSongManager().isPercussionChannel(track.getSong(), selectedChannelId);
			boolean percussionChannelOld = findSongManager().isPercussionChannel(track.getSong(), track.getChannelId());
			
			return (!percussionChannelNew && !percussionChannelOld);
		}
		return false;
	}
	
	private boolean hasTuningChanges(TGTrack track, List<TGString> newStrings){
		List<TGString> oldStrings = track.getStrings();
		//check the number of strings
		if(oldStrings.size() != newStrings.size()){
			return true;
		}
		//check the tuning of strings
		for (TGString oldString : oldStrings) {
			boolean stringExists = false;
			for (TGString newString : newStrings) {
				if (newString.isEqual(oldString)) {
					stringExists = true;
				}
			}
			if (!stringExists) {
				return true;
			}
		}
		return false;
	}

	static String[] getValueLabels() {
		String[] valueNames = new String[MAX_NOTES * MAX_OCTAVES];
		for (int i = 0; i < valueNames.length; i++) {
			valueNames[i] = getValueLabel(i, true);
		}
		return valueNames;
	}
	
	static String getValueLabel(Integer value) {
		return getValueLabel(value, false);
	}
	
	private static String getValueLabel(Integer value, boolean octave) {
		StringBuilder sb = new StringBuilder();
		if( value != null ) {
			sb.append(NOTE_NAMES[value % NOTE_NAMES.length]);
			
			if( octave ) {
				sb.append(value / MAX_NOTES);
			}
		}
		return sb.toString();
	}
	
	private TGSongManager findSongManager() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}
	
	private TGSong findSong() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}
	
	private TGTrack findTrack() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}
	
	public TGViewContext getContext() {
		return this.context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}
	
	public UIWindow getDialog() {
		return this.dialog;
	}
}
