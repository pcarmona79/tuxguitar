package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public abstract class TGMainToolBarSection implements TGToolBarSection {
	
	private static final String SELECTED_CHAR = "\u2713 ";

	private TGMainToolBar toolBar;

	private UIPanel container;
	private UITableLayout layout;
	private List<UIControl> items;
	
	public TGMainToolBarSection(TGMainToolBar toolBar) {
		this.toolBar = toolBar;
		this.container = TGApplication.getInstance(toolBar.getContext()).getFactory().createPanel(toolBar.getControl(), false);
		this.layout = new UITableLayout();
		this.container.setLayout(this.layout);
		this.items = new ArrayList<>();
	}
	
	public abstract void createSection();
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(this.toolBar.getContext(), actionId);
	}
	
	public String getText(String key) {
		return TuxGuitar.getProperty(key);
	}
	
	public String getText(String key, boolean selected) {
		return this.toCheckString(getText(key), selected);
	}
	
	public String toCheckString(String text, boolean selected) {
		return ((selected ? SELECTED_CHAR : "") + text);
	}
	
	public TGIconManager getIconManager() {
		return TuxGuitar.getInstance().getIconManager();
	}
	
	public Tablature getTablature() {
		return TablatureEditor.getInstance(this.toolBar.getContext()).getTablature();
	}
	
	public TGSong getSong() {
		return TGDocumentManager.getInstance(this.toolBar.getContext()).getSong();
	}

	public UIPanel getControl() {
		return this.container;
	}

	public TGMainToolBar getToolBar() {
		return this.toolBar;
	}

	public List<UIControl> getItems() {
		return new ArrayList<>(items);
	}

	public UITableLayout getLayout() {
		return layout;
	}

	public TGContext getContext() {
		return this.getToolBar().getContext();
	}

	public UIFactory getFactory() {
		return TGApplication.getInstance(getContext()).getFactory();
	}

	protected void addItem(UIControl item) {
		this.items.add(item);
	}

	public UIButton createButton() {
		UIButton button = getFactory().createButton(this.container);
		addItem(button);
		this.layout.set(button, 1, this.items.size(), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, 1f, 0f);
		return button;
	}

	public UIToggleButton createToggleButton() {
		UIToggleButton button = getFactory().createToggleButton(this.container, false);
		addItem(button);
		this.layout.set(button, 1, this.items.size(), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, 1f, 0f);
		return button;
	}
}
