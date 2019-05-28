package org.herac.tuxguitar.app.view.widgets.tabwidget;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIContainer;

public class TGTabLabelItem extends TGTabItem {
    private UILabel label;
    TGTabLabelItem(UIFactory factory, UIContainer parent, TGTabWidgetColorModel colors) {
        super(factory, parent, colors);

        this.label = factory.createLabel(this.getInnerControl());
        this.getInnerLayout().set(this.label, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true, 1, 1, null, null, 0f);
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public String getText() {
        return this.label.getText();
    }

    @Override
    public void setBgColor(UIColor color) {
        super.setBgColor(color);
        if (this.label != null) {
            this.label.setBgColor(color);
        }
    }

    @Override
    public void addListeners(TGTabWidget tabs) {
        super.addListeners(tabs);
        this.addListeners(this.label, tabs);
    }

    @Override
    public void setPopupMenu(UIPopupMenu menu) {
        super.setPopupMenu(menu);
        this.label.setPopupMenu(menu);
    }
}
