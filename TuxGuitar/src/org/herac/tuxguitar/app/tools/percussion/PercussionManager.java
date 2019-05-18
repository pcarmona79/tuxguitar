package org.herac.tuxguitar.app.tools.percussion;

import org.herac.tuxguitar.app.tools.percussion.xml.PercussionReader;
import org.herac.tuxguitar.app.tools.percussion.xml.PercussionWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.graphics.control.TGPercussionNote;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class PercussionManager {

    private final TGContext context;
    private PercussionEntry[] entries;

    private PercussionManager(TGContext context) {
        this.context = context;
        this.entries = new PercussionEntry[TGPercussionNote.NOTE_COUNT];
        this.setEntries(this.entries);
        this.init();
    }

    public static PercussionManager getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, PercussionManager.class.getName(), PercussionManager::new);
    }

    private void init() {
        if (!loadPercussion(getUserFileName())) {
            try {
                loadPercussion(TGResourceManager.getInstance(this.context).getResourceAsStream("percussion/percussion.xml"));
            } catch (Throwable e) {
                TGErrorManager.getInstance(this.context).handleError(e);
            }
        }
    }

    public PercussionEntry[] getEntries() {
        return entries;
    }

    public void setEntries(PercussionEntry[] entries) {
        for (int i = 0; i < this.entries.length; i++) {
            if (i < entries.length && entries[i] != null) {
                this.entries[i] = entries[i];
            } else {
                this.entries[i] = new PercussionEntry("", TGPercussionNote.DEFAULT_NOTE, false);
            }
        }
    }

    private String getUserFileName() {
        return TGFileUtils.PATH_USER_CONFIG + File.separator + "percussion.xml";
    }

    private boolean loadPercussion(String fileName) {
        try {
            loadPercussion(new FileInputStream(fileName));
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    private void loadPercussion(InputStream stream) {
        PercussionReader.loadPercussion(this.entries, stream);
    }

    public void savePercussion() {
        PercussionWriter.write(this.entries, getUserFileName());
    }
}
