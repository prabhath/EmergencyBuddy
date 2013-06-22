package com.android.emergency;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TextView;

/*
 *This class downloads the weather data and displays
 */

public class WeatherActivity extends EmegencyBuddyActivity {
	LocationManager locMgr;
	int mProgressCounter = 0;
	WeatherInfoReceiver weatherInfoReciever;
	HashMap<String, String> weatherInfo;
	double lat;
	double lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.weather);
		weatherInfo=new HashMap<String, String>();
		weatherInfo.put("snow", "No Snow");
		weatherInfo.put("stroms", "No strom warnings");
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location recentLoc = locMgr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		lat = recentLoc.getLatitude();
		lon = recentLoc.getLongitude();
		weatherInfoReciever = new WeatherInfoReceiver();
		weatherInfoReciever.execute(weatherInfo);
	}
	
	public WeatherInfoReceiver getAllScoresDownloader() {
		return weatherInfoReciever;
	}

	public void onCLickEarthQuackeAndTsnamiInfo(View view) {	//Tsunami information
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://ptwc.weather.gov/ptwc/index.php?region=3"));
		startActivity(intent);
	}

	public class WeatherInfoReceiver extends AsyncTask<Object, String, Boolean> {		//Do in BAckground
		private static final String DEBUG_TAG = "ScoreDownloaderTask";
		TableLayout table;

		@Override
		protected void onCancelled() {
			Log.i(DEBUG_TAG, "onCancelled");
			mProgressCounter--;
			if (mProgressCounter <= 0) {
				mProgressCounter = 0;
				WeatherActivity.this
						.setProgressBarIndeterminateVisibility(false);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			//updates the UI
			Log.i(DEBUG_TAG, "onPostExecute");
			mProgressCounter--;
			if (mProgressCounter <= 0) {
				mProgressCounter = 0;
				WeatherActivity.this
						.setProgressBarIndeterminateVisibility(false);
				TextView temperature = (TextView) findViewById(R.id.textViewTemperature);
				temperature.setText("Temperature(Celicius): "+weatherInfo.get("temperature"));
				TextView humidity = (TextView) findViewById(R.id.textViewHumidity);
				humidity.setText("Humidity: "+weatherInfo.get("humidity"));
				TextView pressure = (TextView) findViewById(R.id.textViewPressure);
				pressure.setText("Pressure: "+weatherInfo.get("pressure"));
				TextView windspeed = (TextView) findViewById(R.id.textViewWindSpeed);
				windspeed.setText("WIndspeed(kmph): "+weatherInfo.get("windspeed"));
				TextView stroms=(TextView)findViewById(R.id.textViewStrom);
				stroms.setText("Possible Stroms: "+weatherInfo.get("stroms"));
				TextView snow=(TextView)findViewById(R.id.textViewSnow);
				snow.setText("Snow: "+weatherInfo.get("snow"));
				TextView rain=(TextView)findViewById(R.id.textViewRain);
				rain.setText("Rain(mm): "+weatherInfo.get("rain"));
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressCounter++;
			WeatherActivity.this.setProgressBarIndeterminateVisibility(true);
		}

		@Override	//parse the data in background
		protected Boolean doInBackground(Object... params) {
			HashMap<String, String> weatherInfo = (HashMap<String, String>) params[0];
			boolean result = false;
			XmlPullParser weatherData;
			try {
				URL xmlUrl = new URL(	//weather data request
						"http://free.worldweatheronline.com/feed/weather.ashx?q="+lat+","+lon+"&format=xml&num_of_days=2&key=3fd1770096065904120103");
				weatherData = XmlPullParserFactory.newInstance()
						.newPullParser();
				weatherData.setInput(xmlUrl.openStream(), null);
			} catch (XmlPullParserException e) {
				weatherData = null;
			} catch (IOException e) {
				weatherData = null;
			}

			if (weatherData != null) {
				try {
					processData(weatherData,weatherInfo);
				} catch (XmlPullParserException e) {
					Log.e(DEBUG_TAG, "Pull Parser failure", e);
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "IO Exception parsing XML", e);
				}
			}

			return result;
		}

		/**
		 * Churn through an XML score information and populate a
		 * {@code TableLayout}
		 * 
		 * @param parser
		 *            A standard {@code XmlPullParser} containing the scores
		 * @throws XmlPullParserException
		 *             Thrown on XML errors
		 * @throws IOException
		 *             Thrown on IO errors reading the XML
		 */
		public void processData(XmlPullParser parser,HashMap<String, String> weatherInfo)
				throws XmlPullParserException, IOException {
			int eventType = -1;
			int eventType2 = -1;
			int eventType3 = -1;
			boolean bFoundScores = false;
			

			// Find records from XML
			while (eventType2 != XmlResourceParser.END_DOCUMENT) {
				if (eventType2 == XmlResourceParser.START_TAG) {

					// Get the name of the tag 
					String strName2 = parser.getName();

					if (strName2.equals("data")) {
						while (eventType3 != XmlResourceParser.END_DOCUMENT) {
							if (eventType3 == XmlResourceParser.START_TAG) {

								// Get the name of the tag 
								String strName = parser.getName();

								if (strName.equals("current_condition")) {
									while (eventType != XmlResourceParser.END_DOCUMENT) {
										if (eventType == XmlResourceParser.START_TAG) {
											String strName1 = parser.getName();
											if (strName1.equals("temp_C")) {
												weatherInfo.put("temperature", parser.nextText());
											} else if (strName1
													.equals("windspeedKmph")) {
												weatherInfo.put("windspeed", parser.nextText());
											} else if (strName1
													.equals("humidity")) {
												weatherInfo.put("humidity", parser.nextText());
											} else if (strName1
													.equals("pressure")) {
												weatherInfo.put("pressure", parser.nextText());
											} else if (strName1
													.equals("precipMM")) {
												weatherInfo.put("rain", parser.nextText());
											} else if (strName1
													.equals("cloudcover")) {
												break;
											}
										}
										eventType = parser.next();
									}
									break;
								}
							}
							eventType3 = parser.next();
						}
					}
					break;
				}
				eventType2 = parser.next();				
			}

			
			if (bFoundScores == false) {
				publishProgress();
			}
		}

	}

}
