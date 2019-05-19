package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UIListBoxSelect<T> extends UIControl {

	int getSelectedIndex();

	void setSelectedIndex(int index);

	T getSelectedValue();

	void setSelectedValue(T value);
	
	UISelectItem<T> getSelectedItem();
	
	void setSelectedItem(UISelectItem<T> item);
	
	void addItem(UISelectItem<T> item);

	void addItem(UISelectItem<T> item, int index);

	void setIndex(int index, String text);

	void setItem(UISelectItem<T> item, String text);

	void removeIndex(int index);

	void removeItem(UISelectItem<T> item);
	
	void removeItems();
	
	int getItemCount();
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
