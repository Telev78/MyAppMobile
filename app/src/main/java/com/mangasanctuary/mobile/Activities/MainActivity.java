package com.mangasanctuary.mobile.Activities;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.User;

public class MainActivity extends TabActivity  {
	
	User user = User.getInstance();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec; 
        Intent intent; 
 
        intent = new Intent().setClass(this, PlanningActivity.class);
        spec = tabHost
                .newTabSpec("PlanningActivity")
                .setIndicator(createTabView(this, getString(R.string.planning)))
                .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MissingActivity.class);
        spec = tabHost
                .newTabSpec("MissingActivity")
                .setIndicator(createTabView(this, getString(R.string.missing)))
                .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, FullPlanningActivity.class);
    	  spec = tabHost
                .newTabSpec("FullPlanningActivity")
                .setIndicator(createTabView(this, getString(R.string.fullplanning)))
                .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, BooksCollectionActivity.class);
    	  spec = tabHost
                .newTabSpec("BooksCollectionActivity")
                .setIndicator(createTabView(this, getString(R.string.bookscollection)))
                .setContent(intent);
        tabHost.addTab(spec);
 
        intent = new Intent().setClass(this, DVDCollectionActivity.class);
        spec = tabHost
                .newTabSpec("DVDCollectionActivity")
                .setIndicator(createTabView(this, getString(R.string.dvdcollection)))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
	    
	    
	}
	
	@Override
	public void onStop(){
		super.onStop();
		try {
			//CustomHttpClient.executeHttpGet(getString(R.string.URL) +  getString(R.string.disconnect).replace("[SID]", user.getSID()), "UTF-8");
		} catch (Exception e) {
			Log.e(getString(R.string.app_name), e.toString());
		}
	}
	
	private static View createTabView(Context context, String tabText) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null, false);
        TextView tv = (TextView) view.findViewById(R.id.tabTitleText);
        tv.setText(tabText);
        return view;
    }

}


