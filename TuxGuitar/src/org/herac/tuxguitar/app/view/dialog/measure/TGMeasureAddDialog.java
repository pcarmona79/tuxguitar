package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasureAddDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("measure.add"));
		
		//-----------------COUNT------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel countLabel = uiFactory.createLabel(group);
		countLabel.setText(TuxGuitar.getProperty("measure.add.count"));
		groupLayout.set(countLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner countSpinner = uiFactory.createSpinner(group);
		countSpinner.setMinimum( 1 );
		countSpinner.setMaximum( 100 );
		countSpinner.setValue( 1 );
		groupLayout.set(countSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		//----------------------------------------------------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UIPanel options = uiFactory.createPanel(dialog, false);
		options.setLayout(optionsLayout);
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton beforePosition = uiFactory.createRadioButton(options);
		beforePosition.setText(TuxGuitar.getProperty("measure.add-before-current-position"));
		optionsLayout.set(beforePosition, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton afterPosition = uiFactory.createRadioButton(options);
		afterPosition.setText(TuxGuitar.getProperty("measure.add-after-current-position"));
		optionsLayout.set(afterPosition, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton atEnd = uiFactory.createRadioButton(options);
		atEnd.setText(TuxGuitar.getProperty("measure.add-at-end"));
		atEnd.setSelected(true);
		optionsLayout.set(atEnd, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					int number = 0;
					int count = countSpinner.getValue();
					if( beforePosition.isSelected() ){
						number = (header.getNumber());
					}else if( afterPosition.isSelected() ){
						number = (header.getNumber() + 1);
					}else if( atEnd.isSelected() ){
						number = (song.countMeasureHeaders() + 1);
					}
					processAction(context.getContext(), song, number, count);
					dialog.dispose();
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void processAction(TGContext context, TGSong song, Integer number, Integer count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGAddMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, count);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, number);
		tgActionProcessor.process();
	}
}
