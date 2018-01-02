package org.herac.tuxguitar.app.view.docking;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGDockingManager {

  private final UIFactory         uiFactory;
  private final TGContext         context;
  private final UILayoutContainer topDockingArea;
  private final UILayoutContainer bottomDockingArea;

  public TGDockingManager(UIFactory uiFactory, TGContext context, UIWindow window) {
    this.uiFactory = uiFactory;
    this.context = context;
    this.topDockingArea = createDockingArea(window);
    this.bottomDockingArea = createDockingArea(window);
  }

  private UILayoutContainer createDockingArea(UIWindow window) {
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

  public void dock(boolean toTop) {
    this.dock(toTop ? topDockingArea : bottomDockingArea);
  }

  private void dock(UILayoutContainer dockingArea) {
    // Clean docking area first
    this.clear();

    TGConfigManager tgConfig = TGConfigManager.getInstance(this.context);

    UIPanel dockingContainer = uiFactory.createPanel(dockingArea, false);
    UITableLayout uiLayout = new UITableLayout(0f);
    dockingContainer.setLayout(uiLayout);
    dockingContainer.getLayout().set(UITableLayout.IGNORE_INVISIBLE, true);

    TGFretBoardEditor tgFretBoardEditor = TGFretBoardEditor.getInstance(this.context);
    tgFretBoardEditor.createFretBoard(dockingContainer, tgConfig.getBooleanValue(TGConfigKeys.SHOW_FRETBOARD));

    ((UITableLayout) dockingArea.getLayout()).set(dockingContainer, 1, 1, UITableLayout.ALIGN_FILL,
        UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
  }

  private void clear() {
    for (UIControl children : topDockingArea.getChildren()) {
      children.dispose();
    }
    for (UIControl children : bottomDockingArea.getChildren()) {
      children.dispose();
    }
  }
}
