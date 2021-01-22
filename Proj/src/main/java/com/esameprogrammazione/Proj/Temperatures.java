package com.esameprogrammazione.Proj;

import Classes.Cloud;
import Classes.Rain;
import Classes.System;
import Classes.Temperature;
import Classes.Weather;
import Classes.Wind;
import Exceptions.IdNotFoundException;
import Classes.City;

import java.util.Iterator;
import java.util.List;

import org.javatuples.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Temperatures {
	
	/**
     * Calcola le statistiche della città in base al periodo scelto dall'utente (default: begin=0, duration = data odierna - begin)
     * 
     * @param period - array di 2 interi contenente inizio e durata del periodo
     * @param city - città di cui si vogliono le statistiche
     *  
     * @return tupla contenente le statistiche desiderate (media delle temperature e varianza)
     * 
     * @throws IdNotFoundException - Se non esiste nessuna città con quell'id
     */
	
	public static Pair<Temperature, Float> getsStats(int[] period, City city) throws IdNotFoundException {
        getHistory(period, city);
        List<Temperature> temps = city.getTemperatures();
        float somma_temp = 0;
        float media_temp = 0;
        float somma_temperatureMin = 0;
        float media_temperatureMin = 0;
        float somma_temperatureMax = 0;
        float media_temperatureMax = 0;
        float somma_var = 0;
        float varianza = 0;
        for(int i=0; i < temps.size(); i++) {
            somma_temp += temps.get(i).getTemp();
            somma_temperatureMin += temps.get(i).getTemperatureMin();
            somma_temperatureMax += temps.get(i).getTemperatureMax();
        }
        media_temp = somma_temp / temps.size();
        media_temperatureMin = somma_temperatureMin / temps.size();
        media_temperatureMax = somma_temperatureMax / temps.size();
        for(int i=0; i < temps.size(); i++) {
            somma_var += java.lang.Math.pow(temps.get(i).getTemp()-media_temp, 2);
        }
        varianza = somma_var / temps.size(); 
        Pair<Temperature, Float> stats = new Pair<Temperature, Float>(new Temperature(media_temp, 0, media_temperatureMax, 0, media_temperatureMin, 0), (float) varianza);
        return stats;
    }
	
	/**
     * Aggiorna lo storico se necessario (updateHistory), se richiesto chiama getHistory
     * 
     * @param update - boolean per controllare se aggiornare o meno lo storico
     * @param history - boolean per controllare se chiamre o meno getHistory
     * @param period - array di 2 interi contenente inizio e durata del periodo
     * @param city - città della quale aggiornare e/o ottenere i dati
     *  
     * 
     * @throws IdNotFoundException - Se non esiste nessuna città con quell'id
     * 
     */
	
	public static void updateData(boolean update, boolean history, int[] period, City city) throws IdNotFoundException {
		if (update) {
			updateHistory(city);
		}
		if(history) {
			getHistory(period, city);
		}
	}
	
	/**
     * Ottiene i dati relativi ad un periodo specificato di una determinata città dal file dello storico se quella città esiste 
     * 
     * @param period - array di 2 interi contenente inizio e durata del periodo
     * @param city - città della quale aggiornare i dati
     *  
     * @throws IdNotFoundException - Se non esiste nessuna città con quell'id
     * 
     */
	
	public static void getHistory(int[] period, City city) throws IdNotFoundException {
		String filename = "src/main/java/com/esameprogrammazione/Proj/History.json";
	    JSONArray storage = FileManager.readJarray(filename);
	    Triplet<Boolean, Boolean, JSONObject> found = FileManager.find(storage, city.getId(), false);
		if (found.getValue0()) {
			JSONArray records = (JSONArray) found.getValue2().get("records");

			// Svuota le liste prima di utilizzarle
			city.getClouds().clear();
			city.getRains().clear();
			city.getSystems().clear();
			city.getTemperatures().clear();
			city.getWeathers().clear();
			city.getWinds().clear();

			int index = 0;
			int start = period[0]; //1
			int duration = period[1]; //2
		
			Iterator<?> iterator = records.iterator();
	        while (iterator.hasNext()) {
		            JSONObject obj = (JSONObject) iterator.next();
		        	if ((start <= index) && ((start + duration) > index)) {
		            
		            Double cloud = (Double) obj.get("cloud");
		            if (cloud != null) {
			            Cloud clo = new Cloud(cloud.floatValue());
			            city.getClouds().add(clo);
		            }
		   
		            Double oneh = (Double) obj.get("oneh");
		            if (oneh != null) {
			            Rain rai = new Rain(oneh.floatValue());
			            city.getRains().add(rai);
		            }
		            
		            Long id_s = (Long) obj.get("id_s");
		        	Long type = (Long) obj.get("type");
		            String country = (String) obj.get("country"); 
		            Long sunrise = (Long) obj.get("sunrise");
		        	Long sunset = (Long) obj.get("sunset");
		            if (id_s != null && type != null && country != null && sunrise != null && sunset != null) {
			        	System sys = new System(id_s, type, country, sunrise, sunset);
			        	city.getSystems().add(sys);
		            }
		
		        	Double temp = (Double) obj.get("temp");
		        	temp = changeUnit(temp);
		        	Double humidity = (Double) obj.get("humidity");
		        	Double temperatureMax = (Double) obj.get("temperatureMax");
		        	temperatureMax = changeUnit(temperatureMax);
		            Double feelsLike = (Double) obj.get("feelsLike");
		            feelsLike = changeUnit(feelsLike);
		            Double temperatureMin = (Double) obj.get("temperatureMin");
		            temperatureMin = changeUnit(temperatureMin);
		            Double pressure = (Double) obj.get("pressure");
		            if (temp != null && humidity != null && temperatureMax != null && feelsLike != null && temperatureMin != null && pressure != null) {
				        Temperature temperature = new Temperature(temp.floatValue(), humidity.floatValue(), temperatureMax.floatValue(), feelsLike.floatValue(), temperatureMin.floatValue(), pressure.floatValue());
				        city.getTemperatures().add(temperature);
		            }
		            Long id_w = (Long) obj.get("id_w");
		        	String main = "" + obj.get("main");
		        	String description = "" + obj.get("description");
		        	String icon = "" + obj.get("icon");
		        	if (id_w != null && main != null && description != null && icon != null) {
			        	Weather wea = new Weather(id_w, main, description, icon);
			        	city.getWeathers().add(wea);
		        	}
		        	
		        	Double windSpeed = (Double) obj.get("windSpeed");
		            Double windDegrees = (Double) obj.get("windDegrees");
		            Double windGust = (Double) obj.get("windGust");
		            //if (windSpeed != null && windDegrees != null && windGust != null) {
			            Wind wi = new Wind(windSpeed.floatValue(), windDegrees.floatValue(), windGust.floatValue());
			            city.getWinds().add(wi);
		            //}
	        	}
	            index++;
	        }
		}
	}
	
	/**
     * Aggiorna lo storico con i dati di una città se questa è già presente nello storico, 
     * altrimenti crea un nuovo oggetto city con i relativi dati e lo aggiunge allo storico
     * 
     * @param city - città con la quale aggiornare lo storico
     *  
     * @throws IdNotFoundException - Se non esiste nessuna città con quell'id
     */
	
	public static void updateHistory(City city) throws IdNotFoundException {
		long id = city.getId();
		String base = city.getBase();
		float visibility = city.getVisibility();
		float datatime = city.getDatetime();
		float timezone = city.getTimezone();
		String name = city.getName();
		int COD = city.getCOD();
		float longitude = city.getCoordinates().getLongitude();
	    float latitude = city.getCoordinates().getLatitude();
	    float cloud = city.getLastCloud().getClouds();
	    float oneh = city.getLastRain().getOneh();
	    long type = city.getLastSystem().getType();
	    long id_s = city.getLastSystem().getId();
		String country = city.getLastSystem().getCountry();
		long sunrise = city.getLastSystem().getSunrise();
		long sunset = city.getLastSystem().getSunset();
		float humidity = city.getLastTemperature().getHumidity();
		float temp = city.getLastTemperature().getTemp();
	    float temperatureMax = city.getLastTemperature().getTemperatureMax();
	    float feelsLike = city.getLastTemperature().getFeelsLike();
	    float temperatureMin = city.getLastTemperature().getTemperatureMin();
	    float pressure = city.getLastTemperature().getPressure();
	    Long id_w = city.getLastWeather().getId();
	    String main = city.getLastWeather().getMain();
		String description = city.getLastWeather().getDescription();
		String icon = city.getLastWeather().getIcon();
		float windSpeed = city.getLastWind().getWindSpeed();
	    float windDegrees = city.getLastWind().getWindDegrees();
	    float windGust = city.getLastWind().getWindGust();

	    // Converte tutte le temperature in celsius prima di salvarle
	    switch(Variables.globalUnit.un) {
        case K: 
        	temp -= 273.15;
        	temperatureMax -= 273.15;
        	feelsLike -= 273.15;
           	temperatureMin -= 273.15;
            break;
        case F: 
        	temp = (temp - 32);
        	temp /= 1.8;
        	temperatureMax = (temperatureMax - 32); 
            temperatureMax /= 1.8;
            feelsLike = (feelsLike - 32); 
            feelsLike /= 1.8;
            temperatureMin = (temperatureMin - 32); 
            temperatureMin /= 1.8;
            break;
		default:
			break;
	    }
	    
	    JSONArray storage = FileManager.readJarray(Variables.getHistoryPath());
        Triplet<Boolean, Boolean, JSONObject> found = FileManager.find(storage, id, true);
        if(found.getValue0() && found.getValue1()) {
        	// Proseguo solo se esiste un oggetto City con quel determinato id e se posso aggiornarlo
        	JSONObject objCity = found.getValue2();
        	objCity.put("id", id);
        	objCity.put("base", base);
        	objCity.put("name", name);
        	objCity.put("COD", COD);
        	objCity.put("longitude", longitude);
        	objCity.put("latitude", latitude);
        	objCity.put("country", country);
        	JSONArray records = (JSONArray) objCity.get("records");
        	JSONObject record1 = new JSONObject();
        	record1.put("visibility", visibility);
        	record1.put("dt", datatime);
        	record1.put("timezone", timezone);
        	record1.put("cloud", cloud);
        	record1.put("oneh", oneh);
        	record1.put("type", type);
        	record1.put("sunrise", sunrise);
        	record1.put("sunset", sunset);
        	record1.put("humidity", humidity);
        	record1.put("temp", temp);
        	record1.put("temperatureMax", temperatureMax);
        	record1.put("feelsLike", feelsLike);
        	record1.put("temperatureMin", temperatureMin);
        	record1.put("pressure", pressure);
        	record1.put("main", main);
        	record1.put("description", description);
        	record1.put("icon", icon);
        	record1.put("windSpeed", windSpeed);
        	record1.put("windDegrees", windDegrees);
        	record1.put("windGust", windGust);
        	record1.put("id_w", id_w);
        	
        	records.add(record1);
        	
        	objCity.replace("records", records);
        	//replace oldCity with city (storage, city)
        	//storage.add(city);
        	//replaceCity(storage);
        	//storage.add(city);
        	storage.remove(objCity);
        	storage.add(objCity);
        	FileManager.writeJarray(storage, Variables.getHistoryPath());
        	
        } else if (found.getValue1()){
        	// Proseguo solo se non esiste un oggetto City con quel determinato id e se posso aggiornarlo
        	
        	JSONObject objCity = new JSONObject();
        	objCity.put("id", id);
        	objCity.put("base", base);
        	objCity.put("name", name);
        	objCity.put("COD", COD);
        	objCity.put("longitude", longitude);
        	objCity.put("latitude", latitude);
        	objCity.put("country", country);
        	
        	JSONArray records = new JSONArray();
        	
        	JSONObject record1 = new JSONObject();
        	record1.put("visibility", visibility);
        	record1.put("dt", datatime);
        	record1.put("timezone", timezone);
        	record1.put("cloud", cloud);
        	record1.put("oneh", oneh);
        	record1.put("type", type);
        	record1.put("sunrise", sunrise);
        	record1.put("sunset", sunset);
        	record1.put("humidity", humidity);
        	record1.put("temp", temp);
        	record1.put("temperatureMax", temperatureMax);
        	record1.put("feelsLike", feelsLike);
        	record1.put("temperatureMin", temperatureMin);
        	record1.put("pressure", pressure);
        	record1.put("main", main);
        	record1.put("description", description);
        	record1.put("icon", icon);
        	record1.put("windSpeed", windSpeed);
        	record1.put("windDegrees", windDegrees);
        	record1.put("windGust", windGust);
        	record1.put("id_s", id_s);
        	record1.put("id_w", id_w);
        	
        	records.add(record1);

        	objCity.put("records", records);
        	storage.add(objCity);
        	FileManager.writeJarray(storage, Variables.getHistoryPath());
 
        }
	}
	
	/**
     * Converte l'unita di misura di una temperatura (di default in celsius) in quella scelta dall'utente
     * 
     * @param temp - temperatura da convertire
     *  
     * @return - Restituisce il valore convertito
     */
	
	public static double changeUnit (double temp) {
        switch(Variables.globalUnit.un) {
        case K: 
            temp += 273.15;
            break;
        case F: 
            temp = (double) (temp * 1.8 + 32);
            break;
        default:
            break;
        }
        return temp;
    }
}
