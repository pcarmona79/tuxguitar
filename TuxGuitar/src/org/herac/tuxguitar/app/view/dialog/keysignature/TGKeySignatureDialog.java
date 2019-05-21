package org.herac.tuxguitar.app.view.dialog.keysignature;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGKeySignatureDialog {
	
	public void show(final TGViewContext context) {
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.keysignature"));
		
		//-------key Signature-------------------------------------
		UITableLayout keySignatureLayout = new UITableLayout();
		UIPanel keySignature = uiFactory.createPanel(dialog, false);
		keySignature.setLayout(keySignatureLayout);
		dialogLayout.set(keySignature, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel keySignatureLabel = uiFactory.createLabel(keySignature);
		keySignatureLabel.setText(TuxGuitar.getProperty("composition.keysignature") + ":");
		keySignatureLayout.set(keySignatureLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> keySignatures = uiFactory.createDropDownSelect(keySignature);
		
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.natural"), 0));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-1"), 1));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-2"), 2));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-3"), 3));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-4"), 4));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-5"), 5));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-6"), 6));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-7"), 7));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-1"), 8));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-2"), 9));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-3"), 10));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-4"), 11));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-5"), 12));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-6"), 13));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-7"), 14));
		keySignatures.setSelectedValue(measure.getKeySignature());
		keySignatureLayout.set(keySignatures, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UIPanel check = uiFactory.createPanel(dialog, false);
		check.setLayout(checkLayout);
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.keysignature.to-the-end"));
		toEnd.setSelected(true);
		checkLayout.set(toEnd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					changeKeySignature(context.getContext(), track, measure, keySignatures.getSelectedValue(), toEnd.isSelected());
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void changeKeySignature(TGContext context, TGTrack track, TGMeasure measure, Integer value, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, value);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
