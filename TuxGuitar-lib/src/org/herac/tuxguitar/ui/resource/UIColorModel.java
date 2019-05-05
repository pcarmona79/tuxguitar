package org.herac.tuxguitar.ui.resource;

public class UIColorModel {
	
	private int red;
	private int green;
	private int blue;
	
	public UIColorModel(){
		this(0, 0, 0);
	}
	
	public UIColorModel(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public UIColorModel(UIColor color) {
	    this(color.getRed(), color.getGreen(), color.getBlue());
	}

	private static double coef(double val) {
		return Math.abs((val - Math.floor(val)) * 6. - 3.);
	}
	private static double lerp(double x, double y, double a) {
        return x * (1. - a) + y * a;
	}
	private static double clamp(double x, double min, double max) {
		return Math.min(Math.max(x, min), max);
	}

	public UIColorModel(float hue, float saturation, float brightness) {
		this(
				(int) Math.round(brightness * lerp(1., clamp(coef(hue + 1.) - 1., 0., 1.), saturation) * 255),
				(int) Math.round(brightness * lerp(1., clamp(coef(hue + 2./3.) - 1., 0., 1.), saturation) * 255.),
				(int) Math.round(brightness * lerp(1., clamp(coef(hue + 1./3.) - 1., 0., 1.), saturation) * 255.)
		);
	}
	public UIColorModel(float[] color) {
		this(color[0], color[1], color[2]);
	}

	public float[] getHSB() {
		double[] p;
		double[] q;
		if (green < blue) {
			p = new double[] {blue/255., green/255., -1., 2./3.};
		} else {
			p = new double[] {green/255., blue/255., 0., -1./3.};
		}
		if (red < p[0]) {
			q = new double[] {p[0], p[1], p[3], red/255.};
		} else {
			q = new double[] {red/255., p[1], p[2], p[0]};
		}
		double d = q[0] - Math.min(q[3], q[1]);
		double e = 1e-10;
		return new float[] {
				(float) Math.abs(q[2] + (q[3] - q[1]) / (6. * d + e)),
				(float) (d / (q[0] + e)),
				(float) q[0]
		};
	}
	
	public int getRed() {
		return this.red;
	}
	
	public void setRed(int red) {
		this.red = red;
	}
	
	public int getGreen() {
		return this.green;
	}
	
	public void setGreen(int green) {
		this.green = green;
	}
	
	public int getBlue() {
		return this.blue;
	}
	
	public void setBlue(int blue) {
		this.blue = blue;
	}

	public static UIColorModel complementaryTextColor(UIColorModel color) {
		float[] hsb = color.getHSB();
		hsb[1] = 0f;
		hsb[2] = hsb[2] >= .3f ? .1f : .8f;
		return new UIColorModel(hsb);
	}

	public static UIColorModel adjustValue(UIColorModel color, float value) {
		float[] hsb = color.getHSB();
		hsb[2] = Math.min(Math.max(hsb[2] + value, 0f), 1f);
		return new UIColorModel(hsb);
	}

	public static UIColorModel darken(UIColorModel color) {
		return adjustValue(color, -.1f);
	}

}
