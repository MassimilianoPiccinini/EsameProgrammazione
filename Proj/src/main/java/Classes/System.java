package Classes;

public class System {

	private long id;
	private long type;
	private String country;
	private long sunrise;
	private long sunset;

	//TODO: delete this constructor
	public System() {
		
	}
	
	public System(long id, long type, String country, long sunrise, long sunset) {
		this.id = id;
		this.type = type;
		this.country = country;
		this.sunrise = sunrise;
		this.sunset = sunset;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getSunrise() {
		return sunrise;
	}

	public void setSunrise(long sunrise) {
		this.sunrise = sunrise;
	}

	public long getSunset() {
		return sunset;
	}

	public void setSunset(long sunset) {
		this.sunset = sunset;
	}
}
