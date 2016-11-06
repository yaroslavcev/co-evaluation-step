package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.api.WeatherQueryEndpoint;
import com.crossover.trial.weather.api.WeatherQueryStatistic;
import com.crossover.trial.weather.impl.RestWeatherQueryEndpoint;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.WeatherQueryStatisticService;

public class WeatherQueryEndpointTest {

    private WeatherQueryEndpoint _query;

    private AtmosphericInformationService atmosphericInformationServiceMock;
    private WeatherQueryStatisticService queryStatisticMock;

    @Before
    public void setUp() throws Exception {
        atmosphericInformationServiceMock = mock(AtmosphericInformationService.class);
        queryStatisticMock = mock(WeatherQueryStatisticService.class);

        _query = new RestWeatherQueryEndpoint(queryStatisticMock, atmosphericInformationServiceMock);
    }

    @Test
    public void testQueryPing() throws Exception {
        WeatherQueryStatistic stat = new WeatherQueryStatistic();
        stat.setDatasize(10);

        HashMap<String, Double> hm = new HashMap<String, Double>();
        hm.put("AAA", 0.5d);
        hm.put("BBB", 0.5d);
        stat.setIataFrequency(hm);

        stat.setRadiusFrequency(new int[] { 0, 0, 10 });

        when(queryStatisticMock.getStatisitc()).thenReturn(stat);

        String ping = _query.ping();

        assertEquals("{\"iata_freq\":{\"AAA\":0.5,\"BBB\":0.5},\"radius_freq\":[0,0,10],\"datasize\":10}", ping);
    }

    @Test
    public void testQueryWeatherBadRequest() throws Exception {
        Response resp = _query.weather("BOS", "a");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    }

    @Test
    public void testQueryWeatherNotFound() throws Exception {
        Response resp = _query.weather("BOS", "10");
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    public void testQueryWeatherOk() throws Exception {
        List<AtmosphericInformation> atmInfoList = new ArrayList<>();
        AtmosphericInformation atmInfo = new AtmosphericInformation();
        atmInfo.setLastUpdateTime(10);
        atmInfo.setHumidity(new DataPoint.Builder().withLastUpdate(System.currentTimeMillis())
                .withType(DataPointType.HUMIDTY).build());
        atmInfoList.add(atmInfo);

        when(atmosphericInformationServiceMock.getForRadius(anyString(), anyDouble())).thenReturn(atmInfoList);
        Response resp = _query.weather("BOS", "10");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());

        Assert.assertEquals(atmInfoList, resp.getEntity());
    }
}