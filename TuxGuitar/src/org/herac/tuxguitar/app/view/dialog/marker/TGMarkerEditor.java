package org.herac.tuxguitar.app.view.dialog.marker;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGColorButton;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.*;

public class TGMarkerEditor {
	
	private static final float MINIMUM_CONTROL_WIDTH = 180;

	private TGViewContext context;
	private TGMarker marker;
	private UIWindow dialog;
	private UISpinner measureSpinner;
	private UITextField titleText;

	TGMarkerEditor(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		this.createEditableMarker();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context.getContext()).getFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, true, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("marker"));
		
		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(this.dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		// Measure Number
		final int measureCount = TuxGuitar.getInstance().getDocumentManager().getSong().countMeasureHeaders();
		UILabel measureLabel = uiFactory.createLabel(group);
		measureLabel.setText(TuxGuitar.getProperty("measure"));
		groupLayout.set(measureLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.measureSpinner = uiFactory.createSpinner(group);
		this.measureSpinner.setMinimum(1);
		this.measureSpinner.setMaximum(measureCount);
		this.measureSpinner.setValue(this.marker.getMeasure());
		this.measureSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int selection = TGMarkerEditor.this.measureSpinner.getValue();
				if (selection < 1) {
					TGMarkerEditor.this.measureSpinner.setValue(1);
				} else if (selection > measureCount) {
					TGMarkerEditor.this.measureSpinner.setValue(measureCount);
				}
			}
		});
		groupLayout.set(this.measureSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		// Title
		UILabel titleLabel = uiFactory.createLabel(group);
		titleLabel.setText(TuxGuitar.getProperty("title"));
		groupLayout.set(titleLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.titleText = uiFactory.createTextField(group);
		this.titleText.setText(this.marker.getTitle());
		groupLayout.set(this.titleText, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		// Color
		UILabel colorLabel = uiFactory.createLabel(group);
		colorLabel.setText(TuxGuitar.getProperty("color"));
		groupLayout.set(colorLabel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		TGColorButton colorButton = new TGColorButton(uiFactory, dialog, group, TuxGuitar.getProperty("choose"));
		colorButton.addSelectionListener(new TGColorButton.SelectionListener() {
			public void onSelect(UIColorModel selection) {
				TGMarkerEditor.this.marker.getColor().setR(selection.getRed());
				TGMarkerEditor.this.marker.getColor().setG(selection.getGreen());
				TGMarkerEditor.this.marker.getColor().setB(selection.getBlue());
			}
		});
		colorButton.loadColor(this.marker.getColor().toColorModel());
		groupLayout.set(colorButton.getControl(), 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		// ------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					updateMarker();
					dialog.dispose();
				}),
				TGDialogButtons.clean(() -> {
					removeMarker();
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void updateMarker() {
		this.marker.setMeasure(this.measureSpinner.getValue());
		this.marker.setTitle(this.titleText.getText());
		
		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGModifyMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER));
		tgActionProcessor.setAttribute(TGModifyMarkerAction.ATTRIBUTE_MODIFIED_MARKER, this.marker.clone(songManager.getFactory()));
		tgActionProcessor.process();
	}

	private void removeMarker() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGRemoveMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.process();
	}
	
	private void createEditableMarker() {
		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGMarker marker = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker == null ) {
			TGMeasureHeader header = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			
			marker = songManager.getFactory().newMarker();
			marker.setMeasure(header.getNumber());
		}
		
		this.marker = marker.clone(songManager.getFactory());
	}
}
