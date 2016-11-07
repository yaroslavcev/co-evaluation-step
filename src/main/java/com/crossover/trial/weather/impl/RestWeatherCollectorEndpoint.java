package com.crossover.trial.weather.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.api.WeatherCollectorEndpoint;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(RestWeatherCollectorEndpoint.class);

    private Gson gson = new Gson();
    
    private AirportService airportService;
    
    private AtmosphericInformationService atmosphericInformationService;
    
    @Inject
    public RestWeatherCollectorEndpoint(AirportService airportService,
            AtmosphericInformationService atmosphericInformationService) {
        this.airportService = airportService;
        this.atmosphericInformationService = atmosphericInformationService;
    }
    
    @Override
    public Response ping() {
        return Response.ok("1").build();
    }

    @Override
    public Response updateWeather(String iataCode,
                                  String pointType,
                                  String datapointJson) {
        try {
            DataPointType dataPointType = DataPointType.valueOf(pointType);
            DataPoint dataPoint = gson.fromJson(datapointJson, DataPoint.class);
            
            dataPoint = completeDataPoint(dataPoint, dataPointType);
            
            atmosphericInformationService.updateWeather(iataCode, dataPointType, dataPoint);
            
        } catch (IllegalArgumentException | JsonSyntaxException ex) {
            LOG.debug("Exception while serving weather update request", ex);
            return makePlainTextResponse(Response.Status.BAD_REQUEST, ex.getMessage());
        } catch (AirportNotFoundException e) {
            LOG.debug("AirportNotFoundException exception while serving weather update request", e);
            return makePlainTextResponse(Response.Status.NOT_FOUND, makeAirportNotFoundMessage(iataCode));
        }

        return Response.ok().build();
    }

    @Override
    public Response getAirports() {
        Collection<AirportData> foundAirports = airportService.getAllAirports();
        
        List<String> codes = foundAirports.stream()
                .map(a -> a.getIata())
                .collect(Collectors.toList());
        
        return Response.ok(codes).build();
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
            LOG.debug("Exception while serving add airport request", ex);
            return makePlainTextResponse(Response.Status.BAD_REQUEST, ex.getMessage());
        }
        
        return Response.ok().build();
    }

    @Override
    public Response deleteAirport(String iata) {
        airportService.remove(iata);
        return Response.ok().build();
    }
    
    /**
     * If data point does not contain type and update time then complete them.
     */
    private DataPoint completeDataPoint(DataPoint dataPoint, DataPointType dataPointType) {
        if (dataPoint.getLastUpdateTime() > 0 && dataPoint.getType() != null) {
            return dataPoint;
        }
        
        DataPoint dp = new DataPoint.Builder()
                .withType(dataPointType)
                .withLastUpdate(System.currentTimeMillis())
                .withMean(dataPoint.getMean())
                .withFirst(dataPoint.getFirst())
                .withSecond(dataPoint.getSecond())
                .withThird(dataPoint.getThird())
                .withCount(dataPoint.getCount())
                .build();
        
        return dp;
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
