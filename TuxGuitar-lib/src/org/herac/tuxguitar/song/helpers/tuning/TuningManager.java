package org.herac.tuxguitar.song.helpers.tuning;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.helpers.tuning.xml.TuningReader;
import org.herac.tuxguitar.song.helpers.tuning.xml.TuningWriter;
import org.herac.tuxguitar.song.models.TGTuning;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TuningManager {

    private TGContext context;

    private List<TGTuning> builtinTuningList;
    private TuningGroup builtinTunings;
    private TuningGroup customTunings;
    private int treeDepth;

    private TuningManager(TGContext context) {
        this.context = context;
        this.builtinTuningList = new ArrayList<TGTuning>();
        this.builtinTunings = new TuningGroup();
        this.customTunings = new TuningGroup();
        this.loadTunings(TGResourceManager.getInstance(this.context).getResourceAsStream("tunings/tunings.xml"), true);
    }

    public static TuningManager getInstance(TGContext context) {
        return TGSingletonUtil.getInstance(context, TuningManager.class.getName(), new TGSingletonFactory<TuningManager>() {
            public TuningManager createInstance(TGContext context) {
                return new TuningManager(context);
            }
        });
    }

    public List<TGTuning> getBuiltinTuningList() {
        return this.builtinTuningList;
    }

    public TuningGroup getBuiltinTunings() {
        return this.builtinTunings;
    }

    public TuningGroup getCustomTunings() {
        return this.customTunings;
    }

    public int getTreeDepth() {
        return this.treeDepth;
    }

    private void addTuningsToList(int depth, String prefix, TuningGroup group) {
        if (!group.getTunings().isEmpty()) {
            this.treeDepth = Math.max(depth, this.treeDepth);
        }
        for (TuningPreset tuning : group.getTunings()) {
            TGTuning prefixedTuning = new TGTuning();
            prefixedTuning.setName(prefix + tuning.getName());
            prefixedTuning.setValues(tuning.getValues());
            this.builtinTuningList.add(prefixedTuning);
        }
        for (TuningGroup subGroup : group.getGroups()) {
            addTuningsToList(depth + 1, prefix + subGroup.getName() + " / ", subGroup);
        }
    }

    private void loadTunings(InputStream stream, boolean builtin) {
        try {
            TuningGroup group = builtin ? this.builtinTunings : this.customTunings;
            new TuningReader().loadTunings(group, stream);
            if (builtin) {
                addTuningsToList(1, "", group);
            }
        } catch (Throwable e) {
            TGErrorManager.getInstance(this.context).handleError(e);
        }
    }

    public boolean loadCustomTunings(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                this.customTunings = new TuningGroup();
                loadTunings(new FileInputStream(file), false);
                return true;
            } catch (Throwable e) {
                TGErrorManager.getInstance(this.context).handleError(e);
            }
        }
        return false;
    }

    public void saveCustomTunings(String fileName) {
        TuningWriter.write(this.customTunings, fileName);
    }
}
