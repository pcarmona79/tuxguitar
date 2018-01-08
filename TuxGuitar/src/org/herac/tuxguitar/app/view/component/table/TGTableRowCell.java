package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGTableRowCell {
	
	private TGTableRow row;
	private UIPanel cell;
	private UITableLayout layout;

	public TGTableRowCell(TGTableRow row){
		this.row = row;
		this.cell = this.row.getTable().getUIFactory().createPanel(this.row.getControl(), false);
		this.row.getTable().appendListeners(this.cell);
		this.createLayout();
	}
	
	private void createLayout() {
		this.layout = new UITableLayout();
		this.layout.set(UITableLayout.MARGIN_TOP, 2f);
		this.layout.set(UITableLayout.MARGIN_BOTTOM, 2f);
		
		this.cell.setLayout(getLayout());
	}
	
	public UITableLayout getLayout() {
		return layout;
	}
	
	public void setBgColor(UIColor background){
		this.cell.setBgColor(background);
	}
	
	public void setFgColor(UIColor foreground){
		this.cell.setFgColor(foreground);
	}
	
	public void setMenu(UIPopupMenu menu) {
		this.cell.setPopupMenu(menu);
	}
	
	public void setData(String key, Object data) {
		this.cell.setData(key, data);
	}
	
	public <T> T getData(String key) {
		return this.cell.getData(key);
	}
	
	public void addMouseDownListener(UIMouseDownListener listener) {
		this.cell.addMouseDownListener(listener);
	}
	
	public void addMouseUpListener(UIMouseUpListener listener) {
		this.cell.addMouseUpListener(listener);
	}
	
	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.cell.addMouseDoubleClickListener(listener);
	}
	
	public UIPanel getControl() {
		return this.cell;
	}
	
	public void dispose(){
		this.row.dispose();
	}

}
