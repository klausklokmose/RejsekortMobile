package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends IntentService {

	public static final String PARAM_MESSAGE = "IN_MESSAGE";
	public static final String ENTER_GEOFENCE = "ENTER GEOFENCE";

	private Handler handler;
	private ArrayList<StationStop> NBstationStops;
	private ArrayList<String> foundSSIDs;
	private boolean inGeofence;
	private int currentTransitID;
	private SharedPreferences pref;

	@Override
	protected void onHandleIntent(Intent i) {
		// TODO Auto-generated method stub
		String message = getMessageFromIntent(i);
		Log.d("PARAMETER SENT TO SERVICE", message);

		if (message.equals(ENTER_GEOFENCE)) {
			Log.d("SERVICE", "STARTING PROCESS");
			
			pref = getApplicationContext().getSharedPreferences("Rejsekortmobile", Context.MODE_MULTI_PROCESS);
			// get check in status
			boolean checkedIn = getUsersCheckInStatus();

			// get user's current location
			String[] location = getUserLocation();
			// get nearby stations and bus stops
			NBstationStops = getNBstationStops();
			if (checkedIn) {
				do {
					// Look up SSIDs at least once
					foundSSIDs = scanForSSIDs();
					
					// check if user has left the geofence
					inGeofence = pref.getBoolean(ENTER_GEOFENCE, false);
				} while (inGeofence);

				// determine the transit ID from the found SSIDs
				currentTransitID = determineIdFromTransit();
				
				// if the ID is found
				if (currentTransitID != -1) {
					setGeofenceForNextLogicalStops(location, currentTransitID);
				} else {
					
					Intent intent = new Intent("dk.aau.rejsekortmobile.CHECK_IN");
					intent.setClass(getApplicationContext(), CheckInOutReceiver.class);
					intent.putExtra(CheckInOutReceiver.CHECKING_IN, true);
					sendBroadcast(intent);
					
				}

			} else { //If user is not checked in
				//Setup geofences for nearby stations and bus stops
				setupGeoFences(NBstationStops);
				
				inGeofence = pref.getBoolean(ENTER_GEOFENCE, false);
				
				if(inGeofence){
					cancelGeofences();
					//ACTION: User entered geofence
					showNotification("Please, remember to check in");
				}else{
					//TODO
					Log.d("MY SERVICE", "NOT IN GEOFENCE");
				}
			}
			//END if message equals ENTER_GEOFENCE
		}else{
			Log.d("MESSAGE TO SERVICE", "Message to service was not recognized: "+message);
		}
	}

	private void showNotification(String str) {
		// Builds the notification and issues it.
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Entered geofence").setContentText(str);
		// removes the notification when it is pressed by the user.
		mBuilder.setAutoCancel(true);
			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);
//			mBuilder.setSound(Uri.parse("android.resource://"
//		            + getApplicationContext().getPackageName() + "/" + R.raw.siren));
		mBuilder.setLights(Color.BLUE, 500, 500);
		long[] pattern = {500,500,500,500,500,500};
		mBuilder.setVibrate(pattern);
		//notification manager
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// set up what will happen when the user presses the notification


		Intent intent = new Intent("dk.aau.rejsekortmobile.CHECK_IN");
		intent.setClass(getApplicationContext(), CheckInOutReceiver.class);
		intent.putExtra(CheckInOutReceiver.CHECKING_IN, true);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		mNotifyMgr.notify(1, mBuilder.build());
		Log.d("Show Notification", CheckInOutReceiver.CHECKING_IN);

	}
	
	private void cancelGeofences() {
		// TODO Auto-generated method stub
		
	}

	private void setGeofenceForNextLogicalStops(String[] location,
			int currentTransitID2) {
		// TODO mocked

	}

	private int determineIdFromTransit() {
		// TODO compare with our mock list of transits

		// return -1 if ID not valid
		return 0;
	}

	private ArrayList<String> scanForSSIDs() {
		Log.d("SCANNING SSID", "SCANNING SSID");
		
		// TODO Start a timer and scan for SSIDs until it expires
		ArrayList<String> ssids = new ArrayList<String>();
		ssids.add("AAU-1x");
		ssids.add("AAU-2x");
		ssids.add("AAU-3x");
		ssids.add("AAU-4x");
		ssids.add("AAU-5x");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ssids;

	}

	private boolean getUsersCheckInStatus() {
		return pref.getBoolean(CheckInOutReceiver.CHECKED_IN, false);
	}

	private void setupGeoFences(ArrayList<StationStop> nBstationStops2) {
		// TODO Auto-generated method stub

	}

	private ArrayList<StationStop> getNBstationStops() {
		// TODO Auto-generated method stub
		ArrayList<StationStop> st = new ArrayList<StationStop>();
		st.add(new StationStop(1, "Flintholm st. (Bus)", 50, new String[] {
				"111", "444" }, 2));
		st.add(new StationStop(2, "Flintholm st.", 100, new String[] { "123",
				"456" }, 2));
		st.add(new StationStop(3, "Flintholm st. (Metro)", 100, new String[] {
				"123", "678" }, 2));
		st.add(new StationStop(4, "Flintholm st. (Bus)", 50, new String[] {
				"111", "445" }, 2));

		return st;
	}

	private String[] getUserLocation() {
		// TODO Auto-generated method stub
		return new String[] { "123", "456" };
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}
	
	private String getMessageFromIntent(Intent intent) {
		return intent.getExtras().getString(PARAM_MESSAGE);
	}

	public MyService() {
		super("MyService");
	}
}
