package org.herac.tuxguitar.app.view.dialog.confirm;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;

import java.util.ArrayList;
import java.util.List;

public class TGConfirmDialog {

	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_DEFAULT_BUTTON = "defaultButton";
	public static final String ATTRIBUTE_RUNNABLE_YES = "yesRunnable";
	public static final String ATTRIBUTE_RUNNABLE_NO = "noRunnable";
	public static final String ATTRIBUTE_RUNNABLE_CANCEL = "cancelRunnable";
	
	public static int BUTTON_CANCEL = 0x01;
	public static int BUTTON_YES = 0x02;
	public static int BUTTON_NO = 0x04;
	
	public void show(final TGViewContext context) {
		final String message = context.getAttribute(ATTRIBUTE_MESSAGE);
		final Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		final Integer defaultButton = context.getAttribute(ATTRIBUTE_DEFAULT_BUTTON);
		final Runnable yesRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_YES);
		final Runnable noRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_NO);
		final Runnable cancelRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_CANCEL);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("confirm"));
		
		//========================================================================
		UITableLayout panelLayout = new UITableLayout();
		UIPanel uiPanel = uiFactory.createPanel(dialog, false);
		uiPanel.setLayout(panelLayout);
		dialogLayout.set(uiPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIImageView uiIcon = uiFactory.createImageView(uiPanel);
		uiIcon.setImage(TGIconManager.getInstance(context.getContext()).getStatusQuestion());
		panelLayout.set(uiIcon, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		UILabel uiMessage = uiFactory.createLabel(uiPanel);
		uiMessage.setText(message);
		panelLayout.set(uiMessage, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//========================================================================

        List<TGDialogButtons.Button> buttonList = new ArrayList<>();
		if((style & BUTTON_YES) != 0){
		    buttonList.add(TGDialogButtons.yes(() -> exec(dialog, yesRunnable), defaultButton == BUTTON_YES));
		}
		if((style & BUTTON_NO) != 0){
			buttonList.add(TGDialogButtons.no(() -> exec(dialog, noRunnable), defaultButton == BUTTON_NO));
		}
		if((style & BUTTON_CANCEL) != 0){
			buttonList.add(TGDialogButtons.yes(() -> exec(dialog, cancelRunnable), defaultButton == BUTTON_CANCEL));
		}
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog, buttonList);
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void exec(final UIWindow dialog, final Runnable runnable) {
		dialog.dispose();
		if (runnable != null) {
			runnable.run();
		}
	}
}
