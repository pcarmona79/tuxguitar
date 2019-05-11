package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainToolBar extends TGToolBarModel {

	private UIPanel container;

	private TGMainToolBar(TGContext context) {
		super(context);
	}
	
	public void createToolBar(UIContainer parent, boolean visible){
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.container = uiFactory.createPanel(parent, false);
		UITableLayout layout = new UITableLayout();
		this.container.setLayout(layout);
		layout.set(UITableLayout.MARGIN, 0f);

		this.createSections();

		UIPanel border = uiFactory.createPanel(this.container, false);
		UIColorModel borderColor = TGApplication.getInstance(getContext()).getAppearance().getColorModel(UIColorAppearance.WidgetBorder);
		border.setBgColor(uiFactory.createColor(borderColor));
		layout.set(border, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, this.getSections().size(), null, 1f, 0f);
		this.updateVisibility(visible);
	}
	
	public void createSection(TGMainToolBarSection section, int align) {
		section.createSection();
		this.addSection(section);

		UITableLayout layout = (UITableLayout) this.container.getLayout();
		layout.set(section.getControl(), 1, this.getSections().size(), align, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, 1f, 0f);
	}
	
	public void createSections() {
		this.clearSections();
		this.createSection(new TGMainToolBarSectionLayout(this), UITableLayout.ALIGN_LEFT);
		this.createSection(new TGMainToolBarSectionTransport(this), UITableLayout.ALIGN_CENTER);
		this.createSection(new TGMainToolBarSectionView(this), UITableLayout.ALIGN_RIGHT);
	}
	
	public UIPanel getControl() {
		return container;
	}

	public static TGMainToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainToolBar.class.getName(), new TGSingletonFactory<TGMainToolBar>() {
			public TGMainToolBar createInstance(TGContext context) {
				return new TGMainToolBar(context);
			}
		});
	}
}
