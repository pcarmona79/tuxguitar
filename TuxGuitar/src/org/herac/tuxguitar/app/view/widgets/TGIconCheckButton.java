package org.herac.tuxguitar.app.view.widgets;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

import java.util.ArrayList;
import java.util.List;

public class TGIconCheckButton extends TGIconButton {

    private static final int UNSELECTED_ALPHA = 80;
    private static final int UNSELECTED_HOVER_ALPHA = 128;

    private UIImage selectedIcon;
    private boolean selected;

    public TGIconCheckButton(UIFactory factory, UILayoutContainer parent) {
        super(factory, parent);
    }

    @Override
    int getAlpha() {
        if (this.getControl().isEnabled() && !selected) {
            if (this.isHovered()) {
                return UNSELECTED_HOVER_ALPHA;
            }
            return UNSELECTED_ALPHA;
        }
        return super.getAlpha();
    }

    @Override
    protected void paint(UIPaintEvent event) {
        UIPainter painter = event.getPainter();
        UIImage image = getIcon() == null || (selected && selectedIcon != null) ? selectedIcon : getIcon();
        if (image != null) {
            resizeTo(image);
            painter.setAlpha(getAlpha());
            painter.drawImageAdvanced(image, 0, 0);
        }
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            this.getControl().redraw();
        }
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelectedIcon(UIImage icon) {
        if (icon != this.selectedIcon) {
            this.selectedIcon = icon;
            if (this.selected || this.getIcon() == null) {
                resizeTo(icon);
                this.getControl().redraw();
            }
        }
    }
}
