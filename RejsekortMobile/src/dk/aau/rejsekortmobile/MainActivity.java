package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
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
	protected static boolean isVisible;
	protected static MainActivity main;
	public void setVisible(boolean isVisible) {
		MainActivity.isVisible = isVisible;
	}

	public static boolean isVisible() {
		return isVisible;
	}

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

	public static User user = new User(1337, "Freddy Mercury", "testToken1234");
	private static Animation animation;
	private static SharedPreferences pref;
	private static String[] SERVER_INFO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(true);
		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		main = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setVisible(true);
		pref = getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		SERVER_INFO = getServerSettings(pref).split(":");
		if(SERVER_INFO.length == 2){
			editAddress.setText(SERVER_INFO[0]);
			editPort.setText(SERVER_INFO[1]);			
		}
		/*
		checkReceiver = new CheckInOutReceiver();
		checkReceiver.setMainActivity(this);
	    IntentFilter checkIntentFilter = new IntentFilter();
	    checkIntentFilter.addAction("dk.aau.rejsekortmobile.CHECK_IN");
	    registerReceiver(checkReceiver, checkIntentFilter);
		*/
		loadStatus();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		setVisible(false);
		/*
		if (this.checkReceiver!=null)
	        unregisterReceiver(checkReceiver);
	    */
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		setVisible(false);
	}
	@UiThread
	void startLoading() {
		animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		animation.setRepeatCount(Animation.INFINITE);
		checkInImg.startAnimation(animation);
	}
	
	public void stopLoading() {
		animation.setRepeatCount(0);
	}
	
	@Background
	public void loadStatus(){
		//startLoading();
		int rString = user.isCheckedIn(pref) ? R.string.checked_in : R.string.check_in;
		setButtonStatus(rString);
		//stopLoading();
	}
	@UiThread
	void setButtonStatus(int rString){
		Log.d("setButtonStatus", rString+" : "+getString(rString));
		loginText.setText(getString(rString));
	}
	/*
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
	*/
	@Click
	void checkoutClicked() {		
		Log.d("PREF", "CHECKED OUT");
		saveServerSettings();
		startLoading();

		Intent intent = new Intent("dk.aau.rejsekortmobile.CHECK_IN");
		intent.putExtra(CheckInOutReceiver.CHECKING_IN, false);
		sendBroadcast(intent);
	}

	@Click
	void checkInImgClicked() {
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		saveServerSettings();
		if (!user.isCheckedIn(pref)) {
			mNotifyMgr.cancelAll();
			checkInImg.setImageResource(R.drawable.rejsekort_blanck);
			loginText.setText("");
			startLoading();

			Intent intent = new Intent("dk.aau.rejsekortmobile.CHECK_IN");
			intent.putExtra(CheckInOutReceiver.CHECKING_IN, true);
			intent.putExtra("STATION_ID", "1337");
			sendBroadcast(intent);
		}
	}
	
	private void saveServerSettings(){
		setStringPreference("SERVER", editAddress.getText().toString() + ":" + editPort.getText().toString());
	}
	
	static String getServerSettings(SharedPreferences pref){
		return pref.getString("SERVER", ":");
	}
	
	private void setBoolPreference(String boolKey, boolean boolValue) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(boolKey, boolValue);
		editor.commit();
	}
	
	private void setStringPreference(String stringKey, String stringValue) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(stringKey, stringValue);
		editor.commit();
	}
	
	@Click
	void geofenceToggleClicked() {
		// if the user is inside a geofence
		if (geofenceToggle.isChecked()) {
			// Set this in the shared preferences
			setBoolPreference(MyService.ENTER_GEOFENCE, true);
			// Start the service
			//TODO
			Intent in = new Intent(this, MyService.class);
			in.putExtra(MyService.ENTER_MESSAGE, MyService.ENTER_GEOFENCE);
			in.putExtra("STATION_ID", "1337");
			startService(in);
		} else {
			// save in shared preferences that the user is not in a geofence
			setBoolPreference(MyService.ENTER_GEOFENCE, false);
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
