package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.action.impl.note.TGPasteNoteOrMeasureAction;
import org.herac.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.action.note.TGCopyNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCutNoteAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGEditToolBarSectionEdit extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "edit";
	
	private UIToolCheckableItem voice1;
	private UIToolCheckableItem voice2;
	private UIToolCheckableItem modeSelection;
	private UIToolCheckableItem modeEdition;
	private UIToolCheckableItem notNaturalKey;
	private UIToolActionItem undo;
	private UIToolActionItem redo;
	private UIToolActionItem cut;
	private UIToolActionItem copy;
	private UIToolActionItem paste;
	private UIButton lyrics;
	private UIButton song;

	public TGEditToolBarSectionEdit(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}
	
	public void createSectionToolBars() {
		UIPanel topPanel = this.createPanel();
		UITableLayout layout = new UITableLayout();
		topPanel.setLayout(layout);

		this.lyrics = getToolBar().getFactory().createButton(topPanel);
		this.lyrics.addSelectionListener(this.createActionProcessor(TGToggleLyricEditorAction.NAME));
		layout.set(this.lyrics, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);

		this.song = getToolBar().getFactory().createButton(topPanel);
		this.song.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		layout.set(this.song, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);

		UIToolBar toolBar = this.createToolBar();
		
		this.voice1 = toolBar.createCheckItem();
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));
		
		this.voice2 = toolBar.createCheckItem();
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));
		
		toolBar.createSeparator();
		
		this.modeSelection = toolBar.createCheckItem();
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		
		this.modeEdition = toolBar.createCheckItem();
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		
		this.notNaturalKey = toolBar.createCheckItem();
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));

		UIToolBar toolBar2 = this.createToolBar();

		this.undo = toolBar2.createActionItem();
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));

		this.redo = toolBar2.createActionItem();
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));

		toolBar2.createSeparator();

		this.cut = toolBar2.createActionItem();
		this.cut.addSelectionListener(this.createActionProcessor(TGCutNoteAction.NAME));

		this.copy = toolBar2.createActionItem();
		this.copy.addSelectionListener(this.createActionProcessor(TGCopyNoteAction.NAME));

		this.paste = toolBar2.createActionItem();
		this.paste.addSelectionListener(this.createActionProcessor(TGPasteNoteOrMeasureAction.NAME));
	}
	
	public void updateSectionItems() {
		TablatureEditor editor = TablatureEditor.getInstance(this.getToolBar().getContext());
		EditorKit editorKit = editor.getTablature().getEditorKit();
		TGUndoableManager undoableManager = TGUndoableManager.getInstance(this.getToolBar().getContext());

		boolean noteSelected = editorKit.getTablature().getCaret().getSelectedNote() != null || editorKit.getTablature().getSelector().isActive();
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		
		this.voice1.setChecked(editor.getTablature().getCaret().getVoice() == 0);
		this.voice1.setEnabled(!running);
		
		this.voice2.setChecked(editor.getTablature().getCaret().getVoice() == 1);
		this.voice2.setEnabled(!running);
		
		this.modeSelection.setChecked(editorKit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);
		
		this.modeEdition.setChecked(editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);
		
		this.notNaturalKey.setChecked(!editorKit.isNatural());
		this.notNaturalKey.setEnabled(!running && editorKit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);

		this.undo.setEnabled(!running && undoableManager.canUndo());
		this.redo.setEnabled(!running && undoableManager.canRedo());

		this.cut.setEnabled(!running && noteSelected);
		this.copy.setEnabled(!running && noteSelected);
		this.paste.setEnabled(!running && TGClipboard.getInstance(this.getToolBar().getContext()).hasContents());

		this.song.setEnabled(!running);
	}
	
	public void loadSectionProperties() {
		this.voice1.setToolTipText(this.getText("edit.voice-1"));
		this.voice2.setToolTipText(this.getText("edit.voice-2"));
		this.modeSelection.setToolTipText(this.getText("edit.mouse-mode-selection"));
		this.modeEdition.setToolTipText(this.getText("edit.mouse-mode-edition"));
		this.notNaturalKey.setToolTipText(this.getText("edit.not-natural-key"));
		this.undo.setToolTipText(this.getText("edit.undo"));
		this.redo.setToolTipText(this.getText("edit.redo"));
		this.cut.setToolTipText(this.getText("edit.cut"));
		this.copy.setToolTipText(this.getText("edit.copy"));
		this.paste.setToolTipText(this.getText("edit.paste"));
		this.lyrics.setText(this.getText("track.lyrics"));
		this.song.setText(this.getText("composition.properties"));
	}
	
	public void loadSectionIcons() {
		this.voice1.setImage(this.getIconManager().getEditVoice1());
		this.voice2.setImage(this.getIconManager().getEditVoice2());
		this.modeSelection.setImage(this.getIconManager().getEditModeSelection());
		this.modeEdition.setImage(this.getIconManager().getEditModeEdition());
		this.notNaturalKey.setImage(this.getIconManager().getEditModeEditionNotNatural());
		this.undo.setImage(this.getIconManager().getEditUndo());
		this.redo.setImage(this.getIconManager().getEditRedo());
		this.cut.setImage(this.getIconManager().getCut());
		this.copy.setImage(this.getIconManager().getCopy());
		this.paste.setImage(this.getIconManager().getPaste());
	}
}
