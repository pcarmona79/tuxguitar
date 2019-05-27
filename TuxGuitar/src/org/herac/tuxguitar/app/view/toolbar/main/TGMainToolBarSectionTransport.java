package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.transport.*;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventException;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.herac.tuxguitar.ui.event.*;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.*;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

import java.util.*;

public class TGMainToolBarSectionTransport extends TGMainToolBarSection implements TGEventListener {
	
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;

	private static final String COLOR_BACKGROUND = "widget.transport.backgroundColor";
	private static final String COLOR_FOREGROUND = "widget.transport.foregroundColor";

	private static final TGSkinnableColor[] SKINNABLE_COLORS = new TGSkinnableColor[] {
			new TGSkinnableColor(COLOR_BACKGROUND, new UIColorModel(0x10, 0x10, 0x10)),
			new TGSkinnableColor(COLOR_FOREGROUND, new UIColorModel(0xf0, 0xf0, 0xf0)),
	};

	private static final float DISPLAY_MARGIN = 2f;

	private TGProcess redrawProcess;
	private TGProcess updateProcess;

	private TreeMap<Long, TGMeasureHeader> headerMap;
	private HashMap<Integer, Double> tempoMap;

	private UIButton first;
	private UIButton previous;
	private UIButton play;
	private UIButton next;
	private UIButton last;
	private UICanvas display;
	private UIToggleButton countDown;
	private UIToggleButton metronome;
	private UIToggleButton loop;
	private UIButton playMode;
	private int status;

	private UIFont displayFont;

	public TGMainToolBarSectionTransport(TGMainToolBar toolBar) {
		super(toolBar);
		this.redrawProcess = new TGSyncProcessLocked(getContext(), () -> display.redraw());
		this.updateProcess = new TGSyncProcessLocked(getContext(), this::updateTempoMap);

		this.headerMap = new TreeMap<>();
		this.tempoMap = new HashMap<>();
	}
	
	public void createSection() {
		this.first = this.createButton();
		this.first.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoFirst());

