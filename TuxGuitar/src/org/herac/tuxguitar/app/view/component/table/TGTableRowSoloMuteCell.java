package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
import org.herac.tuxguitar.util.TGContext;

public class TGTableRowSoloMuteCell extends TGTableRowCell {

  private UIToggleButton soloButton;
  private UIToggleButton muteButton;
  private TGContext context;

  public TGTableRowSoloMuteCell(final TGTableRow row) {
    super(row);
    this.context = row.getTable().getContext();
    TGTable table = row.getTable();

    this.soloButton = table.getUIFactory().createToggleButton(getControl(), true);
    this.muteButton = table.getUIFactory().createToggleButton(getControl(), true);
    this.setSoloIcon();
    this.setMuteIcon();
    table.appendListeners(this.soloButton);
    table.appendListeners(this.muteButton);
    TGContext context = row.getTable().getContext();
    this.soloButton.addSelectionListener(new TGActionProcessorListener(context, TGChangeTrackSoloAction.NAME));
    this.muteButton.addSelectionListener(new TGActionProcessorListener(context, TGChangeTrackMuteAction.NAME));
    this.soloButton.addSelectionListener(event -> TGTableRowSoloMuteCell.this.setSoloIcon());
    this.muteButton.addSelectionListener(event -> TGTableRowSoloMuteCell.this.setMuteIcon());
    getLayout().set(this.soloButton, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 0f);
    getLayout().set(this.muteButton, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 0f);
  }

  private void setSoloIcon() {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.soloButton.setImage(this.soloButton.isSelected() ? iconManager.getSolo() : iconManager.getSoloDisabled());
  }

  private void setMuteIcon() {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.muteButton.setImage(this.muteButton.isSelected() ? iconManager.getMute() : iconManager.getMuteDisabled());
  }

  @Override
  public void setBgColor(UIColor background) {
    super.setBgColor(background);
    this.soloButton.setBgColor(background);
    this.muteButton.setBgColor(background);
  }

  @Override
  public void setFgColor(UIColor foreground) {
    super.setFgColor(foreground);
    this.soloButton.setFgColor(foreground);
    this.muteButton.setFgColor(foreground);
  }

  @Override
  public void setMenu(UIPopupMenu menu) {
    super.setMenu(menu);
    this.soloButton.setPopupMenu(menu);
    this.muteButton.setPopupMenu(menu);
  }

  @Override
  public void addMouseDownListener(UIMouseDownListener listener) {
    super.addMouseDownListener(listener);
    this.soloButton.addMouseDownListener(listener);
    this.muteButton.addMouseDownListener(listener);
  }

  @Override
  public void addMouseUpListener(UIMouseUpListener listener) {
    super.addMouseUpListener(listener);
    this.soloButton.addMouseUpListener(listener);
    this.muteButton.addMouseUpListener(listener);
  }

  public void setSolo(boolean solo) {
    this.soloButton.setSelected(solo);
    this.setSoloIcon();
  }

  public void setMute(boolean mute) {
    this.muteButton.setSelected(mute);
    this.setMuteIcon();
  }
}
