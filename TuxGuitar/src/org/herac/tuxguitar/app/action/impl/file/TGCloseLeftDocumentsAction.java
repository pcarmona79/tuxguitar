package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGCloseLeftDocumentsAction extends TGActionBase {

	public static final String NAME = "action.file.close-left";

	public TGCloseLeftDocumentsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGDocument current = context.getAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT);
		List<TGDocument> documents = new ArrayList<TGDocument>(TGDocumentListManager.getInstance(getContext()).getDocuments());
		int index = documents.indexOf(current);
        while (!documents.isEmpty() && index < documents.size()) {
        	documents.remove(documents.size() - 1);
        }
		context.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENTS, documents);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGCloseDocumentsAction.NAME, context);
	}
}