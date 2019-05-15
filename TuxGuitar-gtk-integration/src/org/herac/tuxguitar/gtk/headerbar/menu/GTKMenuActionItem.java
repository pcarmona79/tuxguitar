package org.herac.tuxguitar.gtk.headerbar.menu;

import org.eclipse.swt.internal.Callback;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;

import java.util.ArrayList;
import java.util.List;

public class GTKMenuActionItem extends GTKMenuItem implements UIMenuActionItem {
    private List<UISelectionListener> selectionListeners;
    private Callback activatedProc;

    public GTKMenuActionItem(GTKMenuButton owner, GTKMenu parent, String prefix) {
        super(owner, parent);
        this.selectionListeners = new ArrayList<>();
        this.handle = TGGTK.createMenuActionItem(owner.handle, prefix);
    }

    long activatedProc(long action, long param, long data) {
        for (UISelectionListener listener : this.selectionListeners) {
            listener.onSelect( new UISelectionEvent(this));
        }
        return 0;
    }

    @Override
    public void addSelectionListener(UISelectionListener listener) {
        if (this.activatedProc == null) {
            this.activatedProc = new Callback(this, "activatedProc", 3); //$NON-NLS-1$
            TGGTK.menuItemConnectActivated(this.handle, this.activatedProc.getAddress());
        }
        this.selectionListeners.add(listener);
    }

    @Override
    public void removeSelectionListener(UISelectionListener listener) {
        this.selectionListeners.remove(listener);
    }

    @Override
    public void dispose() {
        this.activatedProc.dispose();
        super.dispose();
    }
}
