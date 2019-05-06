package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionZoom extends TGMainToolBarSection {
	
	private UIButton zoomOut;
	private UIButton zoomReset;
	private UIButton zoomIn;
	private UIToggleButton showEditToolBar;
	private UIToggleButton showTrackTable;

	public TGMainToolBarSectionZoom(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.showEditToolBar = this.createToggleButton();
		this.showEditToolBar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));

		this.showTrackTable = this.createToggleButton();
		this.showTrackTable.addSelectionListener(this.createActionProcessor(TGToggleTableViewerAction.NAME));

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
		this.showEditToolBar.setToolTipText(this.getText("view.show-edit-toolbar"));
		this.showTrackTable.setToolTipText(this.getText("view.show-table-viewer"));
		this.zoomIn.setToolTipText(this.getText("view.zoom-in"));
		this.zoomOut.setToolTipText(this.getText("view.zoom-out"));
	}
	
	public void loadIcons(){
		this.showEditToolBar.setImage(this.getIconManager().getToolbarEdit());
		this.showTrackTable.setImage(this.getIconManager().getTableViewer());
		this.zoomIn.setImage(this.getIconManager().getZoomIn());
		this.zoomOut.setImage(this.getIconManager().getZoomOut());
	}
	
	public void updateItems(){
		this.showEditToolBar.setSelected(TGEditToolBar.getInstance(this.getToolBar().getContext()).isVisible());
		this.showTrackTable.setSelected(TGTableViewer.getInstance(this.getToolBar().getContext()).isVisible());
		float scale = TablatureEditor.getInstance(this.getToolBar().getContext()).getTablature().getScale();
		this.zoomOut.setEnabled(scale > TGSetLayoutScaleDecrementAction.MINIMUM_VALUE);
		this.zoomReset.setText(Math.round(scale * 100) + "%");
		this.zoomIn.setEnabled(scale < TGSetLayoutScaleIncrementAction.MAXIMUM_VALUE);

	}
}
