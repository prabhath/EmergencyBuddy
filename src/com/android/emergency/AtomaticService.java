package com.android.emergency;

import java.util.ArrayList;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.Html;

/*
 * runs as an Android service for automatic emergency informing												
 */

public class AtomaticService extends Service { 

	Location location;
	boolean GPSavailable;
	boolean NerworkLocationAvailable;
	LocationManager locationManager;
	SharedPreferences settings;
	ArrayList<String> phoneNumbers = new ArrayList<String>();
	ArrayList<String> emails = new ArrayList<String>();
	final int MAXCONTACTS = 5; // Maximum No of buddies
	String[] phoneNumbers1 = new String[MAXCONTACTS];
	String[] emails1;
	String mapURL; // map image shown in the email
	double latitude;
	double longitude;
	protected Intent i; // to send email
	Geocoder coder;
	private String adress = "Not Availble";
	private String emailMessage;
	private String smsMessage;
	private int timeInterval = 10000;
	boolean automatic; // This indicates whether result is set to OK or
						// OK_AUTOMATIC
	

	@Override
	public IBinder onBind(Intent arg0) {
		// Does not bind to an activity
		return null;
	}

	@Override
	public void onCreate() {

		settings = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//checks for availability of GPS and network
		GPSavailable = checkGPS(locationManager);
		NerworkLocationAvailable = checkNetwork(locationManager);
		coder = new Geocoder(getApplicationContext());
		if (GPSavailable) {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		} else if (NerworkLocationAvailable) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		for (int i = 1; i <= MAXCONTACTS; i++) { // get buddy phone numbers
			String s = settings.getString("buddy" + i + "Phone", null);
			if (s != null) {
				phoneNumbers.add(s);
			}
		}

		short count = 0; // counter for valid email addresses

		for (int i = 1; i <= MAXCONTACTS; i++) { // get buddy email addresses
			String s = settings.getString("buddy" + i + "Email", null);

			if (s != null) {
				emails.add(s);
				count++;
			}
		}

		emails1 = new String[count]; // Arraylist to array
		for (short j = 0; j < count; j++) {
			emails1[j] = emails.get(j);
		}

		i = new Intent(Intent.ACTION_SEND); // email intent
		i.setType("text/html");
		i.putExtra(Intent.EXTRA_EMAIL, emails1);
		i.putExtra(Intent.EXTRA_SUBJECT, "Emergency");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		super.onCreate();
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			emailMessage = String.format(getString(R.string.message), latitude,
					longitude, adress);
			smsMessage = String.format(getString(R.string.message_SMS),
					latitude, longitude);
			try {
				/*
				 * Geocorder not available in emulator
				 * 
				 * adress=coder.getFromLocation(latitude, longitude,
				 * 1).get(0).toString();
				 */
				emailMessage = String.format(getString(R.string.message),
						latitude, longitude, adress);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			mapURL = "http://maps.googleapis.com/maps/api/staticmap?center="
					+ latitude
					+ ","
					+ longitude
					+ "&zoom=14&size=512x512&maptype=roadmap%20&markers=color:blue%7Clabel:S%7C"
					+ latitude + "," + longitude + "&sensor=false";
		} else {
			emailMessage = getString(R.string.messege_short); // location not
																// available
			smsMessage = getString(R.string.messege_short);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			automatic = extras.getBoolean("automatic");
		}
		i.putExtra(
				Intent.EXTRA_TEXT,
				emailMessage
						+ Html.fromHtml("<img src=\"http://lms.uom.lk/moodle192/pix/i/course.gif\" />"));
		for (String no : phoneNumbers) { // send SMSs to all numbers
			sendSMS(no, smsMessage);
		}

		try {
			getApplication().startActivity(i); // starting email
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}

		if (automatic) {	//If repeated massages are on repeated task must be started
			java.util.Timer t1 = new java.util.Timer(true);
			RepeatedTask task = new RepeatedTask(phoneNumbers, smsMessage,settings);
			t1.schedule(task, timeInterval, timeInterval);
		}

		return Service.START_STICKY;
	}

	// check GPS availability
	private boolean checkGPS(LocationManager locationManager) {
		boolean b = false;
		try {
			b = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		return b;
	}

	//check network availability
	private boolean checkNetwork(LocationManager locationManager) {
		boolean b = false;
		try {
			b = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		return b;
	}

	// sends an SMS message to another device
	private void sendSMS(String phoneNumber, String message) {
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
				AtomaticService.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, pi, null);
	}

	//THis sub class is for repeated task
	class RepeatedTask extends TimerTask {

		private ArrayList<String> phoneNumbers;
		private String smsMessage;
		private SharedPreferences settings;

		public RepeatedTask(ArrayList<String> phoneNumbers, String smsMessage,SharedPreferences settings) {
			this.phoneNumbers = phoneNumbers;
			this.smsMessage = smsMessage;
			this.settings=settings;

		}

		public void run() {
			if(!settings.getBoolean("emergency_mode", true)){	//stop if emergency mode is off
				this.cancel();
				stopSelf();
			}
			for (String no : phoneNumbers) { // send SMSs to all numbers
				sendSMS(no, smsMessage);
			}
					
		}
	}

}
