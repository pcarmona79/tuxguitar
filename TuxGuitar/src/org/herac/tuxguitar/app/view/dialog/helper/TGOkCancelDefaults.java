package org.herac.tuxguitar.app.view.dialog.helper;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;

public class TGOkCancelDefaults {

  private static final float MINIMUM_BUTTON_WIDTH  = 80;
  private static final float MINIMUM_BUTTON_HEIGHT = 25;

  private UIPanel            panel;

  public TGOkCancelDefaults(
      TGContext context,
      UIFactory factory,
      UIContainer parent,
      Runnable onOkClick,
      Runnable onCancelClick,
      Runnable onDefaultClick) {
    init(context, factory, parent, onOkClick, onCancelClick, onDefaultClick);
  }

  private void init(
      final TGContext context,
      final UIFactory factory,
      final UIContainer parent,
      final Runnable onOkClick,
      final Runnable onCancelClick,
      final Runnable onDefaultClick) {

    UITableLayout layout = new UITableLayout(0f);
    this.panel = factory.createPanel(parent, false);
    this.panel.setLayout(layout);
    int colIdx = 0;

    final UIButton buttonDefaults = factory.createButton(panel);
    buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
    buttonDefaults.addSelectionListener(new UISelectionListener() {

      public void onSelect(UISelectionEvent event) {
        TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
        tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
        tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("are-you-sure"));
        tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE,
            TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
        tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
        tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, onDefaultClick);
        tgActionProcessor.process();
      }
    });
    layout.set(buttonDefaults, 1, ++colIdx, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, true, 1, 1,
        MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

    final UIDivider divider = factory.createHorizontalDivider(panel);
    layout.set(divider, 1, ++colIdx, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 0f, 0f,
        null);

    final UIButton buttonOK = factory.createButton(panel);
    buttonOK.setDefaultButton();
    buttonOK.setText(TuxGuitar.getProperty("ok"));
    buttonOK.addSelectionListener(new UISelectionListener() {

      public void onSelect(UISelectionEvent event) {
        onOkClick.run();
      }
    });
    layout.set(buttonOK, 1, ++colIdx, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, true, 1, 1,
        MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

    final UIButton buttonCancel = factory.createButton(panel);
    buttonCancel.setText(TuxGuitar.getProperty("cancel"));
    buttonCancel.addSelectionListener(new UISelectionListener() {

      public void onSelect(UISelectionEvent event) {
        onCancelClick.run();
      }
    });

    layout.set(buttonCancel, 1, ++colIdx, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, true, 1, 1,
        MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
    layout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
  }

  public UIPanel getControl() {
    return panel;
  }
}
