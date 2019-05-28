package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.app.action.TGActionMap;
import org.herac.tuxguitar.app.action.impl.caret.*;
import org.herac.tuxguitar.app.action.impl.composition.*;
import org.herac.tuxguitar.app.action.impl.edit.*;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.impl.effects.*;
import org.herac.tuxguitar.app.action.impl.file.*;
import org.herac.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.*;
import org.herac.tuxguitar.app.action.impl.layout.*;
import org.herac.tuxguitar.app.action.impl.marker.*;
import org.herac.tuxguitar.app.action.impl.measure.*;
import org.herac.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGPasteNoteOrMeasureAction;
import org.herac.tuxguitar.app.action.impl.selector.*;
import org.herac.tuxguitar.app.action.impl.settings.*;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.action.impl.tools.*;
import org.herac.tuxguitar.app.action.impl.track.*;
import org.herac.tuxguitar.app.action.impl.transport.*;
import org.herac.tuxguitar.app.action.impl.view.*;
import org.herac.tuxguitar.app.action.listener.cache.TGUpdateController;
import org.herac.tuxguitar.app.action.listener.cache.controller.*;
import org.herac.tuxguitar.app.undo.impl.marker.TGUndoableMarkerGenericController;
import org.herac.tuxguitar.editor.action.channel.*;
import org.herac.tuxguitar.editor.action.composition.*;
import org.herac.tuxguitar.editor.action.duration.*;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.action.effect.*;
import org.herac.tuxguitar.editor.action.file.*;
import org.herac.tuxguitar.editor.action.measure.*;
import org.herac.tuxguitar.editor.action.note.*;
import org.herac.tuxguitar.editor.action.song.TGClearSongAction;
import org.herac.tuxguitar.editor.action.song.TGCopySongFromAction;
import org.herac.tuxguitar.editor.action.tools.TGTransposeAction;
import org.herac.tuxguitar.editor.action.track.*;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.impl.channel.TGUndoableChannelGenericController;
import org.herac.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import org.herac.tuxguitar.editor.undo.impl.custom.*;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableAddMeasureController;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGenericController;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableRemoveMeasureController;
import org.herac.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController;
import org.herac.tuxguitar.editor.undo.impl.track.*;

public class TGActionConfigMap extends TGActionMap<TGActionConfig> {
	
	public static final int LOCKABLE = 0x01;
	public static final int SYNC_THREAD = 0x02;
	public static final int SHORTCUT = 0x04;
	public static final int DISABLE_ON_PLAY = 0x08;
	public static final int STOP_TRANSPORT = 0x10;
	public static final int SAVE_BEFORE = 0x20;

	private static final TGUpdateController UPDATE_ITEMS_CTL = new TGUpdateItemsController();
	private static final TGUpdateController UPDATE_MEASURE_CTL = new TGUpdateMeasureController();
	private static final TGUpdateController UPDATE_SONG_CTL = new TGUpdateSongController();
	private static final TGUpdateController UPDATE_SONG_LOADED_CTL = new TGUpdateLoadedSongController();
	private static final TGUpdateController UPDATE_SONG_SAVED_CTL = new TGUpdateSavedSongController();
	private static final TGUpdateController UPDATE_CHANNELS_CTL = new TGUpdateChannelsController();
	private static final TGUpdateController UPDATE_NOTE_RANGE_CTL = new TGUpdateNoteRangeController();
	private static final TGUpdateController UPDATE_BEAT_RANGE_CTL = new TGUpdateBeatRangeController();

	private static final TGUndoableActionController UNDOABLE_SONG_GENERIC = new TGUndoableSongGenericController();
	private static final TGUndoableActionController UNDOABLE_MEASURE_GENERIC = new TGUndoableMeasureGenericController();
	private static final TGUndoableActionController UNDOABLE_TRACK_GENERIC = new TGUndoableTrackGenericController();
	private static final TGUndoableActionController UNDOABLE_CHANNEL_GENERIC = new TGUndoableChannelGenericController();
	private static final TGUndoableActionController UNDOABLE_NOTE_RANGE = new TGUndoableNoteRangeController();
	private static final TGUndoableActionController UNDOABLE_BEAT_RANGE_GENERIC = new TGUndoableBeatRangeController();

	public TGActionConfigMap() {
		this.createConfigMap();
	}
	
