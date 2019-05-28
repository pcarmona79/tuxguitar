package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGMixerChange {
    private Short bank;
    private Short program;
    private Short volume;
    private Short balance;
    private Short chorus;
    private Short reverb;
    private Short phaser;
    private Short tremolo;

    public TGMixerChange() {
    }

    public Short getBank() {
        return bank;
    }

    public void setBank(Short bank) {
        this.bank = bank;
    }

    public Short getProgram() {
        return program;
    }

    public void setProgram(Short program) {
        this.program = program;
    }

    public Short getVolume() {
        return volume;
    }

    public void setVolume(Short volume) {
        this.volume = volume;
    }

    public Short getBalance() {
        return balance;
    }

    public void setBalance(Short balance) {
        this.balance = balance;
    }

    public Short getChorus() {
        return chorus;
    }

    public void setChorus(Short chorus) {
        this.chorus = chorus;
    }

    public Short getReverb() {
        return reverb;
    }

    public void setReverb(Short reverb) {
        this.reverb = reverb;
    }

    public Short getPhaser() {
        return phaser;
    }

    public void setPhaser(Short phaser) {
        this.phaser = phaser;
    }

    public Short getTremolo() {
        return tremolo;
    }

    public void setTremolo(Short tremolo) {
        this.tremolo = tremolo;
    }

    public TGMixerChange clone(TGFactory factory) {
        TGMixerChange mixer = factory.newMixerChange();
        mixer.setBank(getBank());
        mixer.setProgram(getProgram());
        mixer.setVolume(getVolume());
        mixer.setBalance(getBalance());
        mixer.setChorus(getChorus());
        mixer.setReverb(getReverb());
        mixer.setPhaser(getPhaser());
        mixer.setTremolo(getTremolo());
        return mixer;
    }
}
