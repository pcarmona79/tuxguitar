package org.herac.tuxguitar.app.view.component.tabfolder;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.icons.TGSkinEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TGControl;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.app.view.widgets.tabwidget.TGTabLabelItem;
import org.herac.tuxguitar.app.view.widgets.tabwidget.TGTabPopup;
import org.herac.tuxguitar.app.view.widgets.tabwidget.TGTabWidget;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import java.util.List;

public class TGTabFolder implements TGEventListener {
	
	private TGContext context;
	private TGSyncProcessLocked updateDocumentProcess;
	private TGSyncProcessLocked updateSelectionProcess;

	private UIPanel container;
	private TGTabWidget tabs;
	private UIToolBar toolbar;
	private UIToolActionItem newSong;
	private UIToolActionItem openSong;
	private TGControl control;
	private boolean ignoreEvents;
	private boolean currentUnsaved;
	private TGSyncProcess loadPropertiesProcess;
	private TGSyncProcessLocked loadIconsProcess;

	public TGTabFolder(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
		this.appendListeners();
	}
	
	public UIPanel getControl(){
		return this.container;
	}
	
	public boolean isDisposed() {
		return (this.container == null || this.container.isDisposed());
	}

	public void dispose() {
		if (!this.isDisposed()) {
			this.tabs.dispose();
			this.container.dispose();
		}
	}

	private void appendListeners(){
		TGEditorManager tgEditorManager = TGEditorManager.getInstance(this.context);
		tgEditorManager.addUpdateListener(this);
		tgEditorManager.addRedrawListener(new TGTabEventListener(this.context));
	}
	
