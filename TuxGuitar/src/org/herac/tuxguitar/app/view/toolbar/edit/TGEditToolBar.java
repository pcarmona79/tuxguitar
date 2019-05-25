package org.herac.tuxguitar.app.view.toolbar.edit;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGEditToolBar extends TGToolBarModel implements UIFocusGainedListener {
	
	private static final int SCROLL_INCREMENT = 10;
	
	private UIPanel control;
	private UIScrollBarPanel scroll;
	private UIPanel sectionContainer;
	private UIFactory factory;

	private TGEditToolBar(TGContext context) {
		super(context);
	}
	
	public void createToolBar(UIContainer parent, boolean visible) {
		this.factory = TGApplication.getInstance(this.getContext()).getFactory();

		this.control = factory.createPanel(parent, false);
		UITableLayout layout = new UITableLayout(0f);
		this.control.setLayout(layout);
		
		this.scroll = factory.createScrollBarPanel(this.control, true, false, false);
		this.scroll.setVisible(visible);
		this.scroll.setLayout(new UIScrollBarPanelLayout(false, true, false, false, false, false));
		this.scroll.addFocusGainedListener(this);
		this.scroll.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.scroll.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGEditToolBar.this.getControl().layout();
			}
		});
		layout.set(this.scroll, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		this.sectionContainer = factory.createPanel(this.scroll, false);
		this.sectionContainer.setLayout(new UITableLayout());
		this.sectionContainer.addFocusGainedListener(this);
		this.createSections();

		UISeparator separator = factory.createVerticalSeparator(this.control);
		layout.set(separator, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 2f, null, 0f);
	}

	public void createSection(TGEditToolBarSection section) {
		UIControl control = section.createSection(this.sectionContainer);
		
		UITableLayout uiLayout = (UITableLayout) this.sectionContainer.getLayout();
		uiLayout.set(control, (this.getSections().size() + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		
		this.addSection(section);
	}
	
	public void createSections() {
		this.clearSections();
		this.createSection(new TGEditToolBarSectionEdit(this));
		this.createSection(new TGEditToolBarSectionComposition(this));
		this.createSection(new TGEditToolBarSectionDuration(this));
		this.createSection(new TGEditToolBarSectionDynamic(this));
		this.createSection(new TGEditToolBarSectionEffect(this));
		this.createSection(new TGEditToolBarSectionBeat(this));
	}
	
	public void onFocusGained(UIFocusEvent event) {
		TGTabFolder.getInstance(this.getContext()).updateFocus();
	}
	
	public UIPanel getControl() {
		return control;
	}

	public UIFactory getFactory() {
		return this.factory;
	}

	public static TGEditToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGEditToolBar.class.getName(), new TGSingletonFactory<TGEditToolBar>() {
			public TGEditToolBar createInstance(TGContext context) {
				return new TGEditToolBar(context);
			}
		});
	}
}