		this.previous = this.createButton();
		this.previous.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoPrevious());

		this.play = this.createButton();
		this.play.addSelectionListener(this.createActionProcessor(TGTransportPlayAction.NAME));

		this.next = this.createButton();
		this.next.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoNext());

		this.last = this.createButton();
		this.last.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoLast());

		this.display = getFactory().createCanvas(getControl(), false);
		addItem(this.display);
		getLayout().set(this.display, 1, getItems().size(), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 40f, 18f, 1f);
		this.display.addMouseDownListener(new UIMouseDownListener() {
			public void onMouseDown(UIMouseEvent event) {
				if (event.getButton() == 1) {
					moveTransport(event.getPosition().getX());
				}
			}
		});
		this.display.addMouseMoveListener(new UIMouseMoveListener() {
			public void onMouseMove(UIMouseEvent event) {
				if ((event.getState() & UIMouseEvent.BUTTON1) != 0) {
					moveTransport(event.getPosition().getX());
				}
			}
		});
		this.display.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				TGMainToolBarSectionTransport.this.paintDisplay(event.getPainter());
			}
		});
		this.display.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				if (displayFont != null) {
					displayFont.dispose();
				}
			}
		});

		this.metronome = this.createToggleButton();
		this.metronome.addSelectionListener(this.createActionProcessor(TGTransportMetronomeAction.NAME));

		this.countDown = this.createToggleButton();
		this.countDown.addSelectionListener(this.createActionProcessor(TGTransportCountDownAction.NAME));

		this.loop = this.createToggleButton();
		this.loop.addSelectionListener(this.createActionProcessor(TGTransportSetLoopAction.NAME));

		this.playMode = this.createButton();
		this.playMode.addSelectionListener(this.createActionProcessor(TGOpenTransportModeDialogAction.NAME));

		TGColorManager tgColorManager = TGColorManager.getInstance(getToolBar().getContext());
		tgColorManager.appendSkinnableColors(SKINNABLE_COLORS);

		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();

		final TuxGuitar tg = TuxGuitar.getInstance();
		tg.getEditorManager().addRedrawListener(this);
		tg.getEditorManager().addUpdateListener(this);

		getControl().addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				tg.getEditorManager().removeRedrawListener(TGMainToolBarSectionTransport.this);
				tg.getEditorManager().removeUpdateListener(TGMainToolBarSectionTransport.this);
				tg.updateCache(true);
			}
		});
	}

	private void moveTransport(float x) {
	    Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		final TGSongManager songManager = tablature.getSongManager();
		final TGDocumentManager documentManager = TGDocumentManager.getInstance(getContext());

		TGMeasureHeader first = songManager.getFirstMeasureHeader(documentManager.getSong());
		TGMeasureHeader last = songManager.getLastMeasureHeader(documentManager.getSong());
		long minimum = first.getStart();
		long maximum = last.getStart() + last.getLength() - 1;

		long position = Math.round(x / display.getBounds().getWidth() * ((double) (maximum - minimum)) + minimum);

		Caret caret = tablature.getCaret();
		TGTrack track = caret.getTrack();
		TGMeasure measure = tablature.getSongManager().getTrackManager().getMeasureAt(track, position);
		if (measure != null) {

			TGBeat beat = tablature.getSongManager().getMeasureManager().getBeatIn(measure, position);

			if (beat != null) {
				TGActionProcessor action = new TGActionProcessor(getContext(), TGMoveToAction.NAME);
				action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
				action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
				action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
				action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, caret.getSelectedString());
				action.process();
			}
		}
	}

	private void updateTempoMap() {
		final TGDocumentManager documentManager = TGDocumentManager.getInstance(getContext());
		final TGSong song = documentManager.getSong();

		headerMap.clear();
        tempoMap.clear();

        SortedSet<TGMeasureHeader> headers = new TreeSet<>(Comparator.comparingLong(TGMeasureHeader::getStart));
        for (int i = 0; i < song.countMeasureHeaders(); i++) {
        	headers.add(song.getMeasureHeader(i));
		}
		double totalSeconds = 0.;
        for (TGMeasureHeader header : headers) {
			headerMap.put(header.getStart(), header);
			tempoMap.put(header.getNumber(), totalSeconds);
			totalSeconds += header.getLength() / ((double) TGDuration.QUARTER_TIME) * header.getTempo().getInSeconds();
		}
        display.redraw();
	}

	private void paintDisplay(UIPainter painter) {

		final MidiPlayer player = MidiPlayer.getInstance(getContext());
		final Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		final TGDocumentManager documentManager = TGDocumentManager.getInstance(getContext());
		final UISize size = this.display.getBounds().getSize();

		long position = -1;
		if (!player.getLock().isLocked(null)) {
			if (player.isRunning()) {
				position = player.getTickPosition();
			} else {
				position = tablature.getCaret().getSelectedBeat().getStart();
			}
		}

		TGMeasureHeader first = tablature.getSongManager().getFirstMeasureHeader(documentManager.getSong());
		TGMeasureHeader last = tablature.getSongManager().getLastMeasureHeader(documentManager.getSong());
		long minimum = first.getStart();
		long maximum = last.getStart() + last.getLength() - 1;
		float positionPercent = (position - minimum) / (float) (maximum - minimum);

		TGColorManager tgColorManager = TGColorManager.getInstance(getToolBar().getContext());
		UIColor backgroundColor = tgColorManager.getColor(COLOR_BACKGROUND);
		UIColor foregroundColor = tgColorManager.getColor(COLOR_FOREGROUND);
		painter.setBackground(backgroundColor);

		painter.setAntialias(false);
		painter.setAlpha(255);
		painter.initPath(UIPainter.PATH_FILL);
		painter.addRectangle(0f, 0f, size.getWidth(), size.getHeight());
		painter.closePath();

		painter.setBackground(foregroundColor);

		float gradHeight = size.getHeight() - 2;
		for (int y = 1; y <= gradHeight; y++) {
            painter.setAlpha(16 - Math.round((1f - y / gradHeight) * 16f));
			painter.initPath(UIPainter.PATH_FILL);
			painter.addRectangle(1f, y, size.getWidth() - 2f, 1f);
			painter.closePath();
		}
		if (position != -1) {
			painter.setAlpha(192);
			painter.initPath(UIPainter.PATH_FILL);
			painter.addRectangle(1f, size.getHeight() - 3f, (size.getWidth() - 2) * positionPercent, 2f);
			painter.closePath();

			painter.setAlpha(255);
			painter.setAntialias(true);

			Map.Entry<Long, TGMeasureHeader> entry = headerMap.floorEntry(position);
			TGMeasureHeader current = entry != null ? entry.getValue() : first;
			Double seconds = tempoMap.get(current.getNumber());
			if (seconds == null) {
				seconds = 0.;
			}
			seconds += (position - current.getStart()) / ((double) TGDuration.QUARTER_TIME) * current.getTempo().getInSeconds();

			long s = (long) Math.floor(seconds);
			long ms = (long) Math.floor((seconds - s) * 1000);
			String time = String.format("%d:%02d:%02d.%03d", s / 3600, (s % 3600) / 60, (s % 60), ms);

			float hMargin = this.displayFont.getHeight() * .5f;
			painter.setFont(this.displayFont);
			painter.setForeground(foregroundColor);
			painter.drawString(time, hMargin, (size.getHeight() - painter.getFMHeight()) / 2f);

			float newWidth = painter.getFMWidth(time) + hMargin * 2f;
			if (newWidth > size.getWidth()) {
				getLayout().set(this.display, UITableLayout.PACKED_WIDTH, newWidth);
				getControl().layout();
			}
		}
	}
	
	public void updateItems(){
		MidiPlayer player = MidiPlayer.getInstance(this.getToolBar().getContext());
		this.loop.setSelected(player.getMode().isLoop());
	    this.metronome.setSelected(player.isMetronomeEnabled());
		this.countDown.setSelected(player.getCountDown().isEnabled());
		this.loadIcons(false);
		this.display.redraw();
	}
	
	public void loadProperties(){
		this.first.setToolTipText(this.getText("transport.first"));
		this.previous.setToolTipText(this.getText("transport.previous"));
		this.play.setToolTipText(this.getText("transport.start"));
		this.next.setToolTipText(this.getText("transport.next"));
		this.last.setToolTipText(this.getText("transport.last"));
		this.metronome.setToolTipText(this.getText("transport.metronome"));
		this.countDown.setToolTipText(this.getText("transport.count-down"));
		this.loop.setToolTipText(this.getText("transport.simple.play-looped"));
		this.playMode.setToolTipText(this.getText("transport.mode"));
		if (this.displayFont != null) {
			this.displayFont.dispose();
		}
		UIFontModel fontModel = TGConfigManager.getInstance(getContext()).getFontModelConfigValue(TGConfigKeys.FONT_TRANSPORT);
		this.displayFont = getFactory().createFont(fontModel);

		UIImage dummyImage = getFactory().createImage(1f, 1f);
		UIPainter painter = dummyImage.createPainter();

		painter.setFont(this.displayFont);
		getLayout().set(this.display, UITableLayout.PACKED_WIDTH, painter.getFMWidth("0:00:00.000") + DISPLAY_MARGIN * 2f);
		getLayout().set(this.display, UITableLayout.PACKED_HEIGHT, this.displayFont.getHeight() + DISPLAY_MARGIN * 2f);
		getControl().layout();

		painter.dispose();
		dummyImage.dispose();
	}
	
	public void loadIcons(){
		this.loop.setImage(this.getIconManager().getTransportMode());
		this.metronome.setImage(this.getIconManager().getTransportMetronome());
		this.countDown.setImage(this.getIconManager().getTransportCountDown());
		this.playMode.setImage(this.getIconManager().getPlayMode());
		this.loadIcons(true);
	}
	
	public void loadIcons(boolean force){
		int lastStatus = this.status;
		
		MidiPlayer player = MidiPlayer.getInstance(this.getToolBar().getContext());
		if (player.isRunning()) {
			this.status = STATUS_RUNNING;
		} else if (player.isPaused()) {
			this.status = STATUS_PAUSED;
		} else {
			this.status = STATUS_STOPPED;
		}

		if (force || lastStatus != this.status) {
			if (this.status == STATUS_RUNNING) {
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious2());
				this.next.setImage(this.getIconManager().getTransportIconNext2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.play.setImage(this.getIconManager().getTransportIconPause());
				this.play.setToolTipText(this.getText("transport.pause"));
			} else if (this.status == STATUS_PAUSED) {
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious2());
				this.next.setImage(this.getIconManager().getTransportIconNext2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.play.setImage(this.getIconManager().getTransportIconPlay2());
				this.play.setToolTipText(this.getText("transport.start"));
			} else {
				this.first.setImage(this.getIconManager().getTransportIconFirst1());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious1());
				this.next.setImage(this.getIconManager().getTransportIconNext1());
				this.last.setImage(this.getIconManager().getTransportIconLast1());
				this.play.setImage(this.getIconManager().getTransportIconPlay1());
				this.play.setToolTipText(this.getText("transport.start"));
			}
		}
	}

	@Override
	public void processEvent(TGEvent event) throws TGEventException {
		if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE);
			if( type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT ) {
				this.redrawProcess.process();
			}
		} else if (TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE);
			if (type == TGUpdateEvent.SONG_UPDATED || type == TGUpdateEvent.MEASURE_UPDATED || type == TGUpdateEvent.SONG_LOADED) {
				this.updateProcess.process();
			}
		}
	}
}
