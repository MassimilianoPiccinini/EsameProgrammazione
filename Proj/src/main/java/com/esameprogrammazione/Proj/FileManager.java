package com.esameprogrammazione.Proj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Classes.City;
import Exceptions.IdNotFoundException;

public class FileManager {
	
	/**
     * Scrive un JSON array in un file 
     * 
     * @param array - JSON array da scrivere nel file
     * @param nomefile - nome del file su cui scrivere il JSON array
     *  
     * 
     */
	public static void writeJarray (JSONArray array, String nomefile) {
		try {
			emptyStorage();
			FileWriter file = new FileWriter(nomefile);
			file.write(array.toJSONString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Controlla che l'Id inserito esista all'interno del file "city.list.json"
     * Viene utilizzato per verificare se lanciare o meno l'eccezione IdNotFoundException
     * 
     * @param id - id da cercare nel file
     *  
     * @return boolean, true se l'id è presente, false altrimenti
     */
	public static boolean checkId (Long id){
        JSONParser parser = new JSONParser();
        try {
            Reader reader = new FileReader(Variables.getCityPath());

            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            Iterator<?> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();

                String id1 = Long.toString(id);

                if (obj.get("id") instanceof Long) {
                    Long id2 = (Long) obj.get("id");
                    String id3 = Long.toString(id2);
                    if (id1.contentEquals(id3)) {
                    	return true;
                    }
                }else if(obj.get("id") instanceof Double) {
                    Double id2 = (Double) obj.get("id");
                    String id3 = Double.toString(id2);
                    if (id1.contentEquals(id3)) {
                    	return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return false;
	}
	
	/**
     * Controlla che il nome inserito esista all'interno del file "city.list.json"
     * Viene utilizzato per verificare se lanciare o meno l'eccezione NameNotFoundException
     * 
     * @param name - name da cercare nel file
     *  
     * @return boolean, true se il nome è presente, false altrimenti
     */
	public static boolean checkName (String name){
        JSONParser parser = new JSONParser();
        //JsonParser converte JSONstring in JsonObject

        try {
            Reader reader = new FileReader(Variables.getCityPath());

            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            //parsing JSON string del file

            Iterator<?> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();
                String nameObj = (String) obj.get("name");
                if (name.contentEquals(nameObj)) {
                	return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return false;
	}
	
	/**
     * Restituisce il giorno odierno se il valore boolean è true,
     * altrimenti restituisce il giorno odierno, considerando il 19 Gennaio 2021 come giorno 0 (giorno in cui abbiamo iniziato a salvare i dati nello storico)
     * 
     * @param isZero - valore boolean per determinare che che giorno restituire
     *  
     * @return giorno odierno
     */
	
	public static int getIntData(boolean isZero) {
		DateTimeFormatter dtf_day = DateTimeFormatter.ofPattern("dd");
		DateTimeFormatter dtf_mes = DateTimeFormatter.ofPattern("mm");
        LocalDateTime now = LocalDateTime.now();
        String day = dtf_day.format(now);
        String month = dtf_mes.format(now);
        int giorno = Integer.parseInt(day);
        int mese = Integer.parseInt(month);
        switch(mese) {
        	case 1: break;
        	case 2: giorno += 31;
					break;
        	case 3: giorno += 59;
					break;
        	case 4: giorno += 90;
					break;
        	case 5: giorno += 120;
					break;
        	case 6: giorno += 151;
					break;
        	case 7: giorno += 181;
					break;
        	case 8: giorno += 212;
					break;
        	case 9: giorno += 243;
					break;
        	case 10: giorno += 273;
					break;
        	case 11: giorno += 304;
					break;
        	case 12: giorno += 334;
					break;
        }
        if (!isZero) {
	        switch(giorno) {
		        case 1: giorno = 348;
		        		break;
		    	case 2: giorno = 349;
						break;
		    	case 3: giorno = 350;
						break;
		    	case 4: giorno = 351;
						break;
		    	case 5: giorno = 352;
						break;
		    	case 6: giorno = 353;
						break;
		    	case 7: giorno = 354;
						break;
		    	case 8: giorno = 355;
						break;
		    	case 9: giorno = 356;
						break;
		    	case 10: giorno = 357;
						break;
		    	case 11: giorno = 358;
						break;
		    	case 12: giorno = 359;
						break;
		    	case 13: giorno = 360;
		    			break;
		    	case 14: giorno = 361;
						break;
		    	case 15: giorno = 362;
						break;
		    	case 16: giorno = 363;
						break;
		    	case 17: giorno = 364;
						break;
		    	case 18: giorno = 365;
						break;
		    	default: giorno -= 19;
		    		break;
	        }
        }
        return giorno;
	}
	
	/**
     * Restituisce una lista di città con quel determinato nome
     * 
     * @param name - nome delle città da inserire nella lista
     *  
     * @return lista di città 
     */

	public static List<City> getId(String name){
		List<City> cities = new ArrayList<>();
		JSONParser parser = new JSONParser();

        try {
        	Reader reader = new FileReader(Variables.getCityPath());
    		
        	JSONArray jsonArray = (JSONArray) parser.parse(reader);
        	
        	Iterator<?> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();
                String currentName = (String) obj.get("name");
	            if (currentName.trim().toLowerCase().equals(name.trim().toLowerCase())) {
	            	Long id = (Long) obj.get("id");
		            String country = (String) obj.get("country");
	            	City city = new City(id, currentName, country);
	            	cities.add(city);
	            }
        	}


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return cities;
	}
	
	/**
     * Cerca nello storico la città con quel determinato id
     * 
     * @param array - JSON array che rappresenta lo storico
     * @param id - id della città da cercare
     * @param edit - boolean che permette di modificare lo storico se è true
     *  
     * @return tupla contenente due valori boolean e un JSON object
     * 		   il primo valore boolean è true se esiste una città con quel id
     * 		   il secondo valore boolen è true se i records dell'oggetto City possono essere modificati
     * 		   il JSON object contiene l'oggetto City con quel id 
     * 
     * @throws IdNotFoundException - Se non esiste nessuna città con quell'id
     */
	
	public static Triplet<Boolean, Boolean, JSONObject> find (JSONArray array, long id, boolean edit) throws IdNotFoundException {
		for (int i = 0; i < array.size(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            if (obj != null && obj.get("id") != null) {
	            long obj_id = (long) obj.get("id");
		        if((obj_id == id)) {
		            JSONArray records = (JSONArray) obj.get("records");
		            int dayInt = 0;
		            if (records != null) {
		            	Iterator<?> iterator = records.iterator();
		    	        while (iterator.hasNext()) {
		    	        	JSONObject record = (JSONObject) iterator.next();
		    	        	long dateTimeLong = 0;
		    	        	if (record.get("dt") instanceof Long) {
		    		        	Long date_time = (Long) record.get("dt");
		    		        	dateTimeLong = date_time.longValue();
		    		        }else if(record.get("dt") instanceof Double) {
		    		        	Double date_time = (Double) record.get("dt");
		    		        	dateTimeLong = date_time.longValue();
		    		        }else if(record.get("dt") instanceof Float) {	
		    		        	Float date_time = (Float) record.get("dt");
		    		        	dateTimeLong = date_time.longValue();
		    		        }
		    	            Date date = new Date(dateTimeLong + 28800000); // Fuso orario
		    	            SimpleDateFormat df2 = new SimpleDateFormat("dd");
		    	            String day = df2.format(date);
		    	            if (dayInt < Integer.parseInt(day)) { //prendere giorno complessivo annuale: getIntDate()
		    	            	dayInt = Integer.parseInt(day); //prendere giorno complessivo annuale: getIntDate()
		    	            }
		    	        }
		            }else {
		            	dayInt = getIntData(true) - 1;
		            }
			        if (dayInt < getIntData(true)) {
			        	//if (edit) {
			        		return new Triplet<Boolean, Boolean, JSONObject> (true, edit, obj);
			        	/*}else {
			        		return new Triplet<Boolean, Boolean, JSONObject> (true, false, obj);
			        	}*/
			        }else {
			          	return new Triplet<Boolean, Boolean, JSONObject> (true, false, obj);
			        }
		        }
            }
        }
		throw new IdNotFoundException("The entered id does not refer to an European Capital: " + id);
    }
	
	/**
     * Legge un JSON array dal file con quel nome
     * 
     * @param nomefile - nome del file da cui leggere
     *  
     * @return JSON array 
     */
	
	public static JSONArray readJarray (String nomefile) {
		JSONArray array = new JSONArray();
	    JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(nomefile))
        {
            Object obj = jsonParser.parse(reader);
            if (obj != null) {
            	array = (JSONArray) obj;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
        return array;
	}
	
	/**
     * Sostituisce il contenuto dello storico con un JSON array vuoto
     * 
     * 
     */
	
	public static void emptyStorage() {
        try {
            PrintWriter writer = new PrintWriter(Variables.getHistoryPath());
            writer.print("[ ]");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
