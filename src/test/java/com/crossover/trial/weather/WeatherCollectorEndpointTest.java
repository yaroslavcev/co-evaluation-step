package com.crossover.trial.weather;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.api.WeatherCollectorEndpoint;
import com.crossover.trial.weather.impl.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.google.gson.Gson;

/**
 * Test for WeatherCollectorEndpoint implementation.
 * @author Ilya
 *
 */
public class WeatherCollectorEndpointTest {
    private WeatherCollectorEndpoint collectorEndpoint;

    private AirportService airportServiceMock;
    private AtmosphericInformationService atmosphericInformationServiceMock;

    private Gson _gson = new Gson();

    @Before
    public void setUp() throws Exception {
        airportServiceMock = mock(AirportService.class);
        atmosphericInformationServiceMock = mock(AtmosphericInformationService.class);
        
        collectorEndpoint = new RestWeatherCollectorEndpoint(airportServiceMock, atmosphericInformationServiceMock);
    }
    
    @Test
    public void testCollectorPing() throws Exception {
        Response resp = collectorEndpoint.ping();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        
        Assert.assertEquals("1", resp.getEntity());
    }
    
    @Test
    public void testCollectorUpdateWeatherOk() throws Exception {
        DataPoint dp = new DataPoint.Builder()
            .withLastUpdate(System.currentTimeMillis())
            .withType(DataPointType.HUMIDTY)
            .build();

        Response resp = collectorEndpoint.updateWeather("AAA", DataPointType.HUMIDTY.name(), _gson.toJson(dp));
        
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        
        verify(atmosphericInformationServiceMock).updateWeather("AAA", DataPointType.HUMIDTY, dp);
    }
    
    @Test
    public void testCollectorUpdateWeatherBadRequest() throws Exception {
        Response resp = collectorEndpoint.updateWeather("AAA", DataPointType.HUMIDTY.name(), "{{}");
        
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    }
    
    @Test
    public void testCollectorGetAirports() throws Exception {
        List<AirportData> airports = new ArrayList<>();
        airports.add(new AirportData("AAA", 0, 0));
        
        when(airportServiceMock.getAllAirports()).thenReturn(airports);
        Response resp = collectorEndpoint.getAirports();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Assert.assertEquals(Arrays.asList("AAA"), resp.getEntity());
    }
    
    @Test
    public void testCollectorGetAirport() throws Exception {
        AirportData airport = new AirportData("AAA", 0, 0);
        
        when(airportServiceMock.findAirportData("AAA")).thenReturn(airport);
        Response resp = collectorEndpoint.getAirport("AAA");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        Assert.assertEquals(airport, resp.getEntity());
    }

    @Test
    public void testCollectorAddAirportOk() throws Exception {
        AirportData airport = new AirportData("AAA", 10, 10);
        
        Response resp = collectorEndpoint.addAirport("AAA", "10", "10");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        verify(airportServiceMock).add(airport);
    }
    
    @Test
    public void testCollectorAddAirportBadRequest() throws Exception {
        Response resp = collectorEndpoint.addAirport("AAA", "O", "10");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        
        resp = collectorEndpoint.addAirport("", "10", "10");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
        
        verifyNoMoreInteractions(airportServiceMock);
    }
    
    @Test
    public void testCollectorDeleteAirport() throws Exception {
        Response resp = collectorEndpoint.deleteAirport("AAA");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        verify(airportServiceMock).remove("AAA");
    }
}