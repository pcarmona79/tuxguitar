package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTimeSignatureAction extends TGActionBase {
	
	public static final String NAME = "action.composition.change-time-signature";
	
	public static final String ATTRIBUTE_HEADER_END = "measureHeaderEnd";
	public static final String ATTRIBUTE_TRUNCATE_OR_EXTEND = "truncateOrExtend";

	public TGChangeTimeSignatureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader start = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
		TGMeasureHeader end = ((TGMeasureHeader) context.getAttribute(ATTRIBUTE_HEADER_END));
		TGTimeSignature timeSignature = ((TGTimeSignature) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE));
		boolean truncateOrExtend = ((Boolean) context.getAttribute(ATTRIBUTE_TRUNCATE_OR_EXTEND)).booleanValue();

		songManager.changeTimeSignature(song, start, end, timeSignature, truncateOrExtend);
	}
}
