package org.herac.tuxguitar.app.view.dialog.tremolopicking;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTremoloPickingDialog {
	
	private UIRadioButton thirtySecondButton;
	private UIRadioButton sixTeenthButton;
	private UIRadioButton eighthButton;
	
	public TGTremoloPickingDialog(){
		super();
	}
	
	public void show(final TGViewContext context){
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		final TGNote note = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		if( measure != null && beat != null && note != null && string != null ) {
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
			
			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("effects.tremolo-picking-editor"));
			
			//-----defaults-------------------------------------------------
			int duration = TGDuration.SIXTEENTH;
			if(note.getEffect().isTremoloPicking()){
				duration = note.getEffect().getTremoloPicking().getDuration().getValue();
			}
			
			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			UITableLayout durationLayout = new UITableLayout();
			UIPanel durationGroup = uiFactory.createPanel(dialog, false);
			durationGroup.setLayout(durationLayout);
			dialogLayout.set(durationGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.thirtySecondButton = uiFactory.createRadioButton(durationGroup);
			this.thirtySecondButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.thirtySecondButton.setSelected(duration == TGDuration.THIRTY_SECOND);
			durationLayout.set(this.thirtySecondButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.sixTeenthButton = uiFactory.createRadioButton(durationGroup);
			this.sixTeenthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.sixTeenthButton.setSelected(duration == TGDuration.SIXTEENTH);
			durationLayout.set(this.sixTeenthButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.eighthButton = uiFactory.createRadioButton(durationGroup);
			this.eighthButton.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
			this.eighthButton.setSelected(duration == TGDuration.EIGHTH);
			durationLayout.set(this.eighthButton, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------

			TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
					TGDialogButtons.ok(() -> {
						changeTremoloPicking(context.getContext(), measure, beat, string, getTremoloPicking());
						dialog.dispose();
					}), TGDialogButtons.clean(() -> {
                        changeTremoloPicking(context.getContext(), measure, beat, string, null);
                        dialog.dispose();
                    }), TGDialogButtons.cancel(dialog::dispose));
			dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
			
			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}
	
	public TGEffectTremoloPicking getTremoloPicking(){
		TGEffectTremoloPicking effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectTremoloPicking();
		if(this.thirtySecondButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.THIRTY_SECOND);
		} else if(this.sixTeenthButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.SIXTEENTH);
		} else if(this.eighthButton.isSelected()) {
			effect.getDuration().setValue(TGDuration.EIGHTH);
		} else {
			return null;
		}
		return effect;
	}
	
	public void changeTremoloPicking(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectTremoloPicking effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTremoloPickingAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
