package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.tools.scale.ScaleManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGScaleDialog {
	private static final String[] INTERVAL_NAMES = new String[] {
			"P1", "m2", "M2", "m3", "M3", "P4", "°5", "P5", "m6", "M6", "m7", "M7"
	};

	private int initialScale;
	private int initialKey;

	public void show(final TGViewContext context) {
		final ScaleManager scaleManager = ScaleManager.getInstance(context.getContext());
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, true);

		initialScale = scaleManager.getSelectionIndex();
		initialKey = scaleManager.getSelectionKey();

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("scale.list"));

		final List<UIToggleButton> intervalButtons = new ArrayList<>();

		// ----------------------------------------------------------------------
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(dialog, false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIListBoxSelect<Integer> keys = uiFactory.createListBoxSelect(composite);
		String[] keyNames = scaleManager.getKeyNames();
		for(int i = 0;i < keyNames.length;i ++){
			keys.addItem(new UISelectItem<Integer>(keyNames[i], i));
		}
		keys.setSelectedValue(scaleManager.getSelectionKey());
		compositeLayout.set(keys, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		compositeLayout.set(keys, UITableLayout.PACKED_HEIGHT, 200f);
		
		final UIListBoxSelect<Integer> scales = uiFactory.createListBoxSelect(composite);
		scales.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("scale.custom"), ScaleManager.NONE_SELECTION));
		String[] scaleNames = scaleManager.getScaleNames();
		for(int i = 0;i < scaleNames.length;i ++){
			scales.addItem(new UISelectItem<Integer>(scaleNames[i], i));
		}
		scales.setSelectedValue(scaleManager.getSelectionIndex());

		keys.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				selectScale(context.getContext(), scales.getSelectedValue(), keys.getSelectedValue());
			}
		});
		scales.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				selectScale(context.getContext(), scales.getSelectedValue(), keys.getSelectedValue());
				Integer keys = scaleManager.getScaleKeys(scales.getSelectedValue());
				if (keys == null) {
					keys = 0;
				}
				TGScale scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
				scale.setNotes(keys);
				for (int i = 0; i < intervalButtons.size(); i++) {
					intervalButtons.get(i).setSelected(scale.getNote(i));
				}
			}
		});

		compositeLayout.set(scales, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		compositeLayout.set(scales, UITableLayout.PACKED_HEIGHT, 200f);

		//------------------INTERVALS--------------------------
        UILabel intervalLabel = uiFactory.createLabel(composite);
        intervalLabel.setText(TuxGuitar.getProperty("scale.intervals"));
		compositeLayout.set(intervalLabel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		UITableLayout intervalLayout = new UITableLayout();
		UIPanel intervals = uiFactory.createPanel(composite, false);
		intervals.setLayout(intervalLayout);
		compositeLayout.set(intervals, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		int col = 0;
		for (String name : INTERVAL_NAMES) {
			final UIToggleButton button = uiFactory.createToggleButton(intervals, false);
			button.setText(name);
			intervalButtons.add(button);
            final int index = col;
			button.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					TGScale scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
					scale.setNotes(scaleManager.getScale().getNotes());
					scale.setKey(scaleManager.getScale().getKey());
					scale.setNote(index, button.isSelected());
					scales.setSelectedValue(scaleManager.getScaleIndex(scale));
					selectScale(context.getContext(), scale);
				}
			});
			intervalLayout.set(button, 1, ++col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, null);
		}

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				selectScale(context.getContext(), initialScale, initialKey);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		dialog.computePackedSize(null, null);
		dialog.setMinimumSize(dialog.getPackedSize());
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	public void selectScale(TGContext context, TGScale scale) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGSelectScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_SCALE, scale);
		tgActionProcessor.process();
    }

	public void selectScale(TGContext context, Integer index, Integer key) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGSelectScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_INDEX, index);
		tgActionProcessor.setAttribute(TGSelectScaleAction.ATTRIBUTE_KEY, key);
		tgActionProcessor.process();
	}
}
