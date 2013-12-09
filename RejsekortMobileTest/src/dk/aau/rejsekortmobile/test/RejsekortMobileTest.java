package dk.aau.rejsekortmobile.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import dk.aau.rejsekortmobile.MainActivity_;

public class RejsekortMobileTest extends
		ActivityInstrumentationTestCase2<MainActivity_> {

	private MainActivity_ mainActiv;
	private TextView loginText;
	private String resourceString;

	public RejsekortMobileTest() {
		super("dk.aau.rejsekortmobile", MainActivity_.class);
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mainActiv = this.getActivity();
		loginText = (TextView) mainActiv.findViewById(dk.aau.rejsekortmobile.R.id.loginText);
		resourceString = mainActiv.getString(dk.aau.rejsekortmobile.R.string.checked_in);
		try {
			runTestOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			      loginText.setText(resourceString);
			    }
			  });
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public void testPreconditions() {
		assertNotNull(loginText);
	}
	public void testLoginTextChanged() {
		assertEquals(resourceString, loginText.getText());
	}
	public void test(){
		
	}
}