	public void init(UIContainer parent) {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();

		this.container = factory.createPanel(parent, false);
		UITableLayout layout = new UITableLayout(0f);
		this.container.setLayout(layout);
		this.tabs = new TGTabWidget(factory, this.container);
		this.tabs.addSelectionListener(event -> onTabItemSelected());
		this.tabs.addTabCloseListener(event -> {
			TGDocument document = event.getItem().getData(TGDocument.class.getName());
			if( document != null ) {
				closeDocument(document);
			}
		});
		this.tabs.getControl().addFocusGainedListener(event -> updateFocus());
		layout.set(this.tabs.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);

		this.toolbar = factory.createHorizontalToolBar(this.tabs.getControl());
		this.tabs.packLeft(this.toolbar);

		this.newSong = this.toolbar.createActionItem();
		this.newSong.addSelectionListener(new TGActionProcessorListener(this.context, TGNewSongAction.NAME));

		this.openSong = this.toolbar.createActionItem();
		this.openSong.addSelectionListener(new TGActionProcessorListener(this.context, TGOpenFileAction.NAME));

		this.control = new TGControl(this.context, this.container);
		layout.set(this.control.getContainer(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		this.loadIcons();
		this.loadProperties();
		this.updateDocument();
	}
	
	public void updateFocus() {
		if(!this.isDisposed()) {
			this.control.setFocus();
		}
	}
	
	private void updateDocument() {
		this.updateTabItems();
		this.updateFocus();
	}

	private void updateSelection() {
		if(!this.isDisposed()) {
			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			TGDocument currentDocument = documentManager.findCurrentDocument();
			if( currentDocument.isUnsaved() != this.currentUnsaved || documentManager.countDocuments() != this.tabs.getItemCount() ) {
				this.updateTabItems();
			}
			else {
				this.ignoreEvents = true;
				
				int index = documentManager.findCurrentDocumentIndex();
				this.tabs.setSelectedIndex(index);

				this.ignoreEvents = false;
			}
		}
	}
	
	private void updateTabItems() {
		if(!this.isDisposed()) {
			this.ignoreEvents = true;

			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			TGDocument currentDocument = documentManager.findCurrentDocument();
			List<TGDocument> documents = documentManager.getDocuments();
			while (documents.size() < this.tabs.getItemCount()) {
				TGTabLabelItem item = (TGTabLabelItem) this.tabs.getItem(this.tabs.getItemCount() - 1);
				item.setData(TGDocument.class.getName(), null);
			    this.tabs.removeItem(item);
            }
			for(int i = 0 ; i < documents.size(); i ++) {
				TGDocument document = documents.get(i);
				TGTabLabelItem item;
				if (i < this.tabs.getItemCount()) {
				    item = (TGTabLabelItem) this.tabs.getItem(i);
					TGTabPopup popup = item.getData(TGTabPopup.class.getName());
					popup.update();
				} else {
					item = this.tabs.createLabelTab();
					updateItem(item, document);
				}
				if (item.getData(TGDocument.class.getName()) != document) {
					item.getPopupMenu().dispose();
					updateItem(item, document);
				} else {
					item.setText(this.createTabItemLabel(document));
				}
				if( currentDocument != null && currentDocument.equals(document) ) {
					this.tabs.setSelectedIndex(i);
				}
			}
			this.tabs.getControl().layout();
			this.currentUnsaved = currentDocument != null && currentDocument.isUnsaved();
			this.ignoreEvents = false;
		}
	}

	private void updateItem(TGTabLabelItem item, TGDocument document) {
		UIFactory factory = TGApplication.getInstance(this.context).getFactory();

		item.setData(TGDocument.class.getName(), document);
		item.setText(this.createTabItemLabel(document));

		TGTabPopup popup = new TGTabPopup(this.context, factory, document);
		item.setPopupMenu(popup.getMenu());
		item.setData(TGTabPopup.class.getName(), popup);
	}

	public TGControl getInnerControl() {
	    return this.control;
	}
	
	private String createTabItemLabel(TGDocument document) {
		StringBuffer sb = new StringBuffer();
		if( document.isUnsaved() ) {
			sb.append("*");
		}
		sb.append(TGDocumentListManager.getInstance(this.context).getDocumentName(document));
		
		return sb.toString();
	}

	private void loadIcons() {
		TGIconManager iconManager = TGIconManager.getInstance(this.context);
		this.tabs.loadIcons();
		this.newSong.setImage(iconManager.getFileNew());
		this.openSong.setImage(iconManager.getFileOpen());
	}

	private void loadProperties() {
		this.tabs.loadProperties();
		for (int i = 0; i < this.tabs.getItemCount(); i++) {
			TGTabPopup popup = this.tabs.getItem(i).getData(TGTabPopup.class.getName());
			popup.loadProperties();
		}
		this.newSong.setToolTipText(TuxGuitar.getProperty("file.new"));
		this.openSong.setToolTipText(TuxGuitar.getProperty("file.open"));
	}
	
	private void createSyncProcesses() {
		this.updateDocumentProcess = new TGSyncProcessLocked(this.context, this::updateDocument);
		this.updateSelectionProcess = new TGSyncProcessLocked(this.context, this::updateSelection);
		this.loadPropertiesProcess = new TGSyncProcess(this.context, this::loadProperties);
		this.loadIconsProcess = new TGSyncProcessLocked(this.context, this::loadIcons);
	}
	
	private void onTabItemSelected() {
		if(!this.ignoreEvents) {
			if( MidiPlayer.getInstance(this.context).isRunning() ) {
				this.updateSelection();
			} else {
				this.loadSelectedDocument();
			}
			this.updateFocus();
		}
	}
	
	private void loadSelectedDocument() {
		if(!this.isDisposed()) {
			TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
			List<TGDocument> documents = documentManager.getDocuments();
			int index = this.tabs.getSelectedIndex();
			if( index >= 0 && index < documents.size() ) {
				TGDocument document = documents.get(index);
				TGDocument currentDocument = documentManager.findCurrentDocument();
				if( currentDocument == null || !currentDocument.equals(document) ) {
					this.loadDocument(document);
				}
			}
		}
	}
	
	private void loadDocument(TGDocument document) {
		if(!this.isDisposed()) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGLoadSongAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, document.getSong());
			tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, document.isUnwanted());
			tgActionProcessor.process();
		}
	}
	
	private void closeDocument(TGDocument document) {
		if(!this.isDisposed()) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGCloseDocumentAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT, document);
			tgActionProcessor.process();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE);
			if( type == TGUpdateEvent.SELECTION ){
				this.updateSelectionProcess.process();
			} 
			else if( type == TGUpdateEvent.SONG_LOADED || type == TGUpdateEvent.SONG_SAVED ){
				this.updateDocumentProcess.process();
			} 
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
	}

	public static TGTabFolder getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTabFolder.class.getName(), TGTabFolder::new);
	}
}
