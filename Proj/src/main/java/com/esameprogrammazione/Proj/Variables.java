package com.esameprogrammazione.Proj;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Classes.MUnit;
import Classes.MUnit.unit;

public class Variables {
	
	public static MUnit globalUnit;
	
	// I percorsi dovrebbero funzionare senza problema perchè sono relativi
	private final static String apiKeyPath = "src/main/java/com/esameprogrammazione/Proj/API_KEY.txt";
	private final static String API_KEY = getApiKey();
	private final static String capitalsPath = "src/main/java/com/esameprogrammazione/Proj/Capitals.txt";
	private final static String cityPath = "src/main/java/com/esameprogrammazione/Proj/city.list.json";
	private final static String historyPath = "src/main/java/com/esameprogrammazione/Proj/History.json";
	
	public static void setGlobalVariable(String string) {
		if (string == null) {
			Variables.globalUnit = new MUnit(unit.C);
		}else{
			Variables.globalUnit = new MUnit(string);
		}
	}
	
	/**
     * Legge l'API KEY dal file "API_KEY.txt" e la restituisce
     * 
     *  
     * @return stringa corrispondente all'API KEY
     */
	private static String getApiKey(){
		String apiKey = "";
		try {
            BufferedReader br = new BufferedReader(new FileReader(new File(apiKeyPath)));
            String line = "";
            while((line = br.readLine()) != null){
            	apiKey = line;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiKey;
	}
	
	// Getters
	
	public static String getKey() {
		return API_KEY;
	}
	
	public static String getCapitalsPath() {
		return capitalsPath;
	}
	
	public static String getCityPath() {
		return cityPath;
	}
	
	public static String getHistoryPath() {
		return historyPath;
	}
	
}
