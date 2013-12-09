package dk.aau.rejsekortmobile;

import java.io.IOException;

import android.content.SharedPreferences;
import android.util.Log;

public class User {
	private int ID;
	private String name, accessToken;
	private boolean status;
	
	public static final String CHECKED_IN = "CHECKED IN";
	
	public User(int id, String name, String token){
		setID(id);
		setName(name);
		setAccessToken(token);
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		this.ID = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isCheckedIn(SharedPreferences pref) {
		return this.status = pref.getBoolean(CHECKED_IN, false);
	}

	public boolean setStatus(SharedPreferences pref, boolean checkedIn) throws IOException{
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(CHECKED_IN, checkedIn);
		Log.d("setStatus", ""+checkedIn);
		if(!editor.commit())
			throw new IOException("Failed to save user status");
		this.status = checkedIn;
		return this.status;
	}
	
//	public static void save(){
//		
//	}
	
	public static User load(){
		return null;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	} 
}

