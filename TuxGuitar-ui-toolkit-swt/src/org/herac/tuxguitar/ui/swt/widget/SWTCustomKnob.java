package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIMouseWheelEvent;
import org.herac.tuxguitar.ui.event.UIMouseWheelListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.ui.swt.appearance.SWTAppearance;
import org.herac.tuxguitar.ui.swt.resource.SWTPainter;
import org.herac.tuxguitar.ui.swt.resource.SWTResourceFactory;
import org.herac.tuxguitar.ui.widget.UIKnob;

public class SWTCustomKnob extends SWTControl<Composite> implements UIKnob, UIMouseDragListener, UIMouseUpListener, UIMouseWheelListener, PaintListener {
	 
	private static final int DEFAULT_MAXIMUM = 100;
	private static final int DEFAULT_MINIMUM = 0;
	private static final int DEFAULT_INCREMENT = 1;
	private static final float DEFAULT_PACKED_WIDTH = 32f;
	private static final float DEFAULT_PACKED_HEIGHT = 32f;
	
	private static final float MARGIN = 6;

	private int maximum;
	private int minimum;
	private int increment;
	private int value;
	private float lastDragY;
	private UISelectionListenerManager selectionHandler;
	
	public SWTCustomKnob(SWTContainer<? extends Composite> parent) {
		super(new Composite(parent.getControl(), SWT.DOUBLE_BUFFERED), parent);
		
		this.maximum = DEFAULT_MAXIMUM;
		this.minimum = DEFAULT_MINIMUM;
		this.increment = DEFAULT_INCREMENT;
		this.selectionHandler = new UISelectionListenerManager();
		this.addMouseUpListener(this);
		this.addMouseDragListener(this);
		this.addMouseWheelListener(this);
		this.getControl().addPaintListener(this);
	}

	public int getValue(){
		return this.value;
	}
	
	public void setValue(int value){
		if( this.value != value ){
			this.value = value;
			this.invalidate();
			this.fireSelectionEvent();
		}
	}
	
	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		if( this.maximum != maximum ){
			if( this.minimum > maximum ) {
				this.minimum = maximum;
			}
			if( this.value > maximum ) {
				this.value = maximum;
			}
			this.maximum = maximum;
			this.invalidate();
		}
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		if( this.minimum != minimum ){
			if( this.maximum < minimum ) {
				this.maximum = minimum;
			}
			if( this.value < minimum ) {
				this.value = minimum;
			}
			this.minimum = minimum;
			this.invalidate();
		}
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
		this.invalidate();
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.selectionHandler.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionHandler.removeListener(listener);
	}
	
	public void fireSelectionEvent() {
		if(!this.isIgnoreEvents()) {
			this.selectionHandler.onSelect(new UISelectionEvent(this));
		}
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		super.computePackedSize(fixedWidth != null ? fixedWidth : DEFAULT_PACKED_WIDTH, fixedHeight != null ? fixedHeight : DEFAULT_PACKED_HEIGHT);
	}
	
	public void invalidate() {
		this.getControl().redraw();
	}

	public void paintControl(PaintEvent e) {
		Rectangle area = this.getControl().getClientArea();
		
		// knob
		float ovalSize = (Math.min(area.width, area.height) - MARGIN);
		float notchStart = (float) (ovalSize / 2.f / Math.sqrt(2.f));
		float notchEnd = notchStart + (float) (MARGIN / 2.f / Math.sqrt(2.f));
		float x = area.x + (area.width  / 2f);
		float y = area.y + (area.height / 2f);
		
		// value
		float value = (this.value - this.minimum);
		float maximum = (this.maximum - this.minimum);
		float percent = (0.75f + (value > 0 && maximum > 0 ? ((value / maximum) * 1.5f) : 0f));
		float valueX = (float) Math.cos(Math.PI * percent);
		float valueY = (float) Math.sin(Math.PI * percent);

		SWTAppearance appearance = new SWTAppearance(this.getControl().getDisplay());
		UIResourceFactory factory = new SWTResourceFactory(this.getControl().getDisplay());
		UIPainter uiPainter = new SWTPainter(e.gc);

		UIColor foreground = factory.createColor(appearance.createColorModel(SWT.COLOR_WIDGET_FOREGROUND));
		UIColor background = factory.createColor(appearance.createColorModel(SWT.COLOR_WIDGET_BACKGROUND));

        // background
		uiPainter.setBackground(foreground);
		uiPainter.setAlpha(96);
		uiPainter.initPath(UIPainter.PATH_FILL);
		uiPainter.moveTo(x, y);
		uiPainter.addCircle(x, y, ovalSize + MARGIN);
		uiPainter.closePath();

		// notches
		uiPainter.setAlpha(128);
		uiPainter.setForeground(foreground);
		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(x - notchStart , y + notchStart);
		uiPainter.lineTo(x - notchEnd, y + notchEnd);
		uiPainter.closePath();

		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(x + notchStart, y + notchStart);
		uiPainter.lineTo(x + notchEnd, y + notchEnd);
		uiPainter.closePath();

		// knob
		uiPainter.setAlpha(192);
		uiPainter.setBackground(background);
		uiPainter.initPath(UIPainter.PATH_FILL);
		uiPainter.moveTo(x, y);
		uiPainter.addCircle(x, y, ovalSize);
		uiPainter.closePath();

		// value
		uiPainter.setAlpha(255);
		uiPainter.setLineWidth(2.f);
		uiPainter.setForeground(foreground);
		uiPainter.initPath(UIPainter.PATH_DRAW);
		uiPainter.moveTo(x, y);
		uiPainter.lineTo(x + valueX * ((ovalSize + MARGIN) / 2.f), y + valueY * ((ovalSize + MARGIN) / 2.f));
		uiPainter.closePath();

	}
	
	public void onMouseWheel(UIMouseWheelEvent event) {
		this.setValue(Math.round(Math.max(Math.min(this.value + (Math.signum(event.getValue()) * this.increment), this.maximum), this.minimum)));
	}
	
	public void onMouseDrag(UIMouseEvent event) {
		float dragY = event.getPosition().getY();
		float move = (this.lastDragY - dragY);
		this.lastDragY = dragY;
		this.setValue(Math.round(Math.max(Math.min(this.value + (move * this.increment), this.maximum), this.minimum)));
	}
	
	public void onMouseUp(UIMouseEvent event) {
		this.lastDragY = 0f;
	}
}
