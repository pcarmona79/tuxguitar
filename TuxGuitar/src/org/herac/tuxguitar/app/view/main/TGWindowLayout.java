package org.herac.tuxguitar.app.view.main;

import org.herac.tuxguitar.app.view.docking.TGDockingManager;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public class TGWindowLayout extends UITableLayout {
	
	private UIControl top;
	private UIControl topContainer;
	private UIControl divider;
	private UIControl bottomContainer;
	private TGDockingManager dockingManager;
	
	public TGWindowLayout(UIControl top, UIControl topContainer, UIControl divider, UIControl bottomContainer, TGDockingManager dockingManager) {
		this.top = top;
		this.topContainer = topContainer;
		this.divider = divider;
		this.bottomContainer = bottomContainer;
		this.dockingManager = dockingManager;
		this.configure();
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		return this.computePackedSize(container, true);
	}
	
	public UISize computePackedSize(UILayoutContainer container, boolean resetTableHeight) {
		if( resetTableHeight ) {
			this.set(this.bottomContainer, MAXIMUM_PACKED_HEIGHT, null);
		}
		return super.computePackedSize(container);
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		UISize packedContentSize = container.getPackedContentSize();
		if( packedContentSize.getHeight() > bounds.getHeight() ) {
			UISize preferredSize = this.getPreferredControlSize(this.bottomContainer);
			this.set(this.bottomContainer, MAXIMUM_PACKED_HEIGHT, (preferredSize.getHeight() - (packedContentSize.getHeight() - bounds.getHeight())));
			this.computePackedSize(container, false);
		}
		
		super.setBounds(container, bounds);
	}
	
	public void configure() {
		this.set(UITableLayout.MARGIN, 0f);
		this.set(UITableLayout.IGNORE_INVISIBLE, true);
		int rowIdx = 0;
		this.set(this.top, ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		this.set(this.dockingManager.getTopDockingArea(), ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		this.set(this.topContainer, ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		this.set(this.topContainer, UITableLayout.PACKED_HEIGHT, 0f);
		this.set(this.divider, ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);
		this.set(this.divider, UITableLayout.PACKED_HEIGHT, 2f);
		this.set(this.divider, UITableLayout.MARGIN, 0f);
		this.set(this.bottomContainer, ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		this.set(this.bottomContainer, UITableLayout.MARGIN_TOP, 1f);
		this.set(this.dockingManager.getBottomDockingArea(), ++rowIdx, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
	}
}
