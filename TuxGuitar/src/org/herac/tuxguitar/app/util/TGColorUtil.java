package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.ui.resource.UIColorModel;

public class TGColorUtil {

    public static UIColorModel complementaryTextColor(UIColorModel color) {
        float[] hsb = color.getHSB();
        hsb[1] = 0f;
        hsb[2] = hsb[2] >= .3f ? .1f : .8f;
        return new UIColorModel(hsb);
    }

    public static UIColorModel adjustValue(UIColorModel color, float value) {
        float[] hsb = color.getHSB();
        hsb[2] = Math.min(Math.max(hsb[2] + value, 0f), 1f);
        return new UIColorModel(hsb);
    }

    public static UIColorModel darken(UIColorModel color) {
        return adjustValue(color, -.1f);
    }

}
