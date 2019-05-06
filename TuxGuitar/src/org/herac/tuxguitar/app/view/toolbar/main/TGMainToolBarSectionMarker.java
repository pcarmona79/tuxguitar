package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.ui.widget.UIButton;

public class TGMainToolBarSectionMarker extends TGMainToolBarSection {
	
	private UIButton add;
	private UIButton list;
	private UIButton first;
	private UIButton previous;
	private UIButton next;
	private UIButton last;
	
	public TGMainToolBarSectionMarker(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		//--ADD--
		this.add = this.createButton();
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = this.createButton();
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--FIRST--
		this.first = this.createButton();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = this.createButton();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = this.createButton();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = this.createButton();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.add.setToolTipText(this.getText("marker.add"));
		this.list.setToolTipText(this.getText("marker.list"));
		this.first.setToolTipText(this.getText("marker.first"));
		this.previous.setToolTipText(this.getText("marker.previous"));
		this.next.setToolTipText(this.getText("marker.next"));
		this.last.setToolTipText(this.getText("marker.last"));
	}
	
	public void loadIcons(){
		this.add.setImage(this.getIconManager().getMarkerAdd());
		this.list.setImage(this.getIconManager().getMarkerList());
		this.first.setImage(this.getIconManager().getMarkerFirst());
		this.previous.setImage(this.getIconManager().getMarkerPrevious());
		this.next.setImage(this.getIconManager().getMarkerNext());
		this.last.setImage(this.getIconManager().getMarkerLast());
	}
	
	public void updateItems(){
		//Nothing to do
	}
}
