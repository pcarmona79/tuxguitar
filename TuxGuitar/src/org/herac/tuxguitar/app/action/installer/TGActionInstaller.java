package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
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
import org.herac.tuxguitar.editor.action.TGActionBase;
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
import org.herac.tuxguitar.util.TGContext;

public class TGActionInstaller {
	
	private TGActionAdapterManager manager;
	private TGActionConfigMap configMap;
	
	public TGActionInstaller(TGActionAdapterManager manager) {
		this.manager = manager;
		this.configMap = new TGActionConfigMap();
	}
	
	public void installDefaultActions(){
		TGContext context = this.manager.getContext();

		installAction(new TGDisposeAction(context));

		//file actions
		installAction(new TGLoadSongAction(context));
		installAction(new TGNewSongAction(context));
		installAction(new TGLoadTemplateAction(context));
		installAction(new TGReadSongAction(context));
		installAction(new TGWriteSongAction(context));
		installAction(new TGWriteFileAction(context));
		installAction(new TGSaveAsFileAction(context));
		installAction(new TGSaveFileAction(context));
		installAction(new TGReadURLAction(context));
		installAction(new TGOpenFileAction(context));
		installAction(new TGImportSongAction(context));
		installAction(new TGExportSongAction(context));
		installAction(new TGCloseDocumentsAction(context));
		installAction(new TGCloseDocumentAction(context));
		installAction(new TGCloseCurrentDocumentAction(context));
		installAction(new TGCloseOtherDocumentsAction(context));
		installAction(new TGCloseAllDocumentsAction(context));
		installAction(new TGExitAction(context));
		installAction(new TGPrintAction(context));
		installAction(new TGPrintPreviewAction(context));
		
		//edit actions
		installAction(new TGUndoAction(context));
		installAction(new TGRedoAction(context));
		installAction(new TGSetMouseModeSelectionAction(context));
		installAction(new TGSetMouseModeEditionAction(context));
		installAction(new TGSetNaturalKeyAction(context));
		installAction(new TGSetVoice1Action(context));
		installAction(new TGSetVoice2Action(context));
		
		//tablature actions
		installAction(new TGMouseClickAction(context));
		installAction(new TGMouseMoveAction(context));
		installAction(new TGMouseExitAction(context));
		installAction(new TGMenuShownAction(context));
		
		//caret actions
		installAction(new TGMoveToAction(context));
		installAction(new TGGoRightAction(context));
		installAction(new TGGoLeftAction(context));
		installAction(new TGGoUpAction(context));
		installAction(new TGGoDownAction(context));

		//selector actions
		installAction(new TGClearSelectionAction(context));
		installAction(new TGExtendSelectionLeftAction(context));
		installAction(new TGExtendSelectionRightAction(context));
		installAction(new TGExtendSelectionPreviousAction(context));
		installAction(new TGExtendSelectionNextAction(context));
		installAction(new TGExtendSelectionFirstAction(context));
		installAction(new TGExtendSelectionLastAction(context));
		installAction(new TGSelectAllAction(context));
		installAction(new TGStartDragSelectionAction(context));
		installAction(new TGUpdateDragSelectionAction(context));

		//song actions
		installAction(new TGCopySongFromAction(context));
		installAction(new TGClearSongAction(context));
		
		//track actions
		installAction(new TGAddNewTrackAction(context));
		installAction(new TGAddTrackAction(context));
		installAction(new TGSetTrackMuteAction(context));
		installAction(new TGSetTrackSoloAction(context));
		installAction(new TGSetTrackVisibleAction(context));
		installAction(new TGChangeTrackMuteAction(context));
		installAction(new TGChangeTrackSoloAction(context));
		installAction(new TGChangeTrackVisibleAction(context));
		installAction(new TGCloneTrackAction(context));
		installAction(new TGGoFirstTrackAction(context));
		installAction(new TGGoLastTrackAction(context));
		installAction(new TGGoNextTrackAction(context));
		installAction(new TGGoPreviousTrackAction(context));
		installAction(new TGGoToTrackAction(context));
		installAction(new TGMoveTrackDownAction(context));
		installAction(new TGMoveTrackUpAction(context));
		installAction(new TGRemoveTrackAction(context));
		installAction(new TGSetTrackInfoAction(context));
		installAction(new TGSetTrackNameAction(context));
		installAction(new TGSetTrackChannelAction(context));
		installAction(new TGSetTrackStringCountAction(context));
		installAction(new TGChangeTrackTuningAction(context));
		installAction(new TGCopyTrackFromAction(context));
		installAction(new TGSetTrackLyricsAction(context));
		installAction(new TGChangeTrackPropertiesAction(context));
		
		//measure actions
		installAction(new TGAddMeasureAction(context));
		installAction(new TGAddMeasureListAction(context));
		installAction(new TGCleanMeasureAction(context));
		installAction(new TGCleanMeasureListAction(context));
		installAction(new TGGoFirstMeasureAction(context));
		installAction(new TGGoLastMeasureAction(context));
		installAction(new TGGoNextMeasureAction(context));
		installAction(new TGGoPreviousMeasureAction(context));
		installAction(new TGRemoveMeasureAction(context));
		installAction(new TGRemoveMeasureRangeAction(context));
		installAction(new TGCopyMeasureFromAction(context));
		installAction(new TGInsertMeasuresAction(context));
		installAction(new TGCopyMeasureAction(context));
		installAction(new TGPasteMeasureAction(context));
		
		//beat actions
		installAction(new TGChangeNoteAction(context));
		installAction(new TGChangeTiedNoteAction(context));
		installAction(new TGChangeVelocityAction(context));
		installAction(new TGCleanBeatAction(context));
		installAction(new TGDecrementNoteSemitoneAction(context));
		installAction(new TGCutNoteAction(context));
		installAction(new TGCopyNoteAction(context));
		installAction(new TGPasteNoteAction(context));
		installAction(new TGDeleteNoteAction(context));
		installAction(new TGDeleteNoteOrRestAction(context));
		installAction(new TGIncrementNoteSemitoneAction(context));
		installAction(new TGInsertRestBeatAction(context));
		installAction(new TGMoveBeatsAction(context));
		installAction(new TGMoveBeatsLeftAction(context));
		installAction(new TGMoveBeatsRightAction(context));
		installAction(new TGRemoveUnusedVoiceAction(context));
		installAction(new TGSetVoiceAutoAction(context));
		installAction(new TGSetVoiceDownAction(context));
		installAction(new TGSetVoiceUpAction(context));
		installAction(new TGShiftNoteDownAction(context));
		installAction(new TGShiftNoteUpAction(context));
		installAction(new TGChangeStrokeAction(context));
		installAction(new TGInsertTextAction(context));
		installAction(new TGRemoveTextAction(context));
		installAction(new TGInsertMixerChangeAction(context));
		installAction(new TGRemoveMixerChangeAction(context));
		installAction(new TGInsertChordAction(context));
		installAction(new TGRemoveChordAction(context));
		for( int i = 0 ; i < 10 ; i ++ ){
			installAction(new TGSetNoteFretNumberAction(context, i));
		}
		
		//effect actions
		installAction(new TGChangeAccentuatedNoteAction(context));
		installAction(new TGChangeBendNoteAction(context));
		installAction(new TGChangeDeadNoteAction(context));
		installAction(new TGChangeFadeInAction(context));
		installAction(new TGChangeFadeOutAction(context));
		installAction(new TGChangeGhostNoteAction(context));
		installAction(new TGChangeGraceNoteAction(context));
		installAction(new TGChangeHammerNoteAction(context));
		installAction(new TGChangeHarmonicNoteAction(context));
		installAction(new TGChangeHeavyAccentuatedNoteAction(context));
		installAction(new TGChangeLetRingAction(context));
		installAction(new TGChangePalmMuteAction(context));
		installAction(new TGChangePoppingAction(context));
		installAction(new TGChangeSlappingAction(context));
		installAction(new TGChangeSlideNoteAction(context));
		installAction(new TGChangeSlideFromLowAction(context));
		installAction(new TGChangeSlideFromHighAction(context));
		installAction(new TGChangeSlideToLowAction(context));
		installAction(new TGChangeSlideToHighAction(context));
		installAction(new TGChangeStaccatoAction(context));
		installAction(new TGChangeTappingAction(context));
		installAction(new TGChangeTremoloBarAction(context));
		installAction(new TGChangeTremoloPickingAction(context));
		installAction(new TGChangeTrillNoteAction(context));
		installAction(new TGChangeVibratoNoteAction(context));
		
		//duration actions
		installAction(new TGSetDurationAction(context));
		installAction(new TGSetWholeDurationAction(context));
		installAction(new TGSetHalfDurationAction(context));
		installAction(new TGSetQuarterDurationAction(context));
		installAction(new TGSetEighthDurationAction(context));
		installAction(new TGSetSixteenthDurationAction(context));
		installAction(new TGSetThirtySecondDurationAction(context));
		installAction(new TGSetSixtyFourthDurationAction(context));
		installAction(new TGSetDivisionTypeDurationAction(context));
		installAction(new TGChangeDivisionTypeDurationAction(context));
		installAction(new TGChangeDottedDurationAction(context));
		installAction(new TGChangeDoubleDottedDurationAction(context));
		installAction(new TGIncrementDurationAction(context));
		installAction(new TGDecrementDurationAction(context));
		
		//composition actions
		installAction(new TGChangeTempoAction(context));
		installAction(new TGChangeTempoRangeAction(context));
		installAction(new TGChangeClefAction(context));
		installAction(new TGChangeTimeSignatureAction(context));
		installAction(new TGChangeKeySignatureAction(context));
		installAction(new TGChangeTripletFeelAction(context));
		installAction(new TGChangeInfoAction(context));
		installAction(new TGRepeatOpenAction(context));
		installAction(new TGRepeatCloseAction(context));
		installAction(new TGRepeatAlternativeAction(context));
		
		//channel actions
		installAction(new TGSetChannelsAction(context));
		installAction(new TGAddChannelAction(context));
		installAction(new TGAddNewChannelAction(context));
		installAction(new TGRemoveChannelAction(context));
		installAction(new TGUpdateChannelAction(context));
		
		//transport actions
		installAction(new TGTransportPlayAction(context));
		installAction(new TGTransportStopAction(context));
		installAction(new TGTransportMetronomeAction(context));
		installAction(new TGTransportCountDownAction(context));
		installAction(new TGTransportModeAction(context));
		installAction(new TGTransportSetLoopSHeaderAction(context));
		installAction(new TGTransportSetLoopEHeaderAction(context));
		installAction(new TGTransportSetLoopAction(context));
		installAction(new TGTransportPlaySelectionAction(context));

		//marker actions
		installAction(new TGUpdateMarkerAction(context));
		installAction(new TGRemoveMarkerAction(context));
		installAction(new TGModifyMarkerAction(context));
		installAction(new TGGoToMarkerAction(context));
		installAction(new TGGoPreviousMarkerAction(context));
		installAction(new TGGoNextMarkerAction(context));
		installAction(new TGGoFirstMarkerAction(context));
		installAction(new TGGoLastMarkerAction(context));
		
		//layout actions
		installAction(new TGSetPageLayoutAction(context));
		installAction(new TGSetLinearLayoutAction(context));
		installAction(new TGChangeShowAllTracksAction(context));
		installAction(new TGSetScoreEnabledAction(context));
		installAction(new TGSetTablatureEnabledAction(context));
		installAction(new TGSetCompactViewAction(context));
		installAction(new TGSetChordNameEnabledAction(context));
		installAction(new TGSetChordDiagramEnabledAction(context));
		installAction(new TGSetLayoutScaleAction(context));
		installAction(new TGSetLayoutScaleIncrementAction(context));
		installAction(new TGSetLayoutScaleDecrementAction(context));
		installAction(new TGSetLayoutScaleResetAction(context));
		
		//tools
		installAction(new TGSelectScaleAction(context));
		installAction(new TGChangePercussionMapAction(context));
		installAction(new TGTransposeAction(context));
		installAction(new TGShowExternalBeatAction(context));
		installAction(new TGHideExternalBeatAction(context));
		
		//system
		installAction(new TGReloadSettingsAction(context));
		installAction(new TGReloadTitleAction(context));
		installAction(new TGReloadSkinAction(context));
		installAction(new TGReloadLanguageAction(context));
		installAction(new TGReloadMidiDevicesAction(context));
		installAction(new TGReloadStylesAction(context));
		installAction(new TGReloadTableSettingsAction(context));
		
		installAction(new TGOpenSettingsEditorAction(context));
		installAction(new TGOpenKeyBindingEditorAction(context));
		installAction(new TGOpenPluginListDialogAction(context));

		//gui actions
		installAction(new TGOpenViewAction(context));
		installAction(new TGToggleViewAction(context));
		installAction(new TGPasteNoteOrMeasureAction(context));
		installAction(new TGOpenSongInfoDialogAction(context));
		installAction(new TGOpenTempoDialogAction(context));
		installAction(new TGOpenClefDialogAction(context));
		installAction(new TGOpenKeySignatureDialogAction(context));
		installAction(new TGOpenTimeSignatureDialogAction(context));
		installAction(new TGOpenTripletFeelDialogAction(context));
		installAction(new TGOpenStrokeUpDialogAction(context));
		installAction(new TGOpenStrokeDownDialogAction(context));
		installAction(new TGOpenBendDialogAction(context));
		installAction(new TGOpenTrillDialogAction(context));
		installAction(new TGOpenTremoloPickingDialogAction(context));
		installAction(new TGOpenTremoloBarDialogAction(context));
		installAction(new TGOpenHarmonicDialogAction(context));
		installAction(new TGOpenGraceDialogAction(context));
		installAction(new TGOpenBeatMoveDialogAction(context));
		installAction(new TGOpenTextDialogAction(context));
		installAction(new TGOpenMixerChangeDialogAction(context));
		installAction(new TGOpenChordDialogAction(context));
		installAction(new TGOpenRepeatCloseDialogAction(context));
		installAction(new TGOpenRepeatAlternativeDialogAction(context));
		installAction(new TGOpenMeasureAddDialogAction(context));
		installAction(new TGOpenMeasureCleanDialogAction(context));
		installAction(new TGOpenMeasureRemoveDialogAction(context));
		installAction(new TGOpenMeasureCopyDialogAction(context));
		installAction(new TGOpenMeasurePasteDialogAction(context));
		installAction(new TGOpenTrackTuningDialogAction(context));
		installAction(new TGOpenTrackPropertiesDialogAction(context));
		installAction(new TGOpenScaleDialogAction(context));
		installAction(new TGOpenScaleFinderDialogAction(context));
		installAction(new TGAddAndEditNewTrackAction(context));
		installAction(new TGOpenURLAction(context));
		installAction(new TGOpenTransportModeDialogAction(context));
		installAction(new TGOpenMarkerEditorAction(context));
		installAction(new TGOpenTransposeDialogAction(context) );
		
		installAction(new TGToggleFretBoardEditorAction(context));
		installAction(new TGTogglePianoEditorAction(context));
		installAction(new TGToggleDockingToTopAction(context));

		installAction(new TGTogglePercussionEditorAction(context));
		installAction(new TGToggleMatrixEditorAction(context));
		installAction(new TGToggleLyricEditorAction(context));
		installAction(new TGToggleChannelsDialogAction(context));
		installAction(new TGToggleBrowserAction(context));
		installAction(new TGToggleMarkerListAction(context));
		installAction(new TGToggleMenuBarAction(context));
		installAction(new TGToggleMainToolbarAction(context));
		installAction(new TGToggleEditToolbarAction(context));
		installAction(new TGToggleTableViewerAction(context));

		installAction(new TGOpenDocumentationDialogAction(context));
		installAction(new TGOpenAboutDialogAction(context));
	}
	
