package org.herac.tuxguitar.app.view.dialog.plugin;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionEvent;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGPluginListDialog {
	
	private static final float TABLE_WIDTH = 400;
	private static final float TABLE_HEIGHT = 300;
	
	public void show(final TGViewContext context) {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, true);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("plugins"));
		
		final UICheckTable<String> table = uiFactory.createCheckTable(dialog, true);
		table.setColumns(2);
		table.setColumnName(0, TuxGuitar.getProperty("plugin.column.enabled"));
		table.setColumnName(1, TuxGuitar.getProperty("plugin.column.name"));
		
		dialogLayout.set(table, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(table, UITableLayout.PACKED_WIDTH, TABLE_WIDTH);
		dialogLayout.set(table, UITableLayout.PACKED_HEIGHT, TABLE_HEIGHT);
		
		Iterator<String> it = getModuleIds().iterator();
		while(it.hasNext()){
			String moduleId = (String)it.next();
			TGPluginInfo pluginInfo = new TGPluginInfo(context.getContext(), moduleId);
			
			String pluginName = pluginInfo.getName();
			if( pluginName == null ){
				pluginName = moduleId;
			}
			
			UITableItem<String> item = new UITableItem<String>(moduleId);
			item.setText(1, (pluginName != null ? pluginName : "Undefined Plugin"));
			table.addItem(item);
			table.setCheckedItem(item, TuxGuitar.getInstance().getPluginManager().isEnabled(moduleId));
		}
		
		//------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				new TGDialogButtons.Button("configure", TGDialogButtons.ALIGN_RIGHT, () -> {
					String moduleId = table.getSelectedValue();
					if (moduleId != null && isConfigurable(moduleId)) {
						configure(context.getContext(), dialog, moduleId);
					}
				}), new TGDialogButtons.Button("info", TGDialogButtons.ALIGN_RIGHT, true, () -> {
                    String moduleId = table.getSelectedValue();
                    if( moduleId != null ){
                        showInfo(context.getContext(), dialog, moduleId);
                    }
		}), TGDialogButtons.close(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		buttons.getButton(0).setEnabled(false);
		buttons.getButton(1).setEnabled(false);

		table.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String moduleId = table.getSelectedValue();
				
				buttons.getButton(0).setEnabled(moduleId != null && isConfigurable(moduleId));
				buttons.getButton(1).setEnabled(moduleId != null);
			}
		});
		table.addCheckSelectionListener(new UICheckTableSelectionListener<String>() {
			public void onSelect(UICheckTableSelectionEvent<String> event) {
				if( event.getSelectedItem() != null ) {
					dialog.setCursor(UICursor.WAIT);
					
					UITableItem<String> item = event.getSelectedItem();
					TGPluginManager.getInstance(context.getContext()).updatePluginStatus(item.getValue(), table.isCheckedItem(item));
					table.setSelectedItem(item);
					
					dialog.setCursor(UICursor.NORMAL);
				}
			}
		});

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private List<String> getModuleIds(){
		List<String> moduleIds = new ArrayList<String>();
		Iterator<TGPlugin> it = TuxGuitar.getInstance().getPluginManager().getPlugins().iterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			if(!moduleIds.contains( plugin.getModuleId() )){
				moduleIds.add( plugin.getModuleId() );
			}
		}
		return moduleIds;
	}
	
	private boolean isConfigurable(String moduleId){
		return TGPluginSettingsManager.getInstance().containsPluginSettingsHandler(moduleId);
	}
	
	public void showInfo(TGContext context, UIWindow parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginInfoDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginInfoDialog.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}
	
	public void configure(TGContext context, UIWindow parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginSettingsDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginSettingsDialogController.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}
}
