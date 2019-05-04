package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGColorUtil;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGTableCanvasPainter {
	
	private TGTableViewer viewer;
	private TGTrack track;
	
	public TGTableCanvasPainter(TGTableViewer viewer,TGTrack track){
		this.viewer = viewer;
		this.track = track;
	}

	private static float C_MARGIN = 3f;
	/* border radius */
	private static float C_BR = 3f;
	/* dist from corner make a circle with control point */
	private static float C_CPD = (float) (C_BR - C_BR * 4.*(Math.sqrt(2.)-1.)/3.);

	protected void paintTrack(TGTableRow row, UIPainter painter){
		int x = -this.viewer.getHScrollSelection();
		int y = 0;
		float size = this.viewer.getTable().getRowHeight();
		float width = row.getPainter().getBounds().getWidth();
		boolean playing = TuxGuitar.getInstance().getPlayer().isRunning();

		UIFactory factory = this.viewer.getUIFactory();
		UIColor colorBackground = this.viewer.getColorModel().createBackground(this.viewer.getContext(), 3);
		UIColor borderColor = this.viewer.getBackgroundColor();

		painter.setLineWidth(UIPainter.THINNEST_LINE_WIDTH);
		painter.setBackground(borderColor);
		painter.initPath(UIPainter.PATH_FILL);
		painter.setAntialias(false);
		painter.addRectangle(0, y, width, size);
		painter.closePath();

		UIColor trackColor = factory.createColor(this.track.getColor().getR(), this.track.getColor().getG(), this.track.getColor().getB());
		UIColor caretColor = null;
		UIColor restCaretColor = null;

		int count = this.track.countMeasures();
		for(int j = 0;j < count;j++){
			TGMeasureImpl measure = (TGMeasureImpl) this.track.getMeasure(j);
			boolean isRestMeasure = this.isRestMeasure(measure);
			painter.initPath(UIPainter.PATH_FILL);
			painter.setAntialias(true);
			if(!isRestMeasure){
				painter.setBackground(trackColor);
			}else{
				painter.setBackground(colorBackground);
			}
			painter.addRectangle(x,y,size - 1,size );
			painter.closePath();
			boolean hasCaret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
			if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
				if (!isRestMeasure) {
					if (caretColor == null) {
						caretColor = factory.createColor(TGColorUtil.complementaryTextColor(new UIColorModel(trackColor)));
					}
					painter.setBackground(caretColor);
				} else {
					if (restCaretColor == null) {
						restCaretColor = factory.createColor(TGColorUtil.complementaryTextColor(new UIColorModel(colorBackground)));
					}
					painter.setBackground(restCaretColor);
				}
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(true);
				float x1 = x + C_MARGIN;
				float y1 = y + C_MARGIN;
				float x2 = x1 + size - C_MARGIN * 2 - 1;
				float y2 = y1 + size - C_MARGIN * 2;
				painter.moveTo(x1 + C_BR, y1);
				painter.cubicTo(x1 + C_CPD, y1, x1, y1 + C_CPD, x1, y1 + C_BR);
				painter.lineTo(x1, y2 - C_BR);
				painter.cubicTo(x1, y2 - C_CPD, x1 + C_CPD, y2, x1 + C_BR, y2);
				painter.lineTo(x2 - C_BR, y2);
				painter.cubicTo(x2 - C_CPD, y2, x2, y2 - C_CPD, x2, y2 - C_BR);
				painter.lineTo(x2, y1 + C_BR);
				painter.cubicTo(x2, y1 + C_CPD, x2 - C_CPD, y1, x2 - C_BR, y1);
				painter.closePath();
			}
			x += size;
		}
		
		trackColor.dispose();
		colorBackground.dispose();
		if (caretColor != null) {
			caretColor.dispose();
		}
		if (restCaretColor != null) {
			restCaretColor.dispose();
		}
	}
	
	private boolean isRestMeasure(TGMeasureImpl measure){
		int beatCount = measure.countBeats();
		for(int i = 0; i < beatCount; i++){
			if( !measure.getBeat(i).isRestBeat() ){
				return false;
			}
		}
		return true;
	}
}
