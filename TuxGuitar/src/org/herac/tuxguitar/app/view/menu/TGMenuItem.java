package org.herac.tuxguitar.app.view.menu;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.util.TGContext;

import java.util.Collections;
import java.util.List;

public abstract class TGMenuItem {
	
	public abstract void update();
	
	public abstract void loadProperties();
	
	public abstract void showItems();

	public abstract void loadIcons();

	public abstract UIMenuSubMenuItem getMenuItem();

	public List<UIMenuSubMenuItem> getSubMenuItems() {
		return Collections.emptyList();
	}

	public void setMenuItemTextAndAccelerator(UIMenuItem menuItem, String key,String action) {
		menuItem.setKeyCombination(action != null ? KeyBindingActionManager.getInstance(this.findContext()).getKeyBindingForAction(action) : null);
		menuItem.setText(TuxGuitar.getProperty(key));
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}
	
	public TGContext findContext() {
		return TuxGuitar.getInstance().getContext();
	}
}
