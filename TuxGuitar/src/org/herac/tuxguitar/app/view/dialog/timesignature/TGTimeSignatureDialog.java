package org.herac.tuxguitar.app.view.dialog.timesignature;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGTimeSignatureDialog {

	private TGMeasureHeader getFirstHeader(final TGNoteRange range, final TGViewContext context) {
		if (range.isEmpty()) {
			return context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		}
		return range.firstMeasure().getHeader();
	}
	
	public void show(final TGViewContext context) {
		final TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGNoteRange range = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		final TGMeasureHeader start = getFirstHeader(range, context);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.timesignature"));
		
		//-------------TIME SIGNATURE-----------------------------------------------
		UITableLayout timeSignatureLayout = new UITableLayout();
		UIPanel timeSignature = uiFactory.createPanel(dialog, false);
		timeSignature.setLayout(timeSignatureLayout);
		dialogLayout.set(timeSignature, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		TGTimeSignature currentTimeSignature = start.getTimeSignature();
		
		//numerator
		UILabel numeratorLabel = uiFactory.createLabel(timeSignature);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.timesignature.Numerator"));
		timeSignatureLayout.set(numeratorLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner numerator = uiFactory.createSpinner(timeSignature);
		numerator.setMinimum(1);
		numerator.setMaximum(32);
		numerator.setValue(currentTimeSignature.getNumerator());
		timeSignatureLayout.set(numerator, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//denominator
		UILabel denominatorLabel = uiFactory.createLabel(timeSignature);
		denominatorLabel.setText(TuxGuitar.getProperty("composition.timesignature.denominator"));
		timeSignatureLayout.set(denominatorLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> denominator = uiFactory.createDropDownSelect(timeSignature);
		for (int i = 1; i <= 32; i = i * 2) {
			denominator.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		denominator.setSelectedValue(currentTimeSignature.getDenominator().getValue());
		timeSignatureLayout.set(denominator, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UIPanel check = uiFactory.createPanel(dialog, false);
		check.setLayout(checkLayout);
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

        final UICheckBox toEnd = uiFactory.createCheckBox(check);
        toEnd.setText(TuxGuitar.getProperty("composition.timesignature.to-the-end"));
        checkLayout.set(toEnd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		if (TablatureEditor.getInstance(context.getContext()).getTablature().getSelector().isActive()) {
			toEnd.setEnabled(false);
		} else {
			toEnd.setSelected(true);
		}

		final UICheckBox truncate = uiFactory.createCheckBox(check);
		truncate.setText(TuxGuitar.getProperty("composition.timesignature.truncate-or-extend"));
		checkLayout.set(truncate, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					TGMeasureHeader end = null;
					if (!toEnd.isSelected()) {
						if (range.isEmpty()) {
							end = start;
						} else {
							end = range.lastMeasure().getHeader();
						}
					}
					changeTimeSignature(context.getContext(), song, start, end, parseTimeSignature(songManager, numerator, denominator), truncate.isSelected());
					dialog.dispose();
				}), TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public TGTimeSignature parseTimeSignature(TGSongManager songManager, UISpinner numerator, UIDropDownSelect<Integer> denominator) {
		TGTimeSignature tgTimeSignature = songManager.getFactory().newTimeSignature();
		tgTimeSignature.setNumerator(numerator.getValue());
		tgTimeSignature.getDenominator().setValue(denominator.getSelectedValue());
		return tgTimeSignature;
	}
	
	public void changeTimeSignature(TGContext context, TGSong song, TGMeasureHeader start, TGMeasureHeader end, TGTimeSignature timeSignature, Boolean truncateOrExtend) {
		TablatureEditor.getInstance(context).getTablature().getSelector().clearSelection();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, start);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_HEADER_END, end);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_TRUNCATE_OR_EXTEND, truncateOrExtend);
		tgActionProcessor.processOnNewThread();
	}
}
