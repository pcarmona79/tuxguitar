package org.herac.tuxguitar.gtk.headerbar;

import org.herac.tuxguitar.gtk.TGGTKIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GTKHeaderBarPlugin implements TGPlugin {

    private GTKHeaderBar titleBar;

    @Override
    public String getModuleId() {
        return TGGTKIntegrationPlugin.MODULE_ID;
    }

    @Override
    public void connect(TGContext context) throws TGPluginException {
        try {
            if (this.titleBar == null) {
                this.titleBar = new GTKHeaderBar(context);
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
