package com.android.emergency;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/*
 * This class implements the functionalities of settings activity
 */
public class SettingsActivity extends EmegencyBuddyActivity {

	//to distinguish contact picker results
	private static final int CONTACT_PICKER_RESULT1 = 1001;
	private static final int CONTACT_PICKER_RESULT2 = 1002;
	private static final int CONTACT_PICKER_RESULT3 = 1003;
	private static final int CONTACT_PICKER_RESULT4 = 1004;
	private static final int CONTACT_PICKER_RESULT5 = 1005;

	SharedPreferences settings;
	SharedPreferences.Editor prefEditor;
	EditText buddy1;
	EditText buddy2;
	EditText buddy3;
	EditText buddy4;
	EditText buddy5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_settings);

		TabHost tabHost = (TabHost) findViewById(R.id.TabHost1);	//tabs in the UI
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Buddies",getResources().getDrawable(android.R.drawable.star_on));

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Emergency Contacts",getResources().getDrawable(android.R.drawable.star_on));
		spec2.setContent(R.id.tab2);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);

		settings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		prefEditor = settings.edit();

		//emergency contacts. If not set default values will selected
		EditText policeNo = (EditText) findViewById(R.id.editPoliceNo);
		policeNo.setText(settings.getString(PREFERENCES_POLICE_NO, "119 (Default)"));

		EditText ambulanceNo = (EditText) findViewById(R.id.editAmbulanceNo);
		ambulanceNo.setText(settings
				.getString(PREFERENCES_AMBULANCE_NO, "225 (Default)"));

		EditText fireBrigadeNo = (EditText) findViewById(R.id.editFireBrigadeNo);
		fireBrigadeNo.setText(settings.getString(PREFERENCES_FIRE_BRIGADE_NO, "110 (Default)"));
		
		
		//UI components
		buddy1=(EditText) findViewById(R.id.editTextBuddy1);
		buddy2=(EditText) findViewById(R.id.editTextBuddy2);
		buddy3=(EditText) findViewById(R.id.editTextBuddy3);
		buddy4=(EditText) findViewById(R.id.editTextBuddy4);
		buddy5=(EditText) findViewById(R.id.editTextBuddy5);
		
		buddy1.setText(settings.getString(PREFERENCES_BUDDY1_NAME, "Not Set"));
		buddy2.setText(settings.getString(PREFERENCES_BUDDY2_NAME, "Not Set"));
		buddy3.setText(settings.getString(PREFERENCES_BUDDY3_NAME, "Not Set"));
		buddy4.setText(settings.getString(PREFERENCES_BUDDY4_NAME, "Not Set"));
		buddy5.setText(settings.getString(PREFERENCES_BUDDY5_NAME, "Not Set"));

	}

	//each method will run when selecting each buddy
	public void doLaunchContactPicker1(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT1);
	}

	public void doLaunchContactPicker2(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT2);
	}

	public void doLaunchContactPicker3(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT3);
	}

	public void doLaunchContactPicker4(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT4);
	}

	public void doLaunchContactPicker5(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT5);
	}
	
	////////////////////////////////////////////////////////////////////////
	
	//changing emergency contacts

	public void doSavePoliceNo(View view) {
		EditText policeNo = (EditText) findViewById(R.id.editPoliceNo);
		String s = policeNo.getText().toString();
		prefEditor.putString(PREFERENCES_POLICE_NO, s);
		prefEditor.commit();
		showToast();
	}

	public void doSaveFireBrigadeNo(View view) {
		EditText policeNo = (EditText) findViewById(R.id.editFireBrigadeNo);
		String s = policeNo.getText().toString();
		prefEditor.putString(PREFERENCES_FIRE_BRIGADE_NO, s);
		prefEditor.commit();
		showToast();
	}

	public void doSaveAmbulanceNo(View view) {
		EditText policeNo = (EditText) findViewById(R.id.editAmbulanceNo);
		String s = policeNo.getText().toString();
		prefEditor.putString(PREFERENCES_AMBULANCE_NO, s);
		prefEditor.commit();
		showToast();
	}
	
	///////////////////////////////////////////////////////////////////

	private void showToast() {		//UI method
		Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//This method will identify which buddy is altered and save the changes and updates the UI
		//This will run when a user selected a contact from the contacts list
		if (resultCode == RESULT_OK) {
			Uri result = data.getData();
			String id = result.getLastPathSegment();
			String email = null;
			String phone = null;
			String name = null;
			//Querying email and phone numbers from the result
			Cursor cursor = getContentResolver().query(Email.CONTENT_URI, null,
					Email.CONTACT_ID + "=?", new String[] { id }, null);
			if (cursor.moveToFirst()) {
				int emailIdx = cursor.getColumnIndex(Email.DATA);
				email = cursor.getString(emailIdx);
			}
			cursor = getContentResolver().query(Phone.CONTENT_URI, null,
					Phone.CONTACT_ID + "=?", new String[] { id }, null);
			if (cursor.moveToFirst()) {
				int phoneIdx = cursor.getColumnIndex(Phone.DATA);
				phone = cursor.getString(phoneIdx);
				int nameIdx=cursor.getColumnIndex(Phone.DISPLAY_NAME);
				name=cursor.getString(nameIdx);
			}
		
			//check the result and proceed
			switch (requestCode) {
			case CONTACT_PICKER_RESULT1:
				if (email != null) {
					prefEditor.putString(PREFERENCES_BUDDY1_EMAIL, email);
					prefEditor.commit();
				}
				if (phone != null) {
					prefEditor.putString(PREFERENCES_BUDDY1_PHONE, phone);
					prefEditor.putString(PREFERENCES_BUDDY1_NAME, name);
					prefEditor.commit();
					buddy1.setText(name);
				}
				break;
			case CONTACT_PICKER_RESULT2:
				if (email != null) {
					prefEditor.putString(PREFERENCES_BUDDY2_EMAIL, email);
					prefEditor.commit();
				}
				if (phone != null) {
					prefEditor.putString(PREFERENCES_BUDDY2_PHONE, phone);
					prefEditor.putString(PREFERENCES_BUDDY2_NAME, name);
					prefEditor.commit();
					buddy2.setText(name);
				}
				break;
			case CONTACT_PICKER_RESULT3:
				if (email != null) {
					prefEditor.putString(PREFERENCES_BUDDY3_EMAIL, email);
					prefEditor.commit();
				}
				if (phone != null) {
					prefEditor.putString(PREFERENCES_BUDDY3_PHONE, phone);
					prefEditor.putString(PREFERENCES_BUDDY3_NAME, name);
					prefEditor.commit();
					buddy3.setText(name);
				}
				break;
			case CONTACT_PICKER_RESULT4:
				if (email != null) {
					prefEditor.putString(PREFERENCES_BUDDY4_EMAIL, email);
					prefEditor.commit();
				}
				if (phone != null) {
					prefEditor.putString(PREFERENCES_BUDDY4_PHONE, phone);
					prefEditor.putString(PREFERENCES_BUDDY4_NAME, name);
					prefEditor.commit();
					buddy4.setText(name);
				}
				break;
			case CONTACT_PICKER_RESULT5:
				if (email != null) {
					prefEditor.putString(PREFERENCES_BUDDY5_EMAIL, email);
					prefEditor.commit();
				}
				if (phone != null) {
					prefEditor.putString(PREFERENCES_BUDDY5_PHONE, phone);
					prefEditor.putString(PREFERENCES_BUDDY5_NAME, name);
					prefEditor.commit();
					buddy5.setText(name);
				}
				break;
			}
		} else {
			//handle failure
			Log.w("Buddy Error", "Warning: activity result not ok");
		}
	}

	

}
