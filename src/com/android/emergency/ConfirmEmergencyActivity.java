package com.android.emergency;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/*
 * This activity runs when emergency on button clicked
 */
public class ConfirmEmergencyActivity extends EmegencyBuddyActivity { 
	LocationManager locationManager;
	double latitude;
	double longitude;
	boolean GPSavailable;
	boolean NerworkLocationAvailable;
	Location location;
	String message;
	boolean isResultSet; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		message = getString(R.string.messege_short); // to be displayed if
														// location is unavailable
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		GPSavailable = checkGPS(locationManager);
		NerworkLocationAvailable = checkNetwork(locationManager);
		setLocation(); // initialize location. Null if not available
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_mergency_informing);
		EditText text = (EditText) findViewById(R.id.editTextMessage);
		text.setText(message);
	}

	public void onClickSend(View view) {
		CheckBox radio_automatic_on = (CheckBox) findViewById(R.id.radioButtonAutomaicOn);
		Intent serviceIntent = new Intent(this, AtomaticService.class); // starting automatic emergency informing service
		if (radio_automatic_on.isChecked()) {
			setResult(OK_AUTOMATIC);
			serviceIntent.putExtra("automatic", true);
		} else {
			setResult(OK);
			serviceIntent.putExtra("automatic", false);
		}
		isResultSet = true;
		Toast.makeText(this, "SMS sent, Proceeding to Email!",
				Toast.LENGTH_SHORT).show();
		
		startService(serviceIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// Cancelled by pressing the back button on the device
		if (!isResultSet) {
			setResult(CANCEL);
		}
		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void setLocation() {
		if (GPSavailable) {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		} else if (NerworkLocationAvailable) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			message = String.format(getString(R.string.message_SMS), latitude,
					longitude);

		}
	}

}
