package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.editor.action.song.TGCopySongFromAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableTimeSignature extends TGUndoableEditBase {
	
	private int doAction;
	private TGSong song;
	private int tsStart;
	private int tsEnd;
	private boolean tsTruncateOrExtend;
	private TGTimeSignature ts;
	
	private TGUndoableTimeSignature(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeTimeSignature(actionContext, getSong(), this.getMeasureHeader(this.tsStart), this.getMeasureHeader(this.tsEnd), this.ts, this.tsTruncateOrExtend);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.copySongFrom(actionContext, getSong(), this.song);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableTimeSignature startUndo(TGContext context){
		TGFactory factory = new TGFactory();
		TGSong song = getSong(context);
		TGUndoableTimeSignature undoable = new TGUndoableTimeSignature(context);
		undoable.doAction = UNDO_ACTION;
		undoable.song = song.clone(factory);
		return undoable;
	}
	
	public TGUndoableTimeSignature endUndo(TGTimeSignature timeSignature,int start, int end, boolean truncateOrExtend){
		this.ts = timeSignature;
		this.tsStart = start;
		this.tsEnd = end;
		this.tsTruncateOrExtend = truncateOrExtend;
		return this;
	}
	
	public TGMeasureHeader getMeasureHeader(int number) {
	    if (number == -1) {
	    	return null;
		}
		return getSongManager().getMeasureHeader(getSong(), number);
	}
	
	public void changeTimeSignature(TGActionContext context, TGSong song, TGMeasureHeader start, TGMeasureHeader end, TGTimeSignature timeSignature, Boolean truncateOrExtend) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, start);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_HEADER_END, end);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_TRUNCATE_OR_EXTEND, truncateOrExtend);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void copySongFrom(TGActionContext context, TGSong song, TGSong from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopySongFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
