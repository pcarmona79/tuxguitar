package org.herac.tuxguitar.app.view.component.tabfolder;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.file.*;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

import java.util.List;

public class TGTabPopup {
    private final TGContext context;
    private final TGDocument document;

    private final UIPopupMenu menu;
    private final UIMenuActionItem close;
    private final UIMenuActionItem closeOthers;
    private final UIMenuActionItem closeAll;
    private final UIMenuActionItem closeLeft;
    private final UIMenuActionItem closeRight;

    public TGTabPopup(TGContext context, UIFactory factory, TGDocument document) {
        this.context = context;
        this.document = document;
        UIWindow window = TGWindow.getInstance(context).getWindow();
        this.menu = factory.createPopupMenu(window);

        this.close = this.menu.createActionItem();
        this.close.addSelectionListener(this.createActionProcessor(TGCloseDocumentAction.NAME));
        //--CLOSE OTHERS--
        this.closeOthers = this.menu.createActionItem();
        this.closeOthers.addSelectionListener(this.createActionProcessor(TGCloseAllButOneDocumentAction.NAME));
        //--CLOSE ALL--
        this.closeAll = this.menu.createActionItem();
        this.closeAll.addSelectionListener(this.createActionProcessor(TGCloseAllDocumentsAction.NAME));
        //--CLOSE LEFT--
        this.closeLeft = this.menu.createActionItem();
        this.closeLeft.addSelectionListener(this.createActionProcessor(TGCloseLeftDocumentsAction.NAME));
        //--CLOSE LEFT--
        this.closeRight = this.menu.createActionItem();
        this.closeRight.addSelectionListener(this.createActionProcessor(TGCloseRightDocumentsAction.NAME));

        this.loadProperties();
        this.update();
    }

    private UISelectionListener createActionProcessor(String name) {
        TGActionProcessorListener processor = new TGActionProcessorListener(this.context, name);
        processor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT, this.document);
        return processor;
    }

    public UIPopupMenu getMenu() {
        return this.menu;
    }

    public void loadProperties() {
        this.close.setText(TuxGuitar.getProperty("file.close"));
        this.closeOthers.setText(TuxGuitar.getProperty("file.close-others"));
        this.closeAll.setText(TuxGuitar.getProperty("file.close-all"));
        this.closeLeft.setText(TuxGuitar.getProperty("file.close-left"));
        this.closeRight.setText(TuxGuitar.getProperty("file.close-right"));
    }

    public void update() {
        TGDocumentListManager documentManager = TGDocumentListManager.getInstance(this.context);
        List<TGDocument> documents = documentManager.getDocuments();
        int index = documents.indexOf(this.document);

        this.closeOthers.setEnabled(documents.size() > 1);
        this.closeLeft.setEnabled(index > 0);
        this.closeRight.setEnabled(index < documents.size() - 1);
    }
}
