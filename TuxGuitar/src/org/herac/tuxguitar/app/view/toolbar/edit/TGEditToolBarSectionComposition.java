package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.composition.*;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionComposition extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "composition";
	
	private UIToolActionItem tempo;
	private UIToolActionItem timeSignature;
	private UIToolActionItem clef;
	private UIToolActionItem keySignature;
	private UIToolActionItem tripletFeel;
	private UIToolActionItem marker;
	private UIToolCheckableItem repeatOpen;
	private UIToolCheckableItem repeatClose;
	private UIToolCheckableItem repeatAlternative;

	public TGEditToolBarSectionComposition(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
	}
	
	public void createSectionToolBars() {
		UIToolBar toolBar1 = this.createToolBar();
		UIToolBar toolBar2 = this.createToolBar();

		this.tempo = toolBar1.createActionItem();
		this.tempo.addSelectionListener(this.createActionProcessor(TGOpenTempoDialogAction.NAME));
		
		this.timeSignature = toolBar1.createActionItem();
		this.timeSignature.addSelectionListener(this.createActionProcessor(TGOpenTimeSignatureDialogAction.NAME));

		this.clef = toolBar1.createActionItem();
		this.clef.addSelectionListener(this.createActionProcessor(TGOpenClefDialogAction.NAME));

		this.keySignature = toolBar1.createActionItem();
		this.keySignature.addSelectionListener(this.createActionProcessor(TGOpenKeySignatureDialogAction.NAME));

		this.tripletFeel = toolBar1.createActionItem();
		this.tripletFeel.addSelectionListener(this.createActionProcessor(TGOpenTripletFeelDialogAction.NAME));

		this.marker = toolBar1.createActionItem();
		this.marker.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));

		this.repeatOpen = toolBar2.createCheckItem();
		this.repeatOpen.addSelectionListener(this.createActionProcessor(TGRepeatOpenAction.NAME));
		
		this.repeatClose = toolBar2.createCheckItem();
		this.repeatClose.addSelectionListener(this.createActionProcessor(TGOpenRepeatCloseDialogAction.NAME));
		
		this.repeatAlternative = toolBar2.createCheckItem();
		this.repeatAlternative.addSelectionListener(this.createActionProcessor(TGOpenRepeatAlternativeDialogAction.NAME));
	}
	
	public void updateSectionItems() {
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGMeasure measure = this.getTablature().getCaret().getMeasure();
		
		this.tempo.setEnabled(!running);
		this.timeSignature.setEnabled(!running);
		this.clef.setEnabled(!running);
		this.keySignature.setEnabled(!running);
		this.tripletFeel.setEnabled(!running);
		this.marker.setEnabled(!running);
		this.repeatOpen.setEnabled( !running );
		this.repeatOpen.setChecked(measure != null && measure.isRepeatOpen());
		this.repeatClose.setEnabled( !running );
		this.repeatClose.setChecked(measure != null && measure.getRepeatClose() > 0);
		this.repeatAlternative.setEnabled( !running );
		this.repeatAlternative.setChecked(measure != null && measure.getHeader().getRepeatAlternative() > 0);
	}
	
	public void loadSectionProperties() {
		this.tempo.setToolTipText(this.getText("composition.tempo"));
		this.timeSignature.setToolTipText(this.getText("composition.timesignature"));
		this.clef.setToolTipText(this.getText("composition.clef"));
		this.keySignature.setToolTipText(this.getText("composition.keysignature"));
		this.tripletFeel.setToolTipText(this.getText("composition.tripletfeel"));
		this.marker.setToolTipText(this.getText("marker"));
		this.repeatOpen.setToolTipText(this.getText("repeat.open"));
		this.repeatClose.setToolTipText(this.getText("repeat.close"));
		this.repeatAlternative.setToolTipText(this.getText("repeat.alternative"));
	}
	
	public void loadSectionIcons() {
		this.tempo.setImage(this.getIconManager().getCompositionTempo());
		this.timeSignature.setImage(this.getIconManager().getCompositionTimeSignature());
		this.clef.setImage(this.getIconManager().getCompositionClef());
		this.keySignature.setImage(this.getIconManager().getCompositionKeySignature());
		this.tripletFeel.setImage(this.getIconManager().getCompositionTripletFeel());
		this.marker.setImage(this.getIconManager().getMarkerList());
		this.repeatOpen.setImage(this.getIconManager().getCompositionRepeatOpen());
		this.repeatClose.setImage(this.getIconManager().getCompositionRepeatClose());
		this.repeatAlternative.setImage(this.getIconManager().getCompositionRepeatAlternative());
	}
}
