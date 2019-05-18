package org.herac.tuxguitar.app.view.widgets;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGNoteToolbar {
    private final UIPanel toolbar;
    private final UIPanel leftComposite;
    private final UIPanel rightComposite;
    private final UIButton goLeft;
    private final UIButton goRight;
    private final UIButton decrement;
    private final UIImageView durationLabel;
    private final UIButton increment;
    private final UIButton settings;
    private final UITableLayout leftLayout;

    private int columns = 1;
    private int duration;

    public TGNoteToolbar(TGContext context, UIFactory factory, UIContainer parent) {
        this.toolbar = factory.createPanel(parent, false);
        UITableLayout toolbarLayout = new UITableLayout(0f);
        toolbarLayout.set(UITableLayout.MARGIN, 0f);
        this.toolbar.setLayout(toolbarLayout);

        this.leftComposite = factory.createPanel(this.toolbar, false);
        toolbarLayout.set(this.leftComposite, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
        this.leftLayout = new UITableLayout(0f);
        this.leftComposite.setLayout(leftLayout);

        // position
        this.goLeft = factory.createButton(this.leftComposite);
        this.goLeft.addSelectionListener(new TGActionProcessorListener(context, TGGoLeftAction.NAME));
        leftLayout.set(this.goLeft, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        this.goRight = factory.createButton(this.leftComposite);
        this.goRight.addSelectionListener(new TGActionProcessorListener(context, TGGoRightAction.NAME));
        leftLayout.set(this.goRight, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        // separator
        this.createLeftSeparator(factory);

        // duration
        this.decrement = factory.createButton(this.leftComposite);
        this.decrement.addSelectionListener(new TGActionProcessorListener(context, TGDecrementDurationAction.NAME));
        leftLayout.set(this.decrement, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        this.durationLabel = factory.createImageView(this.leftComposite);
        leftLayout.set(this.durationLabel, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        this.increment = factory.createButton(this.leftComposite);
        this.increment.addSelectionListener(new TGActionProcessorListener(context, TGIncrementDurationAction.NAME));
        leftLayout.set(this.increment, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

        this.rightComposite = factory.createPanel(this.toolbar, false);
        toolbarLayout.set(this.rightComposite, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
        UITableLayout rightLayout = new UITableLayout(0f);
        this.rightComposite.setLayout(rightLayout);

        // settings
        this.settings = factory.createButton(this.rightComposite);
        this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
        this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
        rightLayout.set(this.increment, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);

        TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolbar);
    }

    public void createLeftSeparator(UIFactory factory) {
        UISeparator separator = factory.createVerticalSeparator(this.leftComposite);
        leftLayout.set(separator, 1, nextColumn(), UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false, 1, 1);
        leftLayout.set(separator, UITableLayout.PACKED_HEIGHT, 20f);
    }

    public void loadIcons(){
        this.goLeft.setImage(TuxGuitar.getInstance().getIconManager().getArrowLeft());
        this.goRight.setImage(TuxGuitar.getInstance().getIconManager().getArrowRight());
        this.decrement.setImage(TuxGuitar.getInstance().getIconManager().getArrowUp());
        this.increment.setImage(TuxGuitar.getInstance().getIconManager().getArrowDown());
        this.settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
        this.loadDurationImage(true);
    }

    public void update() {
        this.loadDurationImage(false);
    }

    private void loadDurationImage(boolean force) {
        int duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
        if(force || this.duration != duration){
            this.duration = duration;
            this.durationLabel.setImage(TuxGuitar.getInstance().getIconManager().getDuration(this.duration));
        }
    }


    public void loadProperties() {
        this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
    }

    public UIPanel getControl() {
        return toolbar;
    }

    public UIPanel getLeftComposite() {
        return leftComposite;
    }

    public void setLeftControlLayout(UIControl control) {
        this.leftLayout.set(control, 1, this.nextColumn(), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
    }

    public UIButton getSettings() {
        return settings;
    }

    private int nextColumn() {
        return this.columns++;
    }
}
