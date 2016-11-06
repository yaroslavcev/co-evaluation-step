package com.crossover.trial.weather;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.WeatherQueryStatistic;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.WeatherQueryStatisticService;
import com.crossover.trial.weather.service.impl.WeatherQueryStatisticServiceImpl;

public class WeatherQueryStatisticTest {
    private static final long ATMPOSPHERIC_DATA_COUNT = 100;
    private static final int AIRPORTS_COUNT = 100;
    
    private static final int RADIUS = 42;
    
    private List<AirportData> airports = generateAirports(AIRPORTS_COUNT);
    
    @Test
    public void runTest() {
        WeatherQueryStatisticService statService = createWeatherQueryStatisticService();
        
        int totalRequestCount = recordStatistic(statService);
        
        WeatherQueryStatistic stat = statService.getStatisitc();
        checkRequestCount(stat, totalRequestCount);
        
        //all queries was with radius 42, so fourth bin should be equal to request count
        Assert.assertArrayEquals(new int[] {0, 0, 0, 0, totalRequestCount}, stat.getRadiusFrequency());
        
        Assert.assertEquals(ATMPOSPHERIC_DATA_COUNT, stat.getDatasize());
    }
    
    private void checkRequestCount(WeatherQueryStatistic stat, int totalRequestCount) {
        Iterator<AirportData> airportIterator = airports.iterator();
        int requestPerAirportCount = 0;

        while (airportIterator.hasNext()) {
            requestPerAirportCount++;
            AirportData airport = airportIterator.next();
            String iata = airport.getIata();
            Double actualFrequency = stat.getIataFrequency().get(iata);
            Double expectedFrequency = (double)requestPerAirportCount / totalRequestCount;
            Assert.assertEquals("Unexpected frequency for " + iata, expectedFrequency, actualFrequency);
        }
    }

    private int recordStatistic(WeatherQueryStatisticService statService) {
        Double radius = Double.valueOf(RADIUS);
        Iterator<AirportData> airportIterator = airports.iterator();
        int requestPerAirportCount = 0;
        int totalRequestCount = 0;
        while (airportIterator.hasNext()) {
            requestPerAirportCount++;
            AirportData airport = airportIterator.next();
            for (int i = 0; i < requestPerAirportCount; i++) {
                statService.recordQuery(airport.getIata(), radius);
            }
            totalRequestCount += requestPerAirportCount;
        }
        
        return totalRequestCount;
    }
    
    private WeatherQueryStatisticService createWeatherQueryStatisticService() {
        AirportService airportService = Mockito.mock(AirportService.class);
        Mockito.when(airportService.getAllAirports()).then((s) -> airports);
        
        AtmosphericInformationService atmosphericInformationService = Mockito.mock(AtmosphericInformationService.class);
        Mockito.when(atmosphericInformationService.countAtmosphericInformation(Mockito.anyLong()))
                .then((s) -> ATMPOSPHERIC_DATA_COUNT);
                
        return new WeatherQueryStatisticServiceImpl(airportService, atmosphericInformationService);
    }
    
    private List<AirportData> generateAirports(int count) {
        List<AirportData> res = new LinkedList<>();
        while(count --> 0) {
            res.add(createAirport(generateIata(count)));
        }
        return res;
    }
    
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * Generate three letter iata code by number.
     * @param num order number of airport 
     * @return three letter iata code
     */
    private String generateIata(int num) {
        return "" + ALPHABET.charAt(num / (2 * ALPHABET.length())) + ALPHABET.charAt(num / ALPHABET.length())
                + ALPHABET.charAt(num % ALPHABET.length());
    }
    
    private AirportData createAirport(String iata) {
        AirportData res = new AirportData.AirportDataBuilder().setIata(iata).build();

        return res;
    }
}
