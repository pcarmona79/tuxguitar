package org.herac.tuxguitar.app.view.widgets.tabwidget;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.*;

import java.util.ArrayList;
import java.util.List;

public class TGTabWidget {
    private final UIFactory factory;
    private final TGTabWidgetColorModel colors;

    private final UIPanel container;
    private final UITableLayout containerLayout;
    private final UIScrollBarPanel scroll;
    private final UIPanel tabs;
    private final UITableLayout tabsLayout;
    private final UIButton scrollLeft;
    private final UIButton scrollRight;

    private final List<TGTabItem> items;
    private final List<Listener> selectionListeners;
    private final List<Listener> closeListeners;
    private int currentIndex = -1;

    public TGTabWidget(UIFactory factory, UIContainer parent) {
        this.factory = factory;
        this.colors = new TGTabWidgetColorModel(TuxGuitar.getInstance().getContext(), factory);
        this.items = new ArrayList<>();
        this.selectionListeners = new ArrayList<>();
        this.closeListeners = new ArrayList<>();

        this.container = factory.createPanel(parent, false);
        this.containerLayout = new UITableLayout(0f);
        this.container.setLayout(this.containerLayout);

        this.scrollLeft = factory.createButton(this.container);
        this.scrollLeft.addSelectionListener(event -> scrollTabs(-1));
        this.containerLayout.set(this.scrollLeft, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);

        this.scroll = factory.createScrollBarPanel(this.container, false, true, false);
        this.scroll.setLayout(new UIScrollBarPanelLayout(true, false, true, true, true, false));
        this.scroll.getHScroll().setVisible(false);
        this.containerLayout.set(this.scroll, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

        this.scrollRight = factory.createButton(this.container);
        this.scrollRight.addSelectionListener(event -> scrollTabs(1));
        this.containerLayout.set(this.scrollRight, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);

        this.tabs = factory.createPanel(this.scroll, false);
        this.tabsLayout = new UITableLayout(0f);
        this.tabs.setLayout(this.tabsLayout);

        this.scroll.addResizeListener(event -> updateScrollButtons());

        this.updateScrollButtons();
    }

    private void updateScrollButtons() {
        UIScrollBar bar = this.scroll.getHScroll();
        Float width = bar.getMaximum() > 0 ? null : 0f;
        this.containerLayout.set(this.scrollLeft, UITableLayout.PACKED_WIDTH, width);
        this.containerLayout.set(this.scrollRight, UITableLayout.PACKED_WIDTH, width);
        this.scrollLeft.setEnabled(bar.getValue() > 0);
        this.scrollRight.setEnabled(bar.getValue() < bar.getMaximum());
        this.container.layout();
    }

    private void scrollTabs(int dir) {
        UIScrollBar bar = this.scroll.getHScroll();
        int index = this.getIndexAt(bar.getValue());
        if (this.items.get(this.getIndexAt(bar.getValue())).getControl().getBounds().getX() == bar.getValue()) {
            index = Math.max(index + dir, 0);
        } else {
            index = dir > 0 ? index + 1 : index;
        }
        this.scrollTo(this.items.get(Math.min(index, this.items.size() - 1)));
    }

    private void scrollTo(TGTabItem item) {
        UIScrollBar bar = this.scroll.getHScroll();
        bar.setValue((int) item.getControl().getBounds().getX());
        this.updateScrollButtons();
    }

    private void scrollToIfNeeded(TGTabItem item) {
        UIScrollBar bar = this.scroll.getHScroll();
        float scrollX = bar.getValue();
        float scrollWidth = this.scroll.getBounds().getWidth();
        UIRectangle tab = item.getControl().getBounds();
        if (tab.getX() < scrollX || tab.getWidth() >= scrollWidth) {
            this.scrollTo(item);
        } else if (tab.getX() + tab.getWidth() >= scrollX + scrollWidth) {
            bar.setValue((int) (tab.getX() + tab.getWidth() - scrollWidth));
            this.updateScrollButtons();
        }
    }

    private int getIndexAt(int x) {
        for (int i = 0; i < this.items.size(); i++) {
            TGTabItem item = this.items.get(i);
            UIRectangle area = item.getControl().getBounds();
            if (x >= area.getX() && x < area.getX() + area.getWidth()) {
                return i;
            }
        }
        return -1;
    }

    public TGTabItem createTab() {
        return this.addItem(new TGTabItem(this.factory, this.tabs, this.colors));
    }

    public TGTabLabelItem createLabelTab() {
        return this.addItem(new TGTabLabelItem(this.factory, this.tabs, this.colors));
    }

    private <T extends TGTabItem> T addItem(T item) {
        this.items.add(item);
        item.addListeners(this);
        item.getCloseButton().addSelectionListener(event -> removeItem(item));
        this.tabsLayout.set(item.getControl(), 1, this.items.size(), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
        if (this.currentIndex == -1) {
            this.setSelectedIndex(0);
        }
        this.updateScrollButtons();
        return item;
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void removeItem(TGTabItem item) {
        this.removeIndex(this.items.indexOf(item));
    }

    public void removeIndex(int index) {
        if (index >= 0 && index < this.items.size()) {
            TGTabItem item = this.items.get(index);
            for (Listener listener : this.closeListeners) {
                listener.onEvent(new Event(index, this.items.get(index)));
            }
            this.items.remove(index);
            this.tabsLayout.removeControlAttributes(item.getControl());
            if (this.currentIndex >= this.items.size()) {
                this.setSelectedIndex(this.currentIndex - 1);
            }
            if (this.items.size() == 0) {
                this.currentIndex = -1;
            }
            item.dispose();
            this.updateScrollButtons();
        }
    }

    public void setSelectedItem(TGTabItem item) {
        this.setSelectedIndex(this.items.indexOf(item));
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < this.items.size() && index != this.currentIndex) {
            if (this.currentIndex >= 0 && this.currentIndex < this.items.size()) {
                this.items.get(this.currentIndex).setSelected(false);
            }
            this.currentIndex = index;
            this.items.get(this.currentIndex).setSelected(true);
            for (Listener listener : this.selectionListeners) {
                listener.onEvent(new Event(index, this.items.get(index)));
            }
            this.container.layout();
            this.scrollToIfNeeded(this.items.get(index));
        }
    }

    public int getSelectedIndex() {
        return this.currentIndex;
    }

    public TGTabItem getItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            return this.items.get(index);
        }
        return null;
    }

