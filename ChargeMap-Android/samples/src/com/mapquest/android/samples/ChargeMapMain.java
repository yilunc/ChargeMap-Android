package com.mapquest.android.samples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.mapquest.android.Geocoder;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.MapActivity;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MyLocationOverlay;

public class ChargeMapMain extends MapActivity {

	private MapView myMap;
	private MyLocationOverlay myLocationOverlay;
	private Button followMeButton;

	private double Lat;
	private double Lon;

	private SparseArray<ArrayList<String[]>> locationInfo;
	private HashMap<String, HashMap<String, ArrayList<String[]>>> stateToInfo;
	private Geocoder addrGetter = new Geocoder(getBaseContext());

	private static final UUID APP_UUID = UUID
			.fromString("60f84713-0132-49f4-9a59-d37a35a26bed");

	private static final int DATA_KEY = 0;
	private static final int NAME_KEY = 0;
	private static final int ADDRESS_KEY = 1;
	private static final int ZIP_KEY = 3;
	private static final int HOURS_KEY = 4;
	private static final int CONTYPE_KEY = 5;

	private Address currLocation;
	private int refPostal;

	private int index1;
	private int index2;
	private int index3;

	private double compVal1;
	private double compVal2;
	private double compVal3;
	private double thisDist;

	private static final int BUFFER_LENGTH = 32;

