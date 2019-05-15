package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.layout.*;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionLayout extends TGMainToolBarSection {

	private UIToggleButton pageLayout;
	private UIToggleButton linearLayout;
	private UIToggleButton showAll;
	private UIToggleButton scoreEnabled;
	private UIToggleButton tablatureEnabled;
	private UIToggleButton compact;

	private UIButton zoomOut;
	private UIButton zoomReset;
	private UIButton zoomIn;

	public TGMainToolBarSectionLayout(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.pageLayout = this.createToggleButton();
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));

		this.linearLayout = this.createToggleButton();
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));

		this.showAll = this.createToggleButton();
		this.showAll.addSelectionListener(this.createActionProcessor(TGChangeShowAllTracksAction.NAME));

		this.scoreEnabled = this.createToggleButton();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));

		this.tablatureEnabled = this.createToggleButton();
		this.tablatureEnabled.addSelectionListener(this.createActionProcessor(TGSetTablatureEnabledAction.NAME));

		this.compact = this.createToggleButton();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));

		this.zoomOut = this.createButton();
		this.zoomOut.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleDecrementAction.NAME));
		this.getLayout().set(this.zoomOut, UITableLayout.MARGIN_LEFT, 8f);

		this.zoomReset = this.createButton();
		this.zoomReset.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleResetAction.NAME));
		this.getLayout().set(this.zoomReset, UITableLayout.MINIMUM_PACKED_WIDTH, 64f);

		this.zoomIn = this.createButton();
		this.zoomIn.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleIncrementAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void loadProperties(){
		this.pageLayout.setToolTipText(this.getText("view.layout.page"));
		this.linearLayout.setToolTipText(this.getText("view.layout.linear"));
		this.showAll.setToolTipText(this.getText("view.layout.multitrack"));
		this.scoreEnabled.setToolTipText(this.getText("view.layout.score-enabled"));
		this.tablatureEnabled.setToolTipText(this.getText("view.layout.tablature-enabled"));
		this.compact.setToolTipText(this.getText("view.layout.compact"));
		this.zoomIn.setToolTipText(this.getText("view.zoom-in"));
		this.zoomOut.setToolTipText(this.getText("view.zoom-out"));
	}
	
	public void loadIcons(){
		this.pageLayout.setImage(this.getIconManager().getLayoutPage());
		this.linearLayout.setImage(this.getIconManager().getLayoutLinear());
		this.showAll.setImage(this.getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(this.getIconManager().getLayoutScore());
		this.tablatureEnabled.setImage(this.getIconManager().getLayoutTablature());
		this.compact.setImage(this.getIconManager().getLayoutCompact());
		this.zoomIn.setImage(this.getIconManager().getZoomIn());
		this.zoomOut.setImage(this.getIconManager().getZoomOut());
	}
	
	public void updateItems(){
		TGLayout layout = this.getTablature().getViewLayout();
		int style = layout.getStyle();
		int visibleTrackCount = this.getTablature().getSongManager().countVisibleTracks(this.getSong());
		this.pageLayout.setSelected(layout instanceof TGLayoutVertical);
		this.linearLayout.setSelected(layout instanceof TGLayoutHorizontal);
		this.showAll.setEnabled(this.getSong().countTracks() > 1);
		this.showAll.setSelected(visibleTrackCount == this.getSong().countTracks());
		this.scoreEnabled.setSelected( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setSelected( (style & TGLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setSelected( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled(visibleTrackCount == 1);

		float scale = this.getTablature().getScale();

		this.zoomOut.setEnabled(scale > TGSetLayoutScaleDecrementAction.MINIMUM_VALUE);
		this.zoomReset.setText(Math.round(scale * 100) + "%");
		this.zoomIn.setEnabled(scale < TGSetLayoutScaleIncrementAction.MAXIMUM_VALUE);
	}
}
