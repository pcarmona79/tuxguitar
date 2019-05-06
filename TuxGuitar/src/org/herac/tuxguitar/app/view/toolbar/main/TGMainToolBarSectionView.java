package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.matrix.TGMatrixEditor;
import org.herac.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionView extends TGMainToolBarSection {
	
	private UIToggleButton showFretBoard;
	private UIToggleButton showPiano;
	private UIToggleButton showInstruments;
	private UIToggleButton showMatrix;

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
		
		//--TRANSPORT--
		this.showMatrix = this.createToggleButton();
		this.showMatrix.addSelectionListener(this.createActionProcessor(TGToggleMatrixEditorAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.showFretBoard.setToolTipText(this.getText("view.show-fretboard"));
		this.showPiano.setToolTipText(this.getText("view.show-piano"));
		this.showInstruments.setToolTipText(this.getText("view.show-instruments"));
		this.showMatrix.setToolTipText(this.getText("view.show-matrix"));
	}
	
	public void loadIcons(){
		this.showFretBoard.setImage(this.getIconManager().getFretboard());
		this.showPiano.setImage(this.getIconManager().getPiano());
		this.showInstruments.setImage(this.getIconManager().getInstruments());
		this.showMatrix.setImage(this.getIconManager().getMatrix());
	}
	
	public void updateItems(){
		this.showFretBoard.setSelected(TGFretBoardEditor.getInstance(this.getToolBar().getContext()).isVisible());
		this.showPiano.setSelected(TGPianoEditor.getInstance(this.getToolBar().getContext()).isVisible());
		this.showInstruments.setSelected(!TGChannelManagerDialog.getInstance(this.getToolBar().getContext()).isDisposed());
		this.showMatrix.setSelected(!TGMatrixEditor.getInstance(this.getToolBar().getContext()).isDisposed());
	}
}
