package org.herac.tuxguitar.android.fragment;

import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGFragment extends Fragment {
	
	private TGContext context;
	
	public TGFragment(TGContext context) {
		this.context = context;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.onPostCreate(savedInstanceState);
		this.fireEvent(TGFragmentEvent.ACTION_CREATED);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		
		this.onPostCreateOptionsMenu(menu, menuInflater);
		this.fireEvent(TGFragmentEvent.ACTION_OPTIONS_MENU_CREATED);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View createdView = super.onCreateView(inflater, container, savedInstanceState);
		View view = this.onPostCreateView(inflater, container, savedInstanceState, createdView);
		
		this.fireEvent(TGFragmentEvent.ACTION_VIEW_CREATED);
		
		return view;
	}
	
	public void onCreateDrawer(ViewGroup drawerView) {
		if( drawerView.getChildCount() > 0 ) {
			drawerView.removeAllViews();
		}
		
		this.onPostCreateDrawer(drawerView);
		this.fireEvent(TGFragmentEvent.ACTION_DRAWER_CREATED);
	}
	
	public void onPostCreate(Bundle savedInstanceState) {
		// override me
	}
	
	public void onPostCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		// override me
	}
	
	public void onPostCreateDrawer(ViewGroup drawerView) {
		// override me
	}
	
	public View onPostCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View createdView) {
		return createdView;
	}
	
	public TGContext getContext() {
		return this.context;
	}
	
	public void addListener(TGEventListener listener){
		TGEventManager.getInstance(getContext()).addListener(TGFragmentEvent.EVENT_TYPE, listener);
	}
	
	public void removeListener(TGEventListener listener){
		TGEventManager.getInstance(getContext()).removeListener(TGFragmentEvent.EVENT_TYPE, listener);
	}
	
	public void fireEvent(String action) throws TGActionException{
		TGEventManager.getInstance(getContext()).fireEvent(new TGFragmentEvent(this, action));
	}
}