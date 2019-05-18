package org.herac.tuxguitar.app.view.widgets;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIColorChooserHandler;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIWindow;

import java.util.ArrayList;
import java.util.List;

public class TGColorButton {

    private UIFactory factory;
    private UIWindow window;
    private UIButton button;
    private UIColor color;
    private UIColor fgColor;
    private UIColorModel value;
    private List<SelectionListener> listeners;

    public TGColorButton(UIFactory factory, UIWindow window, UIContainer parent, String text){
        this.factory = factory;
        this.window = window;
        this.value = new UIColorModel();
        this.button = factory.createButton(parent);
        this.button.setText(text);
        this.listeners = new ArrayList<>();
        this.addListeners();
    }

    public void loadColor(UIColorModel cm){
        this.value.setRed(cm.getRed());
        this.value.setGreen(cm.getGreen());
        this.value.setBlue(cm.getBlue());

        this.disposeColor();
        UIColor color = factory.createColor(this.value);
        this.button.setBgColor(color);
        UIColor fg = factory.createColor(UIColorModel.complementaryTextColor(this.value));
        this.button.setFgColor(fg);
        this.color = color;
        this.fgColor = fg;
    }

    private void disposeColor(){
        if( this.color != null && !this.color.isDisposed()){
            this.button.setBgColor(null);
            this.color.dispose();
            this.color = null;
        }
        if( this.fgColor != null && !this.fgColor.isDisposed()){
            this.button.setFgColor(null);
            this.fgColor.dispose();
            this.fgColor = null;
        }
    }

    private void addListeners(){
        this.button.addSelectionListener(new UISelectionListener() {
            public void onSelect(UISelectionEvent event) {
                UIColorChooser dlg = factory.createColorChooser(TGColorButton.this.window);
                dlg.setDefaultModel(TGColorButton.this.value);
                dlg.setText(TuxGuitar.getProperty("choose-color"));
                dlg.choose(new UIColorChooserHandler() {
                    public void onSelectColor(UIColorModel model) {
                        if( model != null) {
                            TGColorButton.this.loadColor(model);
                            for (SelectionListener listener : listeners) {
                                listener.onSelect(model);
                            }
                        }
                    }
                });
            }
        });
        this.button.addDisposeListener(new UIDisposeListener() {
            public void onDispose(UIDisposeEvent event) {
                TGColorButton.this.disposeColor();
            }
        });
    }

    public interface SelectionListener {
        void onSelect(UIColorModel model);
    }

    public void addSelectionListener(SelectionListener listener) {
        this.listeners.add(listener);
    }

    public UIControl getControl() {
        return this.button;
    }

    public UIColorModel getValue(){
        return this.value;
    }
}
