package org.herac.tuxguitar.app.view.dialog.clef;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGClefDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.clef"));
		
		//-------clef-------------------------------------
		UITableLayout clefLayout = new UITableLayout();
		UIPanel clef = uiFactory.createPanel(dialog, false);
		clef.setLayout(clefLayout);
		dialogLayout.set(clef, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel numeratorLabel = uiFactory.createLabel(clef);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.clef") + ":");
		clefLayout.set(numeratorLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> clefs = uiFactory.createDropDownSelect(clef);
		clefs.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.clef.treble"), TGMeasure.CLEF_TREBLE));
		clefs.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.clef.bass"), TGMeasure.CLEF_BASS));
		clefs.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.clef.tenor"), TGMeasure.CLEF_TENOR));
		clefs.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.clef.alto"), TGMeasure.CLEF_ALTO));
		clefs.setSelectedValue(measure.getClef());
		clefLayout.set(clefs, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UIPanel check = uiFactory.createPanel(dialog, false);
		check.setLayout(checkLayout);
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.clef.to-the-end"));
		toEnd.setSelected(true);
		checkLayout.set(toEnd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					changeClef(context.getContext(), song, track, measure, clefs.getSelectedValue(), toEnd.isSelected());
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void changeClef(TGContext context, TGSong song, TGTrack track, TGMeasure measure, Integer value, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, value);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
