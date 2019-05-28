package org.herac.tuxguitar.app.document;

import java.net.URI;

import org.herac.tuxguitar.editor.undo.TGUndoableBuffer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGSong;

public class TGDocument {
	
	private URI uri;
	private TGSong song;
	private TGUndoableBuffer undoableBuffer;
	private boolean unsaved;
	private boolean unwanted;

	private int scrollX;
	private int scrollY;
	private TGBeat caretBeat;
	private int caretString;
	private TGBeat selectionStart;
	private TGBeat selectionEnd;

	public TGDocument() {
		super();
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public TGSong getSong() {
		return song;
	}

	public void setSong(TGSong song) {
		this.song = song;
	}

	public TGUndoableBuffer getUndoableBuffer() {
		return undoableBuffer;
	}

	public void setUndoableBuffer(TGUndoableBuffer undoableBuffer) {
		this.undoableBuffer = undoableBuffer;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	public boolean isUnwanted() {
		return unwanted;
	}

	public void setUnwanted(boolean unwanted) {
		this.unwanted = unwanted;
	}

	public int getScrollX() {
		return scrollX;
	}

	public void setScrollX(int scrollX) {
		this.scrollX = scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void setScrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public TGBeat getCaretBeat() {
		return caretBeat;
	}

	public void setCaretBeat(TGBeat caretBeat) {
		this.caretBeat = caretBeat;
	}

	public int getCaretString() {
		return caretString;
	}

	public void setCaretString(int caretString) {
		this.caretString = caretString;
	}

	public TGBeat getSelectionStart() {
		return selectionStart;
	}

	public void setSelectionStart(TGBeat selectionStart) {
		this.selectionStart = selectionStart;
	}

	public TGBeat getSelectionEnd() {
		return selectionEnd;
	}

	public void setSelectionEnd(TGBeat selectionEnd) {
		this.selectionEnd = selectionEnd;
	}
}
