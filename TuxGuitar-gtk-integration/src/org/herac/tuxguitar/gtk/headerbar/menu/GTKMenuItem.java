package org.herac.tuxguitar.gtk.headerbar.menu;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.ImageList;
import org.eclipse.swt.internal.gtk.GTK;
import org.eclipse.swt.internal.gtk.OS;
import org.herac.tuxguitar.gtk.GTKComponent;
import org.herac.tuxguitar.gtk.TGGTK;
import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;

import java.nio.charset.StandardCharsets;

public class GTKMenuItem extends GTKComponent implements UIMenuItem {

    private GTKMenuButton owner;
    private GTKMenu parent;
    private ImageList imageList;
    private SWTImage image;
    private long imageHandle;

    protected GTKMenuItem(GTKMenuButton owner, GTKMenu parent) {
        this(0, owner, parent);
    }

    protected GTKMenuItem(long handle, GTKMenuButton owner, GTKMenu parent) {
        super(handle);
        this.owner = owner;
        this.parent = parent;
    }

    public GTKMenuButton getOwner() {
        return this.owner;
    }

    public GTKMenu getParent() { return this.parent; }

    @Override
    public String getText() {
        long[] ptr = new long[] { 0 };
        GTK.g_object_get(this.handle, TGGTK.ascii("label"), ptr, 0);
        return TGGTK.charPtrToString(ptr[0]);
    }

    @Override
    public void setText(String text) {
        GTK.g_object_set(this.handle, TGGTK.ascii("label"), Converter.javaStringToCString(text), 0);
        long list = GTK.gtk_container_get_children(this.handle);
        while (list != 0) {
            GTK.gtk_widget_set_halign(OS.g_list_data(list), GTK.GTK_ALIGN_START);
            list = OS.g_list_next(list);
        }
        OS.g_list_free(list);
    }

    @Override
    public UIKeyCombination getKeyCombination() {
        return null;
    }

    @Override
    public void setKeyCombination(UIKeyCombination keyCombination) {

    }

    @Override
    public UIImage getImage() {
        return this.image;
    }

    @Override
    public void setImage(UIImage uiImage) {

        if (imageList != null) imageList.dispose ();
        imageList = null;

        this.image = (SWTImage) uiImage;
        if (this.image != null) {
            Image swtImage = this.image.getControl();
            if (swtImage.isDisposed()) {
                throw new IllegalArgumentException();
            }
            imageList = new ImageList ();
            int imageIndex = imageList.add (swtImage);
            long pixbuf = imageList.getPixbuf (imageIndex);
            if (imageHandle == 0) {
                imageHandle = GTK.gtk_image_new();
            }
            GTK.gtk_image_set_from_gicon(imageHandle, pixbuf, GTK.GTK_ICON_SIZE_MENU);
            GTK.g_object_set(this.handle, TGGTK.ascii("always-show-image"), true, 0);
        } else if (imageHandle != 0) {
            GTK.gtk_image_set_from_gicon(imageHandle, 0, GTK.GTK_ICON_SIZE_MENU);
        }
    }

    @Override
    public boolean isEnabled() {
        return TGGTK.menuItemGetEnabled(this.handle);
    }

    @Override
    public void setEnabled(boolean enabled) {
        TGGTK.menuItemSetEnabled(this.handle, enabled);
    }

    @Override
    public void dispose() {
        this.getParent().dispose(this);
        super.dispose();
    }
}
