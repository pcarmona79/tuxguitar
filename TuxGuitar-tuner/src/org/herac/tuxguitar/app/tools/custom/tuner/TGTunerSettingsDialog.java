package org.herac.tuxguitar.app.tools.custom.tuner;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;

import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITextArea;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerSettingsDialog {

	private TGTunerDialog tunerDialog = null;
	private UIDropDownSelect<Float> sampleRateCombo = null;
	private UIDropDownSelect<Integer> sampleSizeCombo = null;
	private UIDropDownSelect<Integer> bufferSizeCombo = null;
	private UIDropDownSelect<Integer> FFTSizeCombo = null;
	private UIScale noiseGate = null;
	private UILabel noiseGateValue = null;
	private UITextArea settingsInfo = null;
	private boolean updated;
	
	public TGTunerSettingsDialog(TGTunerDialog dialog) {
		this.tunerDialog = dialog;
		this.updated = false;
	}

	public void show() {
		final UIFactory uiFactory = this.tunerDialog.getUIFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(this.tunerDialog.getWindow(), true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		dialog.setText(TuxGuitar.getProperty("tuner.settings"));
		
		// ---------------------------------------------------------------------------
		UITableLayout sampleLayout = new UITableLayout();
		UIPanel sampleComposite = uiFactory.createPanel(dialog, false);
		sampleComposite.setLayout(sampleLayout);
		dialogLayout.set(sampleComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel sampleRateLabel = uiFactory.createLabel(sampleComposite);
		sampleRateLabel.setText(TuxGuitar.getProperty("tuner.sample-rate"));
		sampleLayout.set(sampleRateLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		this.sampleRateCombo = uiFactory.createDropDownSelect(sampleComposite);
		this.sampleRateCombo.addItem(new UISelectItem<Float>("48000", 48000f));
		this.sampleRateCombo.addItem(new UISelectItem<Float>("44100", 44100f));
		this.sampleRateCombo.addItem(new UISelectItem<Float>("22050", 22050f));
		this.sampleRateCombo.addItem(new UISelectItem<Float>("11025", 11025f));
		this.sampleRateCombo.addItem(new UISelectItem<Float>("8000", 8000f));
		this.sampleRateCombo.addSelectionListener(new UpdatedListener());
		sampleLayout.set(this.sampleRateCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		UILabel sampleSizeLabel = uiFactory.createLabel(sampleComposite);
		sampleSizeLabel.setText(TuxGuitar.getProperty("tuner.sample-size"));
		sampleLayout.set(sampleSizeLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		this.sampleSizeCombo = uiFactory.createDropDownSelect(sampleComposite);
		this.sampleSizeCombo.addItem(new UISelectItem<Integer>("16", 16));
		this.sampleSizeCombo.addItem(new UISelectItem<Integer>("8", 8));
		this.sampleSizeCombo.addSelectionListener(new UpdatedListener());
		sampleLayout.set(this.sampleSizeCombo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		// ---------------------------------------------------------------------------
		UITableLayout analyzeLayout = new UITableLayout();
		UIPanel analyzeComposite = uiFactory.createPanel(dialog, false);
		analyzeComposite.setLayout(analyzeLayout);
		dialogLayout.set(analyzeComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// buffer size
		UILabel bufferSizeLabel = uiFactory.createLabel(analyzeComposite);
		bufferSizeLabel.setText(TuxGuitar.getProperty("tuner.sampling-buffer-size"));
		analyzeLayout.set(bufferSizeLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		this.bufferSizeCombo = uiFactory.createDropDownSelect(analyzeComposite);
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("512", 512));
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("1024", 1024));
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("2048", 2048));
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("4096", 4096));
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("8192", 8192));
		this.bufferSizeCombo.addItem(new UISelectItem<Integer>("16348", 16348));
		this.bufferSizeCombo.addSelectionListener(new UpdatedListener());
		analyzeLayout.set(this.bufferSizeCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		// FFT buffer size
		UILabel FFTSizeLabel = uiFactory.createLabel(analyzeComposite);
		FFTSizeLabel.setText(TuxGuitar.getProperty("tuner.fourier-buffer-size"));
		analyzeLayout.set(FFTSizeLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		this.FFTSizeCombo = uiFactory.createDropDownSelect(analyzeComposite);
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("1024", 1024));
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("2048", 2048));
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("4096", 4096));
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("8192", 8192));
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("16384", 16384));
		this.FFTSizeCombo.addItem(new UISelectItem<Integer>("32768", 32768));
		this.FFTSizeCombo.addSelectionListener(new UpdatedListener());
		analyzeLayout.set(this.FFTSizeCombo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		// ---------------------------------------------------------------------------
		UITableLayout noiseGateLayout = new UITableLayout();
		final UIPanel noiseGateComposite = uiFactory.createPanel(dialog, false);
		noiseGateComposite.setLayout(noiseGateLayout);
		dialogLayout.set(noiseGateComposite, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.noiseGate = uiFactory.createHorizontalScale(noiseGateComposite);
		this.noiseGate.setMaximum(100);
		this.noiseGate.setIncrement(5);
		this.noiseGate.setIncrement(1);
		this.noiseGate.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTunerSettingsDialog.this.noiseGateValue.setText(new Integer(TGTunerSettingsDialog.this.noiseGate.getValue()).toString()+"%");
				noiseGateComposite.layout();
			}
		});
		this.noiseGate.addSelectionListener(new UpdatedListener());
		noiseGateLayout.set(this.noiseGate, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.noiseGateValue = uiFactory.createLabel(noiseGateComposite);
		noiseGateLayout.set(this.noiseGateValue, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		// ---------------------------------------------------------------------------
		UITableLayout infoLayout = new UITableLayout();
		UIPanel infoComposite = uiFactory.createPanel(dialog, false);
		infoComposite.setLayout(infoLayout);
		dialogLayout.set(infoComposite, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.settingsInfo = uiFactory.createTextArea(infoComposite, true, false);
		infoLayout.set(this.settingsInfo, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, 100f, null);
		
		//------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> dispose(dialog, true)),
				TGDialogButtons.cancel(() -> dispose(dialog, false)));
		dialogLayout.set(buttons.getControl(), 5, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		
        this.loadSettings(this.tunerDialog.getTuner().getSettings(), dialog);
        
        TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void loadSettings(TGTunerSettings settings, UIWindow dialog) {
		boolean loadedDefaults=false;
		if( settings == null ) {
			settings = TGTunerSettings.getDefaults();
			loadedDefaults=true;
		}
		
		try {
			this.sampleRateCombo.setSelectedValue(settings.getSampleRate());
			this.sampleSizeCombo.setSelectedValue(settings.getSampleSize());
			
			this.FFTSizeCombo.setSelectedValue(settings.getFFTSize());
			
			this.bufferSizeCombo.setSelectedValue(settings.getBufferSize());
			
			this.noiseGate.setValue((int)Math.round(settings.getThreshold()*100));
			this.noiseGateValue.setText(new Integer(this.noiseGate.getValue()).toString()+"%");
		} catch (Exception ex) {
			if (!loadedDefaults) {
				TGMessageDialogUtil.errorMessage(getContext(), dialog, "Failed to load TuxGuitar settings.\nLoading defaults.");
				loadSettings(TGTunerSettings.getDefaults(),dialog);
			}
		}	
	}
	
	private void dispose(UIWindow dialog, boolean saveWanted) {
		try {
			if (this.updated & saveWanted) {
				TGTunerSettings settings = new TGTunerSettings();
				settings.setSampleRate(this.getSampleRate());
				settings.setSampleSize(this.getSampleSize());
				
				settings.setBufferSize(this.getBufferSize());
				settings.setFFTSize(this.getFFTSize());
				settings.setThreshold((float)this.noiseGate.getValue()/100);
				settings.setWaitPeriod(100); // TODO: hard coded?
				
				this.checkBufferValues(settings); // check if they are divisable with buffer size
				
				this.tunerDialog.getTuner().setSettings(settings);
			}
			
	    	this.tunerDialog.getTuner().resumeFromPause();
			dialog.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
			TGMessageDialogUtil.errorMessage(getContext(), dialog, ex.getMessage());
		}
	}
	
	private float getSampleRate() {
		Float value = this.sampleRateCombo.getSelectedValue();
		return (value != null ? value : 0f);
	}
	
	private int getSampleSize() {
		Integer value = this.sampleSizeCombo.getSelectedValue();
		return (value != null ? value : 0);
	}
	
	private int getFFTSize() {
		Integer value = this.FFTSizeCombo.getSelectedValue();
		return (value != null ? value : 0);
	}
	
	private int getBufferSize() {
		Integer value = this.bufferSizeCombo.getSelectedValue();
		return (value != null ? value : 0);
	}
	
	

	/** adapter class which sets update flag */
	private class UpdatedListener implements UISelectionListener {
		
		public void onSelect(UISelectionEvent event) {
        	TGTunerSettingsDialog.this.updated=true;
        	TGTunerSettingsDialog.this.settingsInfo.setText(" Minimal freq diff = "+this.getMinimalFrequencyDiff()+"Hz   \n Time to fill the buffer = "+ this.getTimeToFillBuffer()+" sec");
        }
		
    	private double getMinimalFrequencyDiff() {
    		return ((double) TGTunerSettingsDialog.this.getSampleRate()) / TGTunerSettingsDialog.this.getFFTSize();
    	}
    	
    	private double getTimeToFillBuffer() {
    		return TGTunerSettingsDialog.this.getBufferSize() / TGTunerSettingsDialog.this.getSampleRate();
    	}
	}
	
	private void checkBufferValues(TGTunerSettings settings) throws Exception {
		if (settings.bufferSize % settings.sampleSize != 0 ||
			settings.bufferSize > settings.fftSize	)
			throw new Exception("Invalid sampling buffer size");
	}

	private TGContext getContext() {
		return this.tunerDialog.getContext();
	}
}
