package org.herac.tuxguitar.ui.swt;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.ui.swt.chooser.SWTColorChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTDirectoryChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTFileChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTFontChooser;
import org.herac.tuxguitar.ui.swt.chooser.SWTPrinterChooser;
import org.herac.tuxguitar.ui.swt.menu.SWTMenuBar;
import org.herac.tuxguitar.ui.swt.menu.SWTPopupMenu;
import org.herac.tuxguitar.ui.swt.resource.SWTResourceFactory;
import org.herac.tuxguitar.ui.swt.toolbar.SWTToolBar;
import org.herac.tuxguitar.ui.swt.widget.*;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UIIndeterminateProgressBar;
import org.herac.tuxguitar.ui.widget.UIKnob;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UILinkLabel;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIPasswordField;
import org.herac.tuxguitar.ui.widget.UIProgressBar;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UISlider;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UISplashWindow;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITextArea;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class SWTFactory implements UIFactory {
	
	private Display display;
	private UIResourceFactory resourceFactory;
	
	public SWTFactory(Display display) {
		this.display = display;
		this.resourceFactory = new SWTResourceFactory(this.display);
	}
	
	public UIWindow createWindow() {
		return new SWTWindow(this.display);
	}

	public UIWindow createWindow(UIWindow parent, boolean modal, boolean resizable) {
		return new SWTWindow((SWTWindow) parent, modal, resizable);
	}
	
	public UISplashWindow createSplashWindow() {
		return new SWTSplashWindow(this.display);
	}

	public UIPanel createPanel(UIContainer parent, boolean bordered) {
		return new SWTPanel((SWTContainer<? extends Composite>) parent, bordered);
	}
	
	public UIScrollBarPanel createScrollBarPanel(UIContainer parent, boolean vScroll, boolean hScroll, boolean bordered) {
		return new SWTScrollBarPanel((SWTContainer<? extends Composite>) parent, vScroll, hScroll, bordered);
	}
	
	public UILegendPanel createLegendPanel(UIContainer parent) {
		return new SWTLegendPanel((SWTContainer<? extends Composite>) parent);
	}
	
	public UICanvas createCanvas(UIContainer parent, boolean bordered) {
		return new SWTCanvas((SWTContainer<? extends Composite>) parent, bordered);
	}
	
	public UILabel createLabel(UIContainer parent) {
		return new SWTLabel((SWTContainer<? extends Composite>) parent);
	}
	
	public UIWrapLabel createWrapLabel(UIContainer parent) {
		return new SWTWrapLabel((SWTContainer<? extends Composite>) parent);
	}
	
	public UILinkLabel createLinkLabel(UIContainer parent) {
		return new SWTLinkLabel((SWTContainer<? extends Composite>) parent);
	}
	
	public UIImageView createImageView(UIContainer parent) {
		return new SWTImageView((SWTContainer<? extends Composite>) parent);
	}

	public UISeparator createVerticalSeparator(UIContainer parent) {
		return new SWTSeparator((SWTContainer<? extends Composite>) parent, SWT.VERTICAL);
	}
	
	public UISeparator createHorizontalSeparator(UIContainer parent) {
		return new SWTSeparator((SWTContainer<? extends Composite>) parent, SWT.HORIZONTAL);
	}

	public UIButton createButton(UIContainer parent) {
		return new SWTButton((SWTContainer<? extends Composite>) parent);
	}
	
	public UIToggleButton createToggleButton(UIContainer parent, boolean flat) {
		return new SWTToggleButton((SWTContainer<? extends Composite>) parent, flat);
	}

	public UICheckBox createCheckBox(UIContainer parent) {
		return new SWTCheckBox((SWTContainer<? extends Composite>) parent);
	}
	
	public UIRadioButton createRadioButton(UIContainer parent) {
		return new SWTRadioButton((SWTContainer<? extends Composite>) parent);
	}
	
	public UITextField createTextField(UIContainer parent) {
		return new SWTTextField((SWTContainer<? extends Composite>) parent);
	}

	public UIPasswordField createPasswordField(UIContainer parent) {
		return new SWTPasswordField((SWTContainer<? extends Composite>) parent);
	}
	
	public UIReadOnlyTextField createReadOnlyTextField(UIContainer parent) {
		return new SWTReadOnlyTextField((SWTContainer<? extends Composite>) parent);
	}
	
	public UITextArea createTextArea(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new SWTTextArea((SWTContainer<? extends Composite>) parent, vScroll, hScroll);
	}
	
	public UIReadOnlyTextBox createReadOnlyTextBox(UIContainer parent, boolean vScroll, boolean hScroll) {
		return new SWTReadOnlyTextBox((SWTContainer<? extends Composite>) parent, vScroll, hScroll);
	}

	public UISpinner createSpinner(UIContainer parent) {
		return new SWTSpinner((SWTContainer<? extends Composite>) parent);
	}
	
	public UISlider createHorizontalSlider(UIContainer parent) {
		return new SWTSlider((SWTContainer<? extends Composite>) parent, SWT.HORIZONTAL);
	}
	
	public UISlider createVerticalSlider(UIContainer parent) {
		return new SWTSlider((SWTContainer<? extends Composite>) parent, SWT.VERTICAL);
	}

	public UIScale createHorizontalScale(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getHorizontalScaleAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTCustomScale((SWTContainer<? extends Composite>) parent, true);
		}
		return new SWTScale((SWTContainer<? extends Composite>) parent, SWT.HORIZONTAL);
	}
	
	public UIScale createVerticalScale(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getVerticalScaleAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTCustomScale((SWTContainer<? extends Composite>) parent, false);
		}
		return new SWTScale((SWTContainer<? extends Composite>) parent, SWT.VERTICAL);
	}

	public UIKnob createKnob(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getKnobAlternative();
		if( SWTCustomScale.class.getName().equals(alternative) ) {
			return new SWTScaleKnob((SWTContainer<? extends Composite>) parent);
		}
		return new SWTCustomKnob((SWTContainer<? extends Composite>) parent);
	}
	
	public UIProgressBar createProgressBar(UIContainer parent) {
		return new SWTProgressBar((SWTContainer<? extends Composite>) parent);
	}
	

	public UIIndeterminateProgressBar createIndeterminateProgressBar(UIContainer parent) {
		return new SWTIndeterminateProgressBar((SWTContainer<? extends Composite>) parent);
	}
	

	public <T> UITable<T> createTable(UIContainer parent, boolean headerVisible) {
		return new SWTTable<>((SWTContainer<? extends Composite>) parent, headerVisible);
	}
	

	public <T> UICheckTable<T> createCheckTable(UIContainer parent, boolean headerVisible) {
		return new SWTCheckTable<>((SWTContainer<? extends Composite>) parent, headerVisible);
	}

	public <T> UIDropDownSelect<T> createDropDownSelect(UIContainer parent) {
		String alternative = SWTEnvironment.getInstance().getDropDownSelectAlternative();
		if( SWTDropDownSelectLight.class.getName().equals(alternative) ) {
			return new SWTDropDownSelectLight<>((SWTContainer<? extends Composite>) parent);
		}
		if( SWTDropDownSelectCCombo.class.getName().equals(alternative) ) {
			return new SWTDropDownSelectCCombo<>((SWTContainer<? extends Composite>) parent);
		}
		return new SWTDropDownSelect<>((SWTContainer<? extends Composite>) parent);
	}

	public <T> UIListBoxSelect<T> createListBoxSelect(UIContainer parent) {
		return new SWTListBoxSelect<>((SWTContainer<? extends Composite>) parent);
	}
	
	public UIToolBar createHorizontalToolBar(UIContainer parent) {
		return new SWTToolBar((SWTContainer<? extends Composite>) parent, SWT.HORIZONTAL);
	}
	
	public UIToolBar createVerticalToolBar(UIContainer parent) {
		return new SWTToolBar((SWTContainer<? extends Composite>) parent, SWT.VERTICAL);
	}
	
	public UIMenuBar createMenuBar(UIWindow parent) {
		return new SWTMenuBar((SWTWindow) parent);
	}
	
	public UIPopupMenu createPopupMenu(UIWindow parent) {
		return new SWTPopupMenu((SWTWindow) parent);
	}
	
	public UITabFolder createTabFolder(UIContainer parent, boolean showClose) {
		if (showClose) {
			return new SWTCTabFolder((SWTContainer<? extends Composite>) parent, showClose);
		}
		return new SWTTabFolder((SWTContainer<? extends Composite>) parent);
	}
	
	public UIDivider createHorizontalDivider(UIContainer parent) {
		return new SWTDivider((SWTContainer<? extends Composite>) parent);
	}
	
	public UIDivider createVerticalDivider(UIContainer parent) {
		return new SWTDivider((SWTContainer<? extends Composite>) parent);
	}
	
	public UIFontChooser createFontChooser(UIWindow parent) {
		return new SWTFontChooser((SWTWindow) parent);
	}
	
	public UIColorChooser createColorChooser(UIWindow parent) {
		return new SWTColorChooser((SWTWindow) parent);
	}
	
	public UIFileChooser createOpenFileChooser(UIWindow parent) {
		return new SWTFileChooser((SWTWindow) parent, SWT.OPEN);
	}
	
	public UIFileChooser createSaveFileChooser(UIWindow parent) {
		return new SWTFileChooser((SWTWindow) parent, SWT.SAVE);
	}
	
	public UIDirectoryChooser createDirectoryChooser(UIWindow parent) {
		return new SWTDirectoryChooser((SWTWindow) parent);
	}
	
	public UIPrinterChooser createPrinterChooser(UIWindow parent) {
		return new SWTPrinterChooser((SWTWindow) parent);
	}

	public UIColor createColor(int red, int green, int blue) {
		return this.resourceFactory.createColor(red, green, blue);
	}

	public UIColor createColor(UIColorModel model) {
		return this.resourceFactory.createColor(model);
	}
	
	public UIFont createFont(String name, float height, boolean bold, boolean italic) {
		return this.resourceFactory.createFont(name, height, bold, italic);
	}

	public UIFont createFont(UIFontModel model) {
		return this.resourceFactory.createFont(model);
	}
	
	public UIImage createImage(float width, float height) {
		return this.resourceFactory.createImage(width, height);
	}

	public UIImage createImage(Map<Integer, InputStream> inputStreams) {
		return this.resourceFactory.createImage(inputStreams);
	}
}