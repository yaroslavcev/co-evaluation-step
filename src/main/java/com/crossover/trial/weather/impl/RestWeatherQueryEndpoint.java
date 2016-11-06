package com.crossover.trial.weather.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.WeatherQueryEndpoint;
import com.crossover.trial.weather.api.WeatherQueryStatistic;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.WeatherQueryStatisticService;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container.
 *
 * @author code test administrator
 */

@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    /** shared gson json to object factory */
    private static final Gson gson = new Gson();
    
    private WeatherQueryStatisticService queryStatistic;
    
    private AtmosphericInformationService atmosphericInformationService; 
    
    @Inject
    public RestWeatherQueryEndpoint(WeatherQueryStatisticService queryStatistic,
            AtmosphericInformationService atmosphericInformationService) {
        this.queryStatistic = queryStatistic;
        this.atmosphericInformationService = atmosphericInformationService;
    }
    
    @Override
    public String ping() {
    	WeatherQueryStatistic stastic = queryStatistic.getStatisitc();
    	
        Map<String, Object> retval = new HashMap<>();
        retval.put("datasize", stastic.getDatasize());
        retval.put("iata_freq",stastic.getIataFrequency());
        retval.put("radius_freq", stastic.getRadiusFrequency());

        return gson.toJson(retval);
    }

    @Override
    public Response weather(String iata, String radiusString) {
        double radius =  0;
        try {
            radius = Double.valueOf(radiusString);
        } catch (NumberFormatException ex) {
            return makePlainTextResponse(Response.Status.BAD_REQUEST, ex.getMessage());
        }
        
        List<AtmosphericInformation> retval = atmosphericInformationService.getForRadius(iata, radius);
        if (retval.isEmpty()) {
            return makePlainTextResponse(Response.Status.NOT_FOUND, makeAirportNotFoundMessage(iata));
        }
        updateRequestFrequency(iata, radius);

        return Response.status(Response.Status.OK).entity(retval).build();
    }

    private void updateRequestFrequency(String iata, Double radius) {
        queryStatistic.recordQuery(iata, radius);
    }
    
    private String makeAirportNotFoundMessage(String iata) {
        return "Airport with IATA code [" + iata + "] was not found";
    }
    
    private Response makePlainTextResponse(Status notFound, String message) {
        return Response.status(notFound).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
}
