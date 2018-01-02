package org.herac.tuxguitar.app.view.docking;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGDockingManagerController implements TGToggleViewController {

  public void toggleView(TGViewContext context) {
    TGDockingManager dockingManager = TGDockingManager.getInstance(context.getContext());
    dockingManager.dock(!dockingManager.isDockedToTop());
  }
}
