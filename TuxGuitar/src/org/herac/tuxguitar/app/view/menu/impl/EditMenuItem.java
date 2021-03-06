package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.app.action.impl.selector.TGClearSelectionAction;
import org.herac.tuxguitar.app.action.impl.selector.TGSelectAllAction;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.action.note.TGCopyNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCutNoteAction;
import org.herac.tuxguitar.editor.action.note.TGPasteNoteAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class EditMenuItem extends TGMenuItem{

	private UIMenuSubMenuItem editMenuItem;
	private UIMenuActionItem cut;
	private UIMenuActionItem copy;
	private UIMenuActionItem paste;
	private UIMenuActionItem copyMeasure;
	private UIMenuActionItem pasteMeasure;
	private UIMenuActionItem undo;
	private UIMenuActionItem redo;
	private UIMenuActionItem selectAll;
	private UIMenuActionItem selectNone;
	public UIMenuCheckableItem modeSelection;
	private UIMenuCheckableItem modeEdition;
	private UIMenuCheckableItem notNaturalKey;
	private UIMenuCheckableItem voice1;
	private UIMenuCheckableItem voice2;
	
	public EditMenuItem(UIMenu parent) {
		this.editMenuItem = parent.createSubMenuItem();
	}

	public UIMenuSubMenuItem getMenuItem() {
		return this.editMenuItem;
	}

	public void showItems() {
		//---------------------------------------------------
		//--CUT--
		this.cut = this.editMenuItem.getMenu().createActionItem();
		this.cut.addSelectionListener(this.createActionProcessor(TGCutNoteAction.NAME));
		//--COPY--
		this.copy = this.editMenuItem.getMenu().createActionItem();
		this.copy.addSelectionListener(this.createActionProcessor(TGCopyNoteAction.NAME));
		//--PASTE--
		this.paste = this.editMenuItem.getMenu().createActionItem();
		this.paste.addSelectionListener(this.createActionProcessor(TGPasteNoteAction.NAME));
		//--SEPARATOR
		this.editMenuItem.getMenu().createSeparator();
		//--copy--
		this.copyMeasure = this.editMenuItem.getMenu().createActionItem();
		this.copyMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCopyDialogAction.NAME));
		//--paste--
		this.pasteMeasure = this.editMenuItem.getMenu().createActionItem();
		this.pasteMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasurePasteDialogAction.NAME));
		//--SEPARATOR
		this.editMenuItem.getMenu().createSeparator();
		//--UNDO--
		this.undo = this.editMenuItem.getMenu().createActionItem();
		this.undo.addSelectionListener(this.createActionProcessor(TGUndoAction.NAME));
		//--REDO--
		this.redo = this.editMenuItem.getMenu().createActionItem();
		this.redo.addSelectionListener(this.createActionProcessor(TGRedoAction.NAME));
		//--SEPARATOR
		this.editMenuItem.getMenu().createSeparator();
		//--SELECT ALL--
		this.selectAll = this.editMenuItem.getMenu().createActionItem();
		this.selectAll.addSelectionListener(this.createActionProcessor(TGSelectAllAction.NAME));
		//--SELECT NONE--
		this.selectNone = this.editMenuItem.getMenu().createActionItem();
		this.selectNone.addSelectionListener(this.createActionProcessor(TGClearSelectionAction.NAME));
		//--SEPARATOR
		this.editMenuItem.getMenu().createSeparator();
		//--TABLATURE EDIT MODE
		this.modeSelection = this.editMenuItem.getMenu().createRadioItem();
		this.modeSelection.addSelectionListener(this.createActionProcessor(TGSetMouseModeSelectionAction.NAME));
		//--SCORE EDIT MODE
		this.modeEdition = this.editMenuItem.getMenu().createRadioItem();
		this.modeEdition.addSelectionListener(this.createActionProcessor(TGSetMouseModeEditionAction.NAME));
		//--NATURAL NOTES
		this.notNaturalKey = this.editMenuItem.getMenu().createCheckItem();
		this.notNaturalKey.addSelectionListener(this.createActionProcessor(TGSetNaturalKeyAction.NAME));
		//--SEPARATOR
		this.editMenuItem.getMenu().createSeparator();
		//--VOICE 1
		this.voice1 = this.editMenuItem.getMenu().createRadioItem();
		this.voice1.addSelectionListener(this.createActionProcessor(TGSetVoice1Action.NAME));
		//--VOICE 2
		this.voice2 = this.editMenuItem.getMenu().createRadioItem();
		this.voice2.addSelectionListener(this.createActionProcessor(TGSetVoice2Action.NAME));

		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		EditorKit kit = TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		boolean noteSelected = kit.getTablature().getCaret().getSelectedNote() != null || kit.getTablature().getSelector().isActive();
		this.cut.setEnabled(!running && noteSelected);
		this.copy.setEnabled(!running && noteSelected);
		this.paste.setEnabled(!running && TGClipboard.getInstance(findContext()).hasContents());
		this.copyMeasure.setEnabled(!running);
		this.pasteMeasure.setEnabled(!running && TGClipboard.getInstance(findContext()).getSegment() != null);
		this.undo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canUndo());
		this.redo.setEnabled(!running && TuxGuitar.getInstance().getUndoableManager().canRedo());
		this.modeSelection.setChecked(kit.getMouseMode() == EditorKit.MOUSE_MODE_SELECTION);
		this.modeSelection.setEnabled(!running);
		this.modeEdition.setChecked(kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.modeEdition.setEnabled(!running);
		this.notNaturalKey.setChecked(!kit.isNatural());
		this.notNaturalKey.setEnabled(!running && kit.getMouseMode() == EditorKit.MOUSE_MODE_EDITION);
		this.voice1.setChecked(kit.getTablature().getCaret().getVoice() == 0);
		this.voice2.setChecked(kit.getTablature().getCaret().getVoice() == 1);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.editMenuItem, "edit.menu", null);
		setMenuItemTextAndAccelerator(this.cut, "edit.cut", TGCutNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.copy, "edit.copy", TGCopyNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.paste, "edit.paste", TGPasteNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.copyMeasure, "measure.copy", TGOpenMeasureCopyDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.pasteMeasure, "measure.paste", TGOpenMeasurePasteDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.undo, "edit.undo", TGUndoAction.NAME);
		setMenuItemTextAndAccelerator(this.redo, "edit.redo", TGRedoAction.NAME);
		setMenuItemTextAndAccelerator(this.selectAll, "edit.select-all", TGSelectAllAction.NAME);
		setMenuItemTextAndAccelerator(this.selectNone, "edit.select-none", TGClearSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.modeSelection, "edit.mouse-mode-selection", TGSetMouseModeSelectionAction.NAME);
		setMenuItemTextAndAccelerator(this.modeEdition, "edit.mouse-mode-edition", TGSetMouseModeEditionAction.NAME);
		setMenuItemTextAndAccelerator(this.notNaturalKey, "edit.not-natural-key", TGSetNaturalKeyAction.NAME);
		setMenuItemTextAndAccelerator(this.voice1, "edit.voice-1", TGSetVoice1Action.NAME);
		setMenuItemTextAndAccelerator(this.voice2, "edit.voice-2", TGSetVoice2Action.NAME);
	}

	public void loadIcons(){
	}
}
