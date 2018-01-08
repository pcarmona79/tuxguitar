package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.util.TGContext;

public class TGTableRowSoloMuteCell extends TGTableRowCell {

  private UICheckBox soloCheckbox;
  private UICheckBox muteCheckbox;

  public TGTableRowSoloMuteCell(final TGTableRow row) {
    super(row);
    TGTable table = row.getTable();
    this.soloCheckbox = table.getUIFactory().createCheckBox(getControl());
    this.muteCheckbox = table.getUIFactory().createCheckBox(getControl());
    table.appendListeners(this.soloCheckbox);
    table.appendListeners(this.muteCheckbox);
    TGContext context = row.getTable().getContext();
    this.soloCheckbox.addSelectionListener(new TGActionProcessorListener(context, TGChangeTrackSoloAction.NAME));
    this.muteCheckbox.addSelectionListener(new TGActionProcessorListener(context, TGChangeTrackMuteAction.NAME));
    getLayout().set(this.soloCheckbox, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
    getLayout().set(this.muteCheckbox, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
  }

  @Override
  public void setBgColor(UIColor background) {
    super.setBgColor(background);
    this.soloCheckbox.setBgColor(background);
    this.muteCheckbox.setBgColor(background);
  }

  @Override
  public void setFgColor(UIColor foreground) {
    super.setFgColor(foreground);
    this.soloCheckbox.setFgColor(foreground);
    this.muteCheckbox.setFgColor(foreground);
  }

  @Override
  public void setMenu(UIPopupMenu menu) {
    super.setMenu(menu);
    this.soloCheckbox.setPopupMenu(menu);
    this.muteCheckbox.setPopupMenu(menu);
  }

  @Override
  public void addMouseDownListener(UIMouseDownListener listener) {
    super.addMouseDownListener(listener);
    this.soloCheckbox.addMouseDownListener(listener);
    this.muteCheckbox.addMouseDownListener(listener);
  }

  @Override
  public void addMouseUpListener(UIMouseUpListener listener) {
    super.addMouseUpListener(listener);
    this.soloCheckbox.addMouseUpListener(listener);
    this.muteCheckbox.addMouseUpListener(listener);
  }

  @Override
  public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
    super.addMouseDoubleClickListener(listener);
    this.soloCheckbox.addMouseDoubleClickListener(listener);
    this.muteCheckbox.addMouseDoubleClickListener(listener);
  }

  public void setSolo(boolean solo) {
    this.soloCheckbox.setSelected(solo);
  }

  public void setMute(boolean mute) {
    this.muteCheckbox.setSelected(mute);
  }
}
