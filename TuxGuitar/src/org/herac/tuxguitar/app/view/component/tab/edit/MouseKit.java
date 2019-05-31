package org.herac.tuxguitar.app.view.component.tab.edit;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import org.herac.tuxguitar.app.action.impl.selector.TGStartDragSelectionAction;
import org.herac.tuxguitar.app.action.impl.selector.TGUpdateDragSelectionAction;
import org.herac.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.event.*;
import org.herac.tuxguitar.ui.resource.UIKeyLabel;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.util.TGContext;

public class MouseKit implements UIMouseDownListener, UIMouseUpListener, UIMouseDragListener, UIMouseMoveListener, UIMouseExitListener, UIMenuShowListener, UIMenuHideListener, UIZoomListener {
	
	private EditorKit kit;
	private boolean menuOpen;

	private UIPosition position;
	private UIPosition startPosition;

	private UIPosition scrollPosition;
	private UIPosition scrollStartPosition;

	public MouseKit(EditorKit kit){
		this.kit = kit;
		this.position = new UIPosition();
		this.scrollPosition = new UIPosition();
		this.menuOpen = false;
	}
	
	public boolean isBusy() {
		TGContext context = this.kit.getTablature().getContext();
		return (TGEditorManager.getInstance(context).isLocked() || MidiPlayer.getInstance(context).isRunning());
	}
	
	public void executeAction(String actionId, UIPosition position, UIEvent event, boolean byPassProcessing) {
		if (event != null) {
			// correct for screen DPI
			position.mul(((UIControl) event.getComponent()).getDeviceZoom() / 100f);
		}

		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.kit.getTablature().getContext(), actionId);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_X, position.getX());
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_Y, position.getY());
		tgActionProcessor.setAttribute(TGActionProcessingListener.ATTRIBUTE_BY_PASS, byPassProcessing);
		tgActionProcessor.process();
	}

	public void onMouseDown(UIMouseEvent event) {
		if (event.getButton() == 1) {
			this.position.set(event.getPosition());
			this.startPosition = this.position.clone();
			if ((event.getState() & UIKeyLabel.SHIFT) != 0) {
				this.executeAction(TGUpdateDragSelectionAction.NAME, this.position.clone(), event, false);
			} else {
				this.executeAction(TGStartDragSelectionAction.NAME, this.position.clone(), event, false);
			}
		} else if (event.getButton() == 2) {
			this.scrollPosition.set(event.getPosition());
			this.scrollStartPosition = this.scrollPosition.clone();
		}
	}

	public void onMouseUp(UIMouseEvent event) {
		if (event.getButton() == 1) {
			this.startPosition = null;
			this.executeAction(TGMouseClickAction.NAME, this.position.clone(), event, false);
		} else if (event.getButton() == 2) {
			this.scrollPosition.set(event.getPosition());
			this.scrollStartPosition = null;
        }
	}

	public void onMouseDrag(UIMouseEvent event) {
		if ((event.getState() & UIMouseEvent.BUTTON1) != 0) {
			if (this.startPosition != null) {
				this.position.set(this.startPosition);
				this.position.add(event.getPosition());
				this.executeAction(TGUpdateDragSelectionAction.NAME, this.position.clone(), event, false);
			}
		} else if ((event.getState() & UIMouseEvent.BUTTON2) != 0) {
			UIPosition delta = this.scrollStartPosition.clone();
			delta.add(event.getPosition());
			delta.sub(this.scrollPosition);
			delta.mul(-1f);
			TGTabFolder.getInstance(this.kit.getTablature().getContext()).getInnerControl().scrollBy(delta);
			this.scrollPosition.sub(delta);
        }
	}

	public void onMouseMove(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable() && !this.isBusy()){
			this.executeAction(TGMouseMoveAction.NAME, event.getPosition().clone(), event, true);
		}
	}

	public void onMouseExit(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()) {
			this.executeAction(TGMouseExitAction.NAME, event.getPosition().clone(), event, true);
		}
	}

	public void onMenuShow(UIMenuEvent event) {
		this.menuOpen = true;
	}

	public void onMenuHide(UIMenuEvent event) {
		this.menuOpen = false;
		TuxGuitar.getInstance().updateCache(true);
	}
	
	public void onZoom(UIZoomEvent event) {
		if( event.getValue() > 0 ) {
			new TGActionProcessor(this.kit.getTablature().getContext(), TGSetLayoutScaleIncrementAction.NAME).process();
		} else {
			new TGActionProcessor(this.kit.getTablature().getContext(), TGSetLayoutScaleDecrementAction.NAME).process();
		}
	}
}
