package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import org.herac.tuxguitar.app.action.impl.view.*;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.matrix.TGMatrixEditor;
import org.herac.tuxguitar.app.view.dialog.percussion.TGPercussionEditor;
import org.herac.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionView extends TGMainToolBarSection {
	
	private UIToggleButton showFretBoard;
	private UIToggleButton showPiano;
	private UIToggleButton showInstruments;
	private UIToggleButton showPercussion;
	private UIToggleButton showMatrix;
	private UIToggleButton showEditToolBar;
	private UIToggleButton showTrackTable;
	private UIToggleButton showBrowser;

	public TGMainToolBarSectionView(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		//--FRETBOARD--
		this.showFretBoard = this.createToggleButton();
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));

		//--PIANO--
		this.showPiano = this.createToggleButton();
		this.showPiano.addSelectionListener(this.createActionProcessor(TGTogglePianoEditorAction.NAME));

		//--INSTRUMENTS--
		this.showInstruments = this.createToggleButton();
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));

		//--PERCUSSION--
		this.showPercussion = this.createToggleButton();
		this.showPercussion.addSelectionListener(this.createActionProcessor(TGTogglePercussionEditorAction.NAME));

		//--MATRIX--
		this.showMatrix = this.createToggleButton();
		this.showMatrix.addSelectionListener(this.createActionProcessor(TGToggleMatrixEditorAction.NAME));

		//--BROWSER--
		this.showBrowser = this.createToggleButton();
		this.showBrowser.addSelectionListener(this.createActionProcessor(TGToggleBrowserAction.NAME));

		this.showEditToolBar = this.createToggleButton();
		this.showEditToolBar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));
		this.getLayout().set(this.showEditToolBar, UITableLayout.MARGIN_LEFT, 8f);

		this.showTrackTable = this.createToggleButton();
		this.showTrackTable.addSelectionListener(this.createActionProcessor(TGToggleTableViewerAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties(){
		this.showFretBoard.setToolTipText(this.getText("view.show-fretboard"));
		this.showPiano.setToolTipText(this.getText("view.show-piano"));
		this.showInstruments.setToolTipText(this.getText("view.show-instruments"));
		this.showPercussion.setToolTipText(this.getText("view.show-percussion"));
		this.showMatrix.setToolTipText(this.getText("view.show-matrix"));
		this.showBrowser.setToolTipText(this.getText("tools.browser"));
		this.showEditToolBar.setToolTipText(this.getText("view.show-edit-toolbar"));
		this.showTrackTable.setToolTipText(this.getText("view.show-table-viewer"));
	}
	
	public void loadIcons(){
		this.showFretBoard.setImage(this.getIconManager().getFretboard());
		this.showPiano.setImage(this.getIconManager().getPiano());
		this.showInstruments.setImage(this.getIconManager().getInstruments());
		this.showPercussion.setImage(this.getIconManager().getPercussion());
		this.showMatrix.setImage(this.getIconManager().getMatrix());
		this.showBrowser.setImage(this.getIconManager().getBrowser());
		this.showEditToolBar.setImage(this.getIconManager().getToolbarEdit());
		this.showTrackTable.setImage(this.getIconManager().getTableViewer());
	}
	
	public void updateItems(){
		this.showFretBoard.setSelected(TGFretBoardEditor.getInstance(this.getToolBar().getContext()).isVisible());
		this.showPiano.setSelected(TGPianoEditor.getInstance(this.getToolBar().getContext()).isVisible());
		this.showInstruments.setSelected(!TGChannelManagerDialog.getInstance(this.getToolBar().getContext()).isDisposed());
		this.showPercussion.setSelected(!TGPercussionEditor.getInstance(this.getToolBar().getContext()).isDisposed());
		this.showMatrix.setSelected(!TGMatrixEditor.getInstance(this.getToolBar().getContext()).isDisposed());
		this.showBrowser.setSelected(!TGBrowserDialog.getInstance(this.getToolBar().getContext()).isDisposed());
		this.showEditToolBar.setSelected(TGEditToolBar.getInstance(this.getToolBar().getContext()).isVisible());
		this.showTrackTable.setSelected(TGTableViewer.getInstance(this.getToolBar().getContext()).isVisible());
	}
}
