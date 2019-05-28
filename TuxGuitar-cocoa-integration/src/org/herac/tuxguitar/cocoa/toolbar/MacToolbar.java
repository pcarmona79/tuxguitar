package org.herac.tuxguitar.cocoa.toolbar;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBarSection;
import org.herac.tuxguitar.app.view.toolbar.model.TGToolBarSection;
import org.herac.tuxguitar.cocoa.TGCocoa;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.swt.widget.SWTControl;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.util.TGContext;

public class MacToolbar {
	
	private static final byte[] SWT_OBJECT = {'S', 'W', 'T', '_', 'O', 'B', 'J', 'E', 'C', 'T', '\0'};
	
	private static final long NSWindowToolbarButton = 3;
	
	private static final long sel_toolbarButtonClicked_ = OS.sel_registerName("toolbarButtonClicked:");
	private static final long sel_setTitlebarAppearsTransparent_ = OS.sel_registerName("setTitlebarAppearsTransparent:");
	private static final long sel_initWithColor_ = OS.sel_registerName("initWithColor:");
	private static final long sel_red = OS.sel_registerName("red");
	private static final long sel_green = OS.sel_registerName("green");
	private static final long sel_blue = OS.sel_registerName("blue");

	private boolean enabled;
	
	private long delegateRef;
	
	private MacToolbarDelegate delegate;
	private final TGContext context;
	private UIColor bgColor;
	
	public MacToolbar(TGContext context){
		this.context = context;
	}
	
	public void init() throws Throwable {
		Callback callback = TGCocoa.newCallback( this , "callbackProc64", "callbackProc32", 3 );
		long callbackProc = TGCocoa.getCallbackAddress( callback );
		
		if( callbackProc != 0 ){
			String classname = ("MacToolbarDelegate");
			if( TGCocoa.objc_lookUpClass ( classname ) == 0 ) {
				long cls = TGCocoa.objc_allocateClassPair( classname , 0 ) ;
				TGCocoa.class_addIvar(cls, SWT_OBJECT, C.PTR_SIZEOF , (byte)(C.PTR_SIZEOF == 4 ? 2 : 3), new byte[]{'*','\0'} );
				TGCocoa.class_addMethod(cls, sel_toolbarButtonClicked_, callbackProc , "@:@");
				TGCocoa.objc_registerClassPair(cls);
			}
			
			this.delegate = TGCocoa.newMacToolbarDelegate();
			this.delegate.alloc().init();
			this.delegateRef = TGCocoa.NewGlobalRef( MacToolbar.this );
			
			TGCocoa.object_setInstanceVariable( MacToolbarDelegate.class.getField("id").get( delegate ) , SWT_OBJECT , this.delegateRef );
			
			NSToolbar dummyBar = new NSToolbar();
			dummyBar.alloc();
			dummyBar.initWithIdentifier(NSString.stringWith("SWTToolbar")); //$NON-NLS-1$
			dummyBar.setVisible(false);
			
			SWTWindow window = (SWTWindow) TGWindow.getInstance(context).getWindow();
			Shell shell = window.getControl();
			NSWindow nsWindow = shell.view.window();
			nsWindow.setToolbar(dummyBar);
			dummyBar.release();
			nsWindow.setShowsToolbarButton(true);
			long windowId = (Long) NSWindow.class.getField("id").get(nsWindow);
			//OS.objc_msgSend(windowId, sel_setTitlebarAppearsTransparent_, true);

			NSColor color = NSColor.windowBackgroundColor();
			long ciColor = OS.objc_msgSend(OS.objc_getClass("CIColor"), OS.sel_alloc);
			OS.objc_msgSend(ciColor, sel_initWithColor_, color.id);

			UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
			bgColor = uiFactory.createColor(
					(int) (OS.objc_msgSend_fpret(ciColor, sel_red) * 255),
					(int) (OS.objc_msgSend_fpret(ciColor, sel_green) * 255),
					(int) (OS.objc_msgSend_fpret(ciColor, sel_blue) * 255));
			TGMainToolBar toolBar = TGMainToolBar.getInstance(context);
			toolBar.getControl().setBgColor(bgColor);
			for (TGToolBarSection section : toolBar.getSections()) {
				TGMainToolBarSection mainSection = ((TGMainToolBarSection) section);
				mainSection.getControl().setBgColor(bgColor);
				UITableLayout layout = (UITableLayout) mainSection.getControl().getLayout();
				for (UIControl control : mainSection.getItems()) {
					if (control instanceof SWTControl && ((SWTControl) control).getControl() instanceof Button) {
					    if (layout.get(control, UITableLayout.MINIMUM_PACKED_WIDTH) == null) {
                            layout.set(control, UITableLayout.MINIMUM_PACKED_WIDTH, 32f);
						}
						layout.set(control, UITableLayout.MINIMUM_PACKED_HEIGHT, 24f);
						layout.set(control, UITableLayout.MARGIN, -1f);
						NSButton button = ((NSButton) ((Button) ((SWTControl) control).getControl()).view);
						button.setBezelStyle(8);

					}
				}
			}
			setChildrenBgColor(TGEditToolBar.getInstance(context).getControl(), bgColor);
			window.setBgColor(bgColor);
			TGTabFolder.getInstance(context).getControl().setBgColor(bgColor);
			OS.objc_msgSend(ciColor, OS.sel_release, color.id);

			NSButton toolbarButton = TGCocoa.getStandardWindowButton(nsWindow, NSWindowToolbarButton);
			if (toolbarButton != null) {
				toolbarButton.setTarget( delegate );
				TGCocoa.setControlAction( toolbarButton , sel_toolbarButtonClicked_);
			}
		}
	}

	private static void setChildrenBgColor(UIControl control, UIColor color) {
		control.setBgColor(color);
		if (control instanceof UIContainer) {
			for (UIControl child : ((UIContainer) control).getChildren()) {
			    setChildrenBgColor(child, color);
            }
		}
	}
	
	public void finalize() throws Throwable{
		if( this.delegateRef != 0 ){
			TGCocoa.DeleteGlobalRef( this.delegateRef );
			this.delegateRef = 0;
		}
		if (this.bgColor != null) {
			this.bgColor.dispose();
			this.bgColor = null;
		}
	}
	
	public long callbackProc( long id, long sel, long arg0 ) {
		if ( this.isEnabled() ){
			if ( sel == sel_toolbarButtonClicked_ ) {
				return handleToogleToolbarCommand();
			}
		}
		return TGCocoa.noErr;
	}
	
	public long callbackProc64( long id, long sel, long arg0 ) {
		return this.callbackProc(id, sel, arg0);
	}
	
	public int callbackProc32( int id, int sel, int arg0 ) {
		return (int)this.callbackProc( (long)id, (long)sel, (long)arg0);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public long handleToogleToolbarCommand(){
		MacToolbarAction.toogleToolbar();
		return TGCocoa.noErr;
	}
}
