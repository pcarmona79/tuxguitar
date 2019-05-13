package org.herac.tuxguitar.app.view.component.table;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.track.TGAddAndEditNewTrackAction;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGTable {
	private final TGTableViewer viewer;
	private TGContext context;
	private UIPanel table;
	private UIPanel columnControl;
	private UIPanel rowControl;
	private TGTableColumn columnNumber;
	private TGTableColumn columnSoloMute;
	private TGTableColumn columnName;
	private TGTableColumn columnInstrument;
	private TGTableColumn columnCanvas;
	private List<TGTableRow> rows;
	private UIImageView addTrackButton;

	public TGTable(TGContext context, TGTableViewer viewer, UILayoutContainer parent){
		this.context = context;
		this.viewer = viewer;
		this.rows = new ArrayList<TGTableRow>();
		this.newTable(parent);
	}
	
	public void newTable(UILayoutContainer parent){
		UIFactory uiFactory = this.getUIFactory();

		this.table = uiFactory.createPanel(parent, false);
		this.table.setBgColor(this.viewer.getBackgroundColor());

		this.columnControl = uiFactory.createPanel(this.table, false);
		this.columnControl.setBgColor(this.viewer.getBackgroundColor());

		this.columnNumber = new TGTableColumn(this);
		this.columnSoloMute = new TGTableColumn(this);
		this.columnName = new TGTableColumn(this);
		this.addTrackButton = uiFactory.createImageView(this.columnName.getControl());
		this.addTrackButton.addMouseUpListener(new UIMouseUpListener() {
			public void onMouseUp(UIMouseEvent event) {
				if (event.getButton() == 1) {
					TGActionProcessor processor = new TGActionProcessor(context, TGAddAndEditNewTrackAction.NAME);
					processor.process();
                }
			}
		});
		this.columnName.getLayout().set(this.addTrackButton, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_RIGHT, false, true, 1, 1, null, null, 2f);
		this.columnInstrument = new TGTableColumn(this);
		this.columnCanvas = new TGTableColumn(this);

		this.rowControl = uiFactory.createPanel(this.table, false);
		this.rowControl.setLayout(new TGTableBodyLayout());

		this.createTableLayout();
		this.createColumnLayout();
	}

	public UIPanel getControl(){
		return this.table;
	}

	public TGTableViewer getViewer() {
		return viewer;
	}

	public void createTableLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.columnControl, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.rowControl, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);

		this.table.setLayout(uiLayout);
	}
	
	public void createColumnLayout() {
		TGTableDividerHelper dividerHelper = new TGTableDividerHelper(this);
		UITableLayout uiLayout = new UITableLayout(0f);
		
		int columnIndex = 0;
		this.createColumnHeaderLayout(uiLayout, this.columnNumber, ++columnIndex, false, 25f);
		this.createColumnHeaderLayout(uiLayout, this.columnSoloMute, ++columnIndex, false, 40f);
		this.createColumnHeaderLayout(uiLayout, this.columnName, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnName, this.columnInstrument), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnInstrument, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnInstrument, this.columnCanvas), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnCanvas, ++columnIndex, true, null);
		
		this.columnControl.setLayout(uiLayout);
	}
	
	public void createColumnDividerLayout(UITableLayout uiLayout, UIDivider divider, int columnIndex) {
		uiLayout.set(divider, 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(divider, UITableLayout.PACKED_WIDTH, 2f);
		uiLayout.set(divider, UITableLayout.MARGIN, 0f);
	}
	
	public void createColumnHeaderLayout(UITableLayout uiLayout, TGTableColumn column, int columnIndex, Boolean fillX, Float minimumPackedWidth) {
		uiLayout.set(column.getControl(), 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, fillX, false);
		uiLayout.set(column.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, minimumPackedWidth);
		uiLayout.set(column.getControl(), UITableLayout.MARGIN, 0f);
	}
	
	public void createRow(){
		this.rows.add(new TGTableRow(this));
	}

	public int getRowIndex(TGTableRow row) {
		return this.rows.indexOf(row);
	}
	
	public float getRowHeight(){
		return ((TGTableBodyLayout) this.rowControl.getLayout()).getRowHeight();
	}
	
	public float getMinHeight(){
		return this.table.getPackedSize().getHeight();
	}
	
	public UIPanel getColumnControl(){
		return this.columnControl;
	}
	
	public UIPanel getRowControl(){
		return this.rowControl;
	}
	
	public TGTableColumn getColumnInstrument() {
		return this.columnInstrument;
	}
	
	public TGTableColumn getColumnName() {
		return this.columnName;
	}
	
	public TGTableColumn getColumnNumber() {
		return this.columnNumber;
	}
	
	public TGTableColumn getColumnSoloMute() {
		return this.columnSoloMute;
	}
	
	public TGTableColumn getColumnCanvas() {
		return this.columnCanvas;
	}
	
	public TGTableRow getRow(int index){
		if(index >= 0 && index < this.rows.size()){
			return (TGTableRow)this.rows.get(index);
		}
		return null;
	}
	
	public void removeRowsAfter(int index){
		while(index < this.rows.size()){
			TGTableRow row = (TGTableRow)this.rows.get(index);
			row.dispose();
			this.rows.remove(index);
		}
	}
	
	public int getRowCount(){
		return this.rows.size();
	}
	
	public void appendListeners(UIControl control){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(control);
	}

	public void loadProperties() {
		this.getColumnName().setTitle(TuxGuitar.getProperty("track"));
		this.getColumnInstrument().setTitle(TuxGuitar.getProperty("track.instrument"));
		this.addTrackButton.setToolTipText(TuxGuitar.getProperty("track.add"));
		for (TGTableRow row : this.rows) {
			row.loadProperties();
		}
	}

	public void loadIcons() {
		this.addTrackButton.setImage(TGIconManager.getInstance(context).getListAdd());
		for (TGTableRow row : this.rows) {
			row.loadIcons();
		}
	}

	public void update(){
		this.table.layout();
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
}
