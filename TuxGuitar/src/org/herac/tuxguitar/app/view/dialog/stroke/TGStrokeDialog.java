package org.herac.tuxguitar.app.view.dialog.stroke;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGStrokeDialog {
	
	public static final int WIDTH = 400;
	
	public static final int HEIGHT = 0;
	
	public static final int STATUS_OK = 1;
	public static final int STATUS_CLEAN = 2;
	public static final int STATUS_CANCEL = 3;
	
	private UIRadioButton duration4;
	private UIRadioButton duration8;
	private UIRadioButton duration16;
	private UIRadioButton duration32;
	private UIRadioButton duration64;
	
	public TGStrokeDialog(){
		super();
	}
	
	public void show(final TGViewContext context){
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final Integer direction = context.getAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("beat.stroke"));
		
		//-----defaults-------------------------------------------------
		int duration = TGDuration.SIXTEENTH;
		if( beat.getStroke().getDirection() != TGStroke.STROKE_NONE ){
			duration = beat.getStroke().getValue();
		}
		
		//---------------------------------------------------
		//------------------DURATION-------------------------
		//---------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.duration64 = uiFactory.createRadioButton(group);
		this.duration64.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.duration64.setSelected(duration == TGDuration.SIXTY_FOURTH);
		groupLayout.set(this.duration64, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.duration32 = uiFactory.createRadioButton(group);
		this.duration32.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.duration32.setSelected(duration == TGDuration.THIRTY_SECOND);
		groupLayout.set(this.duration32, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.duration16 = uiFactory.createRadioButton(group);
		this.duration16.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.duration16.setSelected(duration == TGDuration.SIXTEENTH);
		groupLayout.set(this.duration16, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.duration8 = uiFactory.createRadioButton(group);
		this.duration8.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.duration8.setSelected(duration == TGDuration.EIGHTH);
		groupLayout.set(this.duration8, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.duration4 = uiFactory.createRadioButton(group);
		this.duration4.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.QUARTER));
		this.duration4.setSelected(duration == TGDuration.QUARTER);
		groupLayout.set(this.duration4, 1, 5, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//---------------------------------------------------
		//------------------BUTTONS--------------------------
		//---------------------------------------------------

        TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
                TGDialogButtons.ok(() -> {
					changeStroke(context.getContext(), measure, beat, direction, getSelection());
					dialog.dispose();

                }), TGDialogButtons.clean(() -> {
                    changeStroke(context.getContext(), measure, beat, TGStroke.STROKE_NONE, 0);
                    dialog.dispose();
                }),
                TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public int getSelection(){
		if( this.duration4.isSelected() ){
			return TGDuration.QUARTER;
		}
		if( this.duration8.isSelected() ){
			return TGDuration.EIGHTH;
		}
		if( this.duration16.isSelected() ){
			return TGDuration.SIXTEENTH;
		}
		if( this.duration32.isSelected() ){
			return TGDuration.THIRTY_SECOND;
		}
		if( this.duration64.isSelected() ){
			return TGDuration.SIXTY_FOURTH;
		}
		return 0;
	}
	
	public void changeStroke(TGContext context, TGMeasure measure, TGBeat beat, int direction, int value) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeStrokeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, direction);
		tgActionProcessor.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_VALUE, value);
		tgActionProcessor.process();
	}
}
