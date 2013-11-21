package dk.aau.rejsekortmobile;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	

	@ViewById(R.id.checkInImg)
	ImageView checkInImg;
	
	@ViewById
	Button leavingButton;
	
	@ViewById(R.id.progressBarSpinner)
	ProgressBar progressBarSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		progressBarSpinner = (ProgressBar)findViewById(R.id.progressBarSpinner);
//		progressBarSpinner.setVisibility(View.INVISIBLE);
		
//		progressBarSpinner.setVisibility(View.INVISIBLE);
	}

	@Click
	void checkInImgClicked() {
		//Send check in message to Rejsekort server
		boolean checkIn = checkInUser();
		if (checkIn){
			checkInImg.setImageResource(R.drawable.rejsekort_blanck);
			progressBarSpinner.setVisibility(View.VISIBLE);
			
		}
	}

	@Click
	void leavingButtonClicked(){
		progressBarSpinner.setVisibility(View.INVISIBLE);
	}
	private boolean checkInUser() {
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
