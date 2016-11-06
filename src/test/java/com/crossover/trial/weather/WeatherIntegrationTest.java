package com.crossover.trial.weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPointType;

public class WeatherIntegrationTest {
	private static final String BASE_URI = "http://localhost:9090";
	private static final int AIRPORT_COUNT = 14; 
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private WeatherServer weatherServer;
	private WeatherClient weatherClient;
	
	private Set<String> allAirportsIataCodes = new HashSet<>();
	
	@Before
	public void startWeatherServer() throws IOException {
		weatherServer = new WeatherServer(BASE_URI);
		weatherServer.start();
		
		weatherClient = new WeatherClient(BASE_URI);//"http://ipv4.fiddler:9090");//
		
		loadAirports();
	}
	
	private void loadAirports() throws IOException {
	    AirportLoader al = new AirportLoader();
	    try (InputStream is = WeatherIntegrationTest.class.getResourceAsStream("/airports_integration.dat")) {
	        al.upload(is);
	    }
	    
	    for (int i = 0; i < AIRPORT_COUNT; i++) {
	        allAirportsIataCodes.add("AA" + ALPHABET.charAt(i));
	    }
	}
	
	@Test
	public void runTest() {
	    List<String> airports = weatherClient.getAirports();
	    Assert.assertTrue(airports.containsAll(allAirportsIataCodes));
	    
	    final String AAK = "AAK"; 
	    AirportData aak = weatherClient.getAirport(AAK);//1,"NA","L","France","AAK","KBOS",49.408590,5.525207,19,-5,"A"
	    Assert.assertEquals(AAK, aak.getIata());
	    Assert.assertEquals(49.408590, aak.getLatitude(), 0);
	    Assert.assertEquals(5.525207, aak.getLongitude(), 0);
	    
	    Response response = weatherClient.deleteAirport(AAK);
	    assertResponseIsOk(response);
	    allAirportsIataCodes.remove(AAK);
	    
	    aak = weatherClient.getAirport(AAK);
	    Assert.assertNull(aak);
	    
	    updateWeather();
	    
	    List<AtmosphericInformation> ai = weatherClient.weather("AAA", "150");
	    
	    checkAtmosphericInformation(ai);
	    
	    Assert.assertTrue(weatherClient.pingCollect());
	    
	    Map<String, Object> statisticMap = weatherClient.pingQuery();
	    
	    checkStatisitic(statisticMap);
	}
	
	private void checkStatisitic(Map<String, Object> statisticMap) {
        Assert.assertEquals(13d, statisticMap.get("datasize"));
        Assert.assertEquals(Arrays.asList(0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,1d), statisticMap.get("radius_freq"));
        
        Map<String, Double> iataFreq = new HashMap<>();
        for (String iata : allAirportsIataCodes) {
            iataFreq.put(iata, 0d);
        }
        iataFreq.put("AAA", 1d);
        Assert.assertEquals(iataFreq, statisticMap.get("iata_freq"));
    }

    private void checkAtmosphericInformation(List<AtmosphericInformation> atmosphericInformation) {
	    Assert.assertEquals(2, atmosphericInformation.size());
	    for (AtmosphericInformation ai : atmosphericInformation) {
            Assert.assertNotNull(ai.getWind());
        }
    }

    private void updateWeather() {
	    for (String airportIata : allAirportsIataCodes) {
            Response response = weatherClient.updateWeather(airportIata, DataPointType.WIND, 10, 10, 10, 10, 10);
	        assertResponseIsOk(response);
        }
	}
	
	private void assertResponseIsOk(Response resp) {
	    Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
	}
	
	@After
	public void stopWeatherServer() {
		weatherServer.stop();
	    //while (true){}
	}
}
