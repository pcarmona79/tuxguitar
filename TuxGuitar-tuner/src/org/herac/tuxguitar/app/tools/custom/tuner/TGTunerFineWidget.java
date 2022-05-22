package org.herac.tuxguitar.app.tools.custom.tuner;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 * made automatic by cn pterodactylus42 <carmaneu at gmx dot de>
 */
public class TGTunerFineWidget {
	
	private static final float BOTTOM_Y = 10.0f;
    private final static String PITCHCLASS[] = { "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B",  };
	// this would be better off in settings:
    private final static double REFERENCE_FREQ = 440.0;

	private TGContext context;
	private UIPanel panel;
	private UICanvas composite = null;
	protected String currentNoteString = null;
	protected int currentNoteValue = -1;
	protected double currentFrequency = 0.0f;
	protected UIFont letterFont = null;
	protected final float FINE_TUNING_RANGE = 1.5f;

	public TGTunerFineWidget(TGContext context, UIFactory factory, UILayoutContainer parent) {
		this.context = context;
		this.init(factory, parent);
	}

	private void init(final UIFactory factory, UILayoutContainer parent) {
		UITableLayout layout = new UITableLayout();
		
		this.panel = factory.createPanel(parent, false);
		this.panel.setLayout(layout);
		// this.panel.setEnabled(false);
		
		this.composite = factory.createCanvas(this.panel, true);
		this.composite.setBgColor(TGColorManager.getInstance(this.context).getColor(TGColorManager.COLOR_WHITE));
		this.composite.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				TGTunerFineWidget.this.paintWidget(event.getPainter());
			}
		});
		layout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 600f, 200f, null);
		
		this.letterFont = factory.createFont(TGConfigManager.getInstance(this.context).getFontModelConfigValue(TGConfigKeys.MATRIX_FONT).getName(), 14, true, false);
	}
	
	
	public void paintWidget(UIPainter painter) {
		TGColorManager colorManager = TGColorManager.getInstance(this.context);
		UIRectangle compositeSize = this.composite.getBounds();
		
		// margins & stuff
		painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLACK));
		painter.initPath();
		painter.setLineWidth(2);
		float height = compositeSize.getHeight() - BOTTOM_Y-25;
		painter.moveTo(compositeSize.getWidth()/2, compositeSize.getHeight() - BOTTOM_Y);
		painter.lineTo(compositeSize.getWidth()/2, 25);
		painter.closePath();
		painter.initPath();
		height = Math.min(height, compositeSize.getWidth()/2);
		painter.moveTo(compositeSize.getWidth()/2-height, compositeSize.getHeight()-BOTTOM_Y);
		painter.lineTo(compositeSize.getWidth()/2+height, compositeSize.getHeight()-BOTTOM_Y);
		painter.closePath();
		
		if (this.currentNoteValue > -1) {
			// tone name
			painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLUE));
			painter.setFont(this.letterFont);
			painter.drawString(this.currentNoteString, compositeSize.getWidth()*12/15, 10);

			// pointer
			if (this.currentFrequency!=-1) {
				painter.setLineWidth(1);
				painter.setForeground(colorManager.getColor(TGColorManager.COLOR_DARK_BLUE));
				painter.initPath();
				painter.moveTo(compositeSize.getWidth()/2, compositeSize.getHeight()-BOTTOM_Y);
				painter.lineTo((float)(compositeSize.getWidth()/2 + height*Math.cos(this.getAngleRad())),(float)( compositeSize.getHeight()-BOTTOM_Y-height*Math.sin(this.getAngleRad())));
			painter.closePath();
			}
		}
	}
		
	// public void setWantedTone(int tone) {
	// 	// this.panel.setEnabled(true);
	// 	this.currentNoteValue = tone;
	// 	// todo: move TONESSTRING here, as rough widget is unused now
	// 	this.currentNoteString = TGTunerRoughWidget.TONESSTRING[tone%12]+(int)Math.floor(tone/12);
	// 	this.redraw();
	// }
	
	public void setCurrentFrequency(double freq) {
		this.currentFrequency = freq;
		this.currentNoteValue = this.getNearestNoteInt(freq);
		//this.getTone(freq);
		this.currentNoteString = this.getNearestPitchClass(freq);
		this.redraw();
	}

	public void redraw() {
		this.composite.redraw();
	}
	
	protected double getAngleRad() {
		return Math.PI*( 1 - (this.stickDistance(this.getTone(this.currentFrequency) - this.currentNoteValue) + this.FINE_TUNING_RANGE  )/(2*this.FINE_TUNING_RANGE) );
	}
	
	private float getTone(double frequency) {
		System.out.println("getTone: " + (float)(45+12*(Math.log(frequency/110)/Math.log(2))));
		return (float)(45+12*(Math.log(frequency/110)/Math.log(2)));
	}
	
	private double stickDistance(double diff) {
		if (Math.abs(diff) > this.FINE_TUNING_RANGE)
			if (diff > 0)
				return this.FINE_TUNING_RANGE;
			else
				return -this.FINE_TUNING_RANGE;
		return diff;
	}

	public UIControl getControl() {
		return this.panel;
	}
	
	public boolean isDisposed() {
		return (this.panel == null || this.panel.isDisposed());
	}

    private String getNearestPitchClass(double freq) {
        /*
            calculate semitone distance from middle c
            which has approx. 261.6256 Hz
            distance = 9 + (12 log2 (freq / referenceFreq))
            zero means, the note is middle c :-)
            referenceFreq is 440 hz
            //todo make referenceFreq configurable via menu
            distance will be a double, where the part behind the
            decimal point (period) represents distance from the pitch class
         */

        double distance;
        distance = 9 + (12 * (log2(freq/REFERENCE_FREQ)  ) );

        int integerDistance = (int) distance;

        double realDistanceError = distance - integerDistance;
        double distanceError = Math.abs(distance - integerDistance);

        /*
            choose the pitch that is nearest and
            return its name
            set deviation from the frequency
            for positive values of integerDistance
                deviation up to 0.5 is displayed in positive direction
                higher deviation is displayed from the next int in negative direction
            for negative values of integerDistance
                deviation up to 0.5 is displayed in negative direction
                higher deviation is displayed from the next int in positive direction
         */

        if(integerDistance > 0) {
            if(distanceError > 0.5 || distanceError == 0.5) {
                integerDistance++;
            } else {
            }
        } else {
            if(distanceError > 0.5 || distanceError == 0.5) {
                integerDistance--;
            } else {
            }
            if(integerDistance<12) {
                integerDistance = (integerDistance%12)+12;
            }
        }

        return PITCHCLASS[integerDistance%12];
    }

	private int getNearestNoteInt(double freq) {
        double distance;
        distance = (45+12*(Math.log(freq/110)/Math.log(2)));
        int integerDistance = (int) distance;
        double distanceError = Math.abs(distance - integerDistance);

        if(integerDistance > 0) {
            if(distanceError > 0.5 || distanceError == 0.5) {
                integerDistance++;
            }
        }
		System.out.println("nearestNoteInt: " + integerDistance);

        return integerDistance;
    }

    protected double log2(double value) {
        return Math.log( value ) / Math.log( 2.0 );
    }

}