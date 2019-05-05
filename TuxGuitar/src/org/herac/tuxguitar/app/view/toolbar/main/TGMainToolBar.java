package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainToolBar extends TGToolBarModel {

	private UIPanel container;
	private UIToolBar toolBar;
	
	private TGMainToolBar(TGContext context) {
		super(context);
	}
	
	public void createToolBar(UIContainer parent, boolean visible){
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.container = uiFactory.createPanel(parent, false);
		UITableLayout layout = new UITableLayout();
		this.container.setLayout(layout);
		layout.set(UITableLayout.MARGIN, 0f);

		this.toolBar = uiFactory.createHorizontalToolBar(this.container);
		this.toolBar.setVisible(visible);
		this.createSections();

		layout.set(this.toolBar, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 4f);
		UIPanel border = uiFactory.createPanel(this.container, false);
		UIColorModel borderColor = TGApplication.getInstance(getContext()).getAppearance().getColorModel(UIColorAppearance.WidgetBorder);
		border.setBgColor(uiFactory.createColor(borderColor));
		layout.set(border, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, 1f, 0f);
	}
	
	public void createSection(TGMainToolBarSection section) {
		section.createSection();
		
		this.addSection(section);
	}
	
	public void createSections() {
		this.clearSections();
		this.createSection(new TGMainToolBarSectionFile(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionEdit(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionComposition(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionTrack(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionLayout(this));
		this.createSection(new TGMainToolBarSectionView(this));
		this.createSection(new TGMainToolBarSectionMarker(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionZoom(this));
		this.createSection(new TGMainToolBarSectionDivider(this));
		this.createSection(new TGMainToolBarSectionTransport(this));
	}
	
	public UIPanel getControl() {
		return container;
	}

	public UIToolBar getToolBar() {
		return toolBar;
	}

	public static TGMainToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainToolBar.class.getName(), new TGSingletonFactory<TGMainToolBar>() {
			public TGMainToolBar createInstance(TGContext context) {
				return new TGMainToolBar(context);
			}
		});
	}
}
