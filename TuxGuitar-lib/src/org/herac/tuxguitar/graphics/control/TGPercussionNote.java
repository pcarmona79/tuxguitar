package org.herac.tuxguitar.graphics.control;

/**
 * The goal of this class is to provide a configurable interface to drum
 * rendering mapping of rendering flags and notes.
 * 
 * @author simpoir@gmail.com
 */
public class TGPercussionNote {

	public static final TGPercussionNote[] DEFAULT_MAPPING = new TGPercussionNote[128];

	public static final int KIND_CYMBAL  = 1;      // X note
	public static final int KIND_NOTE    = 1 << 1; // round and black
	public static final int KIND_OPEN    = 1 << 2; // small o above
	public static final int KIND_CLOSED  = 1 << 3; // small + above
	public static final int KIND_CIRCLED = 1 << 4; // surrounding circle
	public static final int KIND_DIAMOND = 1 << 5;
	public static final int KIND_TRIANGLE_UP = 1 << 6;
	public static final int KIND_TRIANGLE_DOWN = 1 << 7;
	public static final int KIND_SQUARE = 1 << 8;

	public static final TGPercussionNote DEFAULT_NOTE = new TGPercussionNote(59, KIND_NOTE);

	static {
		for (int i = 0; i < DEFAULT_MAPPING.length; i++) {
			DEFAULT_MAPPING[i] = DEFAULT_NOTE;
		}
		DEFAULT_MAPPING[35] = new TGPercussionNote(52, KIND_NOTE); // acoustic bass drum
		DEFAULT_MAPPING[36] = new TGPercussionNote(53, KIND_NOTE); // bass drum
		DEFAULT_MAPPING[37] = new TGPercussionNote(60, KIND_CYMBAL); // side stick
		DEFAULT_MAPPING[38] = new TGPercussionNote(60, KIND_NOTE); // acoustic snare
		DEFAULT_MAPPING[40] = new TGPercussionNote(60, KIND_NOTE); // electric snare
		DEFAULT_MAPPING[42] = new TGPercussionNote(68, KIND_CYMBAL|KIND_CLOSED); // closed high hat
		DEFAULT_MAPPING[44] = new TGPercussionNote(52, KIND_CYMBAL); // pedal high hat
		DEFAULT_MAPPING[46] = new TGPercussionNote(68, KIND_CYMBAL|KIND_OPEN); // open high hat
		DEFAULT_MAPPING[49] = new TGPercussionNote(69, KIND_CYMBAL); // crash cymbal
		DEFAULT_MAPPING[57] = new TGPercussionNote(71, KIND_CYMBAL); // crash cymbal 2
		DEFAULT_MAPPING[51] = new TGPercussionNote(65, KIND_CYMBAL); // ride cymbal
		DEFAULT_MAPPING[59] = new TGPercussionNote(62, KIND_CYMBAL); // ride cymbal 2
		DEFAULT_MAPPING[53] = new TGPercussionNote(65, KIND_DIAMOND); // ride bell
		DEFAULT_MAPPING[52] = new TGPercussionNote(72, KIND_CYMBAL|KIND_CIRCLED); // china cymbal
		DEFAULT_MAPPING[55] = new TGPercussionNote(72, KIND_CYMBAL); // splash cymbal

		DEFAULT_MAPPING[41] = new TGPercussionNote(55, KIND_NOTE); // low floor tom
		DEFAULT_MAPPING[43] = new TGPercussionNote(57, KIND_NOTE); // high floor tom
		DEFAULT_MAPPING[45] = new TGPercussionNote(59, KIND_NOTE); // low tom
		DEFAULT_MAPPING[47] = new TGPercussionNote(62, KIND_NOTE); // med tom
		DEFAULT_MAPPING[48] = new TGPercussionNote(64, KIND_NOTE); // hi med tom
		DEFAULT_MAPPING[50] = new TGPercussionNote(65, KIND_NOTE); // high tom

		DEFAULT_MAPPING[80] = new TGPercussionNote(69, KIND_TRIANGLE_UP|KIND_CLOSED); // mute triangle
		DEFAULT_MAPPING[81] = new TGPercussionNote(69, KIND_TRIANGLE_UP); // open triangle
		DEFAULT_MAPPING[56] = new TGPercussionNote(64, KIND_TRIANGLE_UP); // cowbell
		DEFAULT_MAPPING[76] = new TGPercussionNote(62, KIND_TRIANGLE_UP); // hi wood block
		DEFAULT_MAPPING[77] = new TGPercussionNote(60, KIND_TRIANGLE_UP); // lo wood block
		DEFAULT_MAPPING[54] = new TGPercussionNote(59, KIND_TRIANGLE_UP); // tambourine

		DEFAULT_MAPPING[58] = new TGPercussionNote(72, KIND_TRIANGLE_DOWN); // vibraslap
		DEFAULT_MAPPING[73] = new TGPercussionNote(71, KIND_TRIANGLE_DOWN); // short guiro
		DEFAULT_MAPPING[74] = new TGPercussionNote(69, KIND_TRIANGLE_DOWN); // long guiro
		DEFAULT_MAPPING[67] = new TGPercussionNote(64, KIND_TRIANGLE_DOWN); // short agogo
		DEFAULT_MAPPING[68] = new TGPercussionNote(62, KIND_TRIANGLE_DOWN); // long agogo
		DEFAULT_MAPPING[60] = new TGPercussionNote(57, KIND_TRIANGLE_DOWN); // hi bongo
		DEFAULT_MAPPING[61] = new TGPercussionNote(55, KIND_TRIANGLE_DOWN); // lo bongo
		DEFAULT_MAPPING[62] = new TGPercussionNote(53, KIND_TRIANGLE_DOWN|KIND_CLOSED); // mute hi conga
		DEFAULT_MAPPING[63] = new TGPercussionNote(53, KIND_TRIANGLE_DOWN); // open hi conga
		DEFAULT_MAPPING[64] = new TGPercussionNote(52, KIND_TRIANGLE_DOWN); // lo conga
		DEFAULT_MAPPING[65] = new TGPercussionNote(50, KIND_TRIANGLE_DOWN); // hi timbale
		DEFAULT_MAPPING[66] = new TGPercussionNote(48, KIND_TRIANGLE_DOWN); // lo timbale
		DEFAULT_MAPPING[78] = new TGPercussionNote(47, KIND_TRIANGLE_DOWN|KIND_CLOSED); // mute cuica
		DEFAULT_MAPPING[79] = new TGPercussionNote(47, KIND_TRIANGLE_DOWN); // open cuica
		DEFAULT_MAPPING[71] = new TGPercussionNote(45, KIND_TRIANGLE_DOWN); // short whistle
		DEFAULT_MAPPING[72] = new TGPercussionNote(43, KIND_TRIANGLE_DOWN); // long whistle

		DEFAULT_MAPPING[39] = new TGPercussionNote(59, KIND_SQUARE); // hand clap
		DEFAULT_MAPPING[69] = new TGPercussionNote(57, KIND_CYMBAL); // cabasa
		DEFAULT_MAPPING[70] = new TGPercussionNote(55, KIND_CYMBAL); // maracas
		DEFAULT_MAPPING[75] = new TGPercussionNote(53, KIND_CYMBAL); // claves
	}

	private int note;
	private int kind;

	public TGPercussionNote(int note, int kind) {
		this.note = note;
		this.kind = kind;
	}

	public int getNote() {
		return note;
	}

	public int getKind() {
		return kind;
	}
}

