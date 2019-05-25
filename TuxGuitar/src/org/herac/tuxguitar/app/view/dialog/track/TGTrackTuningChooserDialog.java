package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;

public class TGTrackTuningChooserDialog {
	
	private TGTrackTuningDialog tuningDialog;
	
	public TGTrackTuningChooserDialog(TGTrackTuningDialog tuningDialog){
		this.tuningDialog = tuningDialog;
	}
	
	public void select(final TGTrackTuningChooserHandler handler) {
		this.select(handler, null);
	}
	
	public void select(final TGTrackTuningChooserHandler handler, TGTrackTuningModel model) {
		final UIFactory uiFactory = this.tuningDialog.getUIFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(this.tuningDialog.getDialog(), true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tuning"));
		
		//-------------MAIN PANEL-----------------------------------------------
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = uiFactory.createPanel(dialog, false);
		panel.setLayout(panelLayout);
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// value
		UILabel tuningValueLabel = uiFactory.createLabel(panel);
		tuningValueLabel.setText(TuxGuitar.getProperty("tuning.value"));
		panelLayout.set(tuningValueLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> tuningValueControl = uiFactory.createDropDownSelect(panel);
		tuningValueControl.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("tuning.value.select")));
		
		String[] tuningTexts = TGTrackTuningDialog.getValueLabels();
		for(int value = 0 ; value < tuningTexts.length ; value ++) {
			tuningValueControl.addItem(new UISelectItem<Integer>(tuningTexts[value], value));
		}
		
		for (int i = 1; i <= 32; i++) {
			tuningValueControl.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		tuningValueControl.setSelectedValue(model != null ? model.getValue() : null);
		panelLayout.set(tuningValueControl, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		// value spinner
		UILabel tuningSpinnerLabel = uiFactory.createLabel(panel);
		tuningSpinnerLabel.setText(TuxGuitar.getProperty("tuning.midi-note"));
		panelLayout.set(tuningSpinnerLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);

		final UISpinner tuningValueSpinner = uiFactory.createSpinner(panel);
		tuningValueSpinner.setMinimum(0);
		tuningValueSpinner.setMaximum(tuningTexts.length - 1);
		tuningValueSpinner.setValue(model != null ? model.getValue() : 0);
		panelLayout.set(tuningValueSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		// label
		UILabel tuningLabelLabel = uiFactory.createLabel(panel);
		tuningLabelLabel.setText(TuxGuitar.getProperty("tuning.label"));
		panelLayout.set(tuningLabelLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIReadOnlyTextField tuningLabelControl = uiFactory.createReadOnlyTextField(panel);
		if( model != null ) {
			tuningLabelControl.setText(TGTrackTuningDialog.getValueLabel(model.getValue()));
		}
		panelLayout.set(tuningLabelControl, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		tuningValueControl.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				tuningLabelControl.setText(TGTrackTuningDialog.getValueLabel(tuningValueControl.getSelectedValue()));
				tuningValueSpinner.setValue(tuningValueControl.getSelectedValue());
			}
		});
		tuningValueSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				tuningValueControl.setSelectedValue(tuningValueSpinner.getValue());
				tuningLabelControl.setText(TGTrackTuningDialog.getValueLabel(tuningValueControl.getSelectedValue()));
			}
		});

		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					if( handleSelection(handler, dialog, tuningValueControl) ) {
						dialog.dispose();
					}
				}), TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public boolean handleSelection(TGTrackTuningChooserHandler handler, UIWindow dialog, UIDropDownSelect<Integer> value) {
		TGTrackTuningModel model = new TGTrackTuningModel();
		model.setValue(value.getSelectedValue());
		
		if( model.getValue() == null ){
			TGMessageDialogUtil.errorMessage(this.tuningDialog.getContext().getContext(), dialog, TuxGuitar.getProperty("tuning.value.empty-error"));
			return false;
		}
		handler.handleSelection(model);
		
		return true;
	}
	
}
