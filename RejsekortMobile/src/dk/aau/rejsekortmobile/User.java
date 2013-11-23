package dk.aau.rejsekortmobile;

public class User {
	private int ID;
	private String name;
	private boolean status;
	
	public User(int id, String name){
		setID(id);
		setName(name);
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

	public boolean isCheckedIn() {
		return status;
	}

	public void setStatus(boolean checkedIn) {
		this.status = checkedIn;
	}
}

