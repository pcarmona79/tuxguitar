package org.herac.tuxguitar.gtk.headerbar;

import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.app.view.menu.TGMenuManager;
import org.herac.tuxguitar.app.view.menu.impl.*;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBarSection;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.gtk.headerbar.menu.GTKMenuButton;
import org.herac.tuxguitar.gtk.headerbar.menu.GTKMenuCheckableItem;
import org.herac.tuxguitar.gtk.headerbar.menu.GTKMenuSubMenuItem;
import org.herac.tuxguitar.io.base.TGFileFormatAvailabilityEvent;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.ui.swt.menu.SWTMenuCheckableItem;
import org.herac.tuxguitar.ui.swt.widget.*;
import org.herac.tuxguitar.util.TGContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GTKHeaderBar implements TGEventListener {

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
    private GTKMenuButton menuButton;
    private List<TGMenuItem> menuItems;
    private long provider;

    private TGSyncProcessLocked updateItemsProcess;
    private TGSyncProcessLocked loadIconsProcess;
    private TGSyncProcessLocked loadPropertiesProcess;
    private TGSyncProcessLocked createMenuProcess;
    private TGSyncProcessLocked redrawProcess;

    public GTKHeaderBar(TGContext context) {
        SWTWindow window = (SWTWindow) TGWindow.getInstance(context).getWindow();
        this.windowId = getParentId(window.getControl().handle, "GtkWindow");
        this.toolBar = TGMainToolBar.getInstance(context);
        this.menuManager = TuxGuitar.getInstance().getItemManager();
        this.widgets = new ArrayList<>();
        this.menuItems = new ArrayList<>();
        this.resourceManager = TGResourceManager.getInstance(context);

        this.updateItemsProcess = new TGSyncProcessLocked(context, this::updateItems);
        this.loadIconsProcess = new TGSyncProcessLocked(context, this::loadIcons);
        this.loadPropertiesProcess = new TGSyncProcessLocked(context, this::loadProperties);
        this.createMenuProcess = new TGSyncProcessLocked(context, this::createMenu);
        this.redrawProcess = new TGSyncProcessLocked(context, this::redraw);
    }

    public void updateItems() {
        if (this.enabled) {
            for (TGMenuItem menuItem : menuItems) {
                menuItem.update();
            }
        }
    }

    public void loadIcons() {
        if (this.enabled) {
            for (TGMenuItem menuItem : menuItems) {
                menuItem.loadIcons();
            }
        }
        for (HeaderWidget widget : this.widgets) {
            if (widget.control instanceof SWTButton) {
                ((SWTButton) widget.control).setText("");
            } else if (widget.control instanceof SWTToggleButton) {
                ((SWTToggleButton) widget.control).setText("");
            }
        }
    }

    public void loadProperties() {
        if (this.enabled) {
            for (TGMenuItem menuItem : menuItems) {
                menuItem.loadProperties();
            }
        }
    }

    public void createMenu() {
        if (this.enabled) {
            if (this.menuButton == null) {
                this.menuButton = new GTKMenuButton();
                GTK.gtk_widget_set_valign(this.menuButton.handle, GTK.GTK_ALIGN_CENTER);
            }
            for (TGMenuItem item: this.menuItems) {
                item.getMenuItem().dispose();
            }
            this.menuItems.clear();

            this.menuItems.add(new FileMenuItem(menuButton));
            this.menuItems.add(new EditMenuItem(menuButton));
            this.menuItems.add(new ViewMenuItem(menuButton));
            this.menuItems.add(new CompositionMenuItem(menuButton));
            this.menuItems.add(new TrackMenuItem(menuButton));
            this.menuItems.add(new MeasureMenuItem(menuButton));
            this.menuItems.add(new BeatMenuItem(menuButton));
            this.menuItems.add(new MarkerMenuItem(menuButton));
            this.menuItems.add(new TransportMenuItem(menuButton));
            this.menuItems.add(new ToolMenuItem(menuButton));
            this.menuItems.add(new HelpMenuItem(menuButton));


            TGGTK.showAll(this.menuButton.getBoxHandle());
            for (TGMenuItem item: this.menuItems) {
                item.showItems();
                TGGTK.showAll(((GTKMenuSubMenuItem) item.getMenuItem()).getMenu().handle);
                for (UIMenuSubMenuItem subItem: item.getSubMenuItems()) {
                    if (subItem != null) {
                        TGGTK.showAll(((GTKMenuSubMenuItem) subItem).getMenu().handle);
                    }
                }
            }
            this.updateViewItemsVisibility(this.menuItems, false);
            this.loadProperties();
            this.loadIcons();
        }
    }

    private void redraw() {
        for (HeaderWidget widget : this.widgets) {
            float width = widget.previousLayout.get(widget.control, UITableLayout.PACKED_WIDTH, -1f);
            if (width > 0f) {
                GTK.gtk_widget_set_size_request(widget.handle, (int) width, -1);
            }
        }
    }

    private static long getParentId(long id, String searchName) {
        for (;;) {
            if (TGGTK.charPtrToString(OS.G_OBJECT_TYPE_NAME(id)).equals(searchName)) {
                break;
            }
            id = GTK.gtk_widget_get_parent(id);
            if (id == 0) {
                break;
            }
        }
        return id;
    }

    public void setEnabled(boolean enabled) throws IOException {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                if (headerBar == 0) {
                    headerBar = TGGTK.createHeaderBar();
                    GTK.g_object_ref(headerBar);

                    this.provider = GTK.gtk_css_provider_new();
                    byte[] css = resourceManager.getResourceAsStream("tuxguitar.css").readAllBytes();
                    GTK.gtk_css_provider_load_from_data(provider, css, css.length, new long[]{0});
                    GTK.gtk_style_context_add_provider_for_screen(GTK.gtk_widget_get_screen(headerBar), provider, GTK.GTK_STYLE_PROVIDER_PRIORITY_APPLICATION);

                    // ---- RIGHT ----
                    this.createMenu();
                    TGGTK.headerBarPackLeft(headerBar, menuButton.handle);

                    TGMainToolBarSection layout = (TGMainToolBarSection) this.toolBar.getSections().get(0);

                    long scrolled = TGGTK.createHidingScrolledWindow(GTK.GTK_ALIGN_START);
                    long container = GTK.gtk_box_new(GTK.GTK_ORIENTATION_HORIZONTAL, 6);
                    GTK.gtk_container_add(scrolled, container);

                    for (Long box : createButtonBoxes(layout, false)) {
                        GTK.gtk_container_add(container, box);
                    }
                    TGGTK.headerBarPackLeft(headerBar, scrolled);

                    // ---- CENTER ----
                    TGMainToolBarSection transport = (TGMainToolBarSection) this.toolBar.getSections().get(1);
                    for (Long box : createButtonBoxes(transport, false)) {
                        TGGTK.headerBarPackCenter(headerBar, box);
                    }

                    // ---- LEFT ----
                    TGMainToolBarSection view = (TGMainToolBarSection) this.toolBar.getSections().get(2);

                    scrolled = TGGTK.createHidingScrolledWindow(GTK.GTK_ALIGN_END);
                    container = GTK.gtk_box_new(GTK.GTK_ORIENTATION_HORIZONTAL, 6);
                    GTK.gtk_widget_set_halign(container, GTK.GTK_ALIGN_END);
                    GTK.gtk_container_add(scrolled, container);

                    for (Long box : createButtonBoxes(view, false)) {
                        GTK.gtk_container_add(container, box);
                    }
                    TGGTK.headerBarPackRight(headerBar, scrolled);

                } else {
                    for (HeaderWidget widget : this.widgets) {
                        moveWidget(widget, widget.previousParent);
                        widget.owner.removeChild(widget.control);
                    }
                }

                this.menuManager.setMainMenuForceHidden(true);
                this.toolBar.setForceHidden(true);

                this.updateViewItemsVisibility(this.menuManager.getLoadedMenuItems(), false);
                TGGTK.windowSetHeaderBar(windowId, headerBar);
                TGGTK.showAll(headerBar);
                this.appendListeners();
            } else {
                this.removeListeners();
                TGGTK.windowSetHeaderBar(windowId, 0);
                this.updateViewItemsVisibility(this.menuManager.getLoadedMenuItems(), true);

                this.toolBar.setForceHidden(false);
                this.menuManager.setMainMenuForceHidden(false);

                for (HeaderWidget widget : this.widgets) {
                    moveWidget(widget, widget.previousParent);
                    widget.owner.addChild(widget.control);
                }
            }
        }
    }

    private void updateViewItemsVisibility(List<TGMenuItem> menu, boolean visible) {
        for (TGMenuItem item : menu) {
            if (item instanceof ViewMenuItem) {
                ViewMenuItem subMenu = (ViewMenuItem) item;
                setVisible(subMenu.getShowMenuBar(), visible);
                setVisible(subMenu.getShowMainToolbar(), visible);
            }
        }
    }

    private void setVisible(UIMenuCheckableItem item, boolean visible) {
        if (item instanceof SWTMenuCheckableItem) {
            GTK.gtk_widget_set_visible(((SWTMenuCheckableItem) item).getControl().handle, visible);
        } else if (item instanceof GTKMenuCheckableItem) {
            GTK.gtk_widget_set_visible(((GTKMenuCheckableItem) item).handle, visible);
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
        long box = TGGTK.createButtonBox();
        GTK.gtk_widget_set_valign(box, GTK.GTK_ALIGN_CENTER);
        for (int i = 0; i < section.getItems().size(); i++) {
            HeaderWidget widget = new HeaderWidget();
            widget.control = (SWTControl) section.getItems().get(i);
            Control wrapped = (Control) widget.control.getControl();

            widget.handle = wrapped.handle;
            widget.owner = (SWTPanel) section.getControl();
            widget.previousLayout = section.getLayout();

            if (i > 0 && widget.previousLayout.get(widget.control, UITableLayout.MARGIN_LEFT, 0f) > 0f) {
                boxes.add(reverse ? 0 : boxes.size(), box);
                box = TGGTK.createButtonBox();
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

    private void appendListeners() {
        TuxGuitar.getInstance().getSkinManager().addLoader(this);
        TuxGuitar.getInstance().getLanguageManager().addLoader(this);
        TuxGuitar.getInstance().getFileFormatManager().addFileFormatAvailabilityListener(this);
        TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
        TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
    }

    private void removeListeners() {
        TuxGuitar.getInstance().getSkinManager().removeLoader(this);
        TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
        TuxGuitar.getInstance().getFileFormatManager().removeFileFormatAvailabilityListener(this);
        TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
        TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
    }

    @Override
    public void processEvent(final TGEvent event) {
        if (TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
            int type = event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE);
            if (type == TGUpdateEvent.SELECTION) {
                this.updateItemsProcess.process();
            }
        }
        else if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.loadIconsProcess.process();
        }
        else if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.loadPropertiesProcess.process();
        }
        else if (TGFileFormatAvailabilityEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.createMenuProcess.process();
        }
        else if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
            this.redrawProcess.process();
        }
    }

    private static class HeaderWidget {
        public SWTControl control;
        public SWTPanel owner;
        public long handle;
        public long previousParent;
        public UITableLayout previousLayout;
    }

}
