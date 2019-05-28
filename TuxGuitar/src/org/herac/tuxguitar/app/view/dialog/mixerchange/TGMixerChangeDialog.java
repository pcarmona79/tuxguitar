package org.herac.tuxguitar.app.view.dialog.mixerchange;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelItem;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertMixerChangeAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveMixerChangeAction;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMixerChange;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.*;
import org.herac.tuxguitar.util.TGContext;

public class TGMixerChangeDialog {

	private UIFactory factory;
	private TGMixerChange initial;
	private TGMixerChange defaults;

	public void show(final TGViewContext context) {
		final TGSongManager songManager = TuxGuitar.getInstance().getSongManager();
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGTrack track = beat.getMeasure().getTrack();
		final boolean percussionChannel = songManager.isPercussionChannel(track.getSong(), track.getChannelId());

		this.initial = beat.getMixerChange();
		this.defaults = songManager.getMostRecentMixerChanges(track, beat, false);

		this.factory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = factory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("mixer-change.editor"));
		
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = factory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);

		//------------------PROGRAM--------------------------
		UICheckBox programEnable = factory.createCheckBox(group);
		programEnable.setText(TuxGuitar.getProperty("instrument.program"));
		programEnable.setSelected(initial != null && initial.getProgram() != null);
		groupLayout.set(programEnable, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UIDropDownSelect<Short> program = factory.createDropDownSelect(group);
		if (!percussionChannel) {
			MidiInstrument[] instruments = MidiPlayer.getInstance(context.getContext()).getInstruments();
			if (instruments != null) {
				int count = instruments.length;
				if (count > 128) {
					count = 128;
				}
				for (short i = 0; i < count; i++) {
					program.addItem(new UISelectItem<>(instruments[i].getName(), i));
				}
			}
		}
		if (program.getItemCount() == 0){
			String programPrefix = TuxGuitar.getProperty("instrument.program");
			for (short i = 0; i < 128; i++) {
				program.addItem(new UISelectItem<>(programPrefix + " #" + i, i));
			}
		}
		program.setSelectedValue(initial != null && initial.getProgram() != null ? initial.getProgram() : defaults.getProgram());
		program.setEnabled(programEnable.isSelected());
		groupLayout.set(program, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		programEnable.addSelectionListener(event -> program.setEnabled(programEnable.isSelected()));

		//------------------BANK--------------------------
		UICheckBox bankEnable = factory.createCheckBox(group);
		bankEnable.setText(TuxGuitar.getProperty("instrument.bank"));
		bankEnable.setEnabled(!percussionChannel);
		bankEnable.setSelected(initial != null && initial.getBank() != null);
		groupLayout.set(bankEnable, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		UIDropDownSelect<Short> bank = factory.createDropDownSelect(group);
		if (!percussionChannel) {
			String bankPrefix = TuxGuitar.getProperty("instrument.bank");
			for(short i = 0; i < 128; i++) {
				bank.addItem(new UISelectItem<Short>((bankPrefix + " #" + i), i));
			}
			bank.setSelectedValue(initial != null && initial.getBank() != null ? initial.getBank() : defaults.getBank());
		}
		bank.setEnabled(!percussionChannel && bankEnable.isSelected());
		groupLayout.set(bank, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		bankEnable.addSelectionListener(event -> bank.setEnabled(bankEnable.isSelected()));

		//------------------KNOBS--------------------------

		UITableLayout knobLayout = new UITableLayout();
		UIPanel knobPanel = factory.createPanel(dialog, false);
		knobPanel.setLayout(knobLayout);
		dialogLayout.set(knobPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------VOLUME--------------------------
		MixerKnob volume = createKnob(knobPanel, 1, "instrument.volume", TGMixerChange::getVolume);
		MixerKnob balance = createKnob(knobPanel, 2, "instrument.balance", TGMixerChange::getBalance);
		MixerKnob reverb = createKnob(knobPanel, 3, "instrument.reverb", TGMixerChange::getReverb);
		MixerKnob chorus = createKnob(knobPanel, 4, "instrument.chorus", TGMixerChange::getChorus);
		MixerKnob tremolo = createKnob(knobPanel, 5, "instrument.tremolo", TGMixerChange::getTremolo);
		MixerKnob phaser = createKnob(knobPanel, 6, "instrument.phaser", TGMixerChange::getPhaser);

		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(factory, dialog,
				TGDialogButtons.ok(() -> {
				    TGMixerChange mixer = null;
				    if (programEnable.isSelected() || bankEnable.isSelected()
							|| volume.enable.isSelected() || balance.enable.isSelected()
							|| reverb.enable.isSelected() || chorus.enable.isSelected()
							|| tremolo.enable.isSelected() || phaser.enable.isSelected()) {
						mixer = songManager.getFactory().newMixerChange();
						mixer.setProgram(programEnable.isSelected() ? program.getSelectedValue() : null);
						mixer.setBank(bankEnable.isSelected() ? bank.getSelectedValue() : null);
						mixer.setVolume(volume.enable.isSelected() ? (short) volume.knob.getValue() : null);
						mixer.setBalance(balance.enable.isSelected() ? (short) balance.knob.getValue() : null);
						mixer.setReverb(reverb.enable.isSelected() ? (short) reverb.knob.getValue() : null);
						mixer.setChorus(chorus.enable.isSelected() ? (short) chorus.knob.getValue() : null);
						mixer.setTremolo(tremolo.enable.isSelected() ? (short) tremolo.knob.getValue() : null);
						mixer.setPhaser(phaser.enable.isSelected() ? (short) phaser.knob.getValue() : null);
					}
					doInsertMixerChange(context.getContext(), beat, mixer);
					dialog.dispose();
				}), TGDialogButtons.clean(() -> {
                    doRemoveMixerChange(context.getContext(), beat);
                    dialog.dispose();
                }),
				TGDialogButtons.cancel(dialog::dispose));
		buttons.getButton(1).setEnabled(initial != null);
		dialogLayout.set(buttons.getControl(), 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private MixerKnob createKnob(UILayoutContainer parent, int col, String property, MixerChangeAttirbute attribute) {
		final MixerKnob control = new MixerKnob();
	    UITableLayout layout = (UITableLayout) parent.getLayout();
		control.knob = factory.createKnob(parent);
		control.knob.setMinimum(TGChannelItem.MINIMUM_KNOB_VALUE);
		control.knob.setMaximum(TGChannelItem.MAXIMUM_KNOB_VALUE);
		control.knob.setIncrement(TGChannelItem.MINIMUM_KNOB_INCREMENT);
		control.knob.setValue(initial != null && attribute.get(initial) != null ? attribute.get(initial) : attribute.get(defaults));
		layout.set(control.knob, 1, col, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_FILL, false, false);

		control.enable = factory.createCheckBox(parent);
		control.enable.setText(TuxGuitar.getProperty(property));
		control.enable.setSelected(initial != null && attribute.get(initial) != null);
		layout.set(control.enable, 2, col, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_FILL, false, false);
		control.enable.addSelectionListener(event -> control.knob.setEnabled(control.enable.isSelected()));
		return control;
	}

	private static class MixerKnob {
		public UIKnob knob;
		public UICheckBox enable;
	}

	private interface MixerChangeAttirbute {
		Short get(TGMixerChange mixer);
	}

	private void doInsertMixerChange(TGContext context, TGBeat beat, TGMixerChange mixer) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGInsertMixerChangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGInsertMixerChangeAction.ATTRIBUTE_MIXER_CHANGE, mixer);
		tgActionProcessor.processOnNewThread();
	}
	
	private void doRemoveMixerChange(TGContext context, TGBeat beat) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGRemoveMixerChangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.processOnNewThread();
	}
}