	private TextView statusLabel;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.followme_map);
		statusLabel = (TextView) findViewById(R.id.buttonLabel1);

		InputStream inputStream = getResources().openRawResource(R.raw.v1);
		CSVParser csvFile = new CSVParser(inputStream);

		locationInfo = csvFile.read();
		stateToInfo = csvFile.getStateToData();

		followMeButton = (Button) findViewById(R.id.followMeButton);
		followMeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myLocationOverlay.setFollowing(true);
			}
		});

		statusLabel.setText("Pebble Connected");

		setupMapView();
		setupMyLocation();
	}

	private void setupMapView() {
		this.myMap = (MapView) findViewById(R.id.map);
	}

	/* Mapquest function to get current phone location. */
	protected void setupMyLocation() {
		this.myLocationOverlay = new MyLocationOverlay(this, myMap);

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				GeoPoint currentLocation = myLocationOverlay.getMyLocation();
				if (currentLocation != null) {
					Lat = currentLocation.getLatitude();
					Lon = currentLocation.getLongitude();
				}
				myMap.getController().animateTo(currentLocation);
				myMap.getController().setZoom(14);
				myMap.getOverlays().add(myLocationOverlay);
				myLocationOverlay.setFollowing(true);
			}
		});
	}

	/* Math utlity function to find distances between two coordinates. */
	private double distance(double lat1, double lon1, double lat2, double lon2,
			char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	/*
	 * Handles the clicking of the get info button on the pebble and the data
	 * processing that must occur on the phone. Calculates current position, and
	 * positions of three nearest charing locations.
	 */
	@Override
	protected void onResume() {
		myLocationOverlay.enableMyLocation();
		super.onResume();

		PebbleKit.registerReceivedDataHandler(this,
				new PebbleKit.PebbleDataReceiver(APP_UUID) {

					@Override
					public void receiveData(final Context context,
							final int transactionId, final PebbleDictionary data) {
						PebbleKit.sendAckToPebble(getApplicationContext(),
								transactionId);
						int received = Integer.parseInt(""
								+ data.getUnsignedIntegerAsLong(0));
						Log.i(getLocalClassName(), "Received value = "
								+ received + " for key: " + DATA_KEY
								+ "Initiating data transfer..");

						switch (received) {
						case ZIP_KEY: {
							followMeButton.performClick();

							setupMyLocation();

							try {
								currLocation = addrGetter.getFromLocation(Lat,
										Lon, 1).get(0);
								refPostal = Integer.parseInt(currLocation
										.getPostalCode());
							} catch (IOException e) {
								Log.i("ordering algorthm",
										"Getting closest postal failed. " + e);
							}

							Log.i("ordering algorithm", "Success, Postal: "
									+ refPostal);

							compVal1 = 10000;
							compVal2 = 10000;
							compVal3 = 10000;

							index1 = locationInfo.size();
							index2 = index1;
							index3 = index1;

							if (locationInfo.get(refPostal) != null) {
								for (int i = 0; i < locationInfo.get(refPostal)
										.size(); i++) {
									thisDist = distance(
											Lat,
											Lon,
											Integer.parseInt(locationInfo.get(
													refPostal).get(i)[1]),
											Integer.parseInt(locationInfo.get(
													refPostal).get(i)[2]), 'M');
									if (thisDist < compVal1) {
										compVal3 = compVal2;
										compVal2 = compVal1;
										compVal1 = thisDist;
										index3 = index2;
										index2 = index1;
										index1 = i;
									} else if (thisDist < compVal2) {
										compVal3 = compVal2;
										compVal2 = thisDist;
										index3 = index2;
										index2 = i;
									} else if (thisDist < compVal3) {
										compVal3 = thisDist;
										index3 = i;
									}
								}
								Log.i("ordering algorithm",
										"Main - Success, I1: " + index1
												+ " I2: " + index2 + " I3: "
												+ index3);
								sendDataDistributed(index1, index2, index3,
										"main");
							} else {
								if (stateToInfo.containsKey(currLocation
										.getAdminArea())
										&& stateToInfo.get(
												currLocation.getAdminArea())
												.containsKey(
														currLocation
																.getLocality())) {
									for (int i = 0; i < stateToInfo
											.get(currLocation.getAdminArea())
											.get(currLocation.getLocality())
											.size(); i++) {
										thisDist = distance(
												Lat,
												Lon,
												Integer.parseInt(locationInfo
														.get(refPostal).get(i)[1]),
												Integer.parseInt(locationInfo
														.get(refPostal).get(i)[2]),
												'M');
										if (thisDist < compVal1) {
											compVal3 = compVal2;
											compVal2 = compVal1;
											compVal1 = thisDist;
											index3 = index2;
											index2 = index1;
											index1 = i;
										} else if (thisDist < compVal2) {
											compVal3 = compVal2;
											compVal2 = thisDist;
											index3 = index2;
											index2 = i;
										} else if (thisDist < compVal3) {
											compVal3 = thisDist;
											index3 = i;
										}
									}
									Log.i("ordering algorithm",
											"Backup - Success, I1: " + index1
													+ " I2: " + index2
													+ " I3: " + index3);
									sendDataDistributed(index1, index2, index3,
											"backup");
								} else {
									Log.i("ordering algorithm",
											"FATAL ERROR. NO NEAR CHARGE STATIONS FOUND. Maybe shoulda bought a hybrid?");
								}
							}
						}
						}
					}
				});
	}

	/* Send charging location data to the pebble piece by piece. */
	private void sendDataDistributed(int index1, int index2, int index3,
			String type) {
		ArrayList<String[]> temp;
		if (type.equals("main")) {
			temp = locationInfo.get(refPostal);
		} else if (type.equals("backup")) {
			temp = stateToInfo.get(currLocation.getAdminArea()).get(
					currLocation.getLocality());
		} else {
			temp = locationInfo.get(refPostal);
		}

		sendStringToPebble(temp.get(index1)[3], NAME_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index1)[4], ADDRESS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble("" + temp.get(index1)[0], ZIP_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index1)[8], HOURS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index1)[9], CONTYPE_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i("OutBox, sendStringToPebble", "Sent first package:- Success");
		sendStringToPebble(temp.get(index2)[3], NAME_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index2)[4], ADDRESS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble("" + temp.get(index2)[0], ZIP_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index2)[8], HOURS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index2)[9], CONTYPE_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i("OutBox, sendStringToPebble", "Sent second package:- Success");
		sendStringToPebble(temp.get(index3)[3], NAME_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index3)[4], ADDRESS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble("" + temp.get(index3)[0], ZIP_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index3)[8], HOURS_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendStringToPebble(temp.get(index3)[9], CONTYPE_KEY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i("OutBox, sendStringToPebble", "Sent third package:- Success");
		Log.i("Finished: Packages Sent: ",
				temp.get(index1)[1] + " | " + temp.get(index2)[1] + " | "
						+ temp.get(index3)[1]);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean isRouteDisplayed() {
		return false;
	}

	/* Method that does the sending of data to the Pebble watch.*/
	private void sendStringToPebble(String message, final int KEY) {
		if (message.length() < BUFFER_LENGTH) {
			Log.i("sendStringToPebble()", "Sent: " + message);

			// Create a PebbleDictionary
			PebbleDictionary dictionary = new PebbleDictionary();

			// Store a string in the dictionary using the correct key
			dictionary.addString(KEY, message);

			// Send the Dictionary
			PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID,
					dictionary);

		} else {
			Log.i("sendStringToPebble()", "String too long! " + message);
		}
	}

}