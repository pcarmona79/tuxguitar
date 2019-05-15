package org.herac.tuxguitar.gtk;

import org.eclipse.swt.internal.gtk.GTK;
import org.herac.tuxguitar.ui.UIComponent;

import java.util.HashMap;
import java.util.Map;

public abstract class GTKComponent implements UIComponent {

    public long handle;
    private Map<String, Object> userData;

    protected GTKComponent() {
        this(0);
    }
    public GTKComponent(long handle) {
        this.handle = handle;
        this.userData = new HashMap<>();
    }
    @Override
    public <T> T getData(String key) {
        return (T) this.userData.get(key);
    }

    @Override
    public <T> void setData(String key, T data) {
        this.userData.put(key, data);
    }

    @Override
    public void dispose() {
        if (this.handle != 0) {
            GTK.gtk_widget_destroy(this.handle);
            this.handle = 0;
        }
    }

    @Override
    public boolean isDisposed() {
        return this.handle == 0;
    }
}
