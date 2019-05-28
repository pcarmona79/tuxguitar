package org.herac.tuxguitar.app.view.widgets.tabwidget;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.widgets.TGIconButton;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIPanel;

import java.util.HashMap;
import java.util.Map;

public class TGTabItem {
    private final static float BORDER = 3f;

    private final TGTabWidgetColorModel colors;

    private final UIPanel outer;
    private final UITableLayout outerLayout;
    private final UIPanel vertical;
    private final UITableLayout verticalLayout;
    private final UIPanel horizontal;
    private final UIPanel inner;
    private final UITableLayout innerLayout;
    private final TGIconButton closeButton;
    private final UIPanel underline;

    private Map<String, Object> data;
    private boolean selected;

    TGTabItem(UIFactory factory, UIContainer parent, TGTabWidgetColorModel colors) {
        this.colors = colors;

        this.outer = factory.createPanel(parent, false);
        this.outerLayout = new UITableLayout(0f);
        this.outer.setLayout(outerLayout);

        UIPanel leftBorder = factory.createPanel(this.outer, false);
        leftBorder.setBgColor(this.colors.getBorderColor());
        this.outerLayout.set(leftBorder, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 1f, null, 0f);
        this.outerLayout.set(leftBorder, UITableLayout.PACKED_WIDTH, 1f);

        this.vertical = factory.createPanel(this.outer, false);
        this.verticalLayout = new UITableLayout(0f);
        this.vertical.setLayout(verticalLayout);
        this.outerLayout.set(this.vertical, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

        this.horizontal = factory.createPanel(this.vertical, false);
        UITableLayout horizontalLayout = new UITableLayout(0f);
        this.horizontal.setLayout(horizontalLayout);
        verticalLayout.set(this.horizontal, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
        verticalLayout.set(this.horizontal, UITableLayout.MARGIN_TOP, BORDER);
        verticalLayout.set(this.horizontal, UITableLayout.MARGIN_LEFT, BORDER);
        verticalLayout.set(this.horizontal, UITableLayout.MARGIN_RIGHT, BORDER);

        this.inner = factory.createPanel(this.horizontal, false);
        this.innerLayout = new UITableLayout(0f);
        this.inner.setLayout(this.innerLayout);
        horizontalLayout.set(this.inner, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true, 1, 1, null, null, 0f);

        this.closeButton = new TGIconButton(factory, this.horizontal);
        horizontalLayout.set(this.closeButton.getControl(), 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, true, 1, 1, null, null, 0f);
        horizontalLayout.set(this.closeButton.getControl(), UITableLayout.MARGIN_LEFT, BORDER);

        this.underline = factory.createPanel(this.vertical, false);
        verticalLayout.set(this.underline, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
        verticalLayout.set(this.underline, UITableLayout.PACKED_HEIGHT, BORDER);

        this.loadIcons();
        this.loadProperties();
    }

    public void setSelected(boolean selected) {
        if (selected != this.selected) {
            this.selected = selected;
            this.updateUnderline();
        }
    }

    void setBgColor(UIColor color) {
        this.vertical.setBgColor(color);
        this.horizontal.setBgColor(color);
        this.inner.setBgColor(color);
        this.closeButton.getControl().setBgColor(color);
    }

    private void updateUnderline() {
        this.underline.setBgColor(null);
        this.setBgColor(null);

        if (this.selected) {
            verticalLayout.set(this.horizontal, UITableLayout.MARGIN_BOTTOM, 0f);
            verticalLayout.set(this.underline, UITableLayout.PACKED_HEIGHT, BORDER);
            this.underline.setBgColor(this.colors.getUnderlineColor());
            this.setBgColor(this.colors.getSelectedBgColor());
        } else {
            verticalLayout.set(this.horizontal, UITableLayout.MARGIN_BOTTOM, BORDER - 1f);
            verticalLayout.set(this.underline, UITableLayout.PACKED_HEIGHT, 1f);

            this.underline.setBgColor(this.colors.getBorderColor());
        }
        this.vertical.layout();
    }

    public TGIconButton getCloseButton() {
        return this.closeButton;
    }

    public void setData(String key, Object value) {
        this.getData().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.getData().get(key);
    }

    public Map<String, Object> getData() {
        if( this.data == null ) {
            this.data = new HashMap<>();
        }
        return this.data;
    }

    public boolean isDisposed() {
        return this.vertical.isDisposed();
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.outer.dispose();
        }
    }

    public UIPanel getControl() {
        return this.outer;
    }

    public UIPanel getInnerControl(){
        return this.inner;
    }

    public UITableLayout getInnerLayout() {
        return this.innerLayout;
    }

    public void loadIcons() {
        TGIconManager iconManager = TGIconManager.getInstance(TuxGuitar.getInstance().getContext());
        this.closeButton.setIcon(iconManager.getCloseDim());
        this.closeButton.setHoveredIcon(iconManager.getClose());
        this.updateUnderline();
    }

    public void loadProperties() {
        this.closeButton.getControl().setToolTipText(TuxGuitar.getProperty("close"));
    }

    protected void addListeners(UIControl control, TGTabWidget tabs) {
        control.addMouseDownListener(event -> tabs.setSelectedItem(this));
    }

    void addListeners(TGTabWidget tabs) {
        this.addListeners(this.horizontal, tabs);
    }

    public void setPopupMenu(UIPopupMenu menu) {
        this.horizontal.setPopupMenu(menu);
        this.closeButton.getControl().setPopupMenu(menu);
    }

    public UIPopupMenu getPopupMenu() {
        return this.horizontal.getPopupMenu();
    }

}
