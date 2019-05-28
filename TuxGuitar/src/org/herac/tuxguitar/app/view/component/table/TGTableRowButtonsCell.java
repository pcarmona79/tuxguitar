package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.app.action.impl.track.TGChangeTrackVisibleAction;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.event.*;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.util.TGContext;

public class TGTableRowButtonsCell extends TGTableRowCell {

  private UIImageView soloButton;
  private UIImageView muteButton;
  private UIImageView visibleButton;
  private TGContext context;

  private boolean solo;
  private boolean mute;
  private boolean visible;

  public TGTableRowButtonsCell(final TGTableRow row) {
    super(row);
    this.context = row.getTable().getContext();
    final TGTable table = row.getTable();

    this.soloButton = table.getUIFactory().createImageView(getControl());
    this.muteButton = table.getUIFactory().createImageView(getControl());
    this.visibleButton = table.getUIFactory().createImageView(getControl());
    table.appendListeners(this.soloButton);
    table.appendListeners(this.muteButton);
    table.appendListeners(this.visibleButton);
    this.soloButton.addMouseUpListener(createClickListener(TGChangeTrackSoloAction.NAME));
    this.muteButton.addMouseUpListener(createClickListener(TGChangeTrackMuteAction.NAME));
    this.visibleButton.addMouseUpListener(createClickListener(TGChangeTrackVisibleAction.NAME));
    getLayout().set(this.soloButton, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 2f);
    getLayout().set(this.muteButton, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 2f);
    getLayout().set(this.visibleButton, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 2f);
    loadProperties();
  }

  private UIMouseUpListener createClickListener(final String action) {
    return event -> {
      if (event.getButton() == 1) {
        final TGTable table = getRow().getTable();
        final Tablature tablature = table.getViewer().getEditor().getTablature();
        final TGTrack track = tablature.getSongManager().getTrack(tablature.getSong(), table.getRowIndex(getRow()) + 1);

        TGActionProcessor processor = new TGActionProcessor(context, action);
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
        processor.process();
      }
    };
  }

  @Override
  public void setBgColor(UIColor background) {
    super.setBgColor(background);
    this.soloButton.setBgColor(background);
    this.muteButton.setBgColor(background);
    this.visibleButton.setBgColor(background);
  }

  @Override
  public void setFgColor(UIColor foreground) {
    super.setFgColor(foreground);
    this.soloButton.setFgColor(foreground);
    this.muteButton.setFgColor(foreground);
    this.visibleButton.setFgColor(foreground);
  }

  @Override
  public void setMenu(UIPopupMenu menu) {
    super.setMenu(menu);
    this.soloButton.setPopupMenu(menu);
    this.muteButton.setPopupMenu(menu);
    this.visibleButton.setPopupMenu(menu);
  }

  @Override
  public void addMouseDownListener(UIMouseDownListener listener) {
    super.addMouseDownListener(listener);
    this.soloButton.addMouseDownListener(listener);
    this.muteButton.addMouseDownListener(listener);
    this.visibleButton.addMouseDownListener(listener);
  }

  @Override
  public void addMouseUpListener(UIMouseUpListener listener) {
    super.addMouseUpListener(listener);
    this.soloButton.addMouseUpListener(listener);
    this.muteButton.addMouseUpListener(listener);
    this.visibleButton.addMouseUpListener(listener);
  }

  public void setSolo(boolean solo) {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.soloButton.setImage(solo ? iconManager.getSolo() : iconManager.getSoloDisabled());
    this.solo = solo;
  }

  public void setMute(boolean mute) {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.muteButton.setImage(mute ? iconManager.getMute() : iconManager.getMuteDisabled());
    this.mute = mute;
  }

  public void setVisible(boolean visible) {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.visibleButton.setImage(visible ? iconManager.getVisible() : iconManager.getVisibleDisabled());
    this.visible = visible;
  }

  public void loadProperties() {
    this.soloButton.setToolTipText(TuxGuitar.getProperty("track.solo"));
    this.muteButton.setToolTipText(TuxGuitar.getProperty("track.mute"));
    this.visibleButton.setToolTipText(TuxGuitar.getProperty("track.visible"));
  }

  public void loadIcons() {
    setSolo(this.solo);
    setMute(this.mute);
    setVisible(this.visible);
  }
}
