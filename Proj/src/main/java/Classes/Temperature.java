package Classes;

public class Temperature {
	private float temp;
    private float humidity;
    private float temperatureMax;
    private float feelsLike;
    private float temperatureMin;
    private float pressure;

	//TODO: delete this constructor
	public Temperature() {
		
	}
	
    public Temperature(float temp, float humidity, float temperatureMax, float feelsLike, float temperatureMin, float pressure) {
		this.temp = temp;
    	this.humidity = humidity;
		this.temperatureMax = temperatureMax;
		this.feelsLike = feelsLike;
		this.temperatureMin = temperatureMin;
		this.pressure = pressure;
	}
    
	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public float getHumidity() {
        return humidity;
    }
	
    public void setHumidity(float umidity) {
        this.humidity = umidity;
    }
    
    public float getTemperatureMax() {
        return temperatureMax;
    }
    
    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }
    
    public float getFeelsLike() {
        return this.feelsLike;
    }
    
    public void setFeelsLike(float feelsLike) {
        this.feelsLike = feelsLike;
    }
    
    public float getTemperatureMin() {
        return this.temperatureMin;
    }
    
    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }
    
    public float getPressure() {
        return pressure;
    }
    
    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
}