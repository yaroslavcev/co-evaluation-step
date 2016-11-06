package com.crossover.trial.weather;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;

/**
 * A reference implementation for the weather client. Consumers of the REST API
 * can look at WeatherClient to understand API semantics.
 *
 * @author code test administrator
 */
public class WeatherClient {

    public static final String BASE_URI = "http://localhost:9090";

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    /**
     * Construct client that uses default base uri http://localhost:9090.
     */
    public WeatherClient() {
        this(BASE_URI);
    }

    /**
     * Construct client with base uri to be used for connecting to the service.
     * @param baseUri base uri
     */
    public WeatherClient(String baseUri) {
        Client client = ClientBuilder.newClient();
        query = client.target(baseUri).path("query");
        collect = client.target(baseUri).path("collect");
    }

    /**
     * Make ping request to query service. 
     * @return return statistic of service usage
     */
    public Map<String, Object> pingQuery() {
        WebTarget path = query.path("/ping");
        return path.request().get(new GenericType<Map<String, Object>>() {});
    }

    /**
     * Make ping request to collect service.
     * @return {@code true} if the service is alive and {@code false} otherwise
     */
    public boolean pingCollect() {
        WebTarget path = collect.path("/ping");
        String response = path.request().get(String.class);
        return "1".equals(response);
    }
    
    /**
     * Request atmospheric information for specified airport and its adjacent airports in specified radius.
     * @param iata IATA code of the airport
     * @param radius radius in km
     * @return list of atmospheric information
     */
    public List<AtmosphericInformation> weather(String iata, String radius) {
        return query.path("/weather/{iata}/{radius}")
                .resolveTemplate("iata", iata)
                .resolveTemplate("radius", radius)
                .request()
                .get(new GenericType<List<AtmosphericInformation>>() {});
    }
    
    /**
     * Update the airports atmospheric information for a particular pointType
     * with json formatted data point information.
     * 
     * @param iata the 3 letter airport code
     * @param pointType the point type, {@link DataPointType} for a complete list
     * @param mean the mean of the observations
     * @param first 1st quartile -- useful as a lower bound
     * @param second 2nd quartile -- median value
     * @param third 3rd quartile value -- less noisy upper value
     * @param count the total number of measurements
     * @return response object
     */
    public Response updateWeather(String iata, DataPointType pointType, double mean, int first, int second, int third,
            int count) {
        WebTarget path = collect.path("/weather/{iata}/{pointType}")
                .resolveTemplate("iata", iata)
                .resolveTemplate("pointType", pointType);

        DataPoint dp = new DataPoint.Builder()
                .withMean(mean)
                .withFirst(first)
                .withSecond(second)
                .withThird(third)
                .withCount(count)
                .build();

        return path.request().post(Entity.entity(dp, MediaType.APPLICATION_JSON));
    }
    
    /**
     * Add airport with specified IATA code, latitude and longitude.
     * @param iata IATA code
     * @param latString latitude
     * @param longString longitude
     * @return response object
     */
    public Response addAirport(String iata, String latString, String longString) {
        return collect.path("/airport/{iata}/{lat}/{long}")
                .resolveTemplate("iata", iata)
                .resolveTemplate("lat", latString)
                .resolveTemplate("long", longString)
                .request()
                .post(Entity.text(""));
    }
    
    /**
     * Delete airport with specified IATA code.
     * @param iata IATA code
     * @return response object
     */
    public Response deleteAirport(String iata) {
        return collect.path("/airport/{iata}")
                .resolveTemplate("iata", iata)
                .request()
                .delete();
    }
    
    /**
     * Get all currently stored airports.
     * @return airports
     */
    public List<String> getAirports() {
        return collect.path("/airports").request().get(new GenericType<List<String>>() {});
    }
    
    /**
     * Get airport data by IATA code.
     * @param iata IATA code
     * @return airport data
     */
    public AirportData getAirport(String iata) {
        return collect.path("/airport/{iata}")
                .resolveTemplate("iata", iata)
                .request()
                .get(AirportData.class);
    }
}
