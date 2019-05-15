package org.herac.tuxguitar.gtk.headerbar.menu;

import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.gtk.GTK;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

import java.util.ArrayList;
import java.util.List;

public class GTKMenuCheckableItem extends GTKMenuItem implements UIMenuCheckableItem {
    private List<UISelectionListener> selectionListeners;
    private Callback activatedProc;

    public GTKMenuCheckableItem(GTKMenuButton owner, GTKMenu parent, String prefix, boolean radio) {
        super(owner, parent);
        this.selectionListeners = new ArrayList<>();
        if (radio) {
            this.handle = TGGTK.createMenuRadioItem(owner.handle, prefix);
        } else {
            this.handle = TGGTK.createMenuCheckboxItem(owner.handle, prefix);
        }
    }

    @Override
    public boolean isChecked() {
        int[] ptr = new int[] { 0 };
        GTK.g_object_get(this.handle, TGGTK.ascii("active"), ptr, 0);
        return ptr[0] != 0;
    }

    @Override
    public void setChecked(boolean checked) {
        GTK.g_object_set(this.handle, TGGTK.ascii("active"), checked, 0);
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
