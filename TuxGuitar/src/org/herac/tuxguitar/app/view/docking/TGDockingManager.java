package org.herac.tuxguitar.app.view.docking;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.piano.TGPianoEditor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGDockingManager {

  private final TGContext context;

  private UIFactory       uiFactory;
  private UIPanel         topDockingArea;
  private UIPanel         bottomDockingArea;

  private boolean         dockedToTop = false;

  public TGDockingManager(TGContext context) {
    this.context = context;
  }

  public void init(UIFactory uiFactory, UIWindow window) {
    this.uiFactory = uiFactory;
    this.topDockingArea = createDockingArea(window);
    this.bottomDockingArea = createDockingArea(window);
  }

  private UIPanel createDockingArea(UIWindow window) {
    UIPanel dockingArea = uiFactory.createPanel(window, false);
    dockingArea.setLayout(new UITableLayout(0f));
    dockingArea.getLayout().set(UITableLayout.IGNORE_INVISIBLE, true);
    return dockingArea;
  }

  public UILayoutContainer getTopDockingArea() {
    return this.topDockingArea;
  }

  public UILayoutContainer getBottomDockingArea() {
    return this.bottomDockingArea;
  }

  public boolean isDockedToTop() {
    return dockedToTop;
  }

  public void dock(boolean toTop) {
    this.dock(toTop ? topDockingArea : bottomDockingArea);
    this.dockedToTop = toTop;
  }

  private void dock(UILayoutContainer dockingArea) {
    this.clear();

    TGConfigManager tgConfig = TGConfigManager.getInstance(this.context);

    UIPanel dockingContainer = uiFactory.createPanel(dockingArea, false);
    UITableLayout uiLayout = new UITableLayout(0f);
    dockingContainer.setLayout(uiLayout);
    dockingContainer.getLayout().set(UITableLayout.IGNORE_INVISIBLE, true);

    TGPianoEditor tgPianoEditor = TGPianoEditor.getInstance(this.context);
    tgPianoEditor.createComponent(dockingContainer, 1, tgConfig.getBooleanValue(TGConfigKeys.SHOW_PIANO));

    TGFretBoardEditor tgFretBoardEditor = TGFretBoardEditor.getInstance(this.context);
    tgFretBoardEditor.createComponent(dockingContainer, 2, tgConfig.getBooleanValue(TGConfigKeys.SHOW_FRETBOARD));

    UITableLayout dockingArealayout = (UITableLayout) dockingArea.getLayout();
    dockingArealayout.set(dockingContainer, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1,
        null, null, 0f);
  }

  private void clear() {
    for (UIControl children : topDockingArea.getChildren()) {
      children.dispose();
    }
    for (UIControl children : bottomDockingArea.getChildren()) {
      children.dispose();
    }
  }

  public static TGDockingManager getInstance(TGContext context) {
    return TGSingletonUtil.getInstance(context, TGDockingManager.class.getName(),
        new TGSingletonFactory<TGDockingManager>() {

          public TGDockingManager createInstance(TGContext context) {
            return new TGDockingManager(context);
          }
        });
  }
}
