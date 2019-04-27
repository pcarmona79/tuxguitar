package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.ui.resource.UIColorModel;

public class TGColorUtil {
    public static UIColorModel complementaryTextColor(UIColorModel color) {
        float[] hsb = color.getHSB();
        hsb[1] = 0f;
        hsb[2] = Math.round(1f - hsb[2]);
        return new UIColorModel(hsb[0], hsb[1], hsb[2]);
    }
}
