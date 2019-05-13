package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.util.TGBeatRange;

public class TGTableCanvasPainter {
	
	private TGTableViewer viewer;
	private TGTrack track;

	private UIColor caretColor;
	private UIColor restCaretColor;

	public TGTableCanvasPainter(TGTableViewer viewer,TGTrack track){
		this.viewer = viewer;
		this.track = track;
	}

	private static final int SELECTION_ALPHA = 128;
	private static final int SELECTION_BORDER_ALPHA = 64;
	/* border radius */
	private static float C_BR = 3f;
	/* dist from corner make a circle with control point */
	private static float C_CPD = (float) (C_BR - C_BR * 4.*(Math.sqrt(2.)-1.)/3.);

	protected void paintTrack(TGTableRow row, UIPainter painter){
		int scrollX = this.viewer.getHScrollSelection();
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

		TGLayout layout = viewer.getEditor().getTablature().getViewLayout();
		TGBeatRange beatRange = viewer.getEditor().getTablature().getSelector().getBeatRange();

		int count = this.track.countMeasures();
		int j = (int) Math.floor(scrollX / size);
		for(float x = -scrollX + j * size; j < count && x < width; j++, x += size) {
			TGMeasureImpl measure = (TGMeasureImpl) this.track.getMeasure(j);
			boolean isRestMeasure = this.isRestMeasure(measure);
			painter.initPath(UIPainter.PATH_FILL);
			painter.setAntialias(true);
			if(!isRestMeasure){
				painter.setBackground(trackColor);
			}else{
				painter.setBackground(colorBackground);
			}
			painter.addRectangle(x + .5f,y + .5f,size - 1f,size - 1f);
			painter.closePath();

			final float C_MARGIN = 2.5f;
			float x1 = x + C_MARGIN;
			float y1 = y + C_MARGIN;
			float x2 = x1 + size - C_MARGIN * 2f;
			float y2 = y1 + size - C_MARGIN * 2f;

			boolean hasCaret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure().equals(measure);
			if((playing && measure.isPlaying(this.viewer.getEditor().getTablature().getViewLayout())) || (!playing && hasCaret)){
				painter.setBackground(getCaretColor(factory, trackColor, colorBackground, isRestMeasure));
				painter.initPath(UIPainter.PATH_FILL);
				paintRoundedRect(painter, x1, y1, x2, y2);
			}

			if (beatRange.containsMeasure(measure)) {
				painter.setBackground(layout.getResources().getSelectionColor());
				painter.setAlpha(SELECTION_ALPHA);
				painter.initPath(UIPainter.PATH_FILL);
				painter.addRectangle(x + 1.5f, y + 1.5f, size - 3f, size - 3f);
				painter.closePath();

				painter.setAlpha(SELECTION_BORDER_ALPHA);
				painter.setForeground(getCaretColor(factory, trackColor, colorBackground, isRestMeasure));
				painter.initPath(UIPainter.PATH_DRAW);
				painter.addRectangle(x + 1.5f, y + 1.5f, size - 3f, size - 3f);
				painter.closePath();
				painter.setAlpha(255);
			}
		}
		
		trackColor.dispose();
		colorBackground.dispose();
		if (caretColor != null) {
			caretColor.dispose();
			caretColor = null;
		}
		if (restCaretColor != null) {
			restCaretColor.dispose();
			restCaretColor = null;
		}
	}

	private UIColor getCaretColor(UIFactory factory, UIColor fg, UIColor bg, boolean isRestMeasure) {
		if (!isRestMeasure) {
			if (caretColor == null) {
				caretColor = factory.createColor(UIColorModel.complementaryTextColor(new UIColorModel(fg)));
			}
			return caretColor;
		} else {
			if (restCaretColor == null) {
				restCaretColor = factory.createColor(UIColorModel.complementaryTextColor(new UIColorModel(bg)));
			}
			return restCaretColor;
		}
	}

	private void paintRoundedRect(UIPainter painter, float x1, float y1, float x2, float y2) {
		painter.setAntialias(true);
		painter.moveTo(x1 + C_BR, y1);
		painter.cubicTo(x1 + C_CPD, y1, x1, y1 + C_CPD, x1, y1 + C_BR);
		painter.lineTo(x1, y2 - C_BR);
		painter.cubicTo(x1, y2 - C_CPD, x1 + C_CPD, y2, x1 + C_BR, y2);
		painter.lineTo(x2 - C_BR, y2);
		painter.cubicTo(x2 - C_CPD, y2, x2, y2 - C_CPD, x2, y2 - C_BR);
		painter.lineTo(x2, y1 + C_BR);
		painter.cubicTo(x2, y1 + C_CPD, x2 - C_CPD, y1, x2 - C_BR, y1);
		painter.lineTo(x1 + C_BR, y1);
		painter.closePath();
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
