package Classes;

public class Coordinates {
    private  float longitude;
    private  float latitude;

	//TODO: delete this constructor
	public Coordinates() {
		
	}
	
    public Coordinates(float longitude, float latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
    	this.longitude = longitude;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

}