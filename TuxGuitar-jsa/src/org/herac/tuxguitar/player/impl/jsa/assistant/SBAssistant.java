package org.herac.tuxguitar.player.impl.jsa.assistant;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.widgets.TGDialogButtons;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortSynthesizer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

import java.net.MalformedURLException;
import java.net.URL;

public class SBAssistant {

	public static final SBUrl[] URLS = new SBUrl[]{
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-min-150078.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.minimal")),
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-mid-149984.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.medium")),
		new SBUrl(toURL("http://www.oracle.com/technetwork/java/soundbank-deluxe-150042.zip"),TuxGuitar.getProperty("jsa.soundbank-assistant.deluxe")),
	};
	
	private TGContext context;
	private MidiPortSynthesizer synthesizer;
	
	public SBAssistant(TGContext context, MidiPortSynthesizer synthesizer){
		this.context = context;
		this.synthesizer = synthesizer;
	}
	
	public void process(){
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("jsa.soundbank-assistant.confirm-message"));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
			public void run() {
				TGSynchronizer.getInstance(SBAssistant.this.context).executeLater(new Runnable() {
					public void run() {
						open();
					}
				});
			}
		});
		tgActionProcessor.process();
	}
	
	protected void open(){		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		dialog.setLayout(dialogLayout);
		
		//------------------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UIPanel group = uiFactory.createPanel(dialog, false);
		group.setLayout(groupLayout);
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 250f, null, null);
        
        final UIRadioButton urls[] = new UIRadioButton[URLS.length];
        for(int i = 0; i < URLS.length ; i ++){
	        urls[i] = uiFactory.createRadioButton(group);
	        urls[i].setText(URLS[i].getName());
	        urls[i].setData(SBUrl.class.getName(), URLS[i]);
	        urls[i].setSelected(i == 0);
	        groupLayout.set(urls[i], (i + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
        }
		
        //------------------BUTTONS--------------------------
		TGDialogButtons buttons = new TGDialogButtons(uiFactory, dialog,
				TGDialogButtons.ok(() -> {
					URL url = getSelection(urls);

					dialog.dispose();

					if( url != null ){
						install(url);
					}
				}),
				TGDialogButtons.cancel(dialog::dispose));
		dialogLayout.set(buttons.getControl(), 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

        TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	protected URL getSelection(UIRadioButton[] buttons){
    	for(int i = 0; i < buttons.length ; i ++){
    		if( buttons[i].isSelected() ){
    			return ((SBUrl) buttons[i].getData(SBUrl.class.getName())).getUrl();
    		}
    	}
    	return null;
	}
	
	protected void install(URL url ){
		new SBInstallerGui(this.context , url, this.synthesizer).open();
	}

	private static URL toURL(String s){
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
