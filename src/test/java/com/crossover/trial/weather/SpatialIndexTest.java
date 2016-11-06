package com.crossover.trial.weather;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.dao.impl.AirportSpatialIndex;

public class SpatialIndexTest {
    AirportSpatialIndex spatiallIndex;
    
    AirportData A = new AirportData("A", 44.366883, -72.239870);
    AirportData B = new AirportData("B", 43.424152, -7.479350);
    AirportData C = new AirportData("C", 5.282579 , -8.133497);
    AirportData D = new AirportData("D", 6.595778 , -73.318745);
    
    @Before
    public void setUp() {
        spatiallIndex = new AirportSpatialIndex();
        
        spatiallIndex.add(A);
        spatiallIndex.add(B);
        spatiallIndex.add(C);
        spatiallIndex.add(D);
    }
    
    @Test
    public void noMatches() {
        List<AirportData> res = spatiallIndex.findAllAirportInsideRectangle(35, -84, 40, -80);
        Assert.assertTrue(res.isEmpty());
    }
    
    @Test
    public void oneMatch() {
        List<AirportData> res = spatiallIndex.findAllAirportInsideRectangle(35, -84, 53, -59);
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.contains(A));
    }
    
    @Test
    public void twoMatches() {
        List<AirportData> res = findTwoMatches();
        Assert.assertEquals(2, res.size());
        Assert.assertTrue(res.contains(B));
        Assert.assertTrue(res.contains(C));
    }
    
    @Test
    public void deleteAndAdd() {
        twoMatches();
        
        spatiallIndex.remove(B);
        List<AirportData> res = findTwoMatches();
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(res.contains(C));
        
        spatiallIndex.add(B);
        
        twoMatches();
    }
    
    private List<AirportData> findTwoMatches() {
        return spatiallIndex.findAllAirportInsideRectangle(0, -9, 50, -10);
    }
}
