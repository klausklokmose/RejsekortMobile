package dk.aau.rejsekortmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import android.widget.TextView;
import android.widget.Toast;

public class CheckInOut extends BroadcastReceiver {

	public static final String CHECKED_IN = "CHECKED IN";

	private final String urlToServer = "http://10.0.2.2:1337";
	// private final String urlToServer = "http://google.com";

	private final String CHECK_IN_OK = "CHECK IN OK";
	private final String CHECK_OUT_OK = "CHECK OUT OK";
	public static final String CHECKING_IN = "CHECKING IN";

	// public static final String CHECKING_OUT = "CHECKING OUT";

	/*
	 * When the user confirms a check-in notification the system should send the
	 * check-in message to the Rejsekort server
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("ON receive", "ON RECEIVE STARTED");
		if (context == null) {
			Log.d("CONTEXT", "NULL");
		}
		Toast.makeText(context, "Broadcast being processed", Toast.LENGTH_LONG)
				.show();
		User user = MainActivity.user;

		boolean checkingIn = intent.getExtras().getBoolean(
				CheckInOut.CHECKING_IN);
		// should it try to check in?
		if (checkingIn) {
			new LongOperation(context, user, true).execute();

			// ELSE RETURN FALSE
		} else { // try to check the user out
			checkOutUser(context, user);
		}
		// ELSE RETURN FALSE

	}

	class LongOperation extends AsyncTask<Void, Void, String> {

		private Context context;
		private User user;
		private boolean checkingIn;

		public LongOperation(Context context, User user, boolean checkingIn) {
			this.context = context;
			this.user = user;
			this.checkingIn = checkingIn;
		}

		@Override
		protected String doInBackground(Void... params) {
			if (checkingIn) {
				boolean working = checkInUser(context, user);
				if (working){
					return "CHECKED IN";
				}
			} else {
				checkOutUser(context, user);
			}
			return "Executed";
		}
		
		@Override
        protected void onPostExecute(String result) {
			if(result.equals("CHECKED IN")){
				
				// Notification that the user is checked in (stays as long as
				// the user is checked in)
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.rejsekort_logo)
						.setContentTitle("Rejsekort status")
						.setContentText("Checked in");
				// removes the notification when it is pressed by the user.
				mBuilder.setOngoing(true);
				// notification manager
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Activity.NOTIFICATION_SERVICE);
				mNotifyMgr.notify(1, mBuilder.build());
				// ---------------------------------------------------------------------------------
				SharedPreferences preferences = context.getSharedPreferences("Rejsekortmobile", Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(CHECKED_IN, true);
				editor.commit();
				
				// Start the service
				Intent in = new Intent(context, MyService.class);
				in.putExtra(MyService.PARAM_MESSAGE, MyService.ENTER_GEOFENCE);
				context.startService(in);
			}
		}
	}

	private boolean checkInUser(Context context, User user) {
//		Toast.makeText(context, "Checking in", Toast.LENGTH_SHORT).show();
		Log.d("REJSEKORT", "CHECKING IN");
		// String respon;
		int userID = user.getID();
		// TODO Send check in message to Rejsekort server
		int responseCode = 0;

		try {
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(new URI(urlToServer + "/checkin/1337"));
			getRequest.setHeader("X-Access-Token", "testToken1234");
			// InputStream stream = null;
			// set up parameters such as connection timeout
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 5000);
			HttpConnectionParams.setSoTimeout(param, 5000);

			HttpResponse response = new DefaultHttpClient(param)
					.execute(getRequest);
			responseCode = response.getStatusLine().getStatusCode();

			// stream = response.getEntity().getContent();

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
		// GET RESULT
		// respon = consumeStream(stream);

		// TODO testing
		// respon = CHECK_IN_OK;

		// IS RESULT OK?
		if (responseCode == 200) {

			
			return true;

		} else {
			// TODO handle a bad response from Rejsekort server
			Log.e("BAD RESPONSE", "Code: " + responseCode);
			return false;
		}
	}

	private void checkOutUser(Context context, User user) {
//		Toast.makeText(context, "Checking out", Toast.LENGTH_SHORT).show();
		// TODO Send check out message to Rejsekort server
		Log.d("REJSEKORT", "CHECKING IN");
		// String respon;
		int userID = user.getID();
		// TODO Send check in message to Rejsekort server
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

			// stream = response.getEntity().getContent();

		} catch (ClientProtocolException e) {
			Log.d("CATCH", "CLIENT PROTOCOL EXCEPTION");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("CATCH", "IO EXCEPTION");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// GET RESULT
		// respon = consumeStream(stream);
		// TODO testing
		// String respon = CHECK_OUT_OK;
		// IS RESULT OK?
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
		} else {
			// TODO handle bad response from Rejsekort server
			Log.e("BAD RESPONSE", "Code: " + responseCode);
		}
	}

	private String consumeStream(InputStream is) {
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			StringBuilder responseBuilder = new StringBuilder();
			String line;
			while ((line = read.readLine()) != null) {
				responseBuilder.append(line);
			}
			return responseBuilder.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
