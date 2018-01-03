package org.herac.tuxguitar.app.view.component.docked;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGDockedPlayingComponentEditor<T extends TGDockedPlayingComponent>
    extends TGDockedComponentEditor<T> {

  public TGDockedPlayingComponentEditor(TGContext context) {
    super(context);
  }

  public void redrawPlayingMode() {
    if (!isDisposed() && !TuxGuitar.getInstance().isLocked()) {
      getComponent().redrawPlayingMode();
    }
  }

  public void loadIcons() {
    if (!isDisposed()) {
      getComponent().loadIcons();
    }
  }

  public void loadScale() {
    if (!isDisposed()) {
      getComponent().loadScale();
    }
  }

  public void showExternalBeat(TGBeat beat) {
    if (!isDisposed()) {
      getComponent().setExternalBeat(beat);
    }
  }

  public void hideExternalBeat() {
    if (!isDisposed()) {
      getComponent().setExternalBeat(null);
    }
  }

  protected void processRedrawEvent(TGEvent event) {
    int type = ((Integer) event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
    if (type == TGRedrawEvent.NORMAL) {
      this.redraw();
    } else if (type == TGRedrawEvent.PLAYING_NEW_BEAT) {
      this.redrawPlayingMode();
    }
  }

  protected void processExternalBeatEvent(TGEvent event) {
    if (TGExternalBeatViewerEvent.ACTION_SHOW.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION))) {
      this.showExternalBeat((TGBeat) event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_BEAT));
    } else if (TGExternalBeatViewerEvent.ACTION_HIDE.equals(
        event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION))) {
      this.hideExternalBeat();
    }
  }

  @Override
  protected void processEventInner(final TGEvent event) {
    super.processEventInner(event);
    if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
      loadIcons();
    } else if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
      processRedrawEvent(event);
    } else if (TGExternalBeatViewerEvent.EVENT_TYPE.equals(event.getEventType())) {
      processExternalBeatEvent(event);
    } else if (ScaleEvent.EVENT_TYPE.equals(event.getEventType())) {
      loadScale();
    }
  }

}
