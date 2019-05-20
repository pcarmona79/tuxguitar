package org.herac.tuxguitar.app.tools.scale;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.scale.xml.ScaleReader;
import org.herac.tuxguitar.app.tools.scale.xml.ScaleWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class ScaleManager {

    private static final String[] KEY_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_SCALE);

    private TGContext context;

    private List<ScaleInfo> scales;
    private int customScaleStartIndex;
    private Map<Integer, Integer> scaleKeysToIndex;

    private TGScale scale;

    private int selectionIndex;
    private int selectionKey;

    private ScaleManager(TGContext context) {
        this.context = context;
        this.scales = new ArrayList<>();
        this.scaleKeysToIndex = new HashMap<>();
        this.scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
        this.selectionKey = 0;
        this.selectionIndex = -1;
        this.init();
    }

    private void init() {
        this.loadScales(TGResourceManager.getInstance(this.context).getResourceAsStream("scales/scales.xml"));
        this.customScaleStartIndex = this.scales.size();
        this.loadScales(getUserFileName());
    }

    public void addListener(TGEventListener listener) {
        TGEventManager.getInstance(this.context).addListener(ScaleEvent.EVENT_TYPE, listener);
    }

    public void removeListener(TGEventListener listener) {
        TGEventManager.getInstance(this.context).removeListener(ScaleEvent.EVENT_TYPE, listener);
    }

    public void fireListeners() {
        TGEventManager.getInstance(this.context).fireEvent(new ScaleEvent());
    }

    public void selectScale(ScaleInfo info, int key) {
        int index = info == null ? -1 : getScaleIndex(info.getKeys());
        if (index == -1) {
            scale.clear();
        } else {
            scale.setNotes(info.getKeys());
            scale.setKey(key);
        }
        this.selectionIndex = index;
        this.selectionKey = key;
        this.fireListeners();
    }

    public void setScale(TGScale scale) {
        this.scale = scale;
        this.selectionIndex = getScaleIndex(scale);
        this.selectionKey = scale.getKey();
        this.fireListeners();
    }

    private int getScaleIndex(int keys) {
        Integer index = this.scaleKeysToIndex.get(keys);
        return index != null ? index : -1;
    }

    private int getScaleIndex(TGScale scale) {
        return getScaleIndex(scale.getNotes());
    }

    public ScaleInfo getScaleInfo(int keys) {
        int index = getScaleIndex(keys);
        if (index == -1) {
            return null;
        }
        return this.scales.get(index);
    }

    public ScaleInfo getScaleInfo(TGScale scale) {
        return this.getScaleInfo(scale.getNotes());
    }

    public TGScale getScale() {
        return this.scale;
    }

    public List<ScaleInfo> getScales() {
        return Collections.unmodifiableList(this.scales);
    }

    public String getKeyName(int index) {
        if (index >= 0 && index < KEY_NAMES.length) {
            return KEY_NAMES[index];
        }
        return null;
    }

    public String[] getKeyNames() {
        return KEY_NAMES;
    }

    public ScaleInfo getSelection() {
        if (this.selectionIndex == -1) {
            return null;
        }
        return this.scales.get(this.selectionIndex);
    }

    public int getSelectionKey() {
        return this.selectionKey;
    }

    private boolean loadScales(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                loadScales(new FileInputStream(file));
                return true;
            }
        } catch (Throwable e) {
            TGErrorManager.getInstance(this.context).handleError(e);
        }
        return false;
    }

    private void loadScales(InputStream stream) {
        try {
            ScaleReader.loadScales(this.scales, stream);
            for (int i = 0; i < this.scales.size(); i++) {
                this.addScale(this.scales.get(i), i);
            }
        } catch (Throwable e) {
            TGErrorManager.getInstance(this.context).handleError(e);
        }
    }

    private void addScale(ScaleInfo info, int index) {
        if (!this.scaleKeysToIndex.containsKey(info.getKeys())) {
            this.scaleKeysToIndex.put(info.getKeys(), index);
        }
    }

    public ScaleInfo addCustomScale(String name) {
        ScaleInfo info = new ScaleInfo(name, this.scale.getNotes());
        Integer oldIndex = this.scaleKeysToIndex.get(info.getKeys());
        if (oldIndex != null) {
            if (isCustomScale(this.scales.get(oldIndex))) {
                this.scales.set(oldIndex, info);
                return info;
            }
        } else {
            this.scales.add(info);
            this.addScale(info, this.scales.size() - 1);
            return info;
        }
        return null;
    }

    public boolean removeCustomScale(ScaleInfo info) {
        if (isCustomScale(info)) {
            int index = getScaleIndex(info.getKeys());
            for (int i = index + 1; i < this.scales.size(); i++) {
                this.scaleKeysToIndex.put(this.scales.get(i).getKeys(), i - 1);
            }
            this.scaleKeysToIndex.remove(info.getKeys());
            this.scales.remove(index);
            if (this.selectionIndex >= this.scales.size()) {
                this.selectionIndex = -1;
            }
            this.fireListeners();
            return true;
        }
        return false;
    }

    public boolean isCustomScale(ScaleInfo info) {
        if (info == null) {
            return false;
        }
        int index = getScaleIndex(info.getKeys());
        return index >= 0 && index < this.scales.size() && index >= this.customScaleStartIndex;
    }

    private String getUserFileName() {
        return TGFileUtils.PATH_USER_CONFIG + File.separator + "scales.xml";
    }

    public void saveCustomScales() {
        ScaleWriter.write(this.scales.subList(this.customScaleStartIndex, this.scales.size()), getUserFileName());
    }

    public static ScaleManager getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, ScaleManager.class.getName(), new TGSingletonFactory<ScaleManager>() {
            public ScaleManager createInstance(TGContext context) {
                return new ScaleManager(context);
            }
        });
    }
}
