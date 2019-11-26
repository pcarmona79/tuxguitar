package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasureRemoveDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		int start;
		int end;
		if (!beats.isEmpty()) {
			start = beats.firstMeasure().getHeader().getNumber();
			end = beats.lastMeasure().getHeader().getNumber();
		} else {
			final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			start = end = header.getNumber();
		}

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("edit.delete"));
		
		//----------------------------------------------------------------------
		UITableLayout rangeLayout = new UITableLayout();
		UIPanel range = uiFactory.createPanel(dialog, false);
		range.setLayout(rangeLayout);
		dialogLayout.set(range, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		int measureCount = song.countMeasureHeaders();
		
		UILabel fromLabel = uiFactory.createLabel(range);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner fromSpinner = uiFactory.createSpinner(range);
		fromSpinner.setMinimum(1);
		fromSpinner.setMaximum(measureCount);
		fromSpinner.setValue(start);
		rangeLayout.set(fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);
		
		UILabel toLabel = uiFactory.createLabel(range);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner toSpinner = uiFactory.createSpinner(range);
		toSpinner.setMinimum(1);
		toSpinner.setMaximum(measureCount);
		toSpinner.setValue(end);
		rangeLayout.set(toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);
		
		final int minSelection = 1;
		final int maxSelection = track.countMeasures();
		
		fromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = fromSpinner.getValue();
				int toSelection = toSpinner.getValue();
				
				if(fromSelection < minSelection){
					fromSpinner.setValue(minSelection);
				}else if(fromSelection > toSelection){
					fromSpinner.setValue(toSelection);
				}
			}
		});
		toSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int toSelection = toSpinner.getValue();
				int fromSelection = fromSpinner.getValue();
				if(toSelection < fromSelection){
					toSpinner.setValue(fromSelection);
				}else if(toSelection > maxSelection){
					toSpinner.setValue(maxSelection);
				}
			}
		});
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					Selector selector = TablatureEditor.getInstance(context.getContext()).getTablature().getSelector();
					selector.clearSelection();
					processAction(context.getContext(), song, fromSpinner.getValue(), toSpinner.getValue());
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void processAction(TGContext context, TGSong song, Integer measure1, Integer measure2) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGRemoveMeasureRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_1, measure1);
		tgActionProcessor.setAttribute(TGRemoveMeasureRangeAction.ATTRIBUTE_MEASURE_NUMBER_2, measure2);
		tgActionProcessor.process();
	}
}
