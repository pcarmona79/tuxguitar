package org.herac.tuxguitar.gtk.headerbar.menu;

import org.eclipse.swt.internal.gtk.GTK;
import org.herac.tuxguitar.gtk.GTKComponent;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.ui.menu.*;

import java.util.ArrayList;
import java.util.List;

public abstract class GTKMenu extends GTKComponent implements UIMenu {

    private static final int BOX_MARGIN = 10;
    private List<UIMenuItem> items;
    private String name;

    public static long createBox() {
        long handle = GTK.gtk_box_new(GTK.GTK_ORIENTATION_VERTICAL, 0);
        GTK.gtk_widget_set_margin_top(handle, BOX_MARGIN);
        GTK.gtk_widget_set_margin_bottom(handle, BOX_MARGIN);
        GTK.gtk_widget_set_margin_start(handle, BOX_MARGIN);
        GTK.gtk_widget_set_margin_end(handle, BOX_MARGIN);
        return handle;
    }

    protected GTKMenu(long handle) {
        this(handle, null);
    }

    protected GTKMenu(long handle, String name) {
        super(handle);
        this.items = new ArrayList<>();
        this.name = name;
    }

    public Integer getItemCount() {
        return this.items.size();
    }

    public UIMenuItem getItem(int index) {
        return this.items.get(index);
    }

    public List<UIMenuItem> getItems() {
        return this.items;
    }

    protected abstract long getBoxHandle();

    protected abstract GTKMenuButton getOwner();

    protected abstract GTKMenu getParent();

    private <T extends GTKMenuItem> T append(T item) {
        this.items.add(item);
        int[] role = new int[]{0};
        if (item instanceof GTKMenuCheckableItem) {
            GTK.g_object_get(item.handle, TGGTK.ascii("role"), role, 0);
        }
        GTK.gtk_container_add(getBoxHandle(), item.handle);
        if (item instanceof GTKMenuCheckableItem) {
            GTK.g_object_set(item.handle, TGGTK.ascii("role"), role[0], 0);
        }
        return item;
    }

    private String generatePrefix() {
        return (name == null ? "" : name) + "sub" + this.items.size();
    }

    public String getName() {
        return this.name == null ? "main" : this.name;
    }

    @Override
    public GTKMenuActionItem createActionItem() {
        return this.append(new GTKMenuActionItem(getOwner(), this, generatePrefix()));
    }

    @Override
    public UIMenuCheckableItem createCheckItem() {
        return this.append(new GTKMenuCheckableItem(getOwner(), this, generatePrefix(), false));
    }

    @Override
    public UIMenuCheckableItem createRadioItem() {
        return this.append(new GTKMenuCheckableItem(getOwner(), this, generatePrefix(), true));
    }

    @Override
    public GTKMenuSubMenuItem createSubMenuItem() {
        return this.append(new GTKMenuSubMenuItem(getOwner(), this, generatePrefix()));
    }

    @Override
    public GTKMenuItem createSeparator() {
        return this.append(new GTKMenuItem(GTK.gtk_separator_new(GTK.GTK_ORIENTATION_HORIZONTAL), getOwner(), this) {});
    }

    public void dispose(GTKMenuItem item) {
        this.items.remove(item);
    }

    @Override
    public void dispose() {
        while (!this.items.isEmpty()) {
            this.items.get(0).dispose();
        }
        super.dispose();
    }
}
