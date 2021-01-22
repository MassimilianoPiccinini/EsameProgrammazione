package com.esameprogrammazione.Proj;
import Classes.*;
import Exceptions.IdNotFoundException;
import Exceptions.NameCityNotFoundException;
import Exceptions.WrongDateException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

class ProjApplicationTests {


	/**
	 * Test sulla ricerca dell'id della città con stesso nome.
	 * 
	 * @result ProjApplication.search("London") mi restituisce un JSONArray di JSONObject
	 * 		   che confrontiamo con assertEquals col nostro JSONArray atteso.
	 *         N.B il test è funzionante ma restituisce name e country in ordine inverso
	 */
	
	@Test
	void testSearchString() {
		JSONArray arr = new JSONArray();
		JSONObject obj1 = new JSONObject();
		obj1.put("name", "London");
		obj1.put("country", "GB");
		obj1.put("id", 2643743);
		arr.add(obj1);
		JSONObject obj2 = new JSONObject();
		obj2.put("name", "London");
		obj2.put("country", "US");
		obj2.put("id", 4119617);
		arr.add(obj2);
		JSONObject obj3 = new JSONObject();
		obj3.put("name", "London");
		obj3.put("country", "US");
		obj3.put("id", 4298960);
		arr.add(obj3);
		JSONObject obj4 = new JSONObject();
		obj4.put("name", "London");
		obj4.put("country", "US");
		obj4.put("id", 4517009);
		arr.add(obj4);
		JSONObject obj5 = new JSONObject();
		obj5.put("name", "London");
		obj5.put("country", "US");
		obj5.put("id", 5056033);
		arr.add(obj5);
		JSONObject obj6 = new JSONObject();
		obj6.put("name", "London");
		obj6.put("country", "US");
		obj6.put("id", 5367815);
		arr.add(obj6);
		JSONObject obj7 = new JSONObject();
		obj7.put("name", "London");
		obj7.put("country", "CA");
		obj7.put("id", 6058560);
		arr.add(obj7);
		
		try {
			assertEquals(arr, ProjApplication.search("London"));
		} catch (NameCityNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Test sulla restituzione dell'elenco delle città ma con nome errato al fine di lanciare l'eccezione.
	 * @result ProjApplication.search("Londo"), "Londo" ovviamente non esiste, ci aspettiamo che lanci 
	 * 		   l'eccezione,tale lancio è controllato da un boolean che diventa true 
	 * 		   qualora l'eccezione venisse lanciata.
	 * 		   
	 *        
	 */

	@Test
	void testSearchStringErrorName(){
        boolean thrown = false;
        ProjApplication prj = new ProjApplication();
        try {
            prj.search("Londo");
        } catch (NameCityNotFoundException e) {
            thrown = true;
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

	/**
	 * Test sulla restituzione del meteo della città selezionata ma con id errato al fine di lanciare l'eccezione.
	 * @result prj.search(31690, "F"), 31690 ovviamente non esiste come id, ci aspettiamo che lanci 
	 * 		   l'eccezione,tale lancio è controllato da assertEquals sul messaggio di errore restituito.
	 * 		        
	 */
	
    @Test
    public void testSearchStringError() {
        ProjApplication prj = new ProjApplication();
        Throwable exception = assertThrows(IdNotFoundException.class, () -> prj.search(31690, "F"));
        assertEquals("Can not find city from id: 31690", exception.getMessage());
    }

	/**
	 * Test sulla restituzione delle statistiche della città selezionata
	 * @result Ci aspettiamo un JSONObject contenente i dati delle statistiche
	 * 		   controllato da assertEquals dove 
	 * 		   il primo valore sono le nostre aspettative e il secondo il reale risultato
	 * 		   assertEquals(aspettative, reale risultato).
	 * 		        
	 */
    
    @Test
    void testGetStats() {
        ProjApplication prj = new ProjApplication();
        JSONObject obj = new JSONObject();
        obj.put("tempMax", 11.280001);
        obj.put("temp", 10.57);
        obj.put("variance", 0.46239975);
        obj.put("tempMin", 10.0);
		try {
			int[] period = {0, 2};
			assertEquals(obj, prj.stats(2643743, "c", "0", "2"));
		} catch (WrongDateException e) {
			e.printStackTrace();
		} catch (IdNotFoundException e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * Test sulla chiamata dell'eccezione di data inizio errata per quanto riguarda le statistiche
	 * @result Ci aspettiamo la chiamata dell'eccezione WrongDateException 
	 *         con relativo check sul messaggio di errore, in questo caso la data di inizio è negativa 
	 *         ma avremmo potuto metterla anche maggiore della data odierna e avremmo ugualmente avuto l'eccezione.
	 * 		        
	 */
    
    @Test
    void testGetStatsBeginErrorDate() {
        ProjApplication prj = new ProjApplication();
        Throwable exception = assertThrows(WrongDateException.class, () -> prj.stats(2643743, "c", "-1", "2"));
        assertEquals("Data di inizio non accettabile", exception.getMessage());
    }

	/**
	 * Test sulla chiamata dell'eccezione sulla durata errate per quanto riguara le statistiche
	 * @result Ci aspettiamo la chiamata dell'eccezione WrongDateException 
	 *         con relativo check sul messaggio di errore, in questo caso la data di inizio è valida
	 *         ma la duration è maggiore rispetto al massimo attuale di 6.
	 * 		        
	 */
    
    @Test
    void testGetStatsDurationErrorDate() {
        ProjApplication prj = new ProjApplication();
        Throwable exception = assertThrows(WrongDateException.class, () -> prj.stats(2643743, "c", "1", "20"));
        assertEquals("Durata non accettabile", exception.getMessage());
    }
    
    /**
     * Test sulla chiamata dell'eccezione dell'id non appartenente ad una capitale europea nelle statistiche
     * @result Ci aspettiamo la chiamata dell'eccezione IdNotFoundException 
	 *         con relativo check sul messaggio di errore, in questo caso l'id non viene trovato,
	 *         come mi aspettavo, in quanto l'id è della città di Ancona, che non è una capitale Europea
     */
    
    @Test
    void testGetStatsIdError() {
        ProjApplication prj = new ProjApplication();
        Throwable exception = assertThrows(IdNotFoundException.class, () -> prj.stats(3183089, "u", "1", "2"));
        assertEquals("The entered id does not refer to an European Capital: 3183089", exception.getMessage());
    }
    
    /**
     * Test sulla chiamata dell'eccezione dell'id non appartenente ad una capitale europea nello storico
     * @result Ci aspettiamo la chiamata dell'eccezione IdNotFoundException 
	 *         con relativo check sul messaggio di errore, in questo caso l'id non viene trovato,
	 *         come mi aspettavo, in quanto l'id è della città di Ancona, che non è una capitale Europea
     */
    
    @Test
    void testGetHistoryIdError() {
        ProjApplication prj = new ProjApplication();
        Throwable exception = assertThrows(IdNotFoundException.class, () -> prj.history(3183089, "u", "1", "2"));
        assertEquals("The entered id does not refer to an European Capital: 3183089", exception.getMessage());
    }
}
