package org.herac.tuxguitar.app.view.dialog.tremolobar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar.TremoloBarPoint;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.event.*;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTremoloBarDialog{
	
	private static final int X_SPACING = 30;
	private static final int Y_SPACING = 10;
	private static final int X_LENGTH = TGEffectTremoloBar.MAX_POSITION_LENGTH + 1;
	private static final int Y_LENGTH = (TGEffectTremoloBar.MAX_VALUE_LENGTH * 2) + 1;
	
	private static final String COLOR_BACKGROUND = "widget.bendEditor.backgroundColor";
	private static final String COLOR_BORDER = "widget.bendEditor.border";
	private static final String COLOR_BEND_LINE = "widget.bendEditor.bendLine";
	private static final String COLOR_BEND_POINT = "widget.bendEditor.bendPoint";
	private static final String COLOR_LINE_1 = "widget.bendEditor.line.1";
	private static final String COLOR_LINE_2 = "widget.bendEditor.line.2";
	private static final String COLOR_LINE_3 = "widget.bendEditor.line.3";
	
	private int[] x; 
	private int[] y;
	private float width;
	private float height;
	private List<IndexPoint> points;
	private UICanvas editor;
	private TGColorManager colorManager;

	private static class IndexPoint {

		private int x;
		private int y;

		public IndexPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}
	public TGTremoloBarDialog() {
		this.points = new ArrayList<>();
		this.resize(-1, -1);
	}

	private void resize(UISize size) {
		this.resize((int) size.getWidth(), (int) size.getHeight());
	}

	private void resize(int w, int h){
		this.x = new int[X_LENGTH];
		this.y = new int[Y_LENGTH];

		if (w == -1) {
			this.width = ((X_SPACING * X_LENGTH) + X_SPACING);
		} else {
			this.width = w;
		}
		if (h == -1) {
			this.height = ((Y_SPACING * Y_LENGTH) + Y_SPACING);
		} else {
			this.height = h;
		}

		for(int i = 0;i < this.x.length;i++){
			this.x[i] = Math.round((i + 1) * (this.width / (X_LENGTH + 1)));
		}
		for(int i = 0;i < this.y.length;i++){
			this.y[i] = Math.round((i + 1) * (this.height / (Y_LENGTH + 1)));
		}
	}
	
	public void show(final TGViewContext context){
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final TGNote note = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		if( measure != null && beat != null && note != null && string != null ) {
			final UIAppearance appearance = TGApplication.getInstance(context.getContext()).getAppearance();;
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, true);
			
			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("effects.tremolo-bar-editor"));
			
			//----------------------------------------------------------------------
			UITableLayout compositeLayout = new UITableLayout();
			UIPanel composite = uiFactory.createPanel(dialog, false);
			composite.setLayout(compositeLayout);
			dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UITableLayout leftCompositeLayout = new UITableLayout();
			UIPanel leftComposite = uiFactory.createPanel(composite, false);
			leftComposite.setLayout(leftCompositeLayout);
			compositeLayout.set(leftComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UITableLayout rightCompositeLayout = new UITableLayout();
			UIPanel rightComposite = uiFactory.createPanel(composite, false);
			rightComposite.setLayout(rightCompositeLayout);
			compositeLayout.set(rightComposite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
			
			//-------------EDITOR---------------------------------------------------
			this.colorManager = TGColorManager.getInstance(context.getContext());
			this.colorManager.appendSkinnableColors(new TGSkinnableColor[] {
				new TGSkinnableColor(COLOR_BACKGROUND, appearance.getColorModel(UIColorAppearance.InputBackground)),
				new TGSkinnableColor(COLOR_BORDER, appearance.getColorModel(UIColorAppearance.InputForeground)),
				new TGSkinnableColor(COLOR_BEND_POINT, appearance.getColorModel(UIColorAppearance.InputForeground)),
				new TGSkinnableColor(COLOR_BEND_LINE, new UIColorModel(0x80, 0x80, 0x80)),
				new TGSkinnableColor(COLOR_LINE_1, new UIColorModel(0x80, 0x80, 0x80)),
				new TGSkinnableColor(COLOR_LINE_2, new UIColorModel(0x80, 0x00, 0x00)),
				new TGSkinnableColor(COLOR_LINE_3, new UIColorModel(0x00, 0x00, 0x80))
			});
			
			this.editor = uiFactory.createCanvas(leftComposite, true);
			this.editor.setBgColor(this.colorManager.getColor(COLOR_BACKGROUND));
			this.editor.addPaintListener(new UIPaintListener() {
				public void onPaint(UIPaintEvent event) {
					paintEditor(event.getPainter());
				}
			});
			this.editor.addMouseUpListener(new UIMouseUpListener() {
				public void onMouseUp(UIMouseEvent event) {
					TGTremoloBarDialog.this.checkPoint(event.getPosition().getX(), event.getPosition().getY());
					TGTremoloBarDialog.this.editor.redraw();
				}
			});
			this.editor.addResizeListener(new UIResizeListener() {
				public void onResize(UIResizeEvent event) {
					TGTremoloBarDialog.this.resize(TGTremoloBarDialog.this.editor.getBounds().getSize());
				}
			});
			leftCompositeLayout.set(this.editor, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, width, height, null);
			
			//-------------DEFAULT BEND LIST---------------------------------------------------
			final List<UISelectItem<TGEffectTremoloBar>> presetItems = this.createPresetItems();
			final UIListBoxSelect<TGEffectTremoloBar> defaultTremoloBarList = uiFactory.createListBoxSelect(rightComposite);
			
			for(UISelectItem<TGEffectTremoloBar> presetItem : presetItems) {
				defaultTremoloBarList.addItem(presetItem);
			}
			
			defaultTremoloBarList.setSelectedItem(presetItems.get(0));
			defaultTremoloBarList.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					TGEffectTremoloBar selection = defaultTremoloBarList.getSelectedValue();
					if( selection != null ){
						setTremoloBar(selection);
						TGTremoloBarDialog.this.editor.redraw();
					}
				}
			});
			rightCompositeLayout.set(defaultTremoloBarList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//------------------BUTTONS--------------------------
			UIButton buttonClean = uiFactory.createButton(rightComposite);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTremoloBar(context.getContext(), measure, beat, string, null);
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonClean, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true, 1, 1, 80f, 25f, null);
			
			UIButton buttonOK = uiFactory.createButton(rightComposite);
			buttonOK.setDefaultButton();
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeTremoloBar(context.getContext(), measure, beat, string, getTremoloBar());
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonOK, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false, 1, 1, 80f, 25f, null);
			
			UIButton buttonCancel = uiFactory.createButton(rightComposite);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonCancel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false, 1, 1, 80f, 25f, null);
			
			if(note.getEffect().isTremoloBar()){
				setTremoloBar(note.getEffect().getTremoloBar());
			}else{
				setTremoloBar(presetItems.get(0).getValue());
			}
			
			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}
	
	private void paintEditor(UIPainter painter){
		for(int i = 0;i < this.x.length;i++){
			this.setStyleX(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[i], this.y[0]);
			painter.lineTo(this.x[i], this.y[this.y.length - 1]);
			painter.closePath();
		}
		for(int i = 0;i < this.y.length;i++){
			this.setStyleY(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[0], this.y[i]);
			painter.lineTo(this.x[this.x.length - 1], this.y[i]);
			painter.closePath();
		}
		
		painter.setLineStyleSolid();
		painter.setLineWidth(2);
		painter.setForeground(this.colorManager.getColor(COLOR_BEND_LINE));
		
		IndexPoint prevPoint = null;
		for(IndexPoint point : this.points) {
			if( prevPoint != null){
				painter.initPath();
				painter.moveTo(this.x[prevPoint.getX()] ,this.y[prevPoint.getY()]);
				painter.lineTo(this.x[point.getX()], this.y[point.getY()]);
				painter.closePath();
			}
			prevPoint = point;
		}
		
		painter.setLineWidth(5);
		painter.setForeground(this.colorManager.getColor(COLOR_BEND_POINT));
		
		for(IndexPoint point : this.points) {
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(this.x[point.getX()] - 2, this.y[point.getY()] - 2, 5, 5);
			painter.closePath();
		}
		painter.setLineWidth(1);
	}
	
	private void setStyleX(UIPainter painter,int i){
		painter.setLineStyleSolid();
		if(i == 0 || i == (X_LENGTH - 1)){
			painter.setForeground(this.colorManager.getColor(COLOR_BORDER));
		}else{
			painter.setForeground(this.colorManager.getColor(COLOR_LINE_3));
			if((i % 3) > 0){
				painter.setLineStyleDot();
			}
		}
	}
	
	private void setStyleY(UIPainter painter,int i){
		painter.setLineStyleSolid();
		if(i == 0 || i == (Y_LENGTH - 1)){
			painter.setForeground(this.colorManager.getColor(COLOR_BORDER));
		}
		else if(i == (TGEffectTremoloBar.MAX_VALUE_LENGTH)){
			painter.setForeground(this.colorManager.getColor(COLOR_BORDER));
		}else{
			painter.setForeground(this.colorManager.getColor(COLOR_LINE_2));
			if((i % 2) > 0){
				painter.setLineStyleDot();
				painter.setForeground(this.colorManager.getColor(COLOR_LINE_1));
			}
		}
	}
	
	private void checkPoint(float x, float y){
		IndexPoint point = new IndexPoint(this.getX(x), this.getY(y));
		if(!this.removePoint(point)){
			this.removePointsAtXLine(point.getX());
			this.addPoint(point);
			this.orderPoints();
		}
	}
	
	private boolean removePoint(IndexPoint point){
		IndexPoint pointToRemove = null;

		for (IndexPoint currPoint : this.points) {
			if (currPoint.getX() == point.getX() && currPoint.getY() == point.getY()) {
				pointToRemove = currPoint;
				break;
			}
		}
		
		if( pointToRemove != null ) {
			this.points.remove(pointToRemove);
			return true;
		}
		return false;
	}
	
	private void orderPoints(){
		for(int i = 0;i < this.points.size();i++){
			IndexPoint minPoint = null;
			for(int noteIdx = i;noteIdx < this.points.size();noteIdx++){
				IndexPoint point = this.points.get(noteIdx);
				if( minPoint == null || point.getX() < minPoint.getX()){
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i,minPoint);
		}
	}
	
	private void removePointsAtXLine(float x){
		List<IndexPoint> pointsToRemove = new ArrayList<>();
		for (IndexPoint point : this.points) {
			if (point.getX() == x) {
				pointsToRemove.add(point);
				break;
			}
		}
		this.points.removeAll(pointsToRemove);
	}
	
	private void addPoint(IndexPoint point){
		this.points.add(point);
	}
	
	private int getX(float pointX){
		int currIndex = -1;
		float currPointX = -1;
		for(int i = 0;i < this.x.length;i++){
			if( currPointX < 0){
				currPointX = this.x[i];
				currIndex = i;
			}else{
				float distanceX = Math.abs(pointX - currPointX);
				float currDistanceX = Math.abs(pointX - this.x[i]);
				if( currDistanceX < distanceX){
					currPointX = this.x[i];
					currIndex = i;
				}
			}
		}
		return currIndex;
	}
	
	private int getY(float pointY){
		int currIndex = -1;
		float currPointY = -1;
		for(int i = 0;i < this.y.length;i++){
			if( currPointY < 0){
				currPointY = this.y[i];
				currIndex = i;
			}else{
				float distanceX = Math.abs(pointY - currPointY);
				float currDistanceX = Math.abs(pointY - this.y[i]);
				if( currDistanceX < distanceX){
					currPointY = this.y[i];
					currIndex = i;
				}
			}
		}
		return currIndex;
	}
	
	public boolean isEmpty(){
		return this.points.isEmpty();
	}
	
	public TGEffectTremoloBar getTremoloBar(){
		if(this.points != null && !this.points.isEmpty()){
			TGEffectTremoloBar tremoloBar = TuxGuitar.getInstance().getSongManager().getFactory().newEffectTremoloBar();
			for(IndexPoint point : this.points){
				tremoloBar.addPoint(point.getX(), TGEffectTremoloBar.MAX_VALUE_LENGTH - point.getY());
			}
			return tremoloBar;
		}
		return null;
	}

	public void setTremoloBar(TGEffectTremoloBar effect){
		this.points.clear();
		for (TremoloBarPoint tremoloBarPoint : effect.getPoints()) {
			this.makePoint(tremoloBarPoint);
		}
	}
	
	private void makePoint(TGEffectTremoloBar.TremoloBarPoint tremoloBarPoint){
		int indexX = tremoloBarPoint.getPosition();
		int indexY = ((this.y.length - TGEffectTremoloBar.MAX_VALUE_LENGTH) - tremoloBarPoint.getValue()) - 1;
		if( indexX >= 0 && indexX < this.x.length && indexY >= 0 && indexY < this.y.length ){
			this.points.add(new IndexPoint(indexX, indexY));
		}
	}
	
	private List<UISelectItem<TGEffectTremoloBar>> createPresetItems() {
		TGEffectTremoloBar tremoloBar = null;
		TGFactory factory = TuxGuitar.getInstance().getSongManager().getFactory();
		List<UISelectItem<TGEffectTremoloBar>> items = new ArrayList<UISelectItem<TGEffectTremoloBar>>();
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,0);
		tremoloBar.addPoint(6,-2);
		tremoloBar.addPoint(12,0);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.dip"), tremoloBar));
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,0);
		tremoloBar.addPoint(9,-2);
		tremoloBar.addPoint(12,-2);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.dive"), tremoloBar));
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,-2);
		tremoloBar.addPoint(9,-2);
		tremoloBar.addPoint(12,0);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.release-up"), tremoloBar));
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,0);
		tremoloBar.addPoint(6,2);
		tremoloBar.addPoint(12,0);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.inverted-dip"), tremoloBar));
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,0);
		tremoloBar.addPoint(9,2);
		tremoloBar.addPoint(12,2);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.return"), tremoloBar));
		
		tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0,2);
		tremoloBar.addPoint(9,2);
		tremoloBar.addPoint(12,0);
		items.add(new UISelectItem<TGEffectTremoloBar>(TuxGuitar.getProperty("effects.tremolo-bar.release-down"), tremoloBar));
		
		return items;
	}
	
	public void changeTremoloBar(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectTremoloBar effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTremoloBarAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTremoloBarAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
