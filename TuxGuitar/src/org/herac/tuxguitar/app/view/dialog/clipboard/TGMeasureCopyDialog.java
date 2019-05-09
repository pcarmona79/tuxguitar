package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGMeasureCopyDialog {
	public static final String ATTRIBUTE_SELECTOR = Selector.class.getName();
	private static final int MIN_SELECTION = 1;
	private static final float BUTTONS_PANEL_MARGIN = 0f;

	private UIFactory uiFactory;
	private UIWindow dialog;
	private UITableLayout dialogLayout;
	private UISpinner fromSpinner;
	private UISpinner toSpinner;
	private UICheckBox allTracksCheckBox = null;
	private UILegendPanel range;
	private UITableLayout rangeLayout;
	private UIPanel buttons;
	private UITableLayout buttonsLayout;

	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final Selector selector = context.getAttribute(ATTRIBUTE_SELECTOR);

		uiFactory = TGApplication.getInstance(context.getContext()).getFactory();

		createWindow(uiParent);

		createRangePanel();
		createFromLabel();
		createFromSpinner(song.countMeasureHeaders());
		createToLabel();
		createToSpinner(song.countMeasureHeaders(), track.countMeasures());

		if (song.countTracks() > 1)
			createOptionsPanel();

		createButtonsPanel();
		createOkButton(context.getContext());
		createCancelButton();

		if (selector.isActive()) {
            setInitialFromToValues(selector.getStartBeat(), selector.getEndBeat());
        } else {
			final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			setInitialFromToValues(beat, beat);
		}

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void createWindow(final UIWindow uiParent) {
		dialogLayout = new UITableLayout();
		dialog = uiFactory.createWindow(uiParent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("edit.copy"));
	}

	private void createRangePanel() {
		rangeLayout = new UITableLayout();
		range = uiFactory.createLegendPanel(dialog);
		range.setLayout(rangeLayout);
		range.setText(TuxGuitar.getProperty("edit.copy"));
		dialogLayout.set(range, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}

	private void createFromLabel() {
		UILabel fromLabel = uiFactory.createLabel(range);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
	}

	private void createFromSpinner(int measureCount) {
		fromSpinner = uiFactory.createSpinner(range);
		fromSpinner.setMinimum(MIN_SELECTION);
		fromSpinner.setMaximum(measureCount);
		rangeLayout.set(fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);

		fromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = fromSpinner.getValue();
				int toSelection = toSpinner.getValue();

				if(fromSelection < MIN_SELECTION){
					fromSpinner.setValue(MIN_SELECTION);
				}else if(fromSelection > toSelection){
					fromSpinner.setValue(toSelection);
				}
			}
		});
	}

	private void createToLabel() {
		UILabel toLabel = uiFactory.createLabel(range);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
	}

	private void createToSpinner(int measureCount, final int maxSelection) {
		toSpinner = uiFactory.createSpinner(range);
		toSpinner.setMinimum(MIN_SELECTION);
		toSpinner.setMaximum(measureCount);
		rangeLayout.set(toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);

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
	}

	private void createOptionsPanel() {
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		allTracksCheckBox = uiFactory.createCheckBox(options);
		allTracksCheckBox.setText(TuxGuitar.getProperty("edit.all-tracks"));
		allTracksCheckBox.setSelected(true);
	}

	private void createButtonsPanel() {
		buttonsLayout = new UITableLayout(BUTTONS_PANEL_MARGIN);
		buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, (allTracksCheckBox != null ? 3 : 2), 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
	}

	private void createOkButton(final TGContext context) {
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				processAction(context);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
	}

	private void createCancelButton() {
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
	}

	private void setInitialFromToValues(TGBeat start, TGBeat end) {
		fromSpinner.setValue(start.getMeasure().getNumber());
		toSpinner.setValue(end.getMeasure().getNumber());
	}

	public void processAction(TGContext context) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, fromSpinner.getValue());
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, toSpinner.getValue());
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, allTracksCheckBox == null || allTracksCheckBox.isSelected());
		tgActionProcessor.process();
	}
}
