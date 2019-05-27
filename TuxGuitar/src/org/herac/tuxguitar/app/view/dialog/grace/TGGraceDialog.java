package org.herac.tuxguitar.app.view.dialog.grace;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGGraceDialog {
	
	private UISpinner fretSpinner;
	private UICheckBox deadButton;
	private UIRadioButton beforeBeatButton;
	private UIRadioButton onBeatButton;
	private UIRadioButton durationButton1;
	private UIRadioButton durationButton2;
	private UIRadioButton durationButton3;
	private UIRadioButton pppButton;
	private UIRadioButton ppButton;
	private UIRadioButton pButton;
	private UIRadioButton mpButton;
	private UIRadioButton mfButton;
	private UIRadioButton fButton;
	private UIRadioButton ffButton;
	private UIRadioButton fffButton;
	private UIRadioButton noneButton;
	private UIRadioButton slideButton;
	private UIRadioButton bendButton;
	private UIRadioButton hammerButton;
	
	public TGGraceDialog(){
		super();
	}
	
	public void show(final TGViewContext context){
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
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
			dialog.setText(TuxGuitar.getProperty("effects.grace-editor"));
			
			//-----defaults-------------------------------------------------
			boolean dead = false;
			boolean onBeat = false;
			int fret = note.getValue();
			int duration = 2;
			int dynamic = TGVelocities.DEFAULT;
			int transition = TGEffectGrace.TRANSITION_NONE;
			if(note.getEffect().isGrace()){
				dead = note.getEffect().getGrace().isDead();
				fret = note.getEffect().getGrace().getFret();
				onBeat = note.getEffect().getGrace().isOnBeat();
				duration = note.getEffect().getGrace().getDuration();
				dynamic = note.getEffect().getGrace().getDynamic();
				transition = note.getEffect().getGrace().getTransition();
			}
			boolean percussionChannel = TuxGuitar.getInstance().getSongManager().isPercussionChannel(track.getSong(), track.getChannelId());

			//---------------------------------------------------
			//------------------NOTE-----------------------------
			//---------------------------------------------------
			UITableLayout noteLayout = new UITableLayout();
			UIPanel noteGroup = uiFactory.createPanel(dialog, false);
			noteGroup.setLayout(noteLayout);
			dialogLayout.set(noteGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2, 350f, null, null);
			
			UILabel fretLabel = uiFactory.createLabel(noteGroup);
			fretLabel.setText(TuxGuitar.getProperty("fret") + ": ");
			noteLayout.set(fretLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
			
			this.fretSpinner = uiFactory.createSpinner(noteGroup);
			this.fretSpinner.setValue(fret);
			noteLayout.set(this.fretSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.deadButton = uiFactory.createCheckBox(noteGroup);
			this.deadButton.setText(TuxGuitar.getProperty("note.deadnote"));
			this.deadButton.setSelected(dead);
			noteLayout.set(this.deadButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 2);
			
			//---------------------------------------------------
			//------------------POSITION-------------------------
			//---------------------------------------------------
			UITableLayout positionLayout = new UITableLayout();
			UIPanel positionGroup = uiFactory.createPanel(dialog, false);
			positionGroup.setLayout(positionLayout);
			dialogLayout.set(positionGroup, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);
			
			this.beforeBeatButton = uiFactory.createRadioButton(positionGroup);
			this.beforeBeatButton.setText(TuxGuitar.getProperty("effects.grace.before-beat"));
			this.beforeBeatButton.setSelected(!onBeat);
			positionLayout.set(this.beforeBeatButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.onBeatButton = uiFactory.createRadioButton(positionGroup);
			this.onBeatButton.setText(TuxGuitar.getProperty("effects.grace.on-beat"));
			this.onBeatButton.setSelected(onBeat);
			positionLayout.set(this.onBeatButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------DURATION-------------------------
			//---------------------------------------------------
			UITableLayout durationLayout = new UITableLayout();
			UIPanel durationGroup = uiFactory.createPanel(dialog, false);
			durationGroup.setLayout(durationLayout);
			dialogLayout.set(durationGroup, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);
			
			this.durationButton1 = uiFactory.createRadioButton(durationGroup);
			this.durationButton1.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
			this.durationButton1.setSelected(duration == 1);
			durationLayout.set(this.durationButton1, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.durationButton2 = uiFactory.createRadioButton(durationGroup);
			this.durationButton2.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
			this.durationButton2.setSelected(duration == 2);
			durationLayout.set(this.durationButton2, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.durationButton3 = uiFactory.createRadioButton(durationGroup);
			this.durationButton3.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
			this.durationButton3.setSelected(duration == 3);
			durationLayout.set(this.durationButton3, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------DYNAMIC--------------------------
			//---------------------------------------------------
			UITableLayout dynamicLayout = new UITableLayout();
			UIPanel dynamicGroup = uiFactory.createPanel(dialog, false);
			dynamicGroup.setLayout(dynamicLayout);
			dialogLayout.set(dynamicGroup, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.pppButton = uiFactory.createRadioButton(dynamicGroup);
			this.pppButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicPPP());
			this.pppButton.setSelected(dynamic == TGVelocities.PIANO_PIANISSIMO);
			dynamicLayout.set(this.pppButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.mfButton = uiFactory.createRadioButton(dynamicGroup);
			this.mfButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicMF());
			this.mfButton.setSelected(dynamic == TGVelocities.MEZZO_FORTE);
			dynamicLayout.set(this.mfButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.ppButton = uiFactory.createRadioButton(dynamicGroup);
			this.ppButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicPP());
			this.ppButton.setSelected(dynamic == TGVelocities.PIANISSIMO);
			dynamicLayout.set(this.ppButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.fButton = uiFactory.createRadioButton(dynamicGroup);
			this.fButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicF());
			this.fButton.setSelected(dynamic == TGVelocities.FORTE);
			dynamicLayout.set(this.fButton, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.pButton = uiFactory.createRadioButton(dynamicGroup);
			this.pButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicP());
			this.pButton.setSelected(dynamic == TGVelocities.PIANO);
			dynamicLayout.set(this.pButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.ffButton = uiFactory.createRadioButton(dynamicGroup);
			this.ffButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicFF());
			this.ffButton.setSelected(dynamic == TGVelocities.FORTISSIMO);
			dynamicLayout.set(this.ffButton, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.mpButton = uiFactory.createRadioButton(dynamicGroup);
			this.mpButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicMP());
			this.mpButton.setSelected(dynamic == TGVelocities.MEZZO_PIANO);
			dynamicLayout.set(this.mpButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.fffButton = uiFactory.createRadioButton(dynamicGroup);
			this.fffButton.setImage(TuxGuitar.getInstance().getIconManager().getDynamicFFF());
			this.fffButton.setSelected(dynamic == TGVelocities.FORTE_FORTISSIMO);
			dynamicLayout.set(this.fffButton, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------TRANSITION-----------------------
			//---------------------------------------------------
			UITableLayout transitionLayout = new UITableLayout();
			UIPanel transitionGroup = uiFactory.createPanel(dialog, false);
			transitionGroup.setLayout(transitionLayout);
			dialogLayout.set(transitionGroup, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.noneButton = uiFactory.createRadioButton(transitionGroup);
			this.noneButton.setText(TuxGuitar.getProperty("effects.grace.transition-none"));
			this.noneButton.setSelected(transition == TGEffectGrace.TRANSITION_NONE);
			transitionLayout.set(this.noneButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.bendButton = uiFactory.createRadioButton(transitionGroup);
			this.bendButton.setText(TuxGuitar.getProperty("effects.grace.transition-bend"));
			this.bendButton.setSelected(transition == TGEffectGrace.TRANSITION_BEND);
			this.bendButton.setEnabled(!percussionChannel);
			transitionLayout.set(this.bendButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.slideButton = uiFactory.createRadioButton(transitionGroup);
			this.slideButton.setText(TuxGuitar.getProperty("effects.grace.transition-slide"));
			this.slideButton.setSelected(transition == TGEffectGrace.TRANSITION_SLIDE);
			this.slideButton.setEnabled(!percussionChannel);
			transitionLayout.set(this.slideButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			this.hammerButton = uiFactory.createRadioButton(transitionGroup);
			this.hammerButton.setText(TuxGuitar.getProperty("effects.grace.transition-hammer"));
			this.hammerButton.setSelected(transition == TGEffectGrace.TRANSITION_HAMMER);
			this.hammerButton.setEnabled(!percussionChannel);
			transitionLayout.set(this.hammerButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			//---------------------------------------------------
			//------------------BUTTONS--------------------------
			//---------------------------------------------------
			TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
					TGDialogButtons.ok(() -> {
						changeGrace(context.getContext(), measure, beat, string, getGrace());
						dialog.dispose();
					}),
					TGDialogButtons.clean(() -> {
						changeGrace(context.getContext(), measure, beat, string, null);
						dialog.dispose();
					}),
					TGDialogButtons.cancel(dialog::dispose));
			dialogLayout.set(buttons.getControl(), 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false, 1, 2);
			
			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}
	
	public TGEffectGrace getGrace(){
		TGEffectGrace effect = TuxGuitar.getInstance().getSongManager().getFactory().newEffectGrace();
		
		effect.setFret(this.fretSpinner.getValue());
		effect.setDead(this.deadButton.isSelected());
		effect.setOnBeat(this.onBeatButton.isSelected());
		
		//duration
		if(this.durationButton1.isSelected()){
			effect.setDuration(1);
		}else if(this.durationButton2.isSelected()){
			effect.setDuration(2);
		}else if(this.durationButton3.isSelected()){
			effect.setDuration(3);
		}
		//velocity
		if(this.pppButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANO_PIANISSIMO);
		}else if(this.ppButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANISSIMO);
		}else if(this.pButton.isSelected()){
			effect.setDynamic(TGVelocities.PIANO);
		}else if(this.mpButton.isSelected()){
			effect.setDynamic(TGVelocities.MEZZO_PIANO);
		}else if(this.mfButton.isSelected()){
			effect.setDynamic(TGVelocities.MEZZO_FORTE);
		}else if(this.fButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTE);
		}else if(this.ffButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTISSIMO);
		}else if(this.fffButton.isSelected()){
			effect.setDynamic(TGVelocities.FORTE_FORTISSIMO);
		}
		
		//transition
		if(this.noneButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_NONE);
		}else if(this.slideButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_SLIDE);
		}else if(this.bendButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_BEND);
		}else if(this.hammerButton.isSelected()){
			effect.setTransition(TGEffectGrace.TRANSITION_HAMMER);
		}
		
		return effect;
	}
	
	public void changeGrace(TGContext context, TGMeasure measure, TGBeat beat, TGString string, TGEffectGrace effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeGraceNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
