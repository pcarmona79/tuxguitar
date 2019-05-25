package org.herac.tuxguitar.app.view.dialog.settings;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.dialog.settings.items.*;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.properties.TGProperties;

import java.util.ArrayList;
import java.util.List;

public class TGSettingsEditor{
	
	private TGViewContext context;
	private TGCursorController cursorController;
	private TGConfigManager config;
	private TGProperties defaults;
	private UIWindow dialog;
	private List<TGSettingsOption> options;
	
	private List<Runnable> runnables;
	
	public TGSettingsEditor(TGViewContext context) {
		this.context = context;
		this.config = TGConfigManager.getInstance(this.context.getContext());
	}
	
	public void show() {
		final UIFactory uiFactory = this.getUIFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout(0f);
		
		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("settings.config"));
		
		//-------main-------------------------------------
		UIPanel mainComposite = uiFactory.createPanel(this.dialog, false);
		mainComposite.setLayout(new UITableLayout(0f));
		dialogLayout.set(mainComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		this.createComposites(mainComposite);
		
		//-------buttons-------------------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, this.dialog,
				TGDialogButtons.ok(() -> {
					updateOptions();
					dispose();
					applyConfigWithConfirmation(false);
				}),
				TGDialogButtons.cancel(this::dispose),
				TGDialogButtons.defaults(() -> {
					dispose();
					setDefaults();
					applyConfigWithConfirmation(true);
				}));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void createComposites(UILayoutContainer parent) {
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UIToolBar toolBar = uiFactory.createHorizontalToolBar(parent);
		parentLayout.set(toolBar, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		UISeparator separator = uiFactory.createHorizontalSeparator(parent);
		parentLayout.set(separator, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);

		UIPanel option = uiFactory.createPanel(parent, false);
		option.setLayout(new UITableLayout(0f));
		parentLayout.set(option, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		initOptions(toolBar, option);
		
		if( this.options.size() > 0 ){
			select(this.options.get(0));
		}
	}
	
	private void initOptions(UIToolBar toolBar, UILayoutContainer parent){
		
		this.options = new ArrayList<TGSettingsOption>();
		this.options.add(new SoundOption(this, toolBar, parent));
		this.options.add(new MainOption(this, toolBar, parent));
		this.options.add(new StylesOption(this, toolBar, parent));
		this.options.add(new LanguageOption(this, toolBar, parent));
		this.options.add(new SkinOption(this, toolBar, parent));

		for(TGSettingsOption option : this.options) {
			option.createOption();
		}
	}
	
	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context.getContext(), this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}
	
	public void pack(){
		this.dialog.pack();
	}
	
	public void select(TGSettingsOption option){
		hideAll();
		option.setVisible(true);
		this.dialog.redraw();
	}
	
	private void hideAll(){
		for(TGSettingsOption option : this.options) {
			option.setVisible(false);
		}
	}
	
	protected void updateOptions(){
		for(TGSettingsOption option : this.options) {
			option.updateConfig();
		}
		this.config.save();
	}
	
	protected void setDefaults(){
		for(TGSettingsOption option : this.options) {
			option.updateDefaults();
		}
		this.config.save();
	}
	
	protected void applyConfigWithConfirmation(final boolean force) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("settings.config.apply-changes-question"));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
			public void run() {
				applyConfig(force);
			}
		});
		tgActionProcessor.process();
	}
	
	protected void applyConfig(final boolean force) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGReloadSettingsAction.NAME);
		tgActionProcessor.setAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE, force);
		tgActionProcessor.process();
	}
	
	protected void dispose(){
		for(TGSettingsOption option : this.options) {
			option.dispose();
		}
		getWindow().dispose();
	}
	
	public TGProperties getDefaults(){
		if( this.defaults == null ){
			this.defaults = TGConfigDefaults.createDefaults();
		}
		return this.defaults;
	}
	
	public TGConfigManager getConfig(){
		return this.config;
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.getInstance().getTablatureEditor();
	}
	
	public TGViewContext getViewContext() {
		return this.context;
	}
	
	public UIWindow getWindow(){
		return this.dialog;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}
	
	public void addSyncThread(Runnable runnable){
		this.runnables.add( runnable );
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
}