    public int getItemIndex(TGTabItem item) {
        return this.items.indexOf(item);
    }

    public void packLeft(UIControl control) {
        this.containerLayout.set(this.tabs, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);
    }

    public void packRight(UIControl control) {
        this.containerLayout.set(this.tabs, 1, 5, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);
    }

    public boolean isDisposed() {
        return this.container.isDisposed();
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.container.dispose();
            this.colors.dispose();
        }
    }

    public UIPanel getControl() {
        return this.container;
    }

    public void loadIcons() {
        for (TGTabItem item : items) {
            item.loadIcons();
        }
        TGIconManager iconManager = TGIconManager.getInstance(TuxGuitar.getInstance().getContext());
        this.scrollLeft.setImage(iconManager.getArrowLeft());
        this.scrollRight.setImage(iconManager.getArrowRight());
    }

    public void loadProperties() {
        for (TGTabItem item : items) {
            item.loadProperties();
        }
        this.scrollLeft.setToolTipText(TuxGuitar.getProperty("scroll-left"));
        this.scrollRight.setToolTipText(TuxGuitar.getProperty("scroll-right"));
    }

    public void addSelectionListener(Listener listener) {
        this.selectionListeners.add(listener);
    }

    public void addTabCloseListener(Listener listener) {
        this.closeListeners.add(listener);
    }

    public interface Listener {
        void onEvent(Event event);
    }

    public static class Event {
        private int index;
        private TGTabItem item;

        Event(int index, TGTabItem item) {
            this.index = index;
            this.item = item;
        }

        public int getIndex() {
            return index;
        }

        public TGTabItem getItem() {
            return item;
        }
    }
}
