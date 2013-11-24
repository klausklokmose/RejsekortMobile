package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	private ArrayList<StationStop> stationStops;
	private MOT current_MOT;

	@ViewById(R.id.checkInImg)
	ImageView checkInImg;

	@ViewById(R.id.geofenceToggle)
	ToggleButton geofenceToggle;

	@ViewById(R.id.progressBarSpinner)
	ProgressBar progressBarSpinner;

	@ViewById(R.id.addSSIDbutton)
	Button addSSIDbutton;

	@ViewById(R.id.checkout)
	Button checkout;
	
//	@ViewById(R.id.listView)
//	ListView listView;

	public static User user = new User(1337, "Freddy Mercury");
	private ArrayList<StationStop> visibleList;
	private MyAdapter aa;
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		// TODO populate stationStops from file
//		stationStops = new ArrayList<StationStop>();
//
//		visibleList = new ArrayList<StationStop>();
//		aa = new MyAdapter(getApplicationContext(), visibleList);
//		listView.setAdapter(aa);
		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile", Context.MODE_MULTI_PROCESS);
		//PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//				}	
		
	}

//	@Click
//	void addSSIDbuttonClicked() {
//		int size = visibleList.size();
//		int ssSize = stationStops.size();
//		if (ssSize > size) {
//			visibleList.add(stationStops.get(size));
//			aa.notifyDataSetChanged();
//		}
//	}

	@Override
	protected void onResume(){
		super.onResume();
		if (pref.getBoolean(CheckInOut.CHECKED_IN, false)) {
			Log.d("PREF", "CHECKED IN");
			user.setStatus(true);
			checkInImg.setImageResource(R.drawable.rejsekort_checked_in);
			progressBarSpinner.setVisibility(View.INVISIBLE);
		} else {
			Log.d("PREF", "CHECKED OUT");
			user.setStatus(false);
			checkInImg.setImageResource(R.drawable.rejsekort_check_in);
			progressBarSpinner.setVisibility(View.INVISIBLE);
		}
	}
	
	@Click
	void checkoutClicked(){
		//TODO this is a manual overwrite!
		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(CheckInOut.CHECKED_IN, false);
		editor.commit();
		Log.d("PREF", "CHECKED OUT");
		user.setStatus(false);
		checkInImg.setImageResource(R.drawable.rejsekort_check_in);
		progressBarSpinner.setVisibility(View.INVISIBLE);
	}
	
	@Click
	void checkInImgClicked() {
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.cancelAll();

		if (!user.isCheckedIn()) {
			checkInImg.setImageResource(R.drawable.rejsekort_blanck);
			progressBarSpinner.setVisibility(View.VISIBLE);
			
//			CheckInOut receiver = new CheckInOut(new Handler());
//			registerReceiver(receiver, new IntentFilter("dk.aau.rejsekortmobile.CHECK_IN"));
//			sendBroadcast(new Intent("dk.aau.rejsekortmobile.CHECK_IN"));
			
			Intent intent = new Intent(this, CheckInOut.class);
			intent.putExtra(CheckInOut.CHECKING_IN, true);
			intent.setAction("dk.aau.rejsekortmobile.CHECK_IN");
			sendBroadcast(intent);
		}
	}

	@Click
	void geofenceToggleClicked() {
		// if the user is inside a geofence
		if (geofenceToggle.isChecked()) {
			// Set this in the shared preferences
			
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean(MyService.ENTER_GEOFENCE, true);
			editor.commit();
			// Start the service
			Intent in = new Intent(this, MyService.class);
			in.putExtra(MyService.PARAM_MESSAGE, MyService.ENTER_GEOFENCE);
			startService(in);
		} else {
			// save in shared preferences that the user is not in a geofence
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean(MyService.ENTER_GEOFENCE, false);
			editor.commit();
		}
		// progressBarSpinner.setVisibility(View.INVISIBLE);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
