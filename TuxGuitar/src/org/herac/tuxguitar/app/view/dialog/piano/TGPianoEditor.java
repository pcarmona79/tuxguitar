package org.herac.tuxguitar.app.view.dialog.piano;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerManager;
import org.herac.tuxguitar.app.view.component.docked.TGDockedPlayingComponentEditor;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPianoEditor extends TGDockedPlayingComponentEditor<TGPiano> {

	public TGPianoEditor(TGContext context){
		super(context);
	}

	@Override
	public void appendListeners(){
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);
	}

	@Override
	protected void setLayoutControl(UITableLayout uiLayout, UIPanel control, int colIdx) {
		uiLayout.set(control, 1, colIdx, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, null, null, 0f);
	}
	
	@Override
	protected TGPiano createComponentInstance(UIPanel parent) {
		return new TGPiano(this.context, parent);
	}
	
	@Override
	protected void hideComponentInner() {
		TGEditorManager.getInstance(this.context).removeRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).removeBeatViewerListener(this);
	}

	@Override
	protected void showComponentInner() {
		TGEditorManager.getInstance(this.context).addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);
	}
	
	public static TGPianoEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPianoEditor.class.getName(), new TGSingletonFactory<TGPianoEditor>() {
			public TGPianoEditor createInstance(TGContext context) {
				return new TGPianoEditor(context);
			}
		});
	}
}
