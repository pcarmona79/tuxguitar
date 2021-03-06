package org.herac.tuxguitar.app.view.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.icons.TGSkinManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.docking.TGDockingManager;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGWindow implements TGEventListener {
	
	private TGContext context;
	private TGSyncProcess loadTitleProcess;
	private TGCursorController cursorController;
	private TGWindowDivider tableDivider;
	
	private UIWindow window;
	
	public TGWindow(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
	}
	
	public void open() {
		if( this.window != null ) {
			this.window.open();
			this.window.layout();
			updateMinimumSize();
		}
	}
	
	public void createWindow() {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		
		this.window = uiFactory.createWindow();
		this.window.addCloseListener(new TGActionProcessorListener(this.context, TGDisposeAction.NAME));
		
		this.createShellComposites(uiFactory);
		this.createShellListeners();
		this.loadIcons();
		this.loadInitialBounds();
	}

	public TGWindowDivider getTableDivider() {
		return tableDivider;
	}

	private void createShellComposites(UIFactory uiFactory) {
		TGConfigManager tgConfig = TGConfigManager.getInstance(this.context);

		TuxGuitar.getInstance().getItemManager().updateMainMenuVisibility(tgConfig.getBooleanValue(TGConfigKeys.SHOW_MENU_BAR));
		TGMainToolBar tgToolBar = TGMainToolBar.getInstance(this.context);
		tgToolBar.createToolBar(this.window, tgConfig.getBooleanValue(TGConfigKeys.SHOW_MAIN_TOOLBAR));
		
		UITableLayout topContainerLayout = new UITableLayout(0f);
		UIPanel topContainer = uiFactory.createPanel(this.window, false);
		topContainer.setLayout(topContainerLayout);
		topContainerLayout.set(UITableLayout.IGNORE_INVISIBLE, true);
		
		TGEditToolBar tgEditToolBar = TGEditToolBar.getInstance(this.context);
		tgEditToolBar.createToolBar(topContainer, tgConfig.getBooleanValue(TGConfigKeys.SHOW_EDIT_TOOLBAR));
		topContainerLayout.set(tgEditToolBar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);
		topContainerLayout.set(tgEditToolBar.getControl(), UITableLayout.PACKED_HEIGHT, 0f);

		TGTabFolder tgTabFolder = TGTabFolder.getInstance(this.context);
		tgTabFolder.init(topContainer);
		tgTabFolder.getControl().computePackedSize(null, null);
		topContainerLayout.set(tgTabFolder.getControl(), 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 1f, 1f, 0f);
		topContainerLayout.set(tgTabFolder.getControl(), UITableLayout.IGNORE_INVISIBLE, false);

		this.tableDivider = new TGWindowDivider(this.context);
		this.tableDivider.createDivider(this.window);
		
		TGTableViewer tgTableViewer = TGTableViewer.getInstance(this.context);
		tgTableViewer.init(this.window, tgConfig.getBooleanValue(TGConfigKeys.SHOW_TRACKS));

		this.tableDivider.getControl().setBgColor(tgTableViewer.getBorderColor());

		TGDockingManager dockingManager = TGDockingManager.getInstance(this.context);
		dockingManager.init(uiFactory, window);
		
		// Layout
		this.window.setLayout(new TGWindowLayout(tgToolBar.getControl(), topContainer, this.tableDivider.getControl(), tgTableViewer.getControl(), dockingManager));
		dockingManager.dock(tgConfig.getBooleanValue(TGConfigKeys.LAYOUT_DOCK_TO_TOP));
	}

	public void updateMinimumSize() {
		this.window.computePackedSize(null, null);
		this.window.setMinimumSize(this.window.getPackedSize());
	}
	
	private void loadInitialBounds() {
		TGConfigManager config = TGConfigManager.getInstance(this.context);
		
		boolean maximized = config.getBooleanValue(TGConfigKeys.MAXIMIZED);
		if( maximized ) {
			this.window.maximize();
		}
		else {
			float width = config.getFloatValue(TGConfigKeys.WIDTH);
			float height = config.getFloatValue(TGConfigKeys.HEIGHT);
			if( width > 0 && height > 0 ){
				UIRectangle uiRectangle = new UIRectangle();
				uiRectangle.setSize(new UISize(width, height));

				this.window.setBounds(uiRectangle);
			}
		}
	}
	
	private void createShellListeners() {
		TGSkinManager tgSkinManager = TGSkinManager.getInstance(this.context);
		tgSkinManager.addLoader(this);
	}
	
	public void loadDefaultCursor() {
		this.loadCursor(UICursor.NORMAL);
	}
	
	public void loadBusyCursor() {
		this.loadCursor(UICursor.WAIT);
	}
	
	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.getWindow()) ) {
				this.cursorController = new TGCursorController(this.context, this.getWindow());
			}
			this.cursorController.loadCursor(cursor);
		}
	}
	
	public void moveToTop() {
		if(!this.isDisposed()) {
			this.getWindow().moveToTop();
		}
	}
	
	public boolean isDisposed() {
		return (this.getWindow() == null || this.getWindow().isDisposed());
	}
	
	public UIWindow getWindow() {
		return this.window;
	}
	
	public void loadTitle() {
		this.loadTitleProcess.process();
	}
	
	public void loadTitleInCurrentThread() {
		if(!this.isDisposed()) {
			String titleLayout = TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.WINDOW_TITLE);
			String title = TGExpressionResolver.getInstance(this.context).resolve(titleLayout);
			
			this.window.setText(title != null ? title : TGApplication.NAME);
		}
	}
	
	public void loadIcons() {
		if(!this.isDisposed()) {
			this.getWindow().setImage(TGIconManager.getInstance(this.context).getAppIcon());
			this.getWindow().layout();
		}
	}
	
	public void createSyncProcesses() {
		this.loadTitleProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				TGWindow.this.loadTitleInCurrentThread();
			}
		});
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
			}
		});
	}
	
	public static TGWindow getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGWindow.class.getName(), new TGSingletonFactory<TGWindow>() {
			public TGWindow createInstance(TGContext context) {
				return new TGWindow(context);
			}
		});
	}
}
