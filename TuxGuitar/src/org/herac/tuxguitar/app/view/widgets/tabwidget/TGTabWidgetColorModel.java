package org.herac.tuxguitar.app.view.widgets.tabwidget;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;

public class TGTabWidgetColorModel {
    private UIColor underlineColor;
    private UIColor selectedBgColor;
    private UIColor borderColor;

    public TGTabWidgetColorModel(TGContext context, UIFactory factory) {
        TGApplication application = TGApplication.getInstance(context);
        UIAppearance appearance = application.getAppearance();

        UIColorModel fg = appearance.getColorModel(UIColorAppearance.InputSelectedBackground);
        this.underlineColor = factory.createColor(UIColorModel.adjustValue(fg, .1f));

        UIColorModel bg = appearance.getColorModel(UIColorAppearance.WidgetLightBackground);
        this.selectedBgColor = factory.createColor(bg);

        UIColorModel border = appearance.getColorModel(UIColorAppearance.WidgetBorder);
        this.borderColor = factory.createColor(border);
    }

    public boolean isDisposed() {
        return this.underlineColor.isDisposed();
    }

    public void dispose() {
        if (!this.isDisposed()) {
            this.underlineColor.dispose();
            this.selectedBgColor.dispose();
            this.borderColor.dispose();
        }
    }

    public UIColor getUnderlineColor() {
        return underlineColor;
    }

    public UIColor getSelectedBgColor() {
        return selectedBgColor;
    }

    public UIColor getBorderColor() {
        return borderColor;
    }
}
