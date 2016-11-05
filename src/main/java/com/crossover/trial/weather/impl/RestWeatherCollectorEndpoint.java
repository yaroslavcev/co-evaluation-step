package com.crossover.trial.weather.impl;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.api.WeatherCollectorEndpoint;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();
    
    @Inject
    private AirportService airportService;
    
    @Inject
    private AtmosphericInformationService atmosphericInformationService;
    
    @Override
    public Response ping() {
        return Response.ok("ready").build();
    }

    @Override
    public Response updateWeather(String iataCode,
                                  String pointType,
                                  String datapointJson) {
        try {
            DataPointType dataPointType = DataPointType.valueOf(pointType);
            DataPoint dataPoint = gson.fromJson(datapointJson, DataPoint.class);
            
            atmosphericInformationService.updateWeather(iataCode, dataPointType, dataPoint);
            
        } catch (IllegalArgumentException | JsonSyntaxException ex) {
            return makePlainTextResponse(Response.Status.BAD_REQUEST, ex.getMessage());
        } catch (AirportNotFoundException e) {
            return makePlainTextResponse(Response.Status.NOT_FOUND, makeAirportNotFoundMessage(iataCode));
        }

        return Response.ok().build();
    }


    @Override
    public Response getAirports() {
        Set<String> retval = new HashSet<>();
        for (AirportData ad : airportService.getAllAirports()) {
            retval.add(ad.getIata());
        }
        return Response.ok(retval).build();
    }

    @Override
    public Response getAirport(String iata) {
        AirportData ad = findAirportData(iata);
        if (ad == null) {
            Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ad).build();
    }

    @Override
    public Response addAirport(String iata, String latString, String longString) {
        try {
            double latitude = Double.parseDouble(latString);
            double longitude = Double.parseDouble(longString);
            
            AirportData airport = new AirportData.AirportDataBuilder()
                    .setIata(iata)
                    .setLatitude(latitude)
                    .setLongitude(longitude)
                    .build();
            airportService.add(airport);
        } catch (IllegalArgumentException ex) {
            return makePlainTextResponse(Response.Status.BAD_REQUEST, ex.getMessage());
        }
        
        return Response.ok().build();
    }

    @Override
    public Response deleteAirport(String iata) {
        airportService.remove(iata);
        return Response.ok().build();
    }
    
    private AirportData findAirportData(String iata) {
        return airportService.findAirportData(iata);
    }
    
    private Response makePlainTextResponse(Status notFound, String message) {
        return Response.status(notFound).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
    
    private String makeAirportNotFoundMessage(String iata) {
        return "Airport with IATA code [" + iata + "] was not found";
    }
}
