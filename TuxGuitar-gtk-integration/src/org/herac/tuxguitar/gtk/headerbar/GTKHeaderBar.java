package org.herac.tuxguitar.gtk.headerbar;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.app.view.menu.TGMenuManager;
import org.herac.tuxguitar.app.view.menu.impl.ViewMenuItem;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBarSection;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.swt.menu.SWTMenuCheckableItem;
import org.herac.tuxguitar.ui.swt.widget.SWTControl;
import org.herac.tuxguitar.ui.swt.widget.SWTPanel;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.util.TGContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GTKHeaderBar {

    private static final String JNI_LIBRARY_NAME = "tuxguitar-gtk-integration-jni";

    static{
        System.loadLibrary(JNI_LIBRARY_NAME);
    }

    private final long windowId;
    private final TGMenuManager menuManager;
    private final TGMainToolBar toolBar;
    private final TGResourceManager resourceManager;

    private long headerBar;
    private boolean enabled;
    private List<HeaderWidget> widgets;
    private long provider;

    public GTKHeaderBar(TGContext context) {
        SWTWindow window = (SWTWindow) TGWindow.getInstance(context).getWindow();
        this.windowId = getParentId(window.getControl().handle, "GtkWindow");
        this.toolBar = TGMainToolBar.getInstance(context);
        this.menuManager = TuxGuitar.getInstance().getItemManager();
        this.widgets = new ArrayList<>();
        this.resourceManager = TGResourceManager.getInstance(context);
    }

    private static String charPtrToString(long address) {
        int length = C.strlen(address);
        byte [] buffer = new byte [length];
        C.memmove(buffer, address, length);
        return new String(Converter.mbcsToWcs(buffer));
    }

    private static long getParentId(long id, String searchName) {
        for (;;) {
            if (charPtrToString(OS.G_OBJECT_TYPE_NAME(id)).equals(searchName)) {
                break;
            }
            id = GTK.gtk_widget_get_parent(id);
            if (id == 0) {
                break;
            }
        }
        return id;
    }

    private static native long createHeaderBar();

    private static native long createButtonBox();

    private static native long createHidingScrolledWindow(int alignment);

    private static native void packLeft(long headerBar, long widget);

    private static native void packCenter(long headerBar, long widget);

    private static native void packRight(long headerBar, long widget);

    private static native void setHeaderBar(long window, long header);

    public void setEnabled(boolean enabled) throws IOException {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                if (headerBar == 0) {
                    headerBar = createHeaderBar();
                    GTK.g_object_ref(headerBar);

                    this.provider = GTK.gtk_css_provider_new();
                    byte[] css = resourceManager.getResourceAsStream("tuxguitar.css").readAllBytes();
                    GTK.gtk_css_provider_load_from_data(provider, css, css.length, new long[]{0});
                    GTK.gtk_style_context_add_provider_for_screen(GTK.gtk_widget_get_screen(headerBar), provider, GTK.GTK_STYLE_PROVIDER_PRIORITY_APPLICATION);

                    // ---- RIGHT ----
                    TGMainToolBarSection layout = (TGMainToolBarSection) this.toolBar.getSections().get(0);

                    long scrolled = createHidingScrolledWindow(GTK.GTK_ALIGN_START);
                    long container = GTK.gtk_box_new(GTK.GTK_ORIENTATION_HORIZONTAL, 6);
                    GTK.gtk_container_add(scrolled, container);

                    for (Long box : createButtonBoxes(layout, false)) {
                        GTK.gtk_container_add(container, box);
                    }
                    packLeft(headerBar, scrolled);

                    // ---- CENTER ----
                    TGMainToolBarSection transport = (TGMainToolBarSection) this.toolBar.getSections().get(1);
                    for (Long box : createButtonBoxes(transport, false)) {
                        packCenter(headerBar, box);
                    }

                    // ---- LEFT ----
                    TGMainToolBarSection view = (TGMainToolBarSection) this.toolBar.getSections().get(2);

                    scrolled = createHidingScrolledWindow(GTK.GTK_ALIGN_END);
                    container = GTK.gtk_box_new(GTK.GTK_ORIENTATION_HORIZONTAL, 6);
                    GTK.gtk_widget_set_halign(container, GTK.GTK_ALIGN_END);
                    GTK.gtk_container_add(scrolled, container);

                    for (Long box : createButtonBoxes(view, false)) {
                        GTK.gtk_container_add(container, box);
                    }
                    packRight(headerBar, scrolled);
                } else {
                    for (HeaderWidget widget : this.widgets) {
                        moveWidget(widget, widget.previousParent);
                        widget.owner.removeChild(widget.control);
                    }
                }

                setHeaderBar(windowId, headerBar);
                this.menuManager.setMainMenuForceHidden(true);
                this.toolBar.setForceHidden(true);
                this.updateViewItemsVisibility(this.menuManager.getLoadedMenuItems(), false);
            } else {
                setHeaderBar(windowId, 0);

                for (HeaderWidget widget : this.widgets) {
                    moveWidget(widget, widget.previousParent);
                    widget.owner.addChild(widget.control);
                }

                this.toolBar.setForceHidden(false);
                this.menuManager.setMainMenuForceHidden(false);
            }
        }
    }

    private void updateViewItemsVisibility(List<TGMenuItem> menu, boolean visible) {
        for (TGMenuItem item : menu) {
            if (item instanceof ViewMenuItem) {
                ViewMenuItem subMenu = (ViewMenuItem) item;
                GTK.gtk_widget_set_visible(((SWTMenuCheckableItem) subMenu.getShowMenuBar()).getControl().handle, visible);
                GTK.gtk_widget_set_visible(((SWTMenuCheckableItem) subMenu.getShowMainToolbar()).getControl().handle, visible);
            }
        }
    }

    private void moveWidget(HeaderWidget widget, long newParent) {
        widget.previousParent = GTK.gtk_widget_get_parent(widget.handle);

        GTK.g_object_ref(widget.handle);
        GTK.gtk_container_remove(widget.previousParent, widget.handle);
        GTK.gtk_container_add(newParent, widget.handle);
        GTK.g_object_unref(widget.handle);
    }

    private List<Long> createButtonBoxes(TGMainToolBarSection section, boolean reverse) {
        List<Long> boxes = new ArrayList<>();
        long box = createButtonBox();
        GTK.gtk_widget_set_valign(box, GTK.GTK_ALIGN_CENTER);
        for (int i = 0; i < section.getItems().size(); i++) {
            HeaderWidget widget = new HeaderWidget();
            widget.control = (SWTControl) section.getItems().get(i);
            Control wrapped = (Control) widget.control.getControl();

            widget.handle = wrapped.handle;
            widget.owner = (SWTPanel) section.getControl();

            if (i > 0 && section.getLayout().get(widget.control, UITableLayout.MARGIN_LEFT, 0f) > 0f) {
                boxes.add(reverse ? 0 : boxes.size(), box);
                box = createButtonBox();
                GTK.gtk_widget_set_valign(box, GTK.GTK_ALIGN_CENTER);
            }
            float width = section.getLayout().get(widget.control, UITableLayout.PACKED_WIDTH, -1f);
            if (width > 0f) {
                GTK.gtk_widget_set_size_request(widget.handle, (int) width, -1);
            }

            moveWidget(widget, box);
            widget.owner.removeChild(widget.control);
            this.widgets.add(widget);
        }
        boxes.add(reverse ? 0 : boxes.size(), box);
        return boxes;
    }

    private static class HeaderWidget {
        public SWTControl control;
        public SWTPanel owner;
        public long handle;
        public long previousParent;
    }
}
