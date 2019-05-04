package org.herac.tuxguitar.gtk.titlebar;

import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.gtk.TGGTKIntegrationPlugin;
import org.herac.tuxguitar.ui.swt.toolbar.SWTToolBar;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GTKTitleBarPlugin implements TGPlugin {

    private GTKTitleBar titleBar;

    @Override
    public String getModuleId() {
        return TGGTKIntegrationPlugin.MODULE_ID;
    }

    @Override
    public void connect(TGContext context) throws TGPluginException {
        try {
            if (this.titleBar == null) {
                SWTWindow window = (SWTWindow) TGWindow.getInstance(context).getWindow();
                SWTToolBar toolBar = (SWTToolBar) TGMainToolBar.getInstance(context).getControl();
                this.titleBar = new GTKTitleBar(window, toolBar);
            }
            this.titleBar.setEnabled(true);
        } catch( Throwable throwable ){
            throw new TGPluginException( throwable );
        }
    }

    @Override
    public void disconnect(TGContext context) throws TGPluginException {
        try {
            if (this.titleBar != null) {
                this.titleBar.setEnabled(false);
            }
        } catch( Throwable throwable ){
            throw new TGPluginException( throwable );
        }
    }
}
