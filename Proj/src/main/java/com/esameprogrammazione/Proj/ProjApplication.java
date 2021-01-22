package com.esameprogrammazione.Proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Classes.*;
import Exceptions.IdNotFoundException;
import Exceptions.NameCityNotFoundException;
import Exceptions.WrongDateException;

import org.springframework.stereotype.Controller;
import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;


@SpringBootApplication
@Controller
public class ProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjApplication.class, args);
	}
	
	/**
     * Visualizza le principali capitali europee e le loro condizioni metereologiche
     * 
     * @return - un JSONObject con le condizioni meteo delle capitali europee
     */

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody JSONObject dashboard() {
		List<City> cities = getCapitalsId();
		JSONObject obj = new JSONObject();
		HashMap<String, Object> capitals = new HashMap<String, Object>();
		for (City city : cities) {
			city.getWeather();
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			write(parameters, city);
			JSONObject data = new JSONObject(parameters);

			capitals.put(city.getName(), data);
			obj = new JSONObject(capitals);
		}
		return obj;
	}
	
	/**
     * Visualizza il meteo di una singola città ricercata per id
     * 
     * @param id - Id della città
     * @param u - Unità di misura della temperatura (default: Celsius)
     * @return - Meteo della città ricercata, con temperature nell'unità di misura desiderata
     * 
     * @throws IdNotFoundException - Se non esiste una città con l'id richiesto.
     */
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody JSONObject search(@RequestParam(required = true) long id, @RequestParam(required = false) String u) throws IdNotFoundException{
		City city = new City(id);
		Variables.setGlobalVariable(u);
		if (FileManager.checkId(city.getId())) {
			city.getWeather();
			JSONObject obj = new JSONObject();
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			write(parameters, city);
			obj = new JSONObject(parameters);
			return obj;
		}else {
			throw new IdNotFoundException("Can not find city from id: " + city.getId());
		}
	}
	
	/**
     * Visualizza lo storico del meteo di una singola città ricercata per id
     * 
     * @param id - Id della città
     * @param u - Unità di misura della temperatura (default: Celsius)
     * @param beg - Data di inizio dello storico richiesto (19 Gennaio = 0) (default: 0)
     * @param dur - Durata (in giorni) degli storici richiesti (default: data di oggi - begin)
     * @return - Storico del meteo della città ricercata, con temperature nell'unità di misura desiderata
     * 
     * @throws WrongDateException - Se begin è antecedente a 0 (cioè al 19 Gennaio) o se la durata supera la data odierna
     * @throws IdNotFoundException - Se non esiste una città con l'id richiesto.
     */

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public @ResponseBody JSONArray history(@RequestParam(required = true) long id, @RequestParam(required = false) String u, @RequestParam(required = false) String beg, @RequestParam(required = false) String dur) throws WrongDateException, IdNotFoundException {
		Variables.setGlobalVariable(u);
		City city = new City(id);
		JSONArray arr = new JSONArray();
        int begin = 0;
        if (beg != null && beg.length() > 0) {
        	begin = Integer.parseInt(beg);
        }
        int duration = 0;
        if (dur != null && dur.length() > 0) {
        	duration = Integer.parseInt(dur);
        }
		if(begin >= 0 && begin < FileManager.getIntData(false)) {
	        if(duration == 0) {
	            duration = FileManager.getIntData(false) - begin;
	        }
		    if (duration >= 1 && duration <= (FileManager.getIntData(false) - begin)) {
				int[] period = {begin, duration};
				Temperatures.updateData(false, true, period, city);
				for (int i = 0; i < period[1]; i++) {
					HashMap<String, Object> obj = new HashMap<String, Object>();
					write(obj, city, i);
					arr.add(obj);
				}
		    }else {
		    	throw new WrongDateException("Durata non accettabile");
		    }
		} else {
            throw new WrongDateException("Data non accettabile");
        }
		return arr;
	}
	
	/**
     * Visualizza le statistiche del meteo di una singola città ricercata per id
     * 
     * @param id - Id della città
     * @param u - Unità di misura della temperatura (default: Celsius)
     * @param beg - Data di inizio dello storico dal quale si calcolano le statistiche richiesto (19 Gennaio = 0) (default: 0)
     * @param dur - Durata (in giorni) degli storici dai quali si calcolano le statistiche richieste (default: data di oggi - begin)
     * @return - Statistica dello storico del meteo della città ricercata, con temperature nell'unità di misura desiderata
     * 
     * @throws WrongDateException - Se begin è antecedente a 0 (cioè al 19 Gennaio) o se la durata supera la data odierna
     * @throws IdNotFoundException - Se non esiste una città con l'id richiesto.
     */

	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public @ResponseBody JSONObject stats(@RequestParam(required = true) long id, @RequestParam(required = false) String u, @RequestParam(required = false) String beg, @RequestParam(required = false) String dur) throws WrongDateException, IdNotFoundException {
        Variables.setGlobalVariable(u);
        JSONObject stats = new JSONObject();
        City city = new City(id);
        int begin = 0;
        if (beg != null && beg.length() > 0) {
        	begin = Integer.parseInt(beg);
        }
        int duration = 0;
        if (dur != null && dur.length() > 0) {
        	duration = Integer.parseInt(dur);
        }
        if(begin >= 0 && begin < FileManager.getIntData(false)) {
	        if(duration == 0) {
	            duration = FileManager.getIntData(false) - begin;
	        }
		    if (duration >= 1 && duration <= (FileManager.getIntData(false) - begin)) {
		        int[] period = {begin, duration};
		        Pair<Temperature, Float> statistics = Temperatures.getsStats(period, city);
		        Temperature temp = statistics.getValue0();
		        float variance = statistics.getValue1();
		        stats.put("temp", temp.getTemp());
		        stats.put("tempMax", temp.getTemperatureMax());
		        stats.put("tempMin", temp.getTemperatureMin());
		        stats.put("variance", variance);
		        return stats;
	        } else {
	            throw new WrongDateException("Durata non accettabile");
	        }
        } else {
            throw new WrongDateException("Data di inizio non accettabile");
        }
    }

	/**
     * Motore di ricerca con nome della città come chiave della ricerca
     * 
     * @param name - Nome della città
     * @return - Insieme di città aventi quel determinato nome
     * 
     * @throws NameCityNotFoundException - Se non c'è nessuna città chiamata col nome inserito
     */
	
	@RequestMapping(value = "/getid", method = RequestMethod.GET)
	public static @ResponseBody JSONArray search(@RequestParam(required = true) String name) throws NameCityNotFoundException {
		List<City> cities = FileManager.getId(name);
		if (cities.size() == 0) {
			throw new NameCityNotFoundException("There are no cities named: " + name);
		}else {
			JSONArray objArray = new JSONArray();
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			for (City city : cities) {
				parameters.put("id", city.getId());
				parameters.put("name", city.getName());
				parameters.put("country", city.getLastSystem().getCountry());
				
				JSONObject data = new JSONObject(parameters);
				objArray.add(data);
			}
			return objArray;
		}
	}

	/**
     * Inserisce il record della data odierna del meteo delle capitali europee, se non esiste già
     * 
     * @return - Successo / Insuccesso dell'operazione
     * 
     * @throws IdNotFoundException - Se non c'è nessuna città corrispondente all'id inserito
     */
	
	@RequestMapping(value = "/capitals", method = RequestMethod.POST)
	public @ResponseBody JSONObject updateCapitals() throws IdNotFoundException {
		JSONObject obj = new JSONObject();
		Variables.setGlobalVariable("c");
		List<City> capitals = getCapitalsId();
		Iterator<?> iterator = capitals.iterator();
        while (iterator.hasNext()) {
            City capital = (City) iterator.next();
            if (capital != null) {
            	long id = (long) capital.getId();
            	updateCity(id);
            }
        }
        obj.put("status", "Capitals updated successfully");
		return obj;
	}

	/**
     * Inserisce il record della data odierna del meteo di una capitale europea, se non esiste già
     * 
     * @param id - Id della capitale europea da aggiornare
     * 
     * @throws IdNotFoundException - Se non c'è nessuna città corrispondente all'id inserito
     */
	
	public void updateCity(long id) throws IdNotFoundException {
		City city = new City(id);
		city.getWeather();
		int[] period = {0, 0};
		Temperatures.updateData(true, false, period, city);
	}

	/**
     * Aggiunge i parametri elaborati al json da mostrare all'utente
     * 
     * @param obj - Oggetto (inizialmente vuoto) JSON da mostrare all'utente
     * @param city - Città dalla quale si prendono i valori
     * 
     */
	
	public void write(HashMap<String, Object> obj, City city) {
		obj.put("id", city.getId());
		obj.put("name", city.getName());
		obj.put("datetime", city.getDatetime());
		obj.put("timezone", city.getTimezone());
		obj.put("visibility", city.getVisibility());
		
		obj.put("degrees", city.getLastTemperature().getTemp());
		obj.put("feels_like", city.getLastTemperature().getFeelsLike());
		obj.put("temp_max", city.getLastTemperature().getTemperatureMax());
		obj.put("temp_min", city.getLastTemperature().getTemperatureMin());
		obj.put("pressure", city.getLastTemperature().getPressure());
		obj.put("humidity", city.getLastTemperature().getHumidity());
		
		obj.put("clouds", city.getLastCloud().getClouds());
		
		obj.put("oneh", city.getLastRain().getOneh());
		
		obj.put("country", city.getLastSystem().getCountry());
		obj.put("sunrise", city.getLastSystem().getSunrise());
		obj.put("sunset", city.getLastSystem().getSunset());
		obj.put("type", city.getLastSystem().getType());

		obj.put("description", city.getLastWeather().getDescription());
		obj.put("main", city.getLastWeather().getMain());

		obj.put("wind_degrees", city.getLastWind().getWindDegrees());
		obj.put("wind_gust", city.getLastWind().getWindGust());
		obj.put("wind_speed", city.getLastWind().getWindSpeed());
	}
	
	/**
     * Aggiunge i parametri elaborati al json da mostrare all'utente, all'indice desiderato
     * 
     * @param obj - Oggetto (inizialmente vuoto) JSON da mostrare all'utente
     * @param city - Città dalla quale si prendono i valori
     * @param index - Indice dei parametri della città dal quale si vogliono prendere i valori
     * 
     */
	
	public void write(HashMap<String, Object> obj, City city, int index) {
		if (city.getTemperatures().size() > index) {
			obj.put("degrees", city.getTemperatures().get(index).getTemp());
			obj.put("feels_like", city.getTemperatures().get(index).getFeelsLike());
			obj.put("temp_max", city.getTemperatures().get(index).getTemperatureMax());
			obj.put("temp_min", city.getTemperatures().get(index).getTemperatureMin());
			obj.put("pressure", city.getTemperatures().get(index).getPressure());
			obj.put("humidity", city.getTemperatures().get(index).getHumidity());
		}

		if (city.getClouds().size() > index) {
			obj.put("clouds", city.getClouds().get(index).getClouds());
		}
		
		if (city.getRains().size() > index) {
			obj.put("oneh", city.getRains().get(index).getOneh());
		}

		if (city.getSystems().size() > index) {
			obj.put("country", city.getSystems().get(index).getCountry());
			obj.put("sunrise", city.getSystems().get(index).getSunrise());
			obj.put("sunset", city.getSystems().get(index).getSunset());
			obj.put("type", city.getSystems().get(index).getType());
		}

		if (city.getWeathers().size() > index) {
			obj.put("description", city.getWeathers().get(index).getDescription());
			obj.put("main", city.getWeathers().get(index).getMain());
		}

		if (city.getWinds().size() > index) {
			obj.put("wind_degrees", city.getWinds().get(index).getWindDegrees());
			obj.put("wind_gust", city.getWinds().get(index).getWindGust());
			obj.put("wind_speed", city.getWinds().get(index).getWindSpeed());
		}
	}
	
	/**
     * Prende gli id delle capitali dal file
     * 
     * @return - Lista di città con gli id presi da file
     * 
     */
	
	public List<City> getCapitalsId() {
		List<City> cityList = new ArrayList<>();
		JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(Variables.getCapitalsPath())) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            
            JSONArray capitals = (JSONArray) jsonObject.get("Capitals");
            Iterator<?> iterator = capitals.iterator();
            while (iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();
                String name = (String) obj.get("name");
                long id = (long) obj.get("id");
                City city = new City(id, name);
                cityList.add(city);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return cityList;
	}
	
	// Inizialmente avevamo gestito così le eccezioni, ma all'aumentare del loro numero abbiamo deciso di trattarle come vere e proprie eccezioni
	/*public JSONObject displayError(int type) {
		JSONObject obj = new JSONObject();
		HashMap<String, Object> error = new HashMap<String, Object>();
		switch (type) {
		case 1:
			error.put("error", "Undefined city");
			break;
		case 2:
			error.put("error", "There is no city with this name");
			break;
		case 3:
			error.put("error", "Wrong unit. Choose between C, F or K");
			break;
		default:
			error.put("error", "Unknown error");
			break;
		}
		obj = new JSONObject(error);
		return obj;
	}*/
}
