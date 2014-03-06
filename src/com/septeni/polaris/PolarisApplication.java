package com.septeni.polaris;

import com.septeni.polaris.provider.SharePrefs;

import android.app.Application;

public class PolarisApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		SharePrefs.getInstance().init(this);
	}
}
