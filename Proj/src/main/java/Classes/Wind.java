package Classes;

public class Wind {
	private  float windSpeed;
    private  float windDegrees;
    private  float windGust;

	//TODO: delete this constructor
	public Wind() {
		
	}
	
    public Wind(float windSpeed, float windDegrees, float windGust) {
		this.windSpeed = windSpeed;
		this.windDegrees = windDegrees;
		this.windGust = windGust;
	}

    public float getWindSpeed() {
        return this.windSpeed;
    }
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
    public float getWindDegrees() {
        return this.windDegrees;
    }
    public void setWindDegrees(float windDegrees) {
        this.windDegrees = windDegrees;
    }
    public float getWindGust() {
        return this.windGust;
    }
    public void setWindGust(float windGust) {
    	this.windGust = windGust;
    }

}