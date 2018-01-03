package org.herac.tuxguitar.app.view.component.docked;

import org.herac.tuxguitar.ui.widget.UIPanel;

public abstract class TGDockedComponent {

  protected UIPanel control;

  public UIPanel getControl() {
    return this.control;
  }

  public boolean isDisposed() {
    return this.control.isDisposed();
  }

  public abstract void dispose();

  public boolean isVisible() {
    return this.control.isVisible();
  }

  public void setVisible(boolean visible) {
    this.control.setVisible(visible);
  }

  public abstract void redraw();

  public abstract void loadProperties();
}
