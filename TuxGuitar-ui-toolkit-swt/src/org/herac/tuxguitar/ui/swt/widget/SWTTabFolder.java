package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;

import java.util.ArrayList;
import java.util.List;

public class SWTTabFolder extends SWTControl<TabFolder> implements UITabFolder, SWTContainer<TabFolder> {

	private List<SWTTabItem> tabs;
	private UICloseListenerManager closeListener;
	private SWTSelectionListenerManager selectionListener;

	public SWTTabFolder(SWTContainer<? extends Composite> container) {
		super(new TabFolder(container.getControl(), SWT.TOP), container);
		
		this.tabs = new ArrayList<>();
		this.closeListener = new UICloseListenerManager();
		this.selectionListener = new SWTSelectionListenerManager(this);
		this.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onTabSelected();
			}
		});
	}
	
	public void dispose(SWTTabItem item) {
		this.tabs.remove(item);
		item.disposeControl();
	}

	public UITabItem createTab() {
		SWTTabItem tabItem = new SWTTabItem(new TabItem(this.getControl(), SWT.NONE), this);
		this.tabs.add(tabItem);
		return tabItem;
	}

	public List<UITabItem> getTabs() {
		return new ArrayList<>(this.tabs);
	}
	
	public UITabItem findTab(TabItem cTabItem) {
		if( cTabItem != null ) {
			for(SWTTabItem tab : this.tabs) {
				if( cTabItem.equals(tab.getItem())) {
					return tab;
				}
			}
		}
		return null;
	}

	public UITabItem getSelectedTab() {
	    TabItem[] selection = this.getControl().getSelection();
	    if (selection.length > 0) {
            return this.findTab(selection[0]);
		}
	    return null;
	}

	public void setSelectedTab(UITabItem tab) {
		this.getControl().setSelection(((SWTTabItem)tab).getItem());
		this.onTabSelected();
	}

	public int getSelectedIndex() {
		return this.getControl().getSelectionIndex();
	}

	public void setSelectedIndex(int index) {
		this.getControl().setSelection(index);
		this.onTabSelected();
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}

	public void addTabCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeTabCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	public void onTabSelected() {
		UITabItem selectedTab = this.getSelectedTab();
		if( selectedTab != null ) {
			((SWTTabItem) selectedTab).onSelect();
		}
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		for(UIControl uiControl : this.getTabs()) {
			uiControl.computePackedSize(null, null);
		}
		
		UISize packedSize = this.getPackedSize();
		if( packedSize.getWidth() == 0f && packedSize.getHeight() == 0f ) {
			super.computePackedSize(fixedWidth, fixedHeight);
		} else {
			if( fixedWidth != null && fixedWidth != packedSize.getWidth() ) {
				packedSize.setWidth(fixedWidth);
			}
			if( fixedHeight != null && fixedHeight != packedSize.getHeight() ) {
				packedSize.setHeight(fixedHeight);
			}
			this.setPackedSize(packedSize);
		}
	}

	public void setTopRight(UIControl control) {
	}

	@Override
	public List<UIControl> getChildren() {
	    return null;
	}

	@Override
	public void addChild(UIControl uiControl) {

	}

	@Override
	public void removeChild(UIControl uiControl) {

	}
}
