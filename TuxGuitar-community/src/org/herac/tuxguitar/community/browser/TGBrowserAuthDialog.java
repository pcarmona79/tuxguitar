package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserAuthDialog {
	
	private TGContext context;
	private Runnable onSuccess;
	
	public TGBrowserAuthDialog(TGContext context, Runnable onSuccess) {
		this.context = context;
		this.onSuccess = onSuccess;
	}
	
	public void open() {
		TGBrowserDialog browser = TGBrowserDialog.getInstance(this.context);
		this.open(!browser.isDisposed() ? browser.getWindow() : TGWindow.getInstance(this.context).getWindow());
	}
	
	public void open(final UIWindow parent) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.title"));
		
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------USERNAME---------------------------------
		UILabel usernameLabel = uiFactory.createLabel(group);
		usernameLabel.setText(TuxGuitar.getProperty("tuxguitar-community.browser-dialog.account.user") + ":");
		groupLayout.set(usernameLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIReadOnlyTextField usernameText = uiFactory.createReadOnlyTextField(group);
		usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
		groupLayout.set(usernameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 250f, null, null);
		
		final UIButton usernameChooser = uiFactory.createButton(group);
		usernameChooser.setText("...");
		usernameChooser.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				new TGCommunityAuthDialog(getContext(), new Runnable() {
					public void run() {
						TGCommunitySingleton.getInstance(getContext()).getAuth().update();
						usernameText.setText( TGCommunitySingleton.getInstance(getContext()).getAuth().getUsername() );
					}
				}, null).open(dialog);
			}
		});
		groupLayout.set(usernameChooser, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					onFinish(dialog, TGBrowserAuthDialog.this.onSuccess);
				}),
				TGDialogButtons.cancel(() -> onFinish(dialog, null)));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void onFinish(UIWindow dialog, Runnable runnable) {
		dialog.dispose();
		if( runnable != null ) {
			runnable.run();
		}
	}
	
	public TGContext getContext() {
		return context;
	}
}
