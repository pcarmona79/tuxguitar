package org.herac.tuxguitar.app.view.dialog.matrix;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGColorButton;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.*;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGMatrixConfig {
	
	private static final float MINIMUM_CONTROL_WIDTH = 180;
	
	private TGContext context;
	private UIFont font;
	private UIColor[] colorLines;
	private UIColor colorForeground;
	private UIColor colorBorder;
	private UIColor colorPosition;
	private UIColor colorNote;
	private UIColor colorPlay;

	public TGMatrixConfig(TGContext context){
		this.context = context;
	}
	
	public UIFont getFont() {
		return this.font;
	}
	
	public UIColor getColorForeground() {
		return this.colorForeground;
	}
	
	public UIColor getColorBorder() {
		return this.colorBorder;
	}
	
	public UIColor getColorPosition() {
		return this.colorPosition;
	}
	
	public UIColor getColorNote() {
		return this.colorNote;
	}
	
	public UIColor getColorPlay() {
		return this.colorPlay;
	}

	public UIColor[] getColorLines() {
		return this.colorLines;
	}
	
	public UIColor getColorLine(int index) {
		return this.colorLines[index];
	}
	
	public UIFont createFont(UIFactory factory, UIFontModel fm) {
		return TGApplication.getInstance(this.context).getFactory().createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
	
	public UIColor createColor(UIFactory factory, UIColorModel cm) {
		return TGApplication.getInstance(this.context).getFactory().createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public void load(){
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		this.font = createFont(factory, config.getFontModelConfigValue(TGConfigKeys.MATRIX_FONT));
		this.colorForeground = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_FOREGROUND));
		this.colorBorder = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_BORDER));
		this.colorPosition = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_POSITION));
		this.colorNote = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_NOTE));
		this.colorPlay = createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE));
		this.colorLines = new UIColor[]{
			createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_1)),
			createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_2)),
			createColor(factory,config.getColorModelConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_3))
		};
	}
	
	public void defaults(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		TGProperties defaults = TGConfigDefaults.createDefaults();
		config.setValue(TGConfigKeys.MATRIX_FONT,defaults.getValue(TGConfigKeys.MATRIX_FONT));
		config.setValue(TGConfigKeys.MATRIX_COLOR_FOREGROUND,defaults.getValue(TGConfigKeys.MATRIX_COLOR_FOREGROUND));
		config.setValue(TGConfigKeys.MATRIX_COLOR_BORDER,defaults.getValue(TGConfigKeys.MATRIX_COLOR_BORDER));
		config.setValue(TGConfigKeys.MATRIX_COLOR_POSITION,defaults.getValue(TGConfigKeys.MATRIX_COLOR_POSITION));
		config.setValue(TGConfigKeys.MATRIX_COLOR_NOTE,defaults.getValue(TGConfigKeys.MATRIX_COLOR_NOTE));
		config.setValue(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,defaults.getValue(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE));
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_1,defaults.getValue(TGConfigKeys.MATRIX_COLOR_LINE_1));
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_2,defaults.getValue(TGConfigKeys.MATRIX_COLOR_LINE_2));
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_3,defaults.getValue(TGConfigKeys.MATRIX_COLOR_LINE_3));
	}
	
	public void save(
			UIFontModel fontData,
			UIColorModel rgbForeground,
			UIColorModel rgbBorder,
			UIColorModel rgbPosition,
			UIColorModel rgbNote,
			UIColorModel rgbPlay,
			UIColorModel rgbLines[]){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		config.setValue(TGConfigKeys.MATRIX_FONT,fontData);
		config.setValue(TGConfigKeys.MATRIX_COLOR_FOREGROUND,rgbForeground);
		config.setValue(TGConfigKeys.MATRIX_COLOR_BORDER,rgbBorder);
		config.setValue(TGConfigKeys.MATRIX_COLOR_POSITION,rgbPosition);
		config.setValue(TGConfigKeys.MATRIX_COLOR_NOTE,rgbNote);
		config.setValue(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,rgbPlay);
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_1,rgbLines[0]);
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_2,rgbLines[1]);
		config.setValue(TGConfigKeys.MATRIX_COLOR_LINE_3,rgbLines[2]);
	}
	
	public void dispose(){
		dispose(this.font);
		dispose(this.colorForeground);
		dispose(this.colorBorder);
		dispose(this.colorPosition);
		dispose(this.colorNote);
		dispose(this.colorPlay);
		dispose(this.colorLines);
	}
	
	protected void dispose(UIResource[] resources){
		if(resources != null){
			for(int i = 0; i < resources.length; i ++){
				dispose(resources[i]);
			}
		}
	}
	
	protected void dispose(UIResource resource){
		if(resource != null){
			resource.dispose();
		}
	}
	
	public void configure(UIWindow parent) {
		final UIFactory factory = getUIFactory();
		final UITableLayout windowLayout = new UITableLayout();
		final UIWindow window = factory.createWindow(parent, true, false);
		window.setLayout(windowLayout);
		window.setText(TuxGuitar.getProperty("matrix.settings"));
		
		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = factory.createPanel(window, false);
		group.setLayout(groupLayout);
		windowLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		int groupRow = 0;
		
		// fonts
		final UIFontModel fontData = getFontChooser(window, group, TuxGuitar.getProperty("matrix.font"), this.font, ++groupRow);
		
		// colors
		final UIColorModel rgbForeground = getColorChooser(window, group, TuxGuitar.getProperty("matrix.foreground-color"), this.colorForeground, ++groupRow);
		
		final UIColorModel rgbLines[] = new UIColorModel[]{
			getColorChooser(window, group, TuxGuitar.getProperty("matrix.line-color-1"), this.colorLines[0], ++groupRow),
			getColorChooser(window, group, TuxGuitar.getProperty("matrix.line-color-2"), this.colorLines[1], ++groupRow),
			getColorChooser(window, group, TuxGuitar.getProperty("matrix.line-color-over"), this.colorLines[2], ++groupRow),
		};
		
		final UIColorModel rgbBorder = getColorChooser(window, group, TuxGuitar.getProperty("matrix.border-color"), this.colorBorder, ++groupRow);
		final UIColorModel rgbPosition = getColorChooser(window, group, TuxGuitar.getProperty("matrix.position-color"), this.colorPosition, ++groupRow);
		final UIColorModel rgbNote = getColorChooser(window, group, TuxGuitar.getProperty("matrix.note-color"), this.colorNote, ++groupRow);
		final UIColorModel rgbPlay = getColorChooser(window, group, TuxGuitar.getProperty("matrix.play-note-color"), this.colorPlay, ++groupRow);

		// ------------------BUTTONS--------------------------

		TGDialogButtons buttons = new TGDialogButtons(factory, window,
				TGDialogButtons.ok(() -> {
					window.dispose();
					save(fontData, rgbForeground, rgbBorder, rgbPosition, rgbNote, rgbPlay, rgbLines);
					applyChanges();
				}),
				TGDialogButtons.cancel(window::dispose),
				TGDialogButtons.defaults(() -> {
					window.dispose();
					defaults();
					applyChanges();
				}));
		windowLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		TGDialogUtil.openDialog(window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	protected void applyChanges(){
		this.dispose();
		this.load();
		
		TGMatrixEditor.getInstance(this.context).reloadFromConfig();
	}
	
	private UIFontModel getFontChooser(final UIWindow window, UILayoutContainer parent, String title, UIFont font, int row) {
		final UIFactory factory = getUIFactory();
		final UIFontModel selection = new UIFontModel(font.getName(), font.getHeight(), font.isBold(), font.isItalic());
		
		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = factory.createLabel(parent);
		label.setText(title);
		layout.set(label, row, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		UIButton button = factory.createButton(parent);
		button.setText(TuxGuitar.getProperty("choose"));
		button.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIFontChooser uiFontChooser = factory.createFontChooser(window);
				uiFontChooser.setDefaultModel(selection);
				uiFontChooser.choose(new UIFontChooserHandler() {
					public void onSelectFont(UIFontModel model) {
						if( model != null ){
							selection.setName(model.getName());
							selection.setHeight(model.getHeight());
							selection.setBold(model.isBold());
							selection.setItalic(model.isItalic());
						}
					}
				});
			}
		});
		layout.set(button, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		return selection;
	}
	
	private UIColorModel getColorChooser(final UIWindow window, UILayoutContainer parent, String title, UIColor rgb, int row){
		final UIFactory factory = getUIFactory();
		
		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = factory.createLabel(parent);
		label.setText(title);
		layout.set(label, row, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		TGColorButton button = new TGColorButton(factory, window, parent, TuxGuitar.getProperty("choose"));
		button.loadColor(new UIColorModel(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
		layout.set(button.getControl(), row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_CONTROL_WIDTH, null, null);
		
		return button.getValue();
	}

}
