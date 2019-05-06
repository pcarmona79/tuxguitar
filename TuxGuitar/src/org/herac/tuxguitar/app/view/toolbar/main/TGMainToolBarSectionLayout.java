package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionLayout extends TGMainToolBarSection {
	
	private UIToggleButton pageLayout;
	private UIToggleButton linearLayout;
	private UIToggleButton multitrack;
	private UIToggleButton scoreEnabled;
	private UIToggleButton compact;
	
	public TGMainToolBarSectionLayout(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.pageLayout = this.createToggleButton();
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		this.linearLayout = this.createToggleButton();
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		this.multitrack = this.createToggleButton();
		this.multitrack.addSelectionListener(this.createActionProcessor(TGSetMultitrackViewAction.NAME));
		
		this.scoreEnabled = this.createToggleButton();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		this.compact = this.createToggleButton();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties() {
		TGLayout layout = this.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.pageLayout.setToolTipText(this.getText("view.layout.page"));
		this.linearLayout.setToolTipText(this.getText("view.layout.linear"));
		this.multitrack.setToolTipText(this.getText("view.layout.multitrack"));
		this.scoreEnabled.setToolTipText(this.getText("view.layout.score-enabled"));
		this.compact.setToolTipText(this.getText("view.layout.compact"));
	}
	
	public void loadIcons(){
		this.pageLayout.setImage(this.getIconManager().getLayoutPage());
		this.linearLayout.setImage(this.getIconManager().getLayoutLinear());
		this.multitrack.setImage(this.getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(this.getIconManager().getLayoutScore());
		this.compact.setImage(this.getIconManager().getLayoutCompact());
	}
	
	public void updateItems(){
		TGLayout layout = this.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.pageLayout.setSelected(layout instanceof TGLayoutVertical);
		this.linearLayout.setSelected(layout instanceof TGLayoutHorizontal);
		this.multitrack.setSelected( (style & TGLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setSelected( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.compact.setSelected( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || this.getSong().countTracks() == 1);
	}
}
