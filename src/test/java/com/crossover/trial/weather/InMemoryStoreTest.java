package com.crossover.trial.weather;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.dao.impl.InMemoryStoreImpl;

public class InMemoryStoreTest {
    private static final long _24H = 86400000; 
    private static final long _48H = 2 * _24H;
    
    private static final String AAA = "AAA";
    private static final String BBB = "BBB";
    private static final String CCC = "CCC";
    
    private InMemoryStoreImpl store = new InMemoryStoreImpl();
    
    private AirportData aaa = new AirportData(AAA, 10, 10);
    private AirportData bbb = new AirportData(BBB, 20, 20);
    private AirportData ccc = new AirportData(CCC, 30, 30);
    
    private Set<AirportData> allAirports = new HashSet<>();
    
    @Before
    public void startWeatherServer() throws AirportNotFoundException {
        addAirport(aaa);
        addAirport(bbb);
        addAirport(ccc);
        
        store.updateWeather(AAA, new DataPoint.Builder()
                .withLastUpdate(System.currentTimeMillis() - _48H)
                .withType(DataPointType.WIND)
                .build());
        
        store.updateWeather(AAA, new DataPoint.Builder()
                .withLastUpdate(System.currentTimeMillis())
                .withType(DataPointType.HUMIDTY)
                .build());
        
        store.updateWeather(BBB, new DataPoint.Builder()
                .withLastUpdate(System.currentTimeMillis())
                .withType(DataPointType.WIND)
                .build());
        
        store.updateWeather(BBB, new DataPoint.Builder()
                .withLastUpdate(System.currentTimeMillis())
                .withType(DataPointType.HUMIDTY)
                .build());
    }
    
    private void addAirport(AirportData airport) {
        store.add(airport);
        allAirports.add(airport);
    }
    
    @Test
    public void findAirportTest() {
        AirportData found = store.findAirport(AAA);
        Assert.assertEquals(aaa.getIata(), found.getIata());
        Assert.assertEquals(aaa.getLatitude(), found.getLatitude(), 0);
        Assert.assertEquals(aaa.getLongitude(), found.getLongitude(), 0);
        
        Collection<AirportData> allFoundAirports = store.getAllAirports();
        Assert.assertTrue(allFoundAirports.containsAll(allAirports));
    }
    
    @Test
    public void removeAirportTest() {
        AirportData found = store.findAirport(AAA);
        Assert.assertTrue(found != null);
        
        store.remove(AAA);
        
        found = store.findAirport(AAA);
        Assert.assertTrue(found == null);
    }
    
    @Test
    public void countDataPointsTest() {
        Assert.assertEquals(3, store.countDataPoints(System.currentTimeMillis() -_24H));
    }
    
    @Test
    public void updateWeather() throws AirportNotFoundException {
        DataPoint newDp = new DataPoint.Builder()
                .withLastUpdate(System.currentTimeMillis())
                .withType(DataPointType.TEMPERATURE)
                .build();
        
        store.updateWeather(CCC, newDp);
        
        Collection<DataPoint> foundDp = store.getAllForAirport(CCC);
        
        Assert.assertEquals(1, foundDp.size());
        Assert.assertTrue(foundDp.contains(newDp));
    }
}
