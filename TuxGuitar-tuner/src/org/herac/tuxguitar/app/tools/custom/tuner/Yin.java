package org.herac.tuxguitar.app.tools.custom.tuner;

/**
 * yin pitch detection
 * http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf
 * integration by cn pterodactylus42 <carmaneu at gmx dot de>
 * yin implementation from tarsos dsp and aubio
 */

public class Yin {

    private static final double THRESHOLD = 0.2;

    private double fZeroCandidate;
    protected TGTunerSettings settings; // here we get samplerate;
    private double probability;
    private double delta;
    private int tau;
    private float sum;

    // constructor
    public Yin(TGTunerSettings settings) {
        this.fZeroCandidate = -1.0;
        this.settings = settings;
        this.probability = -1.0;
        this.delta = 0;
        this.tau = -1;
        this.sum = 0;
    }
    
    public double getfZeroCandidate() {
        return this.fZeroCandidate;
    }

    public double processBuffer(byte[] byteBuffer) {
        // todo: process buffer and return f0 candidate
        // byteBuffer -> audioBuffer -> resultBuffer
        
        // step 0. convert buffer values to [-1 , 1 ]
        // java.lang.Byte ranges from -127 to 127
        double[] audioBuffer = new double[byteBuffer.length];
        for( int a = 0; a < byteBuffer.length; a++ ) {
            if(byteBuffer[a] > 0) {
                audioBuffer[a] = (double) byteBuffer[a] / java.lang.Byte.MAX_VALUE;
            } else if (byteBuffer[a] < 0) {
                audioBuffer[a] = -1.0 * ( (double) byteBuffer[a] / java.lang.Byte.MIN_VALUE);
            } else {
                audioBuffer[a] = 0;
            }
        }

        // // step 1. autocorrelation
        // // ->Yin does NOT do autocorrelation.
        // double[] acf = new double[ buffer.length / 2 ];
        // for( int x = 0; x < acf.length; x++ ) {
        //     acf[x] = 0; // init
        // }
        // for( int lag = 0; lag < buffer.length / 2; lag++ ) {
        //     for( int i = 0; i < acf.length; i++ ) {
        //         acf[lag] += buffer[i] * buffer[i + lag];
        //     }
        // }
        
        // // if you like to test it: get maximum of acf
        // double maximum = 0;
        // int lagInSamples = 0;
        // double fZero = 0;
        // for(int y = 1; y < acf.length; y++ ) {
        //     if( acf[y] > maximum) {
        //         maximum = acf[y];
        //         lagInSamples = y;
        //         // System.out.println("acf value: " + acf[y] + " lag: " + lagInSamples + " samplerate: " + settings.getSampleRate() + " f0: " + fZero);
        //     }
        // }
        // fZero = 1 / (lagInSamples / settings.getSampleRate());
        // System.out.println("acf f0: " + fZero );
        // return (double) 1 / (lagInSamples / settings.getSampleRate());

        // step 2: difference function
        double[] resultBuffer = new double[audioBuffer.length / 2];
        for ( int b = 0; b < resultBuffer.length; b++ ) {
            resultBuffer[b] = 0;
        }

        for ( tau = 1; tau < resultBuffer.length; tau++ ) {
            for ( int i = 0; i < resultBuffer.length; i++ ) {
                delta = audioBuffer[i] - audioBuffer[i + tau];
                resultBuffer[tau] += delta * delta;
            }
        }

        // step 3: cumulative mean normalized difference
        resultBuffer[0] = 1;
        sum = 0;
        for( tau = 1; tau < resultBuffer.length; tau++ ) {
            sum += resultBuffer[tau];
            resultBuffer[tau] *= tau / sum;
        }

        // step 4: absolute threshold
        for( tau = 2; tau < resultBuffer.length; tau++ ) {
            if( resultBuffer[tau] < THRESHOLD ) {
                while ( tau + 1 < resultBuffer.length && resultBuffer[tau + 1] < resultBuffer[tau] ) {
                    tau++;
                }
                this.probability = 1 - resultBuffer[tau];
                break;
            }
        }
        if( tau == resultBuffer.length || resultBuffer[tau] >= THRESHOLD) {
            tau = -1;
            this.probability = 0;
        }
        // System.out.println("yin probability: " + this.probability + " tau: " + tau + "yin delta: " + delta + " sum " + sum);

        // step 5: parabolic interpolation
        if( tau != -1 ) {
            double betterTau;
            int x0, x2;
            if ( tau < 1 ) {
                x0 = tau;
            } else {
                x0 = tau - 1;
            }
            if ( tau + 1 < resultBuffer.length ) {
                x2 = tau + 1;
            } else {
                x2 = tau;
            }
            if ( x0 == tau ) {
                if ( resultBuffer[tau] <= resultBuffer[x2] ) {
                    betterTau = tau;
                } else {
                    betterTau = x2;
                }
            } else if ( x2 == tau ) {
                if( resultBuffer[tau] <= resultBuffer[x0] ) {
                    betterTau = tau;
                } else {
                    betterTau = x0;
                }
            } else {
                double s0, s1, s2;
                s0 = resultBuffer[x0];
                s1 = resultBuffer[tau];
                s2 = resultBuffer[x2];
                betterTau = tau + (s2 - s0) / (2 * ( 2 * s1 - s2 - s0 ));
            }
            //return best guess
            this.fZeroCandidate = settings.getSampleRate() / betterTau;
        } else {
            this.fZeroCandidate = -1;
        }

        System.out.println("yin fZero: " + this.fZeroCandidate);
        return this.fZeroCandidate;
    }
}
