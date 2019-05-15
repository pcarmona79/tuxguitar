package org.herac.tuxguitar.gtk.headerbar.menu;

import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.gtk.GTK;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.ui.menu.*;

public class GTKMenuSubMenuItem extends GTKMenuItem implements UIMenuSubMenuItem {

    private final SubMenu subMenu;

    public GTKMenuSubMenuItem(GTKMenuButton owner, GTKMenu parent, String prefix) {
        super(owner, parent);
        this.subMenu = new SubMenu(prefix);
        this.handle = TGGTK.createMenuSubMenuItem(owner.handle, prefix, this.subMenu.handle);
    }

    @Override
    public GTKMenu getMenu() {
        return this.subMenu;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.subMenu.setTitle(text);
    }

    @Override
    public void dispose() {
        this.subMenu.dispose();
        super.dispose();
    }

    private class SubMenu extends GTKMenu {
        private long backItem;
        private String title;

        public SubMenu(String prefix) {
            super(GTKMenuButton.createBox(), prefix);
            this.backItem = TGGTK.createMenuGoBackItem(getParent().getName());
            GTK.gtk_container_add(this.handle, this.backItem);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String text) {
            this.title = text;
            GTKMenu parent = getParent();
            while (parent instanceof SubMenu) {
                text = ((SubMenu) parent).getTitle() + " \u203a " + text;
                parent = parent.getParent();
            }
            GTK.g_object_set(this.backItem, TGGTK.ascii("text"), Converter.javaStringToCString(text), 0);
        }

        @Override
        protected long getBoxHandle() {
            return this.handle;
        }

        @Override
        protected GTKMenuButton getOwner() {
            return GTKMenuSubMenuItem.this.getOwner();
        }

        @Override
        protected GTKMenu getParent() {
            return GTKMenuSubMenuItem.this.getParent();
        }
    }
}
