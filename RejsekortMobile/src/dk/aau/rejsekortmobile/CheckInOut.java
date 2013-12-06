package dk.aau.rejsekortmobile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class CheckInOut extends BroadcastReceiver {

	public static final String CHECKED_IN = "CHECKED IN";

	private final String urlToServer = "http://" + MainActivity.serverAddress
			+ ":" + MainActivity.serverPort;
	// private final String urlToServer = "http://google.com";

	private SharedPreferences pref;

	// private final String CHECK_IN_OK = "CHECK IN OK";
	// private final String CHECK_OUT_OK = "CHECK OUT OK";

	// private Handler handler;

	public static final String CHECKING_IN = "CHECKING IN";

	/*
	 * When the user confirms a check-in notification the system should send the
	 * check-in message to the Rejsekort server
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		pref = context.getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		Log.d("ON receive", "ON RECEIVE STARTED");

		Toast.makeText(context, "Broadcast being processed", Toast.LENGTH_LONG)
				.show();
		User user = MainActivity.user;

		boolean checkingIn = intent.getExtras().getBoolean(
				CheckInOut.CHECKING_IN);
		// should it try to check in?
		if (checkingIn) {
			new ServerRequestTask(context, user, true).execute();

			// ELSE RETURN FALSE
		} else { // try to check the user out
			new ServerRequestTask(context, user, false).execute();
		}
		// ELSE RETURN FALSE

	}

	class ServerRequestTask extends AsyncTask<Void, Void, String> {

		private Context context;
		private User user;
		private boolean checkingIn;

		public ServerRequestTask(Context context, User user, boolean checkingIn) {
			this.context = context;
			this.user = user;
			this.checkingIn = checkingIn;
		}

		@Override
		protected String doInBackground(Void... params) {
			if (checkingIn) {
				boolean working = checkInUser(context, user);
				if (working) {
					return "CHECKED IN";
				}
			} else {
				boolean working = checkOutUser(context, user);
				if (working) {
					return "CHECKED OUT";
				}
			}
			return "failure";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("CHECKED IN")) {

				// Notification that the user is checked in (stays as long as the user is checked in)
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.rejsekort_logo)
						.setContentTitle("Rejsekort status")
						.setContentText("Checked in");
				// The user cannot remove the notification
				mBuilder.setOngoing(true);
				// notification manager
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Activity.NOTIFICATION_SERVICE);
				mNotifyMgr.notify(1, mBuilder.build());
				// ---------------------------------------------------------------------------------
				pref = context.getSharedPreferences("Rejsekortmobile",
						Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(CHECKED_IN, true);
				editor.commit();
				// ----------------------------------------------------------------------------------
				// Updates the MainActivity view
				MainActivity.stopLoading();

				// Start the service
				Intent in = new Intent(context, MyService.class);
				in.putExtra(MyService.PARAM_MESSAGE, MyService.ENTER_GEOFENCE);
				context.startService(in);
				
			} else if (result.equals("CHECKED OUT")) {		//Checked out
				//save the result i shared preferences
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(CHECKED_IN, false);
				editor.commit();

				//removes any notifications from this app
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Activity.NOTIFICATION_SERVICE);
				mNotifyMgr.cancelAll();

				// Updates the MainActivity view
				MainActivity.stopLoading();
			}else{
				Toast.makeText(context, "Failed to communicate with Rejsekort server", Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean checkInUser(Context context, User user) {
		Log.d("REJSEKORT", "CHECKING IN...");

		int userID = user.getID();
		int responseCode = 0;

		try {
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(new URI(urlToServer + "/checkin/" + userID));
			getRequest.setHeader("X-Access-Token", "testToken1234");

			// set up parameters such as connection timeout
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 2500);
			HttpConnectionParams.setSoTimeout(param, 2500);

			HttpResponse response = new DefaultHttpClient(param)
					.execute(getRequest);
			responseCode = response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			Log.d("CATCH", "CLIENT PROTOCOL EXCEPTION");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("CATCH", "IO EXCEPTION");
			e.printStackTrace();

		} catch (Exception e) {
			Log.d("CATCH", "EXCEPTION");
			e.printStackTrace();
		}

		// IS RESULT OK?
		if (responseCode == 200) {
			// return true to indicate that everything went well
			return true;

		} else {
			// handle a bad response from Rejsekort server
			Log.e("BAD RESPONSE", "Code: " + responseCode);
			return false;
		}
	}

	private boolean checkOutUser(Context context, User user) {
		// Toast.makeText(context, "Checking out", Toast.LENGTH_SHORT).show();
		//Send check out message to Rejsekort server
		Log.d("REJSEKORT", "CHECKING IN");
		// String respon;
		// Send check in message to Rejsekort server
		int responseCode = 0;
		// InputStream stream = null;
		try {
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(new URI(urlToServer + "/checkout"));
			getRequest.setHeader("X-Access-Token", "testToken1234");
			// set up parameters such as connection timeout
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 5000);
			HttpConnectionParams.setSoTimeout(param, 5000);

			HttpResponse response = new DefaultHttpClient(param)
					.execute(getRequest);
			responseCode = response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			Log.d("CATCH", "CLIENT PROTOCOL EXCEPTION");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("CATCH", "IO EXCEPTION");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.d("CATCH", "URI EXCEPTION");
			e.printStackTrace();
		}

		// IS RESPONSE OK?
		if (responseCode == 200) {
			// remove check in notification
			NotificationManager mNotifyMgr = (NotificationManager) context
					.getSystemService(Activity.NOTIFICATION_SERVICE);
			mNotifyMgr.cancelAll();
			// ---------------------------------------------------------------------------------
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(CHECKED_IN, true);
			editor.commit();
			return true;
		} else {
			// handle bad response from Rejsekort server
			Log.e("BAD RESPONSE", "Code: " + responseCode);
			return false;
		}
	}

//	private String consumeStream(InputStream is) {
//		try {
//			BufferedReader read = new BufferedReader(new InputStreamReader(is,
//					"UTF-8"));
//			StringBuilder responseBuilder = new StringBuilder();
//			String line;
//			while ((line = read.readLine()) != null) {
//				responseBuilder.append(line);
//			}
//			return responseBuilder.toString();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
