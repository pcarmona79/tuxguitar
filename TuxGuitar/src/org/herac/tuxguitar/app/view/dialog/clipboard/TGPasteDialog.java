package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.tools.TGTransposeAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGPasteNoteAction;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGPasteDialog {

	private UIFactory uiFactory;
	private UIWindow uiParent;
	private UITableLayout dialogLayout;
	private UIWindow dialog;
	private String action;

	protected abstract String getReplaceText();
	protected abstract String getInsertText();
	protected abstract String getTitle();

	protected TGPasteDialog(String action) {
		this.action = action;
	}

	public void show(final TGViewContext context) {
		uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		dialogLayout = new UITableLayout();

		createWindow();
		
		//-----------------COUNT------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("edit.paste"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel countLabel = uiFactory.createLabel(group);
		countLabel.setText(TuxGuitar.getProperty("edit.paste.count"));
		groupLayout.set(countLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner countSpinner = uiFactory.createSpinner(group);
		countSpinner.setMinimum( 1 );
		countSpinner.setMaximum( 65535 );
		countSpinner.setValue( 1 );
		groupLayout.set(countSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//----------------------------------------------------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton replace = uiFactory.createRadioButton(options);
		replace.setText(this.getReplaceText());
		replace.setSelected(true);
		optionsLayout.set(replace, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton insert = uiFactory.createRadioButton(options);
		insert.setText(this.getInsertText());
		optionsLayout.set(insert, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel transpositionLabel = uiFactory.createLabel(options);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones"));
		optionsLayout.set(transpositionLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UISpinner transpositionSpinner = uiFactory.createSpinner(options);
		transpositionSpinner.setMinimum(-127);
		transpositionSpinner.setMaximum(127);
		transpositionSpinner.setValue(0);
		optionsLayout.set(transpositionSpinner, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox applyToChordsButton = uiFactory.createCheckBox(options);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelected(true);
		optionsLayout.set(applyToChordsButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);

		final UICheckBox tryKeepStringButton = uiFactory.createCheckBox(options);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelected(true);
		optionsLayout.set(tryKeepStringButton, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int pasteMode = 0;
				int pasteCount = countSpinner.getValue();
				if( replace.isSelected() ){
					pasteMode = TGPasteNoteAction.TRANSFER_TYPE_REPLACE;
				}else if(insert.isSelected()){
					pasteMode = TGPasteNoteAction.TRANSFER_TYPE_INSERT;
				}
				int transposition = transpositionSpinner.getValue();
				final boolean tryKeepString = tryKeepStringButton.isSelected();
				final boolean applyToChords = applyToChordsButton.isSelected();
				processAction(context.getContext(), pasteMode, pasteCount, transposition, tryKeepString, applyToChords);
				
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void createWindow() {
		dialog = uiFactory.createWindow(uiParent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(this.getTitle());
	}

	public void processAction(TGContext context, Integer pasteMode, Integer pasteCount, int transposition, boolean tryKeepString, boolean applyToChords) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, this.action);
		tgActionProcessor.setAttribute(TGPasteNoteAction.ATTRIBUTE_PASTE_MODE, pasteMode);
		tgActionProcessor.setAttribute(TGPasteNoteAction.ATTRIBUTE_PASTE_COUNT, pasteCount);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION, transposition);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING, tryKeepString);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS, applyToChords);
		tgActionProcessor.process();
	}
}