	public void createConfigMap() {
		//system actions
		this.map(TGDisposeAction.NAME, LOCKABLE | SYNC_THREAD | SAVE_BEFORE);
		
		//file actions
		this.map(TGLoadSongAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_SONG_LOADED_CTL);
		this.map(TGNewSongAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGLoadTemplateAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGReadSongAction.NAME, LOCKABLE);
		this.map(TGWriteSongAction.NAME, LOCKABLE, UPDATE_SONG_SAVED_CTL);
		this.map(TGWriteFileAction.NAME, LOCKABLE, new TGUpdateWrittenFileController());
		this.map(TGSaveAsFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGSaveFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGReadURLAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_ITEMS_CTL);
		this.map(TGOpenFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGImportSongAction.NAME, LOCKABLE);
		this.map(TGExportSongAction.NAME, LOCKABLE);
		this.map(TGCloseDocumentsAction.NAME, LOCKABLE | SAVE_BEFORE, UPDATE_ITEMS_CTL);
		this.map(TGCloseDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseCurrentDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGCloseAllButOneDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseLeftDocumentsAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseRightDocumentsAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseOtherDocumentsAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGCloseAllDocumentsAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGExitAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGPrintAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGPrintPreviewAction.NAME, LOCKABLE | SHORTCUT);
		
		//edit actions
		this.map(TGUndoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRedoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeEditionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetNaturalKeyAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice1Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice2Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		
		//tablature actions
		this.map(TGMouseClickAction.NAME, LOCKABLE);
		this.map(TGMouseMoveAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateItemsOnSuccessController());
		this.map(TGMouseExitAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGMenuShownAction.NAME, LOCKABLE);
		
		//caret actions
		this.map(TGMoveToAction.NAME, LOCKABLE, new TGUpdateTransportPositionController());
		this.map(TGGoRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);

		//selector actions
		this.map(TGExtendSelectionLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionPreviousAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionNextAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionFirstAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionLastAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSelectAllAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGClearSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGStartDragSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGUpdateDragSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY);

		//song actions
		this.map(TGCopySongFromAction.NAME, LOCKABLE, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGClearSongAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		
		//track actions
		this.map(TGAddNewTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGAddTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGSetTrackMuteAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGSetTrackSoloAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGSetTrackVisibleAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGChangeTrackMuteAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGChangeTrackSoloAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGChangeTrackVisibleAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGCloneTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableCloneTrackController());
		this.map(TGGoFirstTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoLastTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoNextTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoPreviousTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoToTrackAction.NAME, LOCKABLE);
		this.map(TGMoveTrackDownAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableMoveTrackDownController());
		this.map(TGMoveTrackUpAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableMoveTrackUpController());
		this.map(TGRemoveTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateRemovedTrackController(), new TGUndoableRemoveTrackController());
		this.map(TGSetTrackInfoAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL, new TGUndoableTrackInfoController());
		this.map(TGSetTrackNameAction.NAME, LOCKABLE);
		this.map(TGSetTrackChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGSetTrackStringCountAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGChangeTrackTuningAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGCopyTrackFromAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGSetTrackLyricsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, new TGUndoableTrackLyricsController());
		this.map(TGChangeTrackPropertiesAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		
		//measure actions
		this.map(TGAddMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedMeasureController(), new TGUndoableAddMeasureController());
		this.map(TGAddMeasureListAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGCleanMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanMeasureListAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGGoFirstMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoLastMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoNextMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoPreviousMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGRemoveMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateRemovedMeasureController(), new TGUndoableRemoveMeasureController());
		this.map(TGRemoveMeasureRangeAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGCopyMeasureFromAction.NAME, LOCKABLE, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertMeasuresAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGCopyMeasureAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGPasteMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		
		//beat actions
		this.map(TGChangeNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTiedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVelocityAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedVelocityController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGDecrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGCutNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGCopyNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGPasteNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGDeleteNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDeleteNoteOrRestAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGIncrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGInsertRestBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGMoveBeatsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGMoveBeatsLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGMoveBeatsRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRemoveUnusedVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceAutoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGSetVoiceDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGSetVoiceUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGShiftNoteDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeStrokeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGInsertTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertMixerChangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveMixerChangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		for( int i = 0 ; i < 10 ; i ++ ){
			this.map(TGSetNoteFretNumberAction.getActionName(i), LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		}
		
		//effect actions
		this.map(TGChangeAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeBendNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeDeadNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeFadeInAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeFadeOutAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeGhostNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeGraceNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeHammerNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeHarmonicNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeHeavyAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeLetRingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangePalmMuteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangePoppingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideFromHighAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideFromLowAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideToHighAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideToLowAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeStaccatoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTremoloBarAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTremoloPickingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTrillNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeVibratoNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		
		//duration actions
		this.map(TGSetDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedDurationController(), UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGSetWholeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetHalfDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetQuarterDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetEighthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetSixteenthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetThirtySecondDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetSixtyFourthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetDivisionTypeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGChangeDivisionTypeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeDoubleDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGIncrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGDecrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeTempoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableTempoController());
		this.map(TGChangeTempoRangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGChangeClefAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableClefController());
		this.map(TGChangeTimeSignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTimeSignatureController());
		this.map(TGChangeKeySignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableKeySignatureController());
		this.map(TGChangeTripletFeelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTripletFeelController());
		
		//composition actions
		this.map(TGChangeInfoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateSongInfoController(), new TGUndoableSongInfoController());
		this.map(TGRepeatOpenAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableOpenRepeatController());
		this.map(TGRepeatCloseAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableCloseRepeatController());
		this.map(TGRepeatAlternativeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableAltRepeatController());
		
		//channel actions
		this.map(TGSetChannelsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddNewChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGRemoveChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGUpdateChannelAction.NAME, LOCKABLE, new TGUpdateModifiedChannelController(), new TGUndoableModifyChannelController());
		
		//transport actions
		this.map(TGTransportPlayAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportStopAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportMetronomeAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportCountDownAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportModeAction.NAME, LOCKABLE);
		this.map(TGTransportSetLoopSHeaderAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportSetLoopEHeaderAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportSetLoopAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportPlaySelectionAction.NAME, LOCKABLE | SHORTCUT);

		//marker actions
		this.map(TGUpdateMarkerAction.NAME, LOCKABLE, new TGUpdateModifiedMarkerController(), new TGUndoableMarkerGenericController());
		this.map(TGRemoveMarkerAction.NAME, LOCKABLE, new TGUpdateModifiedMarkerController(), new TGUndoableMarkerGenericController());
		this.map(TGModifyMarkerAction.NAME, LOCKABLE);
		this.map(TGGoToMarkerAction.NAME, LOCKABLE);
		this.map(TGGoPreviousMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoNextMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoFirstMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoLastMarkerAction.NAME, LOCKABLE | SHORTCUT);
		
		//layout actions
		this.map(TGSetPageLayoutAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetLinearLayoutAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGChangeShowAllTracksAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetScoreEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetTablatureEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetCompactViewAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetChordNameEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetChordDiagramEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		
		this.map(TGSetLayoutScaleAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleIncrementAction.NAME, LOCKABLE | SHORTCUT | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleDecrementAction.NAME, LOCKABLE | SHORTCUT | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleResetAction.NAME, LOCKABLE | SHORTCUT | SYNC_THREAD, UPDATE_SONG_CTL);
		
		//tools
		this.map(TGSelectScaleAction.NAME, LOCKABLE, new TGUpdateCacheController(false));
		this.map(TGChangePercussionMapAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGTransposeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGShowExternalBeatAction.NAME, LOCKABLE);
		this.map(TGHideExternalBeatAction.NAME, LOCKABLE);
		
		//settings
		this.map(TGReloadSettingsAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGReloadTitleAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadSkinAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadLanguageAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadStylesAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadTableSettingsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadMidiDevicesAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGOpenSettingsEditorAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGOpenKeyBindingEditorAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGOpenPluginListDialogAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		
		//gui actions
		this.map(TGOpenViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGToggleViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGPasteNoteOrMeasureAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenSongInfoDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTempoDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenClefDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenKeySignatureDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTimeSignatureDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTripletFeelDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeUpDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeDownDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBendDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrillDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloPickingDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloBarDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenHarmonicDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenGraceDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBeatMoveDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTextDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMixerChangeDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenChordDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatCloseDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatAlternativeDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureAddDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCleanDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureRemoveDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCopyDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasurePasteDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrackTuningDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrackPropertiesDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenScaleDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenScaleFinderDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGAddAndEditNewTrackAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenURLAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenTransportModeDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenMarkerEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenTransposeDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGToggleDockingToTopAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleFretBoardEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGTogglePianoEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleChannelsDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGTogglePercussionEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMatrixEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleLyricEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleBrowserAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMarkerListAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMenuBarAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGToggleMainToolbarAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleEditToolbarAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleTableViewerAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenDocumentationDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenAboutDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
	}
	
	private void map(String actionId, int flags) {
		this.map(actionId, flags, UPDATE_ITEMS_CTL, null);
	}
	
	private void map(String actionId, int flags, TGUpdateController updateController) {
		this.map(actionId, flags, updateController, null);
	}

	private void map(String actionId, int flags, TGUpdateController updateController, TGUndoableActionController undoableController) {
		TGActionConfig tgActionConfig = new TGActionConfig();
		tgActionConfig.setUpdateController(updateController);
		tgActionConfig.setUndoableController(undoableController);
		tgActionConfig.setLockableAction((flags & LOCKABLE) != 0);
		tgActionConfig.setShortcutAvailable((flags & SHORTCUT) != 0);
		tgActionConfig.setDisableOnPlaying((flags & DISABLE_ON_PLAY) != 0);
		tgActionConfig.setStopTransport((flags & STOP_TRANSPORT) != 0);
		tgActionConfig.setSyncThread((flags & SYNC_THREAD) != 0);
		tgActionConfig.setUnsavedInterceptor((flags & SAVE_BEFORE) != 0);
		tgActionConfig.setDocumentModifier(undoableController != null);
		
		this.set(actionId, tgActionConfig);
	}
}
