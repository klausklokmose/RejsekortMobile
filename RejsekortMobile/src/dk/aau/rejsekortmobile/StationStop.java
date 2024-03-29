package dk.aau.rejsekortmobile;


public class StationStop {

	private int ID;
	private String stationName;
	private int range;
	private String[] coordinates = new String[2];
	private int zone;
	
	public StationStop(int ID, String stationName, int range,
			String[] coordinates, int zone) {
		this.ID = ID;
		this.stationName = stationName;
		this.range = range;
		this.coordinates = coordinates;
		this.zone = zone;
	}

	/*
	 * This method should set up a geofence for this instance
	 * returns true if the geofence was successfully set up
	 */
	public boolean setUpGeoFence(){
		
		return true;
	}
	/*
	 * removes the geofence for this instance
	 */
	public void removeGeoFence(){
		
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public String[] getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(String[] coordinates) {
		this.coordinates = coordinates;
	}
	public int getZone() {
		return zone;
	}
	public void setZone(int zone) {
		this.zone = zone;
	}
	
	
}
