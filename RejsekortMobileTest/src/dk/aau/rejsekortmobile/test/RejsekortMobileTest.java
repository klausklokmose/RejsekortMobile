package dk.aau.rejsekortmobile.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.TextView;
import dk.aau.rejsekortmobile.CheckInOutReceiver;
import dk.aau.rejsekortmobile.MainActivity_;
import dk.aau.rejsekortmobile.User;

public class RejsekortMobileTest extends
		ActivityInstrumentationTestCase2<MainActivity_> {

	private MainActivity_ mainActiv;
	private TextView loginText;
	private String checkedInString;
	private ImageView checkInImg;
	private final String SERVER_INFO = "192.168.0.107:1337";
	
	SharedPreferences pref;
	private User user;
	
	public RejsekortMobileTest() {
		super("dk.aau.rejsekortmobile", MainActivity_.class);
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mainActiv = this.getActivity();
		loginText = (TextView) mainActiv.findViewById(dk.aau.rejsekortmobile.R.id.loginText);
		checkedInString = mainActiv.getString(dk.aau.rejsekortmobile.R.string.checked_in);
		mainActiv.getString(dk.aau.rejsekortmobile.R.string.checked_in);
		
		checkInImg = (ImageView) mainActiv.findViewById(dk.aau.rejsekortmobile.R.id.checkInImg);
		
		pref = mainActiv.getApplicationContext().getSharedPreferences("Rejsekortmobile",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("SERVER", SERVER_INFO);
		editor.commit();
		
		Intent intent = new Intent("dk.aau.rejsekortmobile.CHECK_IN");
		intent.putExtra(CheckInOutReceiver.CHECKING_IN, false);
		mainActiv.sendBroadcast(intent);
        getInstrumentation().waitForIdleSync();		

		try {
			runTestOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			      loginText.setText(checkedInString);
			    }
			  });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		user = new User(1337, "Freddy Mercury", "testToken1234");
	}
	public void testPreconditions() {
		assertNotNull(loginText);
	}
	public void testLoginTextChanged() {
		assertEquals(checkedInString, loginText.getText());
	}
	
	//Server should be running!
	public void testCheckInEndToEnd(){	
	    getActivity().runOnUiThread(new Runnable() {
            public void run() {
        	    checkInImg.performClick();
            }
        });
	    // wait for the request to go through
        getInstrumentation().waitForIdleSync();		
        //testLoginTextChanged();
		assertEquals(checkedInString, loginText.getText());
	}
	
	public void testUserClassFromMainActivity(){
		assertEquals(user.getID(), mainActiv.user.getID());
		assertEquals(user.getName(), mainActiv.user.getName());
		assertEquals(user.getAccessToken(), mainActiv.user.getAccessToken());
	}
}
