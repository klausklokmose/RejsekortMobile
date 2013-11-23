package dk.aau.rejsekortmobile;

public class MOT {

	private String SSID;
	private String MAC;
	
	
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) throws IllegalArgumentException{
		if(sSID.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.SSID = sSID;
	}
	public String getMAC() {
		return MAC;
	}
	public void setMAC(String mac) {
		if(mac.isEmpty()){
			throw new IllegalArgumentException();
		}
		this.MAC = mac;
	}
	
	
}
