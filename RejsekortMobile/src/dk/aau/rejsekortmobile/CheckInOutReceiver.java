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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class CheckInOutReceiver extends BroadcastReceiver {

	public static final String CHECKED_IN = "CHECKED IN",
			CHECKING_IN = "CHECKING IN";

	private String urlToServer = "";
	private SharedPreferences pref;
	private MainActivity main;
	NotificationManager mNotifyMgr;

	/*
	 * When the user confirms a check-in notification the system should send the
	 * check-in message to the Rejsekort server
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		pref = context.getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		main = MainActivity.main;
		Log.d("ON receive", "ON RECEIVE STARTED");

		urlToServer = "http://" + MainActivity.getServerSettings(pref);

		Toast.makeText(context, "Broadcast being processed", Toast.LENGTH_LONG)
				.show();
		User user = MainActivity.user;

		mNotifyMgr = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);

		boolean checkingIn = intent.getExtras().getBoolean(
				CheckInOutReceiver.CHECKING_IN);
		// should it try to check in?
		if (checkingIn) {
			new ServerRequestTask(context, user, true).execute();
		} else { // try to check the user out
			new ServerRequestTask(context, user, false).execute();
		}

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
			int responseCode = 0;
			setButtonStatus(checkingIn ? R.string.checking_in
					: R.string.checking_out);
			try {
				responseCode = requestServer(user, checkingIn ? "/checkin/"
						+ user.getID() : "/checkout");
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (responseCode == 200) {
				return checkingIn ? "CHECKED IN" : "CHECKED OUT";
			}
			return "failure";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("CHECKED IN")) {

				// Notification that the user is checked in (stays as long as
				// the user is checked in)
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("Rejsekort status")
						.setContentText("Checked in");
				// The user cannot remove the notification
				mBuilder.setOngoing(true);
				// notification manager
				mNotifyMgr.notify(1, mBuilder.build());

				try {
					user.setStatus(pref, true);
					setButtonStatus(R.string.checked_in);
					// Start the service
					Intent in = new Intent(context, MyService.class);
					in.putExtra(MyService.PARAM_MESSAGE,
							MyService.ENTER_GEOFENCE);
					context.startService(in);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (result.equals("CHECKED OUT")) {
				// save the result i shared preferences
				try {
					user.setStatus(pref, false);
					setButtonStatus(R.string.check_in);
					// removes any notifications from this app
					mNotifyMgr.cancelAll();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context,
						"Failed to communicate with Rejsekort server",
						Toast.LENGTH_LONG).show();
				setButtonStatus(R.string.try_again);
			}
			// Updates the MainActivity view
			if (MainActivity.isVisible()) {
				main.stopLoading();
			}
		}

		void setButtonStatus(int rString) {
			if (MainActivity.isVisible())
				main.setButtonStatus(rString);
		}

		private int requestServer(User user, String path)
				throws URISyntaxException, IOException, ClientProtocolException {
			int responseCode;
			HttpGet getRequest = new HttpGet();
			getRequest.setURI(new URI(urlToServer + path));
			getRequest.setHeader("X-Access-Token", user.getAccessToken());

			// set up parameters such as connection timeout
			HttpParams param = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(param, 2500);
			HttpConnectionParams.setSoTimeout(param, 2500);

			HttpResponse response = new DefaultHttpClient(param)
					.execute(getRequest);
			responseCode = response.getStatusLine().getStatusCode();
			return responseCode;
		}
	}
	// private String consumeStream(InputStream is) {
	// try {
	// BufferedReader read = new BufferedReader(new InputStreamReader(is,
	// "UTF-8"));
	// StringBuilder responseBuilder = new StringBuilder();
	// String line;
	// while ((line = read.readLine()) != null) {
	// responseBuilder.append(line);
	// }
	// return responseBuilder.toString();
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
}
