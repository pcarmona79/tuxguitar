package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.transport.*;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class TGMainToolBarSectionTransport extends TGMainToolBarSection {
	
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;

	private UIButton first;
	private UIButton play;
	private UIButton last;
	private UIToggleButton countDown;
	private UIToggleButton metronome;
	private UIToggleButton loop;
	private UIButton playMode;
	private int status;
	
	public TGMainToolBarSectionTransport(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.first = this.createButton();
		this.first.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoFirst());

		this.play = this.createButton();
		this.play.addSelectionListener(this.createActionProcessor(TGTransportPlayAction.NAME));

		this.last = this.createButton();
		this.last.addSelectionListener(event -> TGTransport.getInstance(getToolBar().getContext()).gotoLast());

		this.metronome = this.createToggleButton();
		this.metronome.addSelectionListener(this.createActionProcessor(TGTransportMetronomeAction.NAME));

		this.countDown = this.createToggleButton();
		this.countDown.addSelectionListener(this.createActionProcessor(TGTransportCountDownAction.NAME));

		this.loop = this.createToggleButton();
		this.loop.addSelectionListener(this.createActionProcessor(TGTransportSetLoopAction.NAME));

		this.playMode = this.createButton();
		this.playMode.addSelectionListener(this.createActionProcessor(TGOpenTransportModeDialogAction.NAME));

		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();
	}
	
	public void updateItems(){
		MidiPlayer player = MidiPlayer.getInstance(this.getToolBar().getContext());
	    this.metronome.setSelected(player.isMetronomeEnabled());
		this.countDown.setSelected(player.getCountDown().isEnabled());
		this.loadIcons(false);
	}
	
	public void loadProperties(){
		this.first.setToolTipText(this.getText("transport.first"));
		this.play.setToolTipText(this.getText("transport.start"));
		this.last.setToolTipText(this.getText("transport.last"));
		this.metronome.setToolTipText(this.getText("transport.metronome"));
		this.countDown.setToolTipText(this.getText("transport.count-down"));
		this.loop.setToolTipText(this.getText("transport.simple.play-looped"));
		this.playMode.setToolTipText(this.getText("transport.mode"));
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
		if( player.isRunning()){
			this.status = STATUS_RUNNING;
		}else if(player.isPaused()){
			this.status = STATUS_PAUSED;
		}else{
			this.status = STATUS_STOPPED;
		}

		if(force || lastStatus != this.status){
			if( this.status == STATUS_RUNNING ){
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.play.setImage(this.getIconManager().getTransportIconPause());
				this.play.setToolTipText(this.getText("transport.pause"));
			}else if( this.status == STATUS_PAUSED ){
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.play.setImage(this.getIconManager().getTransportIconPlay2());
				this.play.setToolTipText(this.getText("transport.start"));
			}else if( this.status == STATUS_STOPPED ){
				this.first.setImage(this.getIconManager().getTransportIconFirst1());
				this.last.setImage(this.getIconManager().getTransportIconLast1());
				this.play.setImage(this.getIconManager().getTransportIconPlay1());
				this.play.setToolTipText(this.getText("transport.start"));
			}
		}
	}
}
