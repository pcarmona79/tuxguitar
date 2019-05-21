package org.herac.tuxguitar.app.view.dialog.transpose;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.tools.TGTransposeAction;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;

public class TGTransposeDialog {
	
	private static final int TRANSPOSITION_SEMITONES = 12;
	
	public void show(final TGViewContext context) {		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tools.transpose"));
		
		//-----------------TEMPO------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel transpositionLabel = uiFactory.createLabel(group);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones"));
		groupLayout.set(transpositionLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner transpositionSpinner = uiFactory.createSpinner(group);
		transpositionSpinner.setMinimum(-127);
		transpositionSpinner.setMaximum(127);
		transpositionSpinner.setValue(0);
		groupLayout.set(transpositionSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------OPTIONS--------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UIPanel options = uiFactory.createPanel(dialog, false);
		options.setLayout(optionsLayout);
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton applyToAllMeasuresButton = uiFactory.createRadioButton(options);
		applyToAllMeasuresButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-track"));
		applyToAllMeasuresButton.setSelected(true);
		optionsLayout.set(applyToAllMeasuresButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton applyToCurrentMeasureButton = uiFactory.createRadioButton(options);
		applyToCurrentMeasureButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure"));
		optionsLayout.set(applyToCurrentMeasureButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox applyToAllTracksButton = uiFactory.createCheckBox(options);
		applyToAllTracksButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-all-tracks"));
		applyToAllTracksButton.setSelected(true);
		optionsLayout.set(applyToAllTracksButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox applyToChordsButton = uiFactory.createCheckBox(options);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelected(true);
		optionsLayout.set(applyToChordsButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox tryKeepStringButton = uiFactory.createCheckBox(options);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelected(true);
		optionsLayout.set(tryKeepStringButton, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					Integer transposition = transpositionSpinner.getValue();
					if( transposition != null ){
						final boolean tryKeepString = tryKeepStringButton.isSelected();
						final boolean applyToChords = applyToChordsButton.isSelected();
						final boolean applyToAllTracks = applyToAllTracksButton.isSelected();
						final boolean applyToAllMeasures = applyToAllMeasuresButton.isSelected();

						transposeNotes(context, transposition, tryKeepString, applyToChords , applyToAllMeasures, applyToAllTracks);
					}
					dialog.dispose();
                }), TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void transposeNotes(TGViewContext context, int transposition , boolean tryKeepString , boolean applyToChords , boolean applyToAllMeasures , boolean applyToAllTracks) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGTransposeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION, transposition);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING, tryKeepString);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS, applyToChords);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_TRACKS, applyToAllTracks);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_MEASURES, applyToAllMeasures);
		tgActionProcessor.processOnNewThread();
	}
}
