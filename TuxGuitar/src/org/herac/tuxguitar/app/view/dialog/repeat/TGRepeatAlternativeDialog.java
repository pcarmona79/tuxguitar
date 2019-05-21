package org.herac.tuxguitar.app.view.dialog.repeat;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

import java.util.Iterator;

public class TGRepeatAlternativeDialog {
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		
		final int existentEndings = getExistentEndings(song, header);
		final int selectedEndings = (header.getRepeatAlternative() > 0 ? header.getRepeatAlternative() : getDefaultEndings(existentEndings));
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("repeat.alternative.editor"));
		
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 350f, null, null);
		
		final UICheckBox[] selections = new UICheckBox[8];
		for(int i = 0; i < selections.length; i ++){
			boolean enabled = ((existentEndings & (1 << i)) == 0);
			selections[i] = uiFactory.createCheckBox(group);
			selections[i].setText(Integer.toString( i + 1 ));
			selections[i].setEnabled(enabled);
			selections[i].setSelected(enabled && ((selectedEndings & (1 << i)) != 0));
			groupLayout.set(selections[i], (1 + (int)(i / 4)), (1 + (i - (i & 4))), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		}
		
		//----------------------BUTTONS--------------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					int values = 0;
					for(int i = 0; i < selections.length; i ++){
						values |=  (  (selections[i].isSelected()) ? (1 << i) : 0  );
					}
					changeRepeatAlternative(context.getContext(), song, header, values);
					dialog.dispose();

				}), TGDialogButtons.clean(() -> {
                    changeRepeatAlternative(context.getContext(), song, header, 0);
                    dialog.dispose();
                }),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	protected int getExistentEndings(TGSong song, TGMeasureHeader header){
		int existentEndings = 0;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader currentHeader = (TGMeasureHeader)it.next();
			if( currentHeader.getNumber() == header.getNumber() ){
				break;
			}
			if( currentHeader.isRepeatOpen() ){
				existentEndings = 0;
			}
			existentEndings |= currentHeader.getRepeatAlternative();
		}
		return existentEndings;
	}
	
	protected int getDefaultEndings(int existentEndings){
		for(int i = 0; i < 8; i ++){
			if((existentEndings & (1 << i)) == 0){
				return (1 << i);
			}
		}
		return -1;
	}
	
	public void changeRepeatAlternative(TGContext context, TGSong song, TGMeasureHeader header, Integer repeatAlternative) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGRepeatAlternativeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative);
		tgActionProcessor.processOnNewThread();
	}
}
