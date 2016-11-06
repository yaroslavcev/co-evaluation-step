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
 * can look at WeatherClient to understand API semantics. This existing client
 * populates the REST endpoint with dummy data useful for testing.
 *
 * @author code test administrator
 */
public class WeatherClient {

    public static final String BASE_URI = "http://localhost:9090";

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    public WeatherClient() {
        this(BASE_URI);
    }

    public WeatherClient(String baseUri) {
        Client client = ClientBuilder.newClient();
        query = client.target(baseUri).path("query");
        collect = client.target(baseUri).path("collect");
    }

    public Map<String, Object> pingQuery() {
        WebTarget path = query.path("/ping");
        return path.request().get(new GenericType<Map<String, Object>>() {});
    }

    public boolean pingCollect() {
        WebTarget path = collect.path("/ping");
        String response = path.request().get(String.class);
        return "1".equals(response);
    }
    
    public List<AtmosphericInformation> weather(String iata, String radius) {
        return query.path("/weather/{iata}/{radius}")
                .resolveTemplate("iata", iata)
                .resolveTemplate("radius", radius)
                .request()
                .get(new GenericType<List<AtmosphericInformation>>() {});
    }
    
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
    
    public Response addAirport(String iata, String latString, String longString) {
        return collect.path("/airport/{iata}/{lat}/{long}")
                .resolveTemplate("iata", iata)
                .resolveTemplate("lat", latString)
                .resolveTemplate("long", longString)
                .request()
                .post(Entity.text(""));
    }
    
    public Response deleteAirport(String iata) {
        return collect.path("/airport/{iata}")
                .resolveTemplate("iata", iata)
                .request()
                .delete();
    }
    
    public List<String> getAirports() {
        return collect.path("/airports").request().get(new GenericType<List<String>>() {});
    }
    
    public AirportData getAirport(String iata) {
        return collect.path("/airport/{iata}")
                .resolveTemplate("iata", iata)
                .request()
                .get(AirportData.class);
    }
}
