package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGChangeShowAllTracksAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.view.*;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

import java.util.ArrayList;
import java.util.List;

public class ViewMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem layoutMenuItem;
	private UIMenuCheckableItem showMenuBar;
	private UIMenuCheckableItem showMainToolbar;
	private UIMenuCheckableItem showEditToolbar;
	private UIMenuCheckableItem showTableViewer;
	private UIMenuCheckableItem showInstruments;
	private UIMenuCheckableItem showFretBoard;
	private UIMenuCheckableItem showPiano;
	private UIMenuCheckableItem showMatrix;
	private UIMenuCheckableItem dockToTop;
	private UIMenuCheckableItem pageLayout;
	private UIMenuCheckableItem linearLayout;
	private UIMenuCheckableItem showAll;
	private UIMenuCheckableItem scoreEnabled;
	private UIMenuCheckableItem tablatureEnabled;
	private UIMenuCheckableItem compact;
	private UIMenuActionItem zoomIn;
	private UIMenuActionItem zoomOut;
	private UIMenuActionItem zoomReset;
	
	private UIMenuSubMenuItem chordMenuItem;
	private UIMenuCheckableItem chordName;
	private UIMenuCheckableItem chordDiagram;
	
	public ViewMenuItem(UIMenu parent) {
		this.layoutMenuItem = parent.createSubMenuItem();
	}

	public UIMenuSubMenuItem getMenuItem() {
		return this.layoutMenuItem;
	}

	@Override
	public List<UIMenuSubMenuItem> getSubMenuItems() {
		List<UIMenuSubMenuItem> items = new ArrayList<>();
		items.add(chordMenuItem);
		return items;
	}

	public void showItems(){
		if (TGApplication.getInstance(findContext()).getApplication().allowsMenubarHiding()) {
			//--MENU--
			this.showMenuBar = this.layoutMenuItem.getMenu().createCheckItem();
			this.showMenuBar.addSelectionListener(this.createActionProcessor(TGToggleMenuBarAction.NAME));
		}

		//--TOOLBARS--
		this.showMainToolbar = this.layoutMenuItem.getMenu().createCheckItem();
		this.showMainToolbar.addSelectionListener(this.createActionProcessor(TGToggleMainToolbarAction.NAME));
		
		//--EDIT TOOLBAR--
		this.showEditToolbar = this.layoutMenuItem.getMenu().createCheckItem();
		this.showEditToolbar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));

		//--EDIT TOOLBAR--
		this.showTableViewer = this.layoutMenuItem.getMenu().createCheckItem();
		this.showTableViewer.addSelectionListener(this.createActionProcessor(TGToggleTableViewerAction.NAME));

		//--INSTRUMENTS--
		this.showInstruments = this.layoutMenuItem.getMenu().createCheckItem();
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--MATRIX--
		this.showMatrix = this.layoutMenuItem.getMenu().createCheckItem();
		this.showMatrix.addSelectionListener(this.createActionProcessor(TGToggleMatrixEditorAction.NAME));

		this.layoutMenuItem.getMenu().createSeparator();

		//--FRETBOARD--
		this.showFretBoard = this.layoutMenuItem.getMenu().createCheckItem();
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--PIANO--
		this.showPiano = this.layoutMenuItem.getMenu().createCheckItem();
		this.showPiano.addSelectionListener(this.createActionProcessor(TGTogglePianoEditorAction.NAME));

		//--DOCKING--
		this.dockToTop = this.layoutMenuItem.getMenu().createCheckItem();
		this.dockToTop.addSelectionListener(this.createActionProcessor(TGToggleDockingToTopAction.NAME));
		
		this.layoutMenuItem.getMenu().createSeparator();
		
		//--PAGE LAYOUT--
		this.pageLayout = this.layoutMenuItem.getMenu().createRadioItem();
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		//--LINEAR LAYOUT--
		this.linearLayout = this.layoutMenuItem.getMenu().createRadioItem();
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		//--MULTITRACK--
		this.showAll = this.layoutMenuItem.getMenu().createCheckItem();
		this.showAll.addSelectionListener(this.createActionProcessor(TGChangeShowAllTracksAction.NAME));
		
		//--SCORE
		this.scoreEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		//--SCORE
		this.tablatureEnabled = this.layoutMenuItem.getMenu().createCheckItem();
		this.tablatureEnabled.addSelectionListener(this.createActionProcessor(TGSetTablatureEnabledAction.NAME));
		
		//--COMPACT
		this.compact = this.layoutMenuItem.getMenu().createCheckItem();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.layoutMenuItem.getMenu().createSeparator();
		
		//--CHORD STYLE
		this.chordMenuItem = this.layoutMenuItem.getMenu().createSubMenuItem();
		
		this.chordName = this.chordMenuItem.getMenu().createCheckItem();
		this.chordName.addSelectionListener(this.createActionProcessor(TGSetChordNameEnabledAction.NAME));
		
		this.chordDiagram = this.chordMenuItem.getMenu().createCheckItem();
		this.chordDiagram.addSelectionListener(this.createActionProcessor(TGSetChordDiagramEnabledAction.NAME));
		
		this.layoutMenuItem.getMenu().createSeparator();
		
		//-- ZOOM
		this.zoomIn = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomIn.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleIncrementAction.NAME));
		
		this.zoomOut = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomOut.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleDecrementAction.NAME));
		
		this.zoomReset = this.layoutMenuItem.getMenu().createActionItem();
		this.zoomReset.addSelectionListener(this.createActionProcessor(TGSetLayoutScaleResetAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update() {
		Tablature tablature = TablatureEditor.getInstance(this.findContext()).getTablature();
		TGSong song = tablature.getSong();
		int style = tablature.getViewLayout().getStyle();
		int visibleTrackCount = tablature.getSongManager().countVisibleTracks(song);
		if (this.showMenuBar != null) {
			this.showMenuBar.setChecked(TuxGuitar.getInstance().getItemManager().isMainMenuVisible());
		}
		this.showMainToolbar.setChecked(TGMainToolBar.getInstance(this.findContext()).isVisible());
		this.showEditToolbar.setChecked(TGEditToolBar.getInstance(this.findContext()).isVisible());
		this.showTableViewer.setChecked(TGTableViewer.getInstance(this.findContext()).isVisible());
		this.showInstruments.setChecked(!TuxGuitar.getInstance().getChannelManager().isDisposed());
		this.showFretBoard.setChecked(TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		this.showPiano.setChecked(TuxGuitar.getInstance().getPianoEditor().isVisible());
		this.showMatrix.setChecked(!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		this.pageLayout.setChecked(tablature.getViewLayout() instanceof TGLayoutVertical);
		this.linearLayout.setChecked(tablature.getViewLayout() instanceof TGLayoutHorizontal);
		this.dockToTop.setChecked(TuxGuitar.getInstance().getDockingManager().isDockedToTop());
		this.showAll.setEnabled(song.countTracks() > 1);
		this.showAll.setChecked(visibleTrackCount == song.countTracks());
		this.scoreEnabled.setChecked( (style & TGLayout.DISPLAY_SCORE) != 0 );
		this.tablatureEnabled.setChecked( (style & TGLayout.DISPLAY_TABLATURE) != 0 );
		this.compact.setChecked( (style & TGLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled(visibleTrackCount == 1);
		this.chordName.setChecked( (style & TGLayout.DISPLAY_CHORD_NAME) != 0 );
		this.chordDiagram.setChecked( (style & TGLayout.DISPLAY_CHORD_DIAGRAM) != 0 );
		this.zoomReset.setEnabled(Tablature.DEFAULT_SCALE != tablature.getScale());
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.layoutMenuItem, "view", null);
		if (this.showMenuBar != null) {
			setMenuItemTextAndAccelerator(this.showMenuBar, "view.show-menu-bar", TGToggleMenuBarAction.NAME);
		}
		setMenuItemTextAndAccelerator(this.showMainToolbar, "view.show-main-toolbar", TGToggleMainToolbarAction.NAME);
		setMenuItemTextAndAccelerator(this.showEditToolbar, "view.show-edit-toolbar", TGToggleEditToolbarAction.NAME);
		setMenuItemTextAndAccelerator(this.showTableViewer, "view.show-table-viewer", TGToggleTableViewerAction.NAME);
		setMenuItemTextAndAccelerator(this.showInstruments, "view.show-instruments", TGToggleChannelsDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.showFretBoard, "view.show-fretboard", TGToggleFretBoardEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.showPiano, "view.show-piano", TGTogglePianoEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.showMatrix, "view.show-matrix", TGToggleMatrixEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.dockToTop, "view.dock-to-top", TGToggleDockingToTopAction.NAME);
		
		setMenuItemTextAndAccelerator(this.pageLayout, "view.layout.page", TGSetPageLayoutAction.NAME);
		setMenuItemTextAndAccelerator(this.linearLayout, "view.layout.linear", TGSetLinearLayoutAction.NAME);
		setMenuItemTextAndAccelerator(this.showAll, "view.layout.multitrack", TGChangeShowAllTracksAction.NAME);
		setMenuItemTextAndAccelerator(this.scoreEnabled, "view.layout.score-enabled", TGSetScoreEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.tablatureEnabled, "view.layout.tablature-enabled", TGSetTablatureEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.compact, "view.layout.compact", TGSetCompactViewAction.NAME);
		setMenuItemTextAndAccelerator(this.chordMenuItem, "view.layout.chord-style", null);
		setMenuItemTextAndAccelerator(this.chordName, "view.layout.chord-name", TGSetChordNameEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.chordDiagram, "view.layout.chord-diagram", TGSetChordDiagramEnabledAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomIn, "view.zoom.in", TGSetLayoutScaleIncrementAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomOut, "view.zoom.out", TGSetLayoutScaleDecrementAction.NAME);
		setMenuItemTextAndAccelerator(this.zoomReset, "view.zoom.reset", TGSetLayoutScaleResetAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}

	public UIMenuCheckableItem getShowMenuBar() {
		return showMenuBar;
	}

	public UIMenuCheckableItem getShowMainToolbar() {
		return showMainToolbar;
	}

}
