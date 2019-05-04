package org.herac.tuxguitar.gtk.titlebar;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Menu;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.ui.swt.menu.SWTMenuBar;
import org.herac.tuxguitar.ui.swt.toolbar.SWTToolBar;
import org.herac.tuxguitar.ui.swt.widget.SWTLayoutContainer;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class GTKTitleBar {
    private final SWTToolBar toolBar;
    private final SWTLayoutContainer container;
    private final long windowId;
    private final long toolBarId;
    private final long containerId;
    private final Menu menuBar;
    private long headerBar;
    private boolean enabled;

    private static long getParentId(long id, String searchName) {
        for (;;) {
            long address = OS.G_OBJECT_TYPE_NAME(id);
            int length = C.strlen(address);
            byte [] buffer = new byte [length];
            C.memmove(buffer, address, length);
            String type = new String(Converter.mbcsToWcs(buffer));
            System.out.println(type);
            if (type.equals(searchName)) {
                System.out.println("Found!");
                break;
            }
            id = GTK.gtk_widget_get_parent(id);
            if (id == 0) {
                break;
            }
        }
        return id;
    }
    public GTKTitleBar(SWTWindow window, SWTToolBar toolBar) {
        this.toolBar = toolBar;
        this.container = (SWTLayoutContainer) toolBar.getParent();
        this.windowId = getParentId(window.getControl().handle, "GtkWindow");
        this.toolBarId = getParentId(toolBar.getControl().handle, "GtkToolbar");
        this.containerId = GTK.gtk_widget_get_parent(toolBarId);
        this.menuBar = ((SWTMenuBar) TuxGuitar.getInstance().getItemManager().getMenu()).getControl();
        this.headerBar = GTK.gtk_header_bar_new();
        GTK.gtk_header_bar_set_show_close_button(headerBar, true);
    }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {

                GTK.gtk_window_set_titlebar(windowId, headerBar);
            } else {
                GTK.gtk_window_set_titlebar(windowId, 0);

            }
        }
    }
}
