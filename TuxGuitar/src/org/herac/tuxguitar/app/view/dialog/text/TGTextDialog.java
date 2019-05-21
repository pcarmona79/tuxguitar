package org.herac.tuxguitar.app.view.dialog.text;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTextDialog {
	
	public void show(final TGViewContext context) {
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, true);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("text.editor"));
		
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UILabel label = uiFactory.createLabel(group);
		label.setText(TuxGuitar.getProperty("text.text") + ":");
		groupLayout.set(label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UITextField text = uiFactory.createTextField(group);
		text.setText(beat.getText() != null ? beat.getText().getValue() : new String());
		groupLayout.set(text, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					doInsertText(context.getContext(), beat, text.getText());
					dialog.dispose();

				}), TGDialogButtons.clean(() -> {
                    doRemoveText(context.getContext(), beat);
                    dialog.dispose();
                }),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void doInsertText(TGContext context, TGBeat beat, String textValue) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGInsertTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGInsertTextAction.ATTRIBUTE_TEXT_VALUE, textValue);
		tgActionProcessor.processOnNewThread();
	}
	
	public void doRemoveText(TGContext context, TGBeat beat) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGRemoveTextAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.processOnNewThread();
	}
}
