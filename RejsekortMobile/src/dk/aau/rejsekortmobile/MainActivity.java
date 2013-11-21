package dk.aau.rejsekortmobile;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	

	@ViewById
	Button checkInButton;
	
	@ViewById
	Button leavingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Click
	void checkInButtonClicked() {
		//Send check in message to Rejsekort server
		boolean checkIn = checkInUser();
		if (checkIn){
			checkInButton.setText("Checked in");
			
		}
	}

	private static boolean checkInUser() {
		// TODO Send check in message to Rejsekort server
		//URL TO SERVER
		
		//GET RESULT
		
		//IS RESULT OK?
		return true;
		//ELSE RETURN FALSE
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
