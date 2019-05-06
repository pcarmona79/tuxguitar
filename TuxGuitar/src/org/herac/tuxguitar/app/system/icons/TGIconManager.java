package org.herac.tuxguitar.app.system.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGIconManager {
	
	private TGContext context;
	
	private TGIconTheme theme;
	private Map<String, TGIconTheme> themeCache;
	
	private UIImage[] durations;
	private UIImage editUndo;
	private UIImage editRedo;
	private UIImage editVoice1;
	private UIImage editVoice2;
	private UIImage editModeSelection;
	private UIImage editModeEdition;
	private UIImage editModeEditionNotNatural;
	private UIImage layoutPage;
	private UIImage layoutLinear;
	private UIImage layoutMultitrack;
	private UIImage layoutScore;
	private UIImage layoutCompact;
	private UIImage transport;
	private UIImage transportFirst;
	private UIImage transportLast;
	private UIImage transportPrevious;
	private UIImage transportNext;
	private UIImage transportStop;
	private UIImage transportPlay;
	private UIImage transportPause;
	private UIImage transportMetronome;
	private UIImage transportMode;
	private UIImage markerList;
	private UIImage markerAdd;
	private UIImage markerRemove;
	private UIImage markerFirst;
	private UIImage markerLast;
	private UIImage markerPrevious;
	private UIImage markerNext;
	private UIImage aboutDescription;
	private UIImage aboutLicense;
	private UIImage aboutAuthors;
	private UIImage appIcon;
	private UIImage appSplash;
	private UIImage optionMain;
	private UIImage optionStyle;
	private UIImage optionSound;
	private UIImage optionLanguage;
	private UIImage optionSkin;
	private UIImage trackAdd;
	private UIImage trackRemove;
	private UIImage fretboard;
	private UIImage fretboardFirstFret;
	private UIImage fretboardFret;
	private UIImage compositionTimeSignature;
	private UIImage compositionTempo;
	private UIImage compositionClef;
	private UIImage compositionKeySignature;
	private UIImage compositionTripletFeel;
	private UIImage compositionRepeatOpen;
	private UIImage compositionRepeatClose;
	private UIImage compositionRepeatAlternative;
	private UIImage songProperties;
	private UIImage durationDotted;
	private UIImage durationDoubleDotted;
	private UIImage divisionType;
	private UIImage fileNew;
	private UIImage fileOpen;
	private UIImage fileSave;
	private UIImage fileSaveAs;
	private UIImage filePrint;
	private UIImage filePrintPreview;
	private UIImage chord;
	private UIImage text;
	private UIImage noteTied;
	private UIImage instruments;
	private UIImage dynamicPPP;
	private UIImage dynamicPP;
	private UIImage dynamicP;
	private UIImage dynamicMP;
	private UIImage dynamicMF;
	private UIImage dynamicF;
	private UIImage dynamicFF;
	private UIImage dynamicFFF;
	private UIImage effectDead;
	private UIImage effectGhost;
	private UIImage effectAccentuated;
	private UIImage effectHeavyAccentuated;
	private UIImage effectHarmonic;
	private UIImage effectGrace;
	private UIImage effectBend;
	private UIImage effectTremoloBar;
	private UIImage effectSlide;
	private UIImage effectHammer;
	private UIImage effectVibrato;
	private UIImage effectTrill;
	private UIImage effectTremoloPicking;
	private UIImage effectPalmMute;
	private UIImage effectStaccato;
	private UIImage effectTapping;
	private UIImage effectSlapping;
	private UIImage effectPopping;
	private UIImage effectFadeIn;
	private UIImage browserNew;
	private UIImage browserFile;
	private UIImage browserFolder;
	private UIImage browserRoot;
	private UIImage browserBack;
	private UIImage browserRefresh;
	private UIImage arrowUp;
	private UIImage arrowDown;
	private UIImage arrowLeft;
	private UIImage arrowRight;
	private UIImage statusQuestion;
	private UIImage statusError;
	private UIImage statusWarning;
	private UIImage statusInfo;
	private UIImage strokeUp;
	private UIImage strokeDown;
	private UIImage settings;
	private UIImage toolbarEdit;
	private UIImage zoomIn;
	private UIImage zoomOut;
	private UIImage listAdd;
	private UIImage listEdit;
	private UIImage listRemove;
	private UIImage solo;
	private UIImage soloDisabled;
	private UIImage mute;
	private UIImage muteDisabled;
	private UIImage sharp;
	private UIImage flat;

	private TGIconManager(TGContext context){
		this.context = context;
		this.themeCache = new HashMap<String, TGIconTheme>();
		this.loadIcons();
	}
	
	public TGIconTheme findIconTheme(String theme) {
		if( this.themeCache.containsKey(theme) ) {
			return this.themeCache.get(theme);
		}
		
		this.themeCache.put(theme, new TGIconTheme(theme));
		
		return this.findIconTheme(theme);
	}
	
	public String findConfiguredThemeName() {
		return TGSkinManager.getInstance(this.context).getCurrentSkin();
	}
	
	public void loadIcons(){
		this.theme = this.findIconTheme(this.findConfiguredThemeName());
		this.durations = new UIImage[]{
			loadIcon("1"),
			loadIcon("2"),
			loadIcon("4"),
			loadIcon("8"),
			loadIcon("16"),
			loadIcon("32"),
			loadIcon("64")
		};
		this.layoutPage = loadIcon("layout_page");
		this.layoutLinear = loadIcon("layout_linear");
		this.layoutMultitrack = loadIcon("layout_multitrack");
		this.layoutScore = loadIcon("layout_score");
		this.layoutCompact = loadIcon("layout_compact");
		this.fileNew = loadIcon("new");
		this.fileOpen = loadIcon("open");
		this.fileSave = loadIcon("save");
		this.fileSaveAs = loadIcon("save-as");
		this.filePrint = loadIcon("print");
		this.filePrintPreview = loadIcon("print-preview");
		this.editUndo = loadIcon("edit_undo");
		this.editRedo = loadIcon("edit_redo");
		this.editVoice1 = loadIcon("edit_voice_1");
		this.editVoice2 = loadIcon("edit_voice_2");
		this.editModeSelection = loadIcon("edit_mode_selection");
		this.editModeEdition = loadIcon("edit_mode_edition");
		this.editModeEditionNotNatural = loadIcon("edit_mode_edition_no_natural");
		this.appIcon = loadIcon("icon");
		this.appSplash = loadIcon("splash");
		this.aboutDescription = loadIcon("about_description");
		this.aboutLicense = loadIcon("about_license");
		this.aboutAuthors = loadIcon("about_authors");
		this.optionMain = loadIcon("option_view");
		this.optionStyle = loadIcon("option_style");
		this.optionSound = loadIcon("option_sound");
		this.optionSkin = loadIcon("option_skin");
		this.optionLanguage= loadIcon("option_language");
		this.compositionTimeSignature = loadIcon("timesignature");
		this.compositionTempo = loadIcon("tempoicon");
		this.compositionClef = loadIcon("clef");
		this.compositionKeySignature = loadIcon("keysignature");
		this.compositionTripletFeel = loadIcon("tripletfeel");
		this.compositionRepeatOpen = loadIcon("openrepeat");
		this.compositionRepeatClose = loadIcon("closerepeat");
		this.compositionRepeatAlternative = loadIcon("repeat_alternative");
		this.songProperties = loadIcon("song_properties");
		this.trackAdd = loadIcon("track_add");
		this.trackRemove = loadIcon("track_remove");
		this.durationDotted = loadIcon("dotted");
		this.durationDoubleDotted = loadIcon("doubledotted");
		this.divisionType = loadIcon("division-type");
		this.fretboard = loadIcon("fretboard");
		this.fretboardFirstFret = loadIcon("firstfret");
		this.fretboardFret = loadIcon("fret");
		this.chord = loadIcon("chord");
		this.text = loadIcon("text");
		this.noteTied = loadIcon("tiednote");
		this.transport = loadIcon("transport");
		this.transportFirst = loadIcon("transport_first");
		this.transportLast = loadIcon("transport_last");
		this.transportPrevious = loadIcon("transport_previous");
		this.transportNext = loadIcon("transport_next");
		this.transportStop = loadIcon("transport_stop");
		this.transportPlay = loadIcon("transport_play");
		this.transportPause = loadIcon("transport_pause");
		this.transportMetronome = loadIcon("transport_metronome");
		this.transportMode = loadIcon("transport_mode");
		this.markerList = loadIcon("marker_list");
		this.markerAdd = loadIcon("marker_add");
		this.markerRemove = loadIcon("marker_remove");
		this.markerFirst = loadIcon("marker_first");
		this.markerLast = loadIcon("marker_last");
		this.markerPrevious = loadIcon("marker_previous");
		this.markerNext = loadIcon("marker_next");
		this.instruments = loadIcon("mixer");
		this.dynamicPPP = loadIcon("dynamic_ppp");
		this.dynamicPP = loadIcon("dynamic_pp");
		this.dynamicP = loadIcon("dynamic_p");
		this.dynamicMP =loadIcon("dynamic_mp");
		this.dynamicMF = loadIcon("dynamic_mf");
		this.dynamicF = loadIcon("dynamic_f");
		this.dynamicFF = loadIcon("dynamic_ff");
		this.dynamicFFF = loadIcon("dynamic_fff");
		this.effectDead = loadIcon("effect_dead");
		this.effectGhost = loadIcon("effect_ghost");
		this.effectAccentuated = loadIcon("effect_accentuated");
		this.effectHeavyAccentuated = loadIcon("effect_heavy_accentuated");
		this.effectHarmonic = loadIcon("effect_harmonic");
		this.effectGrace = loadIcon("effect_grace");
		this.effectBend = loadIcon("effect_bend");
		this.effectTremoloBar = loadIcon("effect_tremolo_bar");
		this.effectSlide = loadIcon("effect_slide");
		this.effectHammer = loadIcon("effect_hammer");
		this.effectVibrato = loadIcon("effect_vibrato");
		this.effectTrill= loadIcon("effect_trill");
		this.effectTremoloPicking = loadIcon("effect_tremolo_picking");
		this.effectPalmMute= loadIcon("effect_palm_mute");
		this.effectStaccato = loadIcon("effect_staccato");
		this.effectTapping = loadIcon("effect_tapping");
		this.effectSlapping = loadIcon("effect_slapping");
		this.effectPopping = loadIcon("effect_popping");
		this.effectFadeIn = loadIcon("effect_fade_in");
		this.browserNew = loadIcon("browser_new");
		this.browserFile = loadIcon("browser_file");
		this.browserFolder = loadIcon("browser_folder");
		this.browserRoot = loadIcon("browser_root");
		this.browserBack = loadIcon("browser_back");
		this.browserRefresh = loadIcon("browser_refresh");
		this.arrowUp = loadIcon("arrow_up");
		this.arrowDown = loadIcon("arrow_down");
		this.arrowLeft = loadIcon("arrow_left");
		this.arrowRight = loadIcon("arrow_right");
		this.statusQuestion = loadIcon("status_question");
		this.statusError = loadIcon("status_error");
		this.statusWarning = loadIcon("status_warning");
		this.statusInfo = loadIcon("status_info");
		this.strokeUp = loadIcon("stroke_up");
		this.strokeDown = loadIcon("stroke_down");
		this.settings = loadIcon("settings");
		this.toolbarEdit = loadIcon("toolbar_edit");
		this.zoomIn = loadIcon("zoom_in");
		this.zoomOut = loadIcon("zoom_out");
		this.listAdd = loadIcon("list_add");
		this.listEdit = loadIcon("list_edit");
		this.listRemove = loadIcon("list_remove");
		this.solo = loadIcon("solo");
		this.soloDisabled = loadIcon("solo-disabled");
		this.mute = loadIcon("mute");
		this.muteDisabled = loadIcon("mute-disabled");
		this.sharp = loadIcon("sharp");
		this.flat = loadIcon("flat");
	}
	
	private UIImage loadIcon(String name) {
		UIImage image = this.theme.getResource(name);
		if( image == null ) {
			image = TGFileUtils.loadImage(this.context, this.theme.getName(), name);
			
			this.theme.setResource(name, image);
		}
		return image;
	}
	
	public void disposeThemes() {
		List<String> themes = new ArrayList<String>(this.themeCache.keySet());
		for(String theme : themes) {
			this.disposeTheme(this.themeCache.remove(theme));
		}
	}
	
	public void disposeTheme(TGIconTheme theme) {
		List<UIImage> uiImages = new ArrayList<UIImage>(theme.getResources().values());
		for(UIImage uiImage : uiImages) {
			uiImage.dispose();
		}
	}
	
	public void onSkinDisposed(){
		this.disposeThemes();
	}
	
	public void onSkinChange() {
		this.loadIcons();
	}
	
	public UIImage getDuration(int value){
		switch(value){
		case TGDuration.WHOLE:
			return this.durations[0];
		case TGDuration.HALF:
			return this.durations[1];
		case TGDuration.QUARTER:
			return this.durations[2];
		case TGDuration.EIGHTH:
			return this.durations[3];
		case TGDuration.SIXTEENTH:
			return this.durations[4];
		case TGDuration.THIRTY_SECOND:
			return this.durations[5];
		case TGDuration.SIXTY_FOURTH:
			return this.durations[6];
		}
		return null;
	}

	public UIImage getAboutAuthors() {
		return this.aboutAuthors;
	}
	
	public UIImage getAboutDescription() {
		return this.aboutDescription;
	}
	
	public UIImage getAboutLicense() {
		return this.aboutLicense;
	}
	
	public UIImage getAppIcon() {
		return this.appIcon;
	}

	public UIImage getAppSplash() {
		return this.appSplash;
	}
	
	public UIImage getChord() {
		return this.chord;
	}
	
	public UIImage getText() {
		return this.text;
	}
	
	public UIImage getCompositionRepeatClose() {
		return this.compositionRepeatClose;
	}
	
	public UIImage getCompositionRepeatAlternative() {
		return this.compositionRepeatAlternative;
	}
	
	public UIImage getCompositionRepeatOpen() {
		return this.compositionRepeatOpen;
	}
	
	public UIImage getCompositionTempo() {
		return this.compositionTempo;
	}
	
	public UIImage getCompositionTimeSignature() {
		return this.compositionTimeSignature;
	}

	public UIImage getCompositionClef() {
		return this.compositionClef;
	}

	public UIImage getCompositionKeySignature() {
		return this.compositionKeySignature;
	}

	public UIImage getCompositionTripletFeel() {
		return this.compositionTripletFeel;
	}

	public UIImage getDurationDotted() {
		return this.durationDotted;
	}
	
	public UIImage getDurationDoubleDotted() {
		return this.durationDoubleDotted;
	}
	
	public UIImage getDivisionType() {
		return this.divisionType;
	}
	
	public UIImage getDynamicF() {
		return this.dynamicF;
	}
	
	public UIImage getDynamicFF() {
		return this.dynamicFF;
	}
	
	public UIImage getDynamicFFF() {
		return this.dynamicFFF;
	}
	
	public UIImage getDynamicMF() {
		return this.dynamicMF;
	}
	
	public UIImage getDynamicMP() {
		return this.dynamicMP;
	}
	
	public UIImage getDynamicP() {
		return this.dynamicP;
	}
	
	public UIImage getDynamicPP() {
		return this.dynamicPP;
	}
	
	public UIImage getDynamicPPP() {
		return this.dynamicPPP;
	}
	
	public UIImage getEditModeEdition() {
		return this.editModeEdition;
	}
	
	public UIImage getEditModeEditionNotNatural() {
		return this.editModeEditionNotNatural;
	}
	
	public UIImage getEditModeSelection() {
		return this.editModeSelection;
	}
	
	public UIImage getEditRedo() {
		return this.editRedo;
	}
	
	public UIImage getEditUndo() {
		return this.editUndo;
	}
	
	public UIImage getEditVoice1() {
		return this.editVoice1;
	}
	
	public UIImage getEditVoice2() {
		return this.editVoice2;
	}
	
	public UIImage getEffectAccentuated() {
		return this.effectAccentuated;
	}
	
	public UIImage getEffectBend() {
		return this.effectBend;
	}
	
	public UIImage getEffectDead() {
		return this.effectDead;
	}
	
	public UIImage getEffectFadeIn() {
		return this.effectFadeIn;
	}
	
	public UIImage getEffectGhost() {
		return this.effectGhost;
	}
	
	public UIImage getEffectGrace() {
		return this.effectGrace;
	}
	
	public UIImage getEffectHammer() {
		return this.effectHammer;
	}
	
	public UIImage getEffectHarmonic() {
		return this.effectHarmonic;
	}
	
	public UIImage getEffectHeavyAccentuated() {
		return this.effectHeavyAccentuated;
	}
	
	public UIImage getEffectPalmMute() {
		return this.effectPalmMute;
	}
	
	public UIImage getEffectPopping() {
		return this.effectPopping;
	}
	
	public UIImage getEffectSlapping() {
		return this.effectSlapping;
	}
	
	public UIImage getEffectSlide() {
		return this.effectSlide;
	}
	
	public UIImage getEffectStaccato() {
		return this.effectStaccato;
	}
	
	public UIImage getEffectTapping() {
		return this.effectTapping;
	}
	
	public UIImage getEffectTremoloBar() {
		return this.effectTremoloBar;
	}
	
	public UIImage getEffectTremoloPicking() {
		return this.effectTremoloPicking;
	}
	
	public UIImage getEffectTrill() {
		return this.effectTrill;
	}
	
	public UIImage getEffectVibrato() {
		return this.effectVibrato;
	}
	
	public UIImage getFileNew() {
		return this.fileNew;
	}
	
	public UIImage getFileOpen() {
		return this.fileOpen;
	}
	
	public UIImage getFilePrint() {
		return this.filePrint;
	}
	
	public UIImage getFilePrintPreview() {
		return this.filePrintPreview;
	}
	
	public UIImage getFileSave() {
		return this.fileSave;
	}
	
	public UIImage getFileSaveAs() {
		return this.fileSaveAs;
	}
	
	public UIImage getFretboard() {
		return this.fretboard;
	}
	
	public UIImage getFretboardFirstFret() {
		return this.fretboardFirstFret;
	}
	
	public UIImage getFretboardFret() {
		return this.fretboardFret;
	}
	
	public UIImage getLayoutLinear() {
		return this.layoutLinear;
	}
	
	public UIImage getLayoutMultitrack() {
		return this.layoutMultitrack;
	}
	
	public UIImage getLayoutPage() {
		return this.layoutPage;
	}
	
	public UIImage getLayoutScore() {
		return this.layoutScore;
	}
	
	public UIImage getLayoutCompact() {
		return this.layoutCompact;
	}
	
	public UIImage getMarkerAdd() {
		return this.markerAdd;
	}
	
	public UIImage getMarkerFirst() {
		return this.markerFirst;
	}
	
	public UIImage getMarkerLast() {
		return this.markerLast;
	}
	
	public UIImage getMarkerList() {
		return this.markerList;
	}
	
	public UIImage getMarkerNext() {
		return this.markerNext;
	}
	
	public UIImage getMarkerPrevious() {
		return this.markerPrevious;
	}
	
	public UIImage getMarkerRemove() {
		return this.markerRemove;
	}
	
	public UIImage getInstruments() {
		return this.instruments;
	}
	
	public UIImage getNoteTied() {
		return this.noteTied;
	}
	
	public UIImage getOptionLanguage() {
		return this.optionLanguage;
	}
	
	public UIImage getOptionMain() {
		return this.optionMain;
	}
	
	public UIImage getOptionSound() {
		return this.optionSound;
	}
	
	public UIImage getOptionStyle() {
		return this.optionStyle;
	}
	
	public UIImage getOptionSkin() {
		return this.optionSkin;
	}
	
	public UIImage getSongProperties() {
		return this.songProperties;
	}
	
	public UIImage getTrackAdd() {
		return this.trackAdd;
	}
	
	public UIImage getTrackRemove() {
		return this.trackRemove;
	}
	
	public UIImage getTransport() {
		return this.transport;
	}
	
	public UIImage getTransportFirst1() {
		return this.transportFirst;
	}
	
	public UIImage getTransportFirst2() {
		return this.transportFirst;
	}
	
	public UIImage getTransportIconFirst1() {
		return this.transportFirst;
	}
	
	public UIImage getTransportIconFirst2() {
		return this.transportFirst;
	}
	
	public UIImage getTransportIconLast1() {
		return this.transportLast;
	}
	
	public UIImage getTransportIconLast2() {
		return this.transportLast;
	}
	
	public UIImage getTransportIconNext1() {
		return this.transportNext;
	}
	
	public UIImage getTransportIconNext2() {
		return this.transportNext;
	}
	
	public UIImage getTransportIconPause() {
		return this.transportPause;
	}
	
	public UIImage getTransportIconPlay1() {
		return this.transportPlay;
	}
	
	public UIImage getTransportIconPlay2() {
		return this.transportPlay;
	}
	
	public UIImage getTransportIconPrevious1() {
		return this.transportPrevious;
	}
	
	public UIImage getTransportIconPrevious2() {
		return this.transportPrevious;
	}
	
	public UIImage getTransportIconStop1() {
		return this.transportStop;
	}
	
	public UIImage getTransportIconStop2() {
		return this.transportStop;
	}
	
	public UIImage getTransportLast1() {
		return this.transportLast;
	}
	
	public UIImage getTransportLast2() {
		return this.transportLast;
	}
	
	public UIImage getTransportNext1() {
		return this.transportNext;
	}
	
	public UIImage getTransportNext2() {
		return this.transportNext;
	}
	
	public UIImage getTransportPause() {
		return this.transportPause;
	}
	
	public UIImage getTransportPlay1() {
		return this.transportPlay;
	}
	
	public UIImage getTransportPlay2() {
		return this.transportPlay;
	}
	
	public UIImage getTransportPrevious1() {
		return this.transportPrevious;
	}
	
	public UIImage getTransportPrevious2() {
		return this.transportPrevious;
	}
	
	public UIImage getTransportStop1() {
		return this.transportStop;
	}
	
	public UIImage getTransportStop2() {
		return this.transportStop;
	}
	
	public UIImage getTransportMetronome() {
		return this.transportMetronome;
	}
	
	public UIImage getTransportMode() {
		return this.transportMode;
	}
	
	public UIImage getBrowserBack() {
		return this.browserBack;
	}
	
	public UIImage getBrowserFile() {
		return this.browserFile;
	}
	
	public UIImage getBrowserFolder() {
		return this.browserFolder;
	}
	
	public UIImage getBrowserRefresh() {
		return this.browserRefresh;
	}
	
	public UIImage getBrowserRoot() {
		return this.browserRoot;
	}
	
	public UIImage getBrowserNew() {
		return this.browserNew;
	}
	
	public UIImage getStrokeUp() {
		return this.strokeUp;
	}

	public UIImage getStrokeDown() {
		return this.strokeDown;
	}

	public UIImage getSettings() {
		return this.settings;
	}
	
	public UIImage getToolbarEdit() {
		return this.toolbarEdit;
	}
	
	public UIImage getArrowUp() {
		return this.arrowUp;
	}

	public UIImage getArrowDown() {
		return this.arrowDown;
	}

	public UIImage getArrowLeft() {
		return this.arrowLeft;
	}

	public UIImage getArrowRight() {
		return this.arrowRight;
	}

	public UIImage getStatusQuestion() {
		return this.statusQuestion;
	}

	public UIImage getStatusError() {
		return this.statusError;
	}

	public UIImage getStatusWarning() {
		return this.statusWarning;
	}

	public UIImage getStatusInfo() {
		return this.statusInfo;
	}

	public UIImage getListAdd() {
		return this.listAdd;
	}

	public UIImage getListEdit() {
		return this.listEdit;
	}

	public UIImage getListRemove() {
		return listRemove;
	}

	public UIImage getSolo() {
		return solo;
	}

	public UIImage getSoloDisabled() {
		return soloDisabled;
	}

	public UIImage getMute() {
		return mute;
	}

	public UIImage getMuteDisabled() {
		return muteDisabled;
	}
	public UIImage getSharp() {
		return sharp;
	}

	public UIImage getFlat() {
		return flat;
	}

	public UIImage getZoomIn() {
		return zoomIn;
	}

	public UIImage getZoomOut() {
		return zoomOut;
	}

	public static TGIconManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGIconManager.class.getName(), new TGSingletonFactory<TGIconManager>() {
			public TGIconManager createInstance(TGContext context) {
				return new TGIconManager(context);
			}
		});
	}
}
