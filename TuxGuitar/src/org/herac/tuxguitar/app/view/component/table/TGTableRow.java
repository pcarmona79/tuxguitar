package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGTableRow {
	
	private TGTable table;
	private UIPanel row;
	private TGTableRowTextCell number;
	private TGTableRowButtonsCell buttons;
	private TGTableRowTextCell name;
	private TGTableRowTextCell instrument;
	private UICanvas painter;
	
	private UIMouseUpListener mouseUpListenerLabel;
	private UIMouseDownListener mouseDownListenerLabel;
	private UIMouseDoubleClickListener mouseDoubleClickListenerLabel;
	
	private UIMouseUpListener mouseUpListenerCanvas;
	private UIMouseDownListener mouseDownListenerCanvas;
	private UIMouseDoubleClickListener mouseDoubleClickListenerCanvas;
	
	private TGTableCanvasPainter paintListenerCanvas;
	
	public TGTableRow(TGTable table){
		this.table = table;
		this.init();
	}
	
	private void init(){
		UIFactory uiFactory = this.table.getUIFactory();
		MouseListenerLabel mouseListenerLabel = new MouseListenerLabel();
		MouseListenerCanvas mouseListenerCanvas = new MouseListenerCanvas();
		
		this.row = uiFactory.createPanel(this.table.getRowControl(), false);
		this.row.setBgColor(this.table.getViewer().getBackgroundColor());
		this.row.setLayout(new TGTableRowLayout(this));
		
		this.number = new TGTableRowTextCell(this);
		this.number.addMouseDownListener(mouseListenerLabel);
		this.number.addMouseUpListener(mouseListenerLabel);
		this.number.addMouseDoubleClickListener(mouseListenerLabel);
		
		this.buttons = new TGTableRowButtonsCell(this);
		this.buttons.getLayout().set(UITableLayout.MARGIN_LEFT, 0f);
		this.buttons.getLayout().set(UITableLayout.MARGIN_RIGHT, 0f);

		this.name = new TGTableRowTextCell(this);
		this.name.addMouseDownListener(mouseListenerLabel);
		this.name.addMouseUpListener(mouseListenerLabel);
		this.name.addMouseDoubleClickListener(mouseListenerLabel);
		
		this.instrument = new TGTableRowTextCell(this);
		this.instrument.addMouseDownListener(mouseListenerLabel);
		this.instrument.addMouseUpListener(mouseListenerLabel);
		this.instrument.addMouseDoubleClickListener(mouseListenerLabel);
		
		this.painter = uiFactory.createCanvas(this.row, false);
		this.painter.addMouseDownListener(mouseListenerCanvas);
		this.painter.addMouseUpListener(mouseListenerCanvas);
		this.painter.addMouseDoubleClickListener(mouseListenerCanvas);
		this.painter.addPaintListener(new TGBufferedPainterListenerLocked(this.table.getContext(), new TGTableRowPaintHandle()));
		this.table.appendListeners(this.painter);
	}

	public void loadProperties() {
		this.buttons.loadProperties();
	}

	public void loadIcons() {
	    this.buttons.loadIcons();
	}

	public void setBgColor(UIColor background){
		this.number.setBgColor(background);
		this.buttons.setBgColor(background);
		this.name.setBgColor(background);
		this.instrument.setBgColor(background);
	}
	
	public void setFgColor(UIColor foreground){
		this.number.setFgColor(foreground);
		this.buttons.setFgColor(foreground);
		this.name.setFgColor(foreground);
		this.instrument.setFgColor(foreground);
	}
	
	public void dispose(){
		this.row.dispose();
	}
	
	public TGTable getTable() {
		return this.table;
	}
	
	public UIPanel getControl() {
		return this.row;
	}
	
	public UICanvas getPainter() {
		return this.painter;
	}
	
	public TGTableRowTextCell getInstrument() {
		return this.instrument;
	}
	
	public TGTableRowTextCell getName() {
		return this.name;
	}
	
	public TGTableRowTextCell getNumber() {
		return this.number;
	}
	
	public TGTableRowButtonsCell getButtons() {
		return this.buttons;
	}

	public UIMouseUpListener getMouseUpListenerLabel() {
		return mouseUpListenerLabel;
	}

	public void setMouseUpListenerLabel(UIMouseUpListener mouseUpListenerLabel) {
		this.mouseUpListenerLabel = mouseUpListenerLabel;
	}

	public UIMouseDownListener getMouseDownListenerLabel() {
		return mouseDownListenerLabel;
	}

	public void setMouseDownListenerLabel(UIMouseDownListener mouseDownListenerLabel) {
		this.mouseDownListenerLabel = mouseDownListenerLabel;
	}

	public UIMouseDoubleClickListener getMouseDoubleClickListenerLabel() {
		return mouseDoubleClickListenerLabel;
	}

	public void setMouseDoubleClickListenerLabel(UIMouseDoubleClickListener mouseDoubleClickListenerLabel) {
		this.mouseDoubleClickListenerLabel = mouseDoubleClickListenerLabel;
	}

	public UIMouseUpListener getMouseUpListenerCanvas() {
		return mouseUpListenerCanvas;
	}

	public void setMouseUpListenerCanvas(UIMouseUpListener mouseUpListenerCanvas) {
		this.mouseUpListenerCanvas = mouseUpListenerCanvas;
	}

	public UIMouseDownListener getMouseDownListenerCanvas() {
		return mouseDownListenerCanvas;
	}

	public void setMouseDownListenerCanvas(UIMouseDownListener mouseDownListenerCanvas) {
		this.mouseDownListenerCanvas = mouseDownListenerCanvas;
	}

	public UIMouseDoubleClickListener getMouseDoubleClickListenerCanvas() {
		return mouseDoubleClickListenerCanvas;
	}

	public void setMouseDoubleClickListenerCanvas(UIMouseDoubleClickListener mouseDoubleClickListenerCanvas) {
		this.mouseDoubleClickListenerCanvas = mouseDoubleClickListenerCanvas;
	}

	public TGTableCanvasPainter getPaintListenerCanvas() {
		return this.paintListenerCanvas;
	}
	
	public void setPaintListenerCanvas(TGTableCanvasPainter paintListenerCanvas) {
		this.paintListenerCanvas = paintListenerCanvas;
	}

	private class MouseListenerLabel implements UIMouseUpListener, UIMouseDownListener, UIMouseDoubleClickListener{
		
		public MouseListenerLabel(){
			super();
		}
		
		public void onMouseDoubleClick(UIMouseEvent event) {
			if( getMouseDoubleClickListenerLabel() != null && event.getButton() == 1){
				getMouseDoubleClickListenerLabel().onMouseDoubleClick(event);
			}
		}

		public void onMouseDown(UIMouseEvent event) {
			if( getMouseDownListenerLabel() != null){
				getMouseDownListenerLabel().onMouseDown(event);
			}
		}

		public void onMouseUp(UIMouseEvent event) {
			if( getMouseUpListenerLabel() != null){
				getMouseUpListenerLabel().onMouseUp(event);
			}
		}
	}
	
	private class MouseListenerCanvas implements UIMouseUpListener, UIMouseDownListener, UIMouseDoubleClickListener{
		
		public MouseListenerCanvas(){
			super();
		}
		
		public void onMouseDoubleClick(UIMouseEvent event) {
			if( getMouseDoubleClickListenerCanvas() != null && event.getButton() == 1){
				getMouseDoubleClickListenerCanvas().onMouseDoubleClick(event);
			}
		}

		public void onMouseDown(UIMouseEvent event) {
			if( getMouseDownListenerCanvas() != null){
				getMouseDownListenerCanvas().onMouseDown(event);
			}
		}

		public void onMouseUp(UIMouseEvent event) {
			if( getMouseUpListenerCanvas() != null){
				getMouseUpListenerCanvas().onMouseUp(event);
			}
		}
	}
	
	private class TGTableRowPaintHandle implements TGBufferedPainterHandle {
		
		public TGTableRowPaintHandle(){
			super();
		}

		public void paintControl(UIPainter painter) {
			if( TGTableRow.this.getPaintListenerCanvas() != null ){
				TGTableRow.this.getPaintListenerCanvas().paintTrack(TGTableRow.this, painter);
			}
		}

		public UICanvas getPaintableControl() {
			return TGTableRow.this.getPainter();
		}
	}
}
