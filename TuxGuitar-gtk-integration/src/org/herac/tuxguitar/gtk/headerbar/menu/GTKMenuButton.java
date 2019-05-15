package org.herac.tuxguitar.gtk.headerbar.menu;

import org.herac.tuxguitar.gtk.TGGTK;

public class GTKMenuButton extends GTKMenu {

    private long boxHandle;

    public GTKMenuButton() {
        super(0);
        this.boxHandle = createBox();
        this.handle = TGGTK.createMenuButton(this.boxHandle);
    }

    @Override
    public long getBoxHandle() {
        return this.boxHandle;
    }

    @Override
    protected GTKMenuButton getOwner() {
        return this;
    }

    @Override
    protected GTKMenu getParent() {
        return null;
    }
}
