package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private ArrayList<StationStop> stationStops;
	private MOT current_MOT;

	@ViewById(R.id.checkInImg)
	ImageView checkInImg;

	@ViewById(R.id.geofenceToggle)
	Button geofenceToggle;

	@ViewById(R.id.progressBarSpinner)
	ProgressBar progressBarSpinner;

	@ViewById(R.id.addSSIDbutton)
	Button addSSIDbutton;

	@ViewById(R.id.listView)
	ListView listView;

	public static User user = new User(1, "Freddy Mercury");
	private ArrayList<StationStop> visibleList;
	private MyAdapter aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO populate stationStops from file
		stationStops = new ArrayList<StationStop>();

		visibleList = new ArrayList<StationStop>();
		aa = new MyAdapter(getApplicationContext(), visibleList);
//		listView.setAdapter(aa);

	}

	@Click
	void addSSIDbuttonClicked() {
		int size = visibleList.size();
		int ssSize = stationStops.size();
		if (ssSize > size) {
			visibleList.add(stationStops.get(size));
			aa.notifyDataSetChanged();
		}
	}

	@Click
	void checkInImgClicked() {
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.cancelAll();

		if (!user.isCheckedIn()) {
			checkInImg.setImageResource(R.drawable.rejsekort_blanck);
			progressBarSpinner.setVisibility(View.VISIBLE);

			Intent intent = new Intent(this, CheckInOut.class);
			intent.putExtra(CheckInOut.CHECKING_IN, true);
			intent.setAction("dk.aau.rejsekortmobile.CHECK_IN");
			sendBroadcast(intent);
		}
	}

	@Click
	void geofenceToggleClicked() {
		// if the user is inside a geofence
		if (geofenceToggle.isActivated()) {
			// Set this in the shared preferences
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(MyService.ENTER_GEOFENCE, true);
			editor.commit();
			// Start the service
			Intent in = new Intent(this, MyService.class);
			in.putExtra(MyService.PARAM_MESSAGE, MyService.ENTER_GEOFENCE);
			startService(in);
		} else {
			// save in shared preferences that the user is not in a geofence
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(MyService.ENTER_GEOFENCE, false);
			editor.commit();
		}
		// progressBarSpinner.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(CheckInOut.CHECKED_IN)) {
			if (sharedPreferences.getBoolean(CheckInOut.CHECKED_IN, false)) {
				user.setStatus(true);
				checkInImg.setImageResource(R.drawable.rejsekort_checked_in);
				progressBarSpinner.setVisibility(View.INVISIBLE);
			} else {
				user.setStatus(false);
				checkInImg.setImageResource(R.drawable.rejsekort_check_in);
				progressBarSpinner.setVisibility(View.INVISIBLE);
			}
		}

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