	public void installAction(TGActionBase action) {
		String actionId = action.getName();
		
		TGActionManager.getInstance(this.manager.getContext()).mapAction(actionId, action);
		TGActionConfig config = this.configMap.get(actionId);
		if( config != null ) {
			if( config.isShortcutAvailable() ) {
				this.manager.getKeyBindingActionIds().addActionId(actionId);
			}
			if( config.isDisableOnPlaying() ) {
				this.manager.getDisableOnPlayInterceptor().addActionId(actionId);
			}
			if( config.isStopTransport() ) {
				this.manager.getStopTransportInterceptor().addActionId(actionId);
			}
			if( config.isSyncThread() ) {
				this.manager.getSyncThreadInterceptor().addActionId(actionId);
			}
			if( config.isUnsavedInterceptor()) {
				this.manager.getUnsavedDocumentInterceptor().addActionId(actionId);
			}
			if( config.isDocumentModifier() ) {
				this.manager.getDocumentModifierListener().addActionId(actionId);
			}
			if( config.isLockableAction() ) {
				this.manager.getLockableActionListener().addActionId(actionId);
			}
			
			this.manager.getUpdatableActionListener().getControllers().set(actionId, config.getUpdateController());
			this.manager.getUndoableActionListener().getControllers().set(actionId, config.getUndoableController());
		}
	}
}
