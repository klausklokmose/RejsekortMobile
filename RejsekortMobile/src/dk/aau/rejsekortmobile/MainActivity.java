package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {


	@ViewById(R.id.checkInImg)
	static ImageView checkInImg;

	@ViewById(R.id.geofenceToggle)
	ToggleButton geofenceToggle;

	@ViewById(R.id.addSSIDbutton)
	Button addSSIDbutton;

	@ViewById(R.id.checkout)
	Button checkout;

	@ViewById(R.id.editAddress)
	EditText editAddress;

	@ViewById(R.id.editPort)
	EditText editPort;

	@ViewById(R.id.loginText)
	static TextView loginText;

	private ArrayList<StationStop> stationStops;
	private MOT current_MOT;
	private ArrayList<StationStop> visibleList;

	public static User user = new User(1337, "Freddy Mercury");
	private static Animation animation;
	private static SharedPreferences pref;
	public static String serverAddress = "192.168.43.104";
	public static String serverPort = "1337";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
	}

	@Override
	protected void onResume() {
		super.onResume();

		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);

		editAddress.setText(serverAddress);
		editPort.setText(serverPort);

		if (pref.getBoolean(CheckInOut.CHECKED_IN, false)) {
			Log.d("PREF", "CHECKED IN");
			user.setStatus(true);
			loginText.setText("Checked in");
		} else {
			Log.d("PREF", "CHECKED OUT");
			user.setStatus(false);
			loginText.setText("Check in");
		}

	}

	private void startLoading() {
		animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		animation.setRepeatCount(Animation.INFINITE);
		checkInImg.startAnimation(animation);
	}

	public static void stopLoading() {
		animation.setRepeatCount(0);

		if (pref.getBoolean(CheckInOut.CHECKED_IN, false)) {
			Log.d("PREF", "CHECKED IN");
			user.setStatus(true);
			loginText.setText("Checked in");
		} else {
			Log.d("PREF", "CHECKED OUT");
			user.setStatus(false);
			loginText.setText("Check in");
		}
	}

	@TextChange(R.id.editAddress)
	void m1(TextView editText, CharSequence s, int before) {
		Log.d("CHANGE", "TEXT CHANGE " + s.toString());
		serverAddress = s.toString();
	}

	@TextChange(R.id.editPort)
	void m2(TextView editText, CharSequence s, int before) {
		Log.d("CHANGE", "Port CHANGE " + s.toString());
		serverPort = s.toString();
	}

	@Click
	void checkoutClicked() {
		// this is a manual overwrite!
		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(CheckInOut.CHECKED_IN, false);
		editor.commit();
		Log.d("PREF", "CHECKED OUT");
		user.setStatus(false);
		// checkInImg.setImageResource(R.drawable.rejsekort_check_in);
		loginText.setText("");
		// progressBarSpinner.setVisibility(View.INVISIBLE);

		// checkInImg.setImageResource(R.drawable.rejsekort_blanck);
		// progressBarSpinner.setVisibility(View.VISIBLE);
		startLoading();

		Intent intent = new Intent(this, CheckInOut.class);
		intent.putExtra(CheckInOut.CHECKING_IN, false);
		intent.setAction("dk.aau.rejsekortmobile.CHECK_IN");
		sendBroadcast(intent);
	}

	@Click
	void checkInImgClicked() {
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.cancelAll();

		if (!user.isCheckedIn()) {
			checkInImg.setImageResource(R.drawable.rejsekort_blanck);
			loginText.setText("");
			// progressBarSpinner.setVisibility(View.VISIBLE);
			startLoading();

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
