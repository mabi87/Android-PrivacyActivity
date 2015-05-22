package com.mabi87.privacyactivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.List;

// Super class of all secure activity that will you create
public abstract class PrivacyActivity extends ActionBarActivity {
	protected static boolean isBackFromHomeKey = false;					// if back from home key
	private final String packageName = "com.mabi87.privacyactivity";	// type your package name
	private ScreenReceiver mReceiver;									// screen off
	
	protected Context mContext;
	protected SharedPreferencesManager mSharedPreferencesManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		mSharedPreferencesManager = new SharedPreferencesManager(mContext);
		
		// create screen off receiver and register
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        
        isBackFromHomeKey = false;
		ScreenReceiver.SCREEN_OFFED = false;
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		
		// check if come from screen off or back from home and password has set
		// and start activity
		if ((ScreenReceiver.SCREEN_OFFED || isBackFromHomeKey) && mSharedPreferencesManager.hasPassword() && !PasswordActivity.PASSWORD_ACTIVITY_STARTED) {
			startActivity(new Intent(mContext, PasswordActivity.class));
		}
		isBackFromHomeKey = false;
		ScreenReceiver.SCREEN_OFFED = false;
	}

	@Override
	public void onStop() {
		ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);

		// check if this activity comes from other application package
		for (RunningTaskInfo info : list) {
			if (info.baseActivity.getClassName().indexOf(packageName) < 0) {
				isBackFromHomeKey = true;
			}
		}
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister screen off receiver
		if(mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

}
