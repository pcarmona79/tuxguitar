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

public class TGIconButton {

    private static final int DEFAULT_ALPHA = 192;
    private static final int HOVER_ALPHA = 255;
    private static final int DISABLED_ALPHA = 18;

    private List<UISelectionListener> listeners;
    private UICanvas canvas;

    private UIImage icon;
    private boolean hovered;

    public TGIconButton(UIFactory factory, UILayoutContainer parent) {
        this.listeners = new ArrayList<>();
        this.canvas = factory.createCanvas(parent, false);
        this.canvas.addPaintListener(this::paint);
        this.canvas.addMouseUpListener(this::clicked);
        this.canvas.addMouseEnterListener(event -> setHovered(true));
        this.canvas.addMouseExitListener(event -> setHovered(false));
    }

    private void setHovered(boolean hovered) {
        if (this.hovered != hovered) {
            this.hovered = hovered;
            this.canvas.redraw();
        }
    }

    boolean isHovered() {
        return this.hovered;
    }

    int getAlpha() {
        if (!this.canvas.isEnabled()) {
            return DISABLED_ALPHA;
        } else {
            if (hovered) {
                return HOVER_ALPHA;
            }
            return DEFAULT_ALPHA;
        }
    }

    protected void paint(UIPaintEvent event) {
        UIPainter painter = event.getPainter();
        if (icon != null) {
            painter.setAlpha(getAlpha());
            painter.drawImageAdvanced(icon, 0, 0);
        }
    }

    private void clicked(UIMouseEvent event) {
        if (this.canvas.isEnabled() && event.getButton() == 1) {
            for (UISelectionListener listener : this.listeners) {
                listener.onSelect(new UISelectionEvent(this.canvas, event.getState()));
            }
        }
    }

    public void setIcon(UIImage icon) {
        if (icon != this.icon) {
            this.icon = icon;
            resizeTo(icon);
            this.canvas.redraw();
        }
    }

    public UIImage getIcon() {
        return this.icon;
    }

    void resizeTo(UIImage image) {
        final UIRectangle area = this.canvas.getBounds();
        float w = image.getWidth();
        float h = image.getHeight();
        if (w != area.getWidth() || h != area.getHeight()) {
            UILayoutContainer parent = (UILayoutContainer) this.canvas.getParent();
            if (parent != null) {
                UITableLayout layout = (UITableLayout) parent.getLayout();
                layout.set(this.canvas, UITableLayout.PACKED_WIDTH, w);
                layout.set(this.canvas, UITableLayout.PACKED_HEIGHT, h);
                parent.layout();
            }
        }
    }

    public UICanvas getControl() {
        return this.canvas;
    }

    public void addSelectionListener(UISelectionListener listener) {
        this.listeners.add(listener);
    }
}
