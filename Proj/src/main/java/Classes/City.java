package Classes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.javatuples.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import com.esameprogrammazione.Proj.FileManager;
import com.esameprogrammazione.Proj.Variables;

public class City {
	
	private long id;
	private String base;
	private long visibility;
	private long datetime;
	private long timezone;
	private String name;
	private int COD;
	private Coordinates coordinates = new Coordinates();
	private List<Cloud> clouds = new ArrayList<Cloud>();
	private List<Rain> rains = new ArrayList<Rain>();
	private List<System> systems = new ArrayList<System>();
	private List<Temperature> temperatures = new ArrayList<Temperature>();
	private List<Weather> weathers = new ArrayList<Weather>();
	private List<Wind> winds = new ArrayList<Wind>();
	
	public City() {
		
	}
	
	public City(long id) {
		this.id = id;
	}
	
	public City(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public City(long id, String name, String country) {
		this.id = id;
		this.name = name;
		getLastSystem().setCountry(country);
	}
	
	public City(long id, String base, int visibility, int datetime, int timezone, String name, int COD) {
		this.id = id;
		this.base = base;
		this.visibility = visibility;
		this.datetime = datetime;
		this.timezone = timezone;
		this.name = name;
		this.COD = COD;
	}
	/**
	 * 
	 * Restituisce il meteo della città tramite id 
	 * 		        
	 */
	
	public void getWeather() {
		try {
            String LOCATION = Long.toString(this.id);
            MUnit unit1 = Variables.globalUnit;
            if (unit1 == null) {
            	unit1 = new MUnit("C");
            }
            String unitString;
            switch (unit1.un) {
                case F:
                	unitString = "imperial";
                	break;
                case K:
                    unitString = "kelvin";
                    break;
                default:
                    unitString = "metric";
                    break;
            }
            // Notare che la api key è nascosta, ma ottenibile attraverso Variables.getKey() che a sua volta va a leggere la chiave da file .txt
	        String urlString = "http://api.openweathermap.org/data/2.5/weather?id=" + LOCATION + "&appid=" + Variables.getKey() + "&units=" + unitString; // URL chiamata sito 
	        URL url = new URL(urlString);
	
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Apro la connessione con l'URL
	        conn.setRequestMethod("GET"); // Richiesta in GET al fine di prendere i dati
	        conn.connect();
	
	        String inline = "";
	        Scanner scanner = new Scanner(url.openStream());
	
	        // Mette i dati JSON in una stringa
	        while (scanner.hasNext()) {
	            inline += scanner.nextLine();
	        }
	
	        // Chiude lo scanner
	        scanner.close();
	
	        // Usando la libreria JSONSimple vado a fare il parsing della mia stringa
	        JSONParser parser = new JSONParser();
	        JSONObject data_obj = (JSONObject) parser.parse(inline);
	
	        // Ottengo la parte che mi interessa dal mio oggetto, in questo caso il meteo, e allo stesso modo mostro altro

	        String name = (String) data_obj.get("name");
	        this.setName(name);
	        
	        long visibility = (long) data_obj.get("visibility");
	        this.setVisibility(visibility);
	        
	        long dt = (long) data_obj.get("dt");
	        this.setDatetime(dt);
	        
	        long timezone = (long) data_obj.get("timezone");
	        this.setTimezone(timezone);
	        
	        long COD = (long) data_obj.get("cod");
	        this.setTimezone(COD);
	        
	        JSONObject objMain = (JSONObject) data_obj.get("main");
	        
	        // Casting dell'oggetto per evitare formato errato e relativi errori
	        if (objMain.get("temp") instanceof Long) {
	        	Long temp = (Long) objMain.get("temp");
				getLastTemperature().setTemp(temp.floatValue());
	        }else if(objMain.get("temp") instanceof Double) {
	        	Double temp = (Double) objMain.get("temp");
				getLastTemperature().setTemp(temp.floatValue());
	        }
	        
	        if (objMain.get("feels_like") instanceof Long) {
				Long feels_like = (Long) objMain.get("feels_like");
				getLastTemperature().setFeelsLike(feels_like.floatValue());
	        }else if(objMain.get("feels_like") instanceof Double) {
	        	Double feels_like = (Double) objMain.get("feels_like");
				getLastTemperature().setFeelsLike(feels_like.floatValue());
	        }

	        if (objMain.get("temp_min") instanceof Long) {
				Long temp_min = (Long) objMain.get("temp_min");
				getLastTemperature().setTemperatureMin(temp_min.floatValue());
	        }else if(objMain.get("temp_min") instanceof Double) {
	        	Double temp_min = (Double) objMain.get("temp_min");
				getLastTemperature().setTemperatureMin(temp_min.floatValue());
	        }	

	        if (objMain.get("temp_max") instanceof Long) {
				Long temp_max = (Long) objMain.get("temp_max");
				getLastTemperature().setTemperatureMax(temp_max.floatValue());
	        }else if(objMain.get("temp_max") instanceof Double) {
	        	Double temp_max = (Double) objMain.get("temp_max");
				getLastTemperature().setTemperatureMax(temp_max.floatValue());
	        }

	        if (objMain.get("pressure") instanceof Long) {
				Long pressure = (Long) objMain.get("pressure");
				getLastTemperature().setPressure(pressure.floatValue());
	        }else if(objMain.get("pressure") instanceof Double) {
	        	Double pressure = (Double) objMain.get("pressure");
				getLastTemperature().setPressure(pressure.floatValue());
	        }
	        
	        if (objMain.get("humidity") instanceof Long) {
				Long humidity = (Long) objMain.get("humidity");
				getLastTemperature().setHumidity(humidity.floatValue());
	        }else if(objMain.get("humidity") instanceof Double) {
	        	Double humidity = (Double) objMain.get("humidity");
				getLastTemperature().setHumidity(humidity.floatValue());
	        }

	        JSONObject objCoord = (JSONObject) data_obj.get("coord");
	        if (objCoord.get("lon") instanceof Long) {
	        	Long longitude = (Long) objCoord.get("lon");
	        	this.coordinates.setLongitude(longitude.floatValue());
	        }else if(objCoord.get("lon") instanceof Double) {
	        	Double longitude = (Double) objCoord.get("lon");
	        	this.coordinates.setLongitude(longitude.floatValue());
	        }else if(objCoord.get("lon") instanceof Float) {	
	        	Float longitude = (Float) objCoord.get("lon");
	        	this.coordinates.setLongitude(longitude);
	        }
	        
	        if (objCoord.get("lat") instanceof Long) {
	        	Long latitude = (Long) objCoord.get("lat");
	        	this.coordinates.setLatitude(latitude.floatValue());
	        }else if(objCoord.get("lat") instanceof Double) {
	        	Double latitude = (Double) objCoord.get("lat");
	        	this.coordinates.setLatitude(latitude.floatValue());
	        }else if(objCoord.get("lat") instanceof Float) {	
	        	Float latitude = (Float) objCoord.get("lat");
	        	this.coordinates.setLatitude(latitude);
	        }
	        
	        // Parte Weather con main, id e descrizione, ignorando immagini e icone	        
	        JSONArray arrWeather = (JSONArray) data_obj.get("weather");
	        JSONObject objWeather = (JSONObject) arrWeather.get(0);
	        
	        String main = (String) objWeather.get("main");
	        this.getLastWeather().setMain(main);
	        
	        String description = (String) objWeather.get("description");
	        this.getLastWeather().setDescription(description);
	        

	        JSONObject objWind = (JSONObject) data_obj.get("wind");
	        
	        if (objWind.get("speed") instanceof Long) {
	        	Long speed = (Long) objWind.get("speed");
	        	getLastWind().setWindSpeed(speed.floatValue());
	        }else if(objWind.get("speed") instanceof Double) {
	        	Double speed = (Double) objWind.get("speed");
	        	getLastWind().setWindSpeed(speed.floatValue());
	        }else if(objWind.get("speed") instanceof Float) {	
	        	Float speed = (Float) objWind.get("speed");
	        	getLastWind().setWindSpeed(speed);
	        }
	        
	        if (objWind.get("deg") instanceof Long) {
	        	Long deg = (Long) objWind.get("deg");
	        	getLastWind().setWindSpeed(deg.floatValue());
	        }else if(objWind.get("deg") instanceof Double) {
	        	Double deg = (Double) objWind.get("deg");
	        	getLastWind().setWindSpeed(deg.floatValue());
	        }else if(objWind.get("deg") instanceof Float) {	
	        	Float deg = (Float) objWind.get("deg");
	        	getLastWind().setWindSpeed(deg);
	        }
	        
	        
	        JSONObject objClouds = (JSONObject) data_obj.get("clouds");
	        
	        if (objClouds.get("all") instanceof Long) {
	        	Long all = (Long) objClouds.get("all");
	        	getLastCloud().setClouds(all.floatValue());
	        }else if(objClouds.get("all") instanceof Double) {
	        	Double all = (Double) objClouds.get("all");
	        	getLastCloud().setClouds(all.floatValue());
	        }else if(objClouds.get("all") instanceof Float) {	
	        	Float all = (Float) objClouds.get("all");
	        	getLastCloud().setClouds(all);
	        }
	        

	        JSONObject objSys = (JSONObject) data_obj.get("sys");
	        
	        if (objSys.get("type") instanceof Long) {//ricontrollare
	        	Long type = (Long) objSys.get("type");
				getLastSystem().setType(type.intValue());
	        }else if(objSys.get("type") instanceof Double) {
	        	Double type = (Double) objSys.get("type");
				getLastSystem().setType(type.intValue());
	        }else if(objSys.get("type") instanceof Float) {	
	        	Float type = (Float) objSys.get("type");
	        	getLastSystem().setType(type.intValue());
	        }
	        
	        if (objSys.get("id") instanceof Long) {
				Long id = (Long) objSys.get("id");
				getLastSystem().setId(id.intValue());
	        }else if(objSys.get("id") instanceof Double) {
	        	Double id = (Double) objSys.get("id");
				getLastSystem().setId(id.intValue());
	        }
	        
	        String country = (String) objSys.get("country");
	        this.getLastSystem().setCountry(country);

	        if (objSys.get("sunrise") instanceof Long) {
				Long sunrise = (Long) objSys.get("sunrise");
				getLastSystem().setSunrise(sunrise.intValue());
	        }else if(objSys.get("sunrise") instanceof Double) {
	        	Double sunrise = (Double) objSys.get("sunrise");
				getLastSystem().setSunrise(sunrise.intValue());
	        }	

	        if (objSys.get("sunset") instanceof Long) {
				Long sunset = (Long) objSys.get("sunrise");
				getLastSystem().setSunset(sunset.intValue());
	        }else if(objSys.get("sunset") instanceof Double) {
	        	Double sunset = (Double) objSys.get("sunrise");
				getLastSystem().setSunset(sunset.intValue());
	        }

       	} catch (Exception e) {
       		e.printStackTrace();
       	}
	}
	
	// Automatically generated Getters and Setters
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public long getVisibility() {
		return visibility;
	}

	public void setVisibility(long visibility) {
		this.visibility = visibility;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	public long getTimezone() {
		return timezone;
	}

	public void setTimezone(long timezone) {
		this.timezone = timezone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCOD() {
		return COD;
	}

	public void setCOD(int cOD) {
		this.COD = cOD;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public List<Cloud> getClouds() {
		return clouds;
	}

	public void setClouds(List<Cloud> clouds) {
		this.clouds = clouds;
	}

	public List<Rain> getRains() {
		return rains;
	}

	public void setRains(List<Rain> rains) {
		this.rains = rains;
	}

	public List<System> getSystems() {
		return systems;
	}

	public void setSystems(List<System> systems) {
		this.systems = systems;
	}

	public List<Temperature> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(List<Temperature> temperatures) {
		this.temperatures = temperatures;
	}

	public List<Weather> getWeathers() {
		return weathers;
	}

	public void setWeathers(List<Weather> weathers) {
		this.weathers = weathers;
	}

	public List<Wind> getWinds() {
		return winds;
	}

	public void setWinds(List<Wind> winds) {
		this.winds = winds;
	}
	
	// Metodi per ottenere l'ultimo elemento della mia lista al quale faccio riferimento
	public Cloud getLastCloud() {
		if (this.clouds != null && this.clouds.size() > 0) {
			int lastIndex = this.clouds.size() - 1;
			return this.clouds.get(lastIndex);
		}else {
			this.clouds = new ArrayList<Cloud>();
			this.clouds.add(new Cloud());
			return this.clouds.get(0);
		}
	}
	
	public Rain getLastRain() {
		if (this.rains != null && this.rains.size() > 0) {
			int lastIndex = this.rains.size() - 1;
			return this.rains.get(lastIndex);
		}else {
			this.rains = new ArrayList<Rain>();
			this.rains.add(new Rain());
			return this.rains.get(0);
		}
	}

	public System getLastSystem() {
		if (this.systems != null && this.systems.size() > 0) {
			int lastIndex = this.systems.size() - 1;
			return this.systems.get(lastIndex);
		}else {
			this.systems = new ArrayList<System>();
			this.systems.add(new System());
			return this.systems.get(0);
		}
	}
	
	public Temperature getLastTemperature() {
		if (this.temperatures != null && this.temperatures.size() > 0) {
			int lastIndex = this.temperatures.size() - 1;
			return this.temperatures.get(lastIndex);
		}else {
			this.temperatures = new ArrayList<Temperature>();
			this.temperatures.add(new Temperature());
			return this.temperatures.get(0);
		}
	}
	
	public Weather getLastWeather() {
		if (this.weathers != null && this.weathers.size() > 0) {
			int lastIndex = this.weathers.size() - 1;
			return this.weathers.get(lastIndex);
		}else {
			this.weathers = new ArrayList<Weather>();
			this.weathers.add(new Weather());
			return this.weathers.get(0);
		}
		
		
	}
	
	public Wind getLastWind() {
		if (this.winds != null && this.winds.size() > 0) {
			int lastIndex = this.winds.size() - 1;
			return this.winds.get(lastIndex);
		}else {
			this.winds = new ArrayList<Wind>();
			this.winds.add(new Wind());
			return this.winds.get(0);
		}
	}
}
