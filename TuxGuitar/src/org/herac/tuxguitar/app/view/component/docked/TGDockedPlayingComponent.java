package org.herac.tuxguitar.app.view.component.docked;

import org.herac.tuxguitar.song.models.TGBeat;

public abstract class TGDockedPlayingComponent extends TGDockedComponent {

  public abstract void redrawPlayingMode();

  public abstract void loadIcons();

  public abstract void loadScale();

  public abstract void setExternalBeat(TGBeat externalBeat);
}
