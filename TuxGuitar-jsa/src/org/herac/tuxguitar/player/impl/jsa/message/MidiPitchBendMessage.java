package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class MidiPitchBendMessage extends MidiShortMessage{
	
	public MidiPitchBendMessage(int channel,int value, int voice, boolean bendMode) throws InvalidMidiDataException{
		this.setChannel(channel);
		this.setMessage(ShortMessage.PITCH_BEND, value & 0x7f, (value & 0x3f80) >> 7);
		this.setVoice(voice);
		this.setBendMode(bendMode);
	}
}
