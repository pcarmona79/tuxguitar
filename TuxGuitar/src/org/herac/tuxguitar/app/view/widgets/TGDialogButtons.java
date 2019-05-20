package org.herac.tuxguitar.app.view.widgets;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGDialogButtons {

    public static final int ALIGN_RIGHT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_LEFT = 2;

    private static final float MINIMUM_BUTTON_WIDTH = 80;
    private static final float MINIMUM_BUTTON_HEIGHT = 25;

    private UIPanel panel;
    private int columns;
    private List<UIButton> buttons;
    private List<String> properties;

    public TGDialogButtons(UIFactory factory, UIContainer parent, Button... buttons) {
        this.buttons = new ArrayList<>();
        this.properties = new ArrayList<>();
        init(factory, parent, buttons);
    }

    public static Button close(Runnable callback) {
        return new Button("close", ALIGN_RIGHT, true, callback);
    }

    public static Button ok(Runnable callback) {
        return new Button("ok", ALIGN_RIGHT, true, callback);
    }

    public static Button cancel(Runnable callback) {
        return new Button("cancel", ALIGN_RIGHT, callback);
    }

    public static Button yes(Runnable callback) {
        return new Button("yes", ALIGN_RIGHT, true, callback);
    }

    public static Button no(Runnable callback) {
        return new Button("no", ALIGN_RIGHT, callback);
    }

    public static Button revert(Runnable callback) {
        return new Button("revert", ALIGN_LEFT, callback);
    }

    public static ConfirmButton defaults(Runnable callback) {
        return new ConfirmButton("defaults", ALIGN_LEFT, callback);
    }

    private void init(final UIFactory factory, final UIContainer parent, Button[] buttons) {

        UITableLayout layout = new UITableLayout(0f);
        this.panel = factory.createPanel(parent, false);
        this.panel.setLayout(layout);
        this.columns = 1;

        List<Button> leftButtons = new ArrayList<>();
        List<Button> centerButtons = new ArrayList<>();
        List<Button> rightButtons = new ArrayList<>();

        for (Button button : buttons) {
            if (button.getAlign() == ALIGN_RIGHT) {
                rightButtons.add(button);
            } else if (button.getAlign() == ALIGN_CENTER) {
                centerButtons.add(button);
            } else if (button.getAlign() == ALIGN_LEFT) {
                leftButtons.add(button);
            }
        }

        for (Button button : leftButtons) {
            addButton(factory, layout, button, UITableLayout.ALIGN_LEFT);
        }

        final UIDivider divider1 = factory.createHorizontalDivider(panel);
        layout.set(divider1, 1, columns++, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 0f, 0f, null);

        for (Button button : centerButtons) {
            addButton(factory, layout, button, UITableLayout.ALIGN_CENTER);
        }

        final UIDivider divider2 = factory.createHorizontalDivider(panel);
        layout.set(divider2, 1, columns++, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 0f, 0f, null);

        for (Button button : rightButtons) {
            addButton(factory, layout, button, UITableLayout.ALIGN_RIGHT);
        }
    }

    public void loadProperties() {
        for (int i = 0; i < buttons.size(); i++) {
            this.buttons.get(i).setText(TuxGuitar.getProperty(this.properties.get(i)));
        }
    }

    private void addButton(UIFactory factory, UITableLayout layout, final Button button, int alignment) {
        UIButton uiButton = factory.createButton(panel);
        uiButton.setText(TuxGuitar.getProperty(button.getProperty()));
        if (button.isDefaultButton()) {
            uiButton.setDefaultButton();
        }
        uiButton.addSelectionListener(event -> button.getCallback().run());
        layout.set(uiButton, 1, columns++, alignment, UITableLayout.ALIGN_FILL, false, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
        this.buttons.add(uiButton);
        this.properties.add(button.getProperty());
    }

    public UIPanel getControl() {
        return panel;
    }

    public UIButton getButton(int index) {
        return this.buttons.get(index);
    }

    public static class Button {
        private String property;
        private Runnable callback;
        private int align;
        private boolean defaultButton;

        public Button(String property, int align, boolean defaultButton, Runnable callback) {
            this.property = property;
            this.align = align;
            this.defaultButton = defaultButton;
            this.callback = callback;
        }

        public Button(String property, int align, Runnable callback) {
            this(property, align, false, callback);
        }

        public int getAlign() {
            return align;
        }

        public Runnable getCallback() {
            return callback;
        }

        public String getProperty() {
            return property;
        }

        public boolean isDefaultButton() {
            return defaultButton;
        }
    }

    public static class ConfirmButton extends Button {
        public ConfirmButton(String property, int align, boolean defaultButton, final Runnable callback) {
            super(property, align, defaultButton, () -> {
                TGContext context = TuxGuitar.getInstance().getContext();
                TGActionProcessor.process(context, TGOpenViewAction.NAME,
                        TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController(),
                        TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("are-you-sure"),
                        TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO,
                        TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO,
                        TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, callback);
            });
        }

        public ConfirmButton(String property, int align, final Runnable callback) {
            this(property, align, false, callback);
        }
    }
}
