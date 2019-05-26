package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.effect.*;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.util.TGNoteRange;

public class NoteEffectsMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem noteEffectsMenuItem;
	private UIMenuCheckableItem vibrato;
	private UIMenuCheckableItem bend;
	private UIMenuCheckableItem tremoloBar;
	private UIMenuCheckableItem deadNote;
	private UIMenuCheckableItem slide;
 	private UIMenuCheckableItem slideFromLow;
 	private UIMenuCheckableItem slideFromHigh;
 	private UIMenuCheckableItem slideToLow;
 	private UIMenuCheckableItem slideToHigh;
	private UIMenuCheckableItem hammer;
	private UIMenuCheckableItem ghostNote;
	private UIMenuCheckableItem accentuatedNote;
	private UIMenuCheckableItem heavyAccentuatedNote;
	private UIMenuCheckableItem letRing;
	private UIMenuCheckableItem harmonicNote;
	private UIMenuCheckableItem graceNote;
	private UIMenuCheckableItem trill;
	private UIMenuCheckableItem tremoloPicking;
	private UIMenuCheckableItem palmMute;
	private UIMenuCheckableItem staccato;
	private UIMenuCheckableItem tapping;
	private UIMenuCheckableItem slapping;
	private UIMenuCheckableItem popping;
	private UIMenuCheckableItem fadeIn;
 	private UIMenuCheckableItem fadeOut;

	public NoteEffectsMenuItem(UIMenuSubMenuItem noteEffectsMenuItem) {
		this.noteEffectsMenuItem = noteEffectsMenuItem;
	}
	
	public NoteEffectsMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public UIMenuSubMenuItem getMenuItem() {
		return this.noteEffectsMenuItem;
	}

	public void showItems(){
		//--VIBRATO--
		this.vibrato = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.vibrato.addSelectionListener(this.createActionProcessor(TGChangeVibratoNoteAction.NAME));
		
		//--BEND--
		this.bend = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.bend.addSelectionListener(this.createActionProcessor(TGOpenBendDialogAction.NAME));
		
		//--BEND--
		this.tremoloBar = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tremoloBar.addSelectionListener(this.createActionProcessor(TGOpenTremoloBarDialogAction.NAME));
		
		//--SLIDE--
		this.slide = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slide.addSelectionListener(this.createActionProcessor(TGChangeSlideNoteAction.NAME));

		this.slideFromLow = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slideFromLow.addSelectionListener(this.createActionProcessor(TGChangeSlideFromLowAction.NAME));

		this.slideFromHigh = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slideFromHigh.addSelectionListener(this.createActionProcessor(TGChangeSlideFromHighAction.NAME));

		this.slideToLow = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slideToLow.addSelectionListener(this.createActionProcessor(TGChangeSlideToLowAction.NAME));

		this.slideToHigh = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slideToHigh.addSelectionListener(this.createActionProcessor(TGChangeSlideToHighAction.NAME));

		//--DEAD NOTE--
		this.deadNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.deadNote.addSelectionListener(this.createActionProcessor(TGChangeDeadNoteAction.NAME));
		
		//--HAMMER--
		this.hammer = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.hammer.addSelectionListener(this.createActionProcessor(TGChangeHammerNoteAction.NAME));
		
		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();
		
		//--GHOST NOTE--
		this.ghostNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.ghostNote.addSelectionListener(this.createActionProcessor(TGChangeGhostNoteAction.NAME));
		
		//--ACCENTUATED NOTE--
		this.accentuatedNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.accentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME));
		
		//--HEAVY ACCENTUATED NOTE--
		this.heavyAccentuatedNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.heavyAccentuatedNote.addSelectionListener(this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME));
		
		//--LET RING--
		this.letRing = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.letRing.addSelectionListener(this.createActionProcessor(TGChangeLetRingAction.NAME));
		
		//--HARMONIC NOTE--
		this.harmonicNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.harmonicNote.addSelectionListener(this.createActionProcessor(TGOpenHarmonicDialogAction.NAME));
		
		//--GRACE NOTE--
		this.graceNote = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.graceNote.addSelectionListener(this.createActionProcessor(TGOpenGraceDialogAction.NAME));
		
		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();
		
		//--TRILL--
		this.trill = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.trill.addSelectionListener(this.createActionProcessor(TGOpenTrillDialogAction.NAME));
		
		//--TREMOLO PICKING--
		this.tremoloPicking = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tremoloPicking.addSelectionListener(this.createActionProcessor(TGOpenTremoloPickingDialogAction.NAME));
		
		//--PALM MUTE--
		this.palmMute = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.palmMute.addSelectionListener(this.createActionProcessor(TGChangePalmMuteAction.NAME));
		
		//--STACCATO
		this.staccato = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.staccato.addSelectionListener(this.createActionProcessor(TGChangeStaccatoAction.NAME));
		
		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();
		
		//--TAPPING
		this.tapping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.tapping.addSelectionListener(this.createActionProcessor(TGChangeTappingAction.NAME));
		
		//--SLAPPING
		this.slapping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.slapping.addSelectionListener(this.createActionProcessor(TGChangeSlappingAction.NAME));
		
		//--POPPING
		this.popping = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.popping.addSelectionListener(this.createActionProcessor(TGChangePoppingAction.NAME));
		
		//--SEPARATOR--
		this.noteEffectsMenuItem.getMenu().createSeparator();
		
		//--FADE IN
		this.fadeIn = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.fadeIn.addSelectionListener(this.createActionProcessor(TGChangeFadeInAction.NAME));

		//--FADE OUT
		this.fadeOut = this.noteEffectsMenuItem.getMenu().createCheckItem();
		this.fadeOut.addSelectionListener(this.createActionProcessor(TGChangeFadeOutAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGNoteRange range = tablature.getCurrentNoteRange();
		TGTrack track = tablature.getCaret().getTrack();
		boolean percussion = tablature.getSongManager().isPercussionChannel(track.getSong(), track.getChannelId());

		this.vibrato.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isVibrato()));
		this.vibrato.setEnabled(!running && !percussion && !range.isEmpty());
		this.bend.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isBend()));
		this.bend.setEnabled(!running && !percussion && !range.isEmpty());
		this.tremoloBar.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isTremoloBar()));
		this.tremoloBar.setEnabled(!running && !percussion && !range.isEmpty());
		this.deadNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isDeadNote()));
		this.deadNote.setEnabled(!running && !range.isEmpty());
		this.slide.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlide()));
		this.slide.setEnabled(!running && !percussion && !range.isEmpty());
		this.slideFromLow.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlideFromLow()));
		this.slideFromLow.setEnabled(!running && !percussion && !range.isEmpty());
		this.slideFromHigh.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlideFromHigh()));
		this.slideFromHigh.setEnabled(!running && !percussion && !range.isEmpty());
		this.slideToLow.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlideToLow()));
		this.slideToLow.setEnabled(!running && !percussion && !range.isEmpty());
		this.slideToHigh.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlideToHigh()));
		this.slideToHigh.setEnabled(!running && !percussion && !range.isEmpty());
		this.hammer.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isHammer()));
		this.hammer.setEnabled(!running && !percussion && !range.isEmpty());
		this.ghostNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isGhostNote()));
		this.ghostNote.setEnabled(!running && !range.isEmpty());
		this.accentuatedNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isAccentuatedNote()));
		this.accentuatedNote.setEnabled(!running && !range.isEmpty());
		this.heavyAccentuatedNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isHeavyAccentuatedNote()));
		this.heavyAccentuatedNote.setEnabled(!running && !range.isEmpty());
		this.letRing.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isLetRing()));
		this.letRing.setEnabled(!running && !range.isEmpty());
		this.harmonicNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isHarmonic()));
		this.harmonicNote.setEnabled(!running && !percussion && !range.isEmpty());
		this.graceNote.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isGrace()));
		this.graceNote.setEnabled(!running && !range.isEmpty());
		this.trill.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isTrill()));
		this.trill.setEnabled(!running && !percussion && !range.isEmpty());
		this.tremoloPicking.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isTremoloPicking()));
		this.tremoloPicking.setEnabled(!running && !range.isEmpty());
		this.palmMute.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isPalmMute()));
		this.palmMute.setEnabled(!running && !range.isEmpty());
		this.staccato.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isStaccato()));
		this.staccato.setEnabled(!running && !range.isEmpty());
		this.tapping.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isTapping()));
		this.tapping.setEnabled(!running && !percussion && !range.isEmpty());
		this.slapping.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isSlapping()));
		this.slapping.setEnabled(!running && !percussion && !range.isEmpty());
		this.popping.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isPopping()));
		this.popping.setEnabled(!running && !percussion && !range.isEmpty());
		this.fadeIn.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isFadeIn()));
		this.fadeIn.setEnabled(!running && !range.isEmpty());
		this.fadeOut.setChecked(!range.isEmpty() && range.getNotes().stream().allMatch(n -> n.getEffect().isFadeOut()));
		this.fadeOut.setEnabled(!running && !range.isEmpty());
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.noteEffectsMenuItem, "effects", null);
		setMenuItemTextAndAccelerator(this.vibrato, "effects.vibrato", TGChangeVibratoNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.bend, "effects.bend", TGOpenBendDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tremoloBar, "effects.tremolo-bar", TGOpenTremoloBarDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.deadNote, "effects.deadnote", TGChangeDeadNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.slide, "effects.slide", TGChangeSlideNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.slideFromLow, "effects.slide-fromlow", TGChangeSlideFromLowAction.NAME);
		setMenuItemTextAndAccelerator(this.slideFromHigh, "effects.slide-fromhigh", TGChangeSlideFromHighAction.NAME);
		setMenuItemTextAndAccelerator(this.slideToLow, "effects.slide-tolow", TGChangeSlideToLowAction.NAME);
		setMenuItemTextAndAccelerator(this.slideToHigh, "effects.slide-tohigh", TGChangeSlideToHighAction.NAME);
		setMenuItemTextAndAccelerator(this.hammer, "effects.hammer", TGChangeHammerNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.ghostNote, "effects.ghostnote", TGChangeGhostNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.accentuatedNote, "effects.accentuatednote", TGChangeAccentuatedNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.heavyAccentuatedNote, "effects.heavyaccentuatednote", TGChangeHeavyAccentuatedNoteAction.NAME);
		setMenuItemTextAndAccelerator(this.letRing, "effects.let-ring", TGChangeLetRingAction.NAME);
		setMenuItemTextAndAccelerator(this.harmonicNote, "effects.harmonic", TGOpenHarmonicDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.graceNote, "effects.grace", TGOpenGraceDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.trill, "effects.trill", TGOpenTrillDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.tremoloPicking, "effects.tremolo-picking", TGOpenTremoloPickingDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.palmMute, "effects.palm-mute", TGChangePalmMuteAction.NAME);
		setMenuItemTextAndAccelerator(this.staccato, "effects.staccato", TGChangeStaccatoAction.NAME);
		setMenuItemTextAndAccelerator(this.tapping, "effects.tapping", TGChangeTappingAction.NAME);
		setMenuItemTextAndAccelerator(this.slapping, "effects.slapping", TGChangeSlappingAction.NAME);
		setMenuItemTextAndAccelerator(this.popping, "effects.popping", TGChangePoppingAction.NAME);
		setMenuItemTextAndAccelerator(this.fadeIn, "effects.fade-in", TGChangeFadeInAction.NAME);
		setMenuItemTextAndAccelerator(this.fadeOut, "effects.fade-out", TGChangeFadeOutAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
