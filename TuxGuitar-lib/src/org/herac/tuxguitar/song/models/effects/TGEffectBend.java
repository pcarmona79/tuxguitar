/*
 * Created on 26-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGEffectBend {
	
	public static final int SEMITONE_LENGTH = 1;
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = (SEMITONE_LENGTH * 12);
	
	// these are GPT values, powertab are different
	public static final int None = 0;
	public static final int Bend = 1;
	public static final int BendRelease = 2;
	public static final int BendReleaseBend = 3;
	public static final int PreBend = 4;
	public static final int PreBendRelease = 5;
	public static final int Dip = 6;
	public static final int Dive = 7;
	public static final int ReleaseUp = 8;
	public static final int InvertedDip = 9;
	public static final int ReturnBar = 10;
	public static final int RelaseBar = 11;
	
	private List<BendPoint> points;
	private int bendtype;
	private int bendvalue;
	
	public TGEffectBend(){
		this.points = new ArrayList<BendPoint>();
		this.bendtype = 0;
	}
	
	public void addPoint(int position,int value){
		this.points.add(new BendPoint(position,value));
	}
	
	public List<BendPoint> getPoints(){
		return this.points;
	}
	
	public int getBendType() {
		return bendtype;
	}
	
	public void setBendType(int bendtype) {
		this.bendtype = bendtype;
	}
	
	public int getBendValue()
	{
		return bendvalue;
	}
	
	public void setBendValue(int bendvalue) {
		this.bendvalue = bendvalue;
	}
	
	public TGEffectBend clone(TGFactory factory){
		TGEffectBend effect = factory.newEffectBend();
		Iterator<BendPoint> it = getPoints().iterator();
		while(it.hasNext()){
			BendPoint point = it.next();
			effect.addPoint(point.getPosition(),point.getValue());
		}
		return effect;
	}
	
	public class BendPoint{
		
		private int position;
		private int value;
		
		public BendPoint(int position,int value){
			this.position = position;
			this.value = value;
		}
		
		public int getPosition() {
			return this.position;
		}
		
		public int getValue() {
			return this.value;
		}
		
		public long getTime(long duration){
			return (duration * getPosition() / MAX_POSITION_LENGTH);
		}
		
		public Object clone(){
			return new BendPoint(getPosition(),getValue());
		}
	}
}
