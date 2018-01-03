package org.herac.tuxguitar.app.view.component.docked;

import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public abstract class TGDockedComponentEditor<T extends TGDockedComponent> implements TGEventListener {

  protected TGContext context;
  protected boolean   visible;

  private T           component;

  public TGDockedComponentEditor(TGContext context) {
    this.context = context;
    this.appendListeners();
  }

  protected abstract void appendListeners();

  public T getComponent() {
    return component;
  }

  public void createComponent(UIPanel dock, int colIdx, boolean visible) {
    this.visible = visible;
    this.component = createComponentInstance(dock);
    this.component.getControl().setVisible(visible);

    UITableLayout uiLayout = (UITableLayout) dock.getLayout();
    setLayoutControl(uiLayout, component.getControl(), colIdx);

    if (visible) {
      this.showComponent();
    }
  }

  /** By default, fill both axes of the docking container with a control */
  protected void setLayoutControl(UITableLayout layout, UIPanel control, int colIdx) {
    layout.set(control, 1, colIdx, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null,
        0f);
  }

  protected abstract T createComponentInstance(UIPanel parent);

  public void hideComponent() {
    this.visible = false;
    this.component.getControl().setVisible(this.visible);

    hideComponentInner();

    TGWindow tgWindow = TGWindow.getInstance(this.context);
    tgWindow.getWindow().layout();
  }

  protected abstract void hideComponentInner();

  public void showComponent() {
    this.visible = true;
    this.component.getControl().setVisible(this.visible);

    showComponentInner();

    TGWindow tgWindow = TGWindow.getInstance(this.context);
    tgWindow.getWindow().layout();
  }

  protected abstract void showComponentInner();

  public boolean isVisible() {
    return (component != null && !component.isDisposed() && this.visible);
  }

  public boolean isDisposed() {
    return (component == null || component.isDisposed());
  }

  public void dispose() {
    if (!isDisposed()) {
      getComponent().dispose();
    }
  }

  public void redraw() {
    if (!isDisposed()) {
      getComponent().redraw();
    }
  }

  public void loadProperties() {
    if (!isDisposed()) {
      getComponent().loadProperties();
    }
  }

  public void processEvent(final TGEvent event) {
    TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {

      public void run() {
        processEventInner(event);
      }
    });
  }

  protected void processEventInner(final TGEvent event) {
    if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
      loadProperties();
    }
  }
}
