package org.herac.tuxguitar.util;

import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGShapeUtil {
    public static void addRoundedRect(UIPainter painter, float x1, float y1, float w, float h, float br) {
        //control point
        float cp =  (float) (br - br * 4.*(Math.sqrt(2.)-1.)/3.);
        float x2 = x1 + w;
        float y2 = y1 + h;

        painter.moveTo(x1 + br, y1);
        painter.cubicTo(x1 + cp, y1, x1, y1 + cp, x1, y1 + br);
        painter.lineTo(x1, y2 - br);
        painter.cubicTo(x1, y2 - cp, x1 + cp, y2, x1 + br, y2);
        painter.lineTo(x2 - br, y2);
        painter.cubicTo(x2 - cp, y2, x2, y2 - cp, x2, y2 - br);
        painter.lineTo(x2, y1 + br);
        painter.cubicTo(x2, y1 + cp, x2 - cp, y1, x2 - br, y1);
        painter.lineTo(x1 + br, y1);
    }
}
