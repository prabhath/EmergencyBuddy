package com.android.emergency;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*
 *This activity implements the functionality of the main menu
 */

public class MenuActivity extends EmegencyBuddyActivity {
	
	SharedPreferences settings;
	SharedPreferences.Editor prefEditor;
	protected String[] buddy_phone;
	protected String[] buddy_email;
	LocationManager locMgr;
	ToggleButton toggleEmergency;
	TextView text;
	LocationUpdater locationUpdater;

	private static final int CONFIRM_RESULT = 2000;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		settings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		prefEditor = settings.edit();

		buddy_phone = new String[5];
		buddy_email = new String[5];
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		toggleEmergency = (ToggleButton) findViewById(R.id.toggleEmergency);
		text = (TextView) findViewById(R.id.textViewStatus);

		locationUpdater = new LocationUpdater();
		
		if(!settings.contains(PREFERENCES_EMERGENCY_MODE)){
			prefEditor.putBoolean(PREFERENCES_EMERGENCY_MODE, false);
			toggleEmergency.setChecked(false);
			prefEditor.commit();
		}else if(settings.getBoolean(PREFERENCES_EMERGENCY_MODE, true)){
			toggleEmergency.setChecked(true);
		}		

//		locationUpdater.execute(locMgr);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);
		menu.findItem(R.id.help_menu_item).setIntent(
				new Intent(this, HelpActivity.class));
		menu.findItem(R.id.settings_menu_item).setIntent(
				new Intent(this, SettingsActivity.class));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		startActivity(item.getIntent());
		return true;
	}

	
	//Dial police
	public void onCallPoliceButtonClicked(View view) {
		String url = "tel:" + settings.getString(PREFERENCES_POLICE_NO, "119");
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	//Dial Ambulance
	public void onCallAmbulanceButtonClicked(View view) {
		String url = "tel:"
				+ settings.getString(PREFERENCES_AMBULANCE_NO, "911");
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	
	//Dial fire brigade
	public void onCallFire_brigadeButtonClicked(View view) {
		String url = "tel:"
				+ settings.getString(PREFERENCES_FIRE_BRIGADE_NO, "911");
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
	
	//Invoking weather services
	public void onWeatherInfoRequest(View view) {
		
		startActivity(new Intent(this,WeatherActivity.class));
	}
	

	public void onEmergencyModechanged(View view) {
		//need to save emergency status because automatic informing done even application is not runnig once started
		if (!settings.getBoolean(PREFERENCES_EMERGENCY_MODE, false)) {
			prefEditor.putBoolean(PREFERENCES_EMERGENCY_MODE, true);
			prefEditor.commit();
			text.setText(R.string.emergency_on);
			startActivityForResult(new Intent(MenuActivity.this,
					ConfirmEmergencyActivity.class), CONFIRM_RESULT);
		} else {
			cancelEmeregency();
		}
	}

	private void cancelEmeregency() {	//to change to normal mode
		prefEditor.putBoolean(PREFERENCES_EMERGENCY_MODE, false);
		prefEditor.commit();
		text.setText(R.string.emergency_off);

	    
	   
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CONFIRM_RESULT) {
			if (resultCode == OK_AUTOMATIC) {	//automatic messages on

			} else if (resultCode == OK) {		//automatic messages off

			} else if (resultCode == CANCEL){							//Cancelled
				toggleEmergency.setChecked(false);
				Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
				cancelEmeregency();
			}
		}

	}

	
	//location finding

	public void onFindHospitalclick(View view) {
		Location recentLoc = locMgr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double lat = recentLoc.getLatitude();
		double lon = recentLoc.getLongitude();
		String geoURI = String.format("geo:%f,%f?q=hospital", lat, lon);
		Uri geo = Uri.parse(geoURI);
		Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
		startActivity(geoMap);
	}

	public void onFindPoliceStaionClick(View view) {
		Location recentLoc = locMgr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double lat = recentLoc.getLatitude();
		double lon = recentLoc.getLongitude();
		String geoURI = String.format("geo:%f,%f?q=police", lat, lon);
		Uri geo = Uri.parse(geoURI);
		Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
		startActivity(geoMap);
	}
	
	/////////////////////////////////////////////////////////
	
	
	
	//This asynchronous task is used to asynchronously update GPS location
	private class LocationUpdater extends AsyncTask<Object, Void, Boolean> {		

		boolean GPSEnabled;
		boolean NetworkEnabled;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Object... arg0) {
			LocationManager locationManager = (LocationManager) arg0[0];

			GPSEnabled = checkGPS(locationManager);
			NetworkEnabled = checkNetwork(locationManager);

			LocationListener listener = new LocationListener() {
			
				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(Location arg0) {
					// TODO Auto-generated method stub

				}
			};

			if (GPSEnabled) {
				locationManager.requestSingleUpdate(
						locationManager.GPS_PROVIDER, listener, null);
				return true;
			} else if (NetworkEnabled) {
				locationManager.requestSingleUpdate(
						locationManager.NETWORK_PROVIDER, listener, null);
				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(MenuActivity.this, "Location updated!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MenuActivity.this, "Location update failed!",
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

}
