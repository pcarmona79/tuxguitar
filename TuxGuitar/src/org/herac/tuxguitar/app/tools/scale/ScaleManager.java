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
	
	public static final int NONE_SELECTION = -1;

	private TGContext context;

	private List<ScaleInfo> scales;
	private Set<ScaleInfo> defaultScales;
	private Set<ScaleInfo> customScales;
	private Map<Integer, Integer> scaleKeysToIndex;

	private TGScale scale;
	
	private int selectionIndex;
	private int selectionKey;

	private ScaleManager(TGContext context){
		this.context = context;
		this.scales = new ArrayList<>();
		this.defaultScales = new HashSet<>();
		this.customScales = new HashSet<>();
		this.scaleKeysToIndex = new HashMap<>();
		this.scale = TuxGuitar.getInstance().getSongManager().getFactory().newScale();
		this.selectionKey = 0;
		this.selectionIndex = NONE_SELECTION;
		this.init();
	}

	private void init() {
		try{
		    this.loadScales(TGResourceManager.getInstance(this.context).getResourceAsStream("scales/scales.xml"), false);
		} catch (Throwable e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		}
		this.loadScales(getUserFileName());
	}

	public void addListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(ScaleEvent.EVENT_TYPE, listener);
	}
	
	public void removeListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(ScaleEvent.EVENT_TYPE, listener);
	}
	
	public void fireListeners(){
		TGEventManager.getInstance(this.context).fireEvent(new ScaleEvent());
	}
	
	public void selectScale(int index, int key){
		if( index == NONE_SELECTION ){
			scale.clear();
		}
		else if(index >= 0 && index < this.scales.size()){
			ScaleInfo info = this.scales.get(index);
			scale.setNotes(info.getKeys());
			scale.setKey(key);
		}
		this.selectionIndex = index;
		this.selectionKey = key;
		this.fireListeners();
	}

	public void setScale(TGScale scale) {
		this.scale = scale;
		Integer index = getScaleIndex(scale);
		this.selectionIndex = index != null ? index : NONE_SELECTION;
		this.selectionKey = scale.getKey();
		this.fireListeners();
	}

	public Integer getScaleIndex(TGScale scale) {
		Integer index = this.scaleKeysToIndex.get(scale.getNotes());
		return index != null ? index : NONE_SELECTION;
	}
	
	public TGScale getScale() {
		return this.scale;
	}
	
	public int countScales() {
		return this.scales.size();
	}
	
	public String getScaleName(int index) {
		if(index >= 0 && index < this.scales.size()) {
			return this.scales.get(index).getName();
		}
		return null;
	}
	
	public Integer getScaleKeys(int index) {
		if(index >= 0 && index < this.scales.size()) {
			return this.scales.get(index).getKeys();
		}
		return null;
	}

	public String[] getScaleNames(){
		String[] names = new String[this.scales.size()];
		for(int i = 0;i < this.scales.size();i ++){
			ScaleInfo info = this.scales.get(i);
			names[i] = info.getName();
		}
		return names;
	}
	
	public String getKeyName(int index){
		if( index >=0 && index < KEY_NAMES.length){
			return KEY_NAMES[ index ];
		}
		return null;
	}

	public String[] getKeyNames(){
		return KEY_NAMES;
	}
	
	public int getSelectionIndex() {
		return this.selectionIndex;
	}
	
	public int getSelectionKey() {
		return this.selectionKey;
	}

	private boolean loadScales(String fileName) {
		try{
			loadScales(new FileInputStream(fileName), true);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
	
	private void loadScales(InputStream stream, boolean custom){
        ScaleReader.loadScales(this.scales, stream);
        for (int i = 0; i < this.scales.size(); i++) {
            addScale(this.scales.get(i), i, custom);
        }
	}

	private void addScale(ScaleInfo info, int index, boolean custom) {
		if (!this.scaleKeysToIndex.containsKey(info.getKeys())) {
			this.scaleKeysToIndex.put(info.getKeys(), index);
		}
		if (custom) {
			this.customScales.add(info);
		} else {
			this.defaultScales.add(info);
		}
	}

	public void addCustomScale(ScaleInfo info) {
		Integer oldIndex = this.scaleKeysToIndex.get(info.getKeys());
	    if (oldIndex != null) {
	    	ScaleInfo old = this.scales.get(oldIndex);
	    	if (this.customScales.contains(old)) {
				this.customScales.remove(old);
				this.scales.set(oldIndex, info);
			}
		} else {
			this.scales.add(info);
			this.addScale(info, this.scales.size() - 1, true);
		}
	}

	public void removeCustomScale(ScaleInfo info) {
		if (isCustomScale(info)) {
			int index = this.scaleKeysToIndex.get(info.getKeys());
			this.scales.remove(index);
			this.scaleKeysToIndex.remove(info.getKeys());
			this.customScales.remove(info);
			if (this.selectionIndex > index) {
				this.selectionIndex--;
			}
		}
	}

	public boolean isCustomScale(int index) {
		return index != NONE_SELECTION && this.customScales.contains(this.scales.get(index));
	}

	private String getUserFileName() {
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "scales.xml";
	}

	public void saveCustomScales() {
		ScaleWriter.write(this.customScales, getUserFileName());
	}
	
	public static ScaleManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, ScaleManager.class.getName(), new TGSingletonFactory<ScaleManager>() {
			public ScaleManager createInstance(TGContext context) {
				return new ScaleManager(context);
			}
		});
	}
}
