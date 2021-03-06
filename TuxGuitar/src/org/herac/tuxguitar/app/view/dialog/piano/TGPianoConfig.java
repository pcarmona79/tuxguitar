package org.herac.tuxguitar.app.view.dialog.piano;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGColorButton;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGPianoConfig {
	
	private static final float MINIMUM_CONTROL_WIDTH = 180;
	
	private TGContext context;
	private UIColor colorNatural;
	private UIColor colorNotNatural;
	private UIColor colorNote;
	private UIColor colorScale;
	
	public TGPianoConfig(TGContext context){
		this.context = context;
	}
	
	public UIColor getColorNatural() {
		return this.colorNatural;
	}
	
	public UIColor getColorNotNatural() {
		return this.colorNotNatural;
	}
	
	public UIColor getColorNote() {
		return this.colorNote;
	}
	
	public UIColor getColorScale() {
		return this.colorScale;
	}
	
	public UIColor createColor(UIFactory factory, UIColorModel cm) {
		return TGApplication.getInstance(this.context).getFactory().createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public void load(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		
		this.colorNatural = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL));
		this.colorNotNatural = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL));
		this.colorNote = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_NOTE));
		this.colorScale = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.PIANO_COLOR_SCALE));
	}
	
	public void defaults(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		TGProperties defaults = TGConfigDefaults.createDefaults();
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL, defaults.getValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL));
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL, defaults.getValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL));
		config.setValue(TGConfigKeys.PIANO_COLOR_NOTE, defaults.getValue(TGConfigKeys.PIANO_COLOR_NOTE));
		config.setValue(TGConfigKeys.PIANO_COLOR_SCALE, defaults.getValue(TGConfigKeys.PIANO_COLOR_SCALE));
	}
	
	public void save(UIColorModel rgbNatural,UIColorModel rgbNotNatural,UIColorModel rgbNote,UIColorModel rgbScale){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NATURAL,rgbNatural);
		config.setValue(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL,rgbNotNatural);
		config.setValue(TGConfigKeys.PIANO_COLOR_NOTE,rgbNote);
		config.setValue(TGConfigKeys.PIANO_COLOR_SCALE,rgbScale);
	}
	
	public void dispose(){
		this.colorNatural.dispose();
		this.colorNotNatural.dispose();
		this.colorNote.dispose();
		this.colorScale.dispose();
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public void configure(UIWindow parent) {
		final UIFactory factory = getUIFactory();
		final UITableLayout windowLayout = new UITableLayout();
		final UIWindow window = factory.createWindow(parent, true, false);
		window.setLayout(windowLayout);
		window.setText(TuxGuitar.getProperty("piano.settings"));
		
		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = factory.createPanel(window, false);
		group.setLayout(groupLayout);
		windowLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		int groupRow = 0;
		
		// Color
		final UIColorModel rgbNatural = getColorChooser(window, group, TuxGuitar.getProperty("piano.natural-key-color"), this.colorNatural, ++groupRow);
		final UIColorModel rgbNotNatural = getColorChooser(window, group, TuxGuitar.getProperty("piano.not-natural-key-color"), this.colorNotNatural, ++groupRow);
		final UIColorModel rgbNote = getColorChooser(window, group, TuxGuitar.getProperty("piano.note-color"), this.colorNote, ++groupRow);
		final UIColorModel rgbScale = getColorChooser(window, group, TuxGuitar.getProperty("piano.scale-note-color"), this.colorScale, ++groupRow);
		
		// ------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(factory, window,
				TGDialogButtons.ok(() -> {
					window.dispose();
					save(rgbNatural, rgbNotNatural,rgbNote, rgbScale);
					applyChanges();
				}),
				TGDialogButtons.cancel(window::dispose),
				TGDialogButtons.defaults(() -> {
					window.dispose();
					defaults();
					applyChanges();
				}));
		windowLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	protected void applyChanges(){
		this.dispose();
		this.load();
		
		TGPianoEditor.getInstance(this.context).getComponent().reloadFromConfig();
	}
	
	private UIColorModel getColorChooser(final UIWindow window, UILayoutContainer parent, String title, UIColor rgb, int row){
		final UIFactory factory = getUIFactory();
		
		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = factory.createLabel(parent);
		label.setText(title);
		layout.set(label, row, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		TGColorButton button = new TGColorButton(factory, window, parent, TuxGuitar.getProperty("choose"));
		button.loadColor(new UIColorModel(rgb));
		layout.set(button.getControl(), row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		return button.getValue();
	}
}
