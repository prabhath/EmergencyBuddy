package com.android.emergency;

import android.app.Activity;
import android.location.LocationManager;

/*
 * Super class of activity classes.
 */
public class EmegencyBuddyActivity extends Activity {	

	
	//app constants
	public static final String PREFERENCES = "Prefs";
	public static final String PREFERENCES_POLICE_NO="police";
	public static final String PREFERENCES_AMBULANCE_NO="ambulance";
	public static final String PREFERENCES_FIRE_BRIGADE_NO="fire_brigade";
	public static final String PREFERENCES_EMERGENCY_MODE="emergency_mode";
	
	public static final String PREFERENCES_BUDDY1_NAME="buddy1";
	public static final String PREFERENCES_BUDDY2_NAME="buddy2";
	public static final String PREFERENCES_BUDDY3_NAME="buddy3";
	public static final String PREFERENCES_BUDDY4_NAME="buddy4";
	public static final String PREFERENCES_BUDDY5_NAME="buddy5";
	
	public static final String PREFERENCES_BUDDY1_PHONE="buddy1Phone";
	public static final String PREFERENCES_BUDDY2_PHONE="buddy2Phone";
	public static final String PREFERENCES_BUDDY3_PHONE="buddy3Phone";
	public static final String PREFERENCES_BUDDY4_PHONE="buddy4Phone";
	public static final String PREFERENCES_BUDDY5_PHONE="buddy5Phone";
	
	public static final String PREFERENCES_BUDDY1_EMAIL="buddy1Email";
	public static final String PREFERENCES_BUDDY2_EMAIL="buddy2Email";
	public static final String PREFERENCES_BUDDY3_EMAIL="buddy3Email";
	public static final String PREFERENCES_BUDDY4_EMAIL="buddy4Email";
	public static final String PREFERENCES_BUDDY5_EMAIL="buddy5Email";
	
	public static final int CANCEL=444;
	public static final int OK=555;
	public static final int OK_AUTOMATIC=666;
	
	
	//Check GPS functionality of the device
	public boolean checkGPS(LocationManager locationManager) {
		boolean b=false;
		try {
			b = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		return b;
	}
	
	//check network location service
	public boolean checkNetwork(LocationManager locationManager) {
		boolean b=false;
		try {
			b = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		return b;
	}
	


}
