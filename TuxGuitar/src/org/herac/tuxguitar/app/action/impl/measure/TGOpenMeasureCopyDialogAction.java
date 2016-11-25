package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.dialog.measure.TGMeasureCopyDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenMeasureCopyDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-measure-copy-dialog";
	
	public TGOpenMeasureCopyDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
		TGMeasureCopyDialogController measureCopyDialogController = new TGMeasureCopyDialogController(selector);

		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, measureCopyDialogController);
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
