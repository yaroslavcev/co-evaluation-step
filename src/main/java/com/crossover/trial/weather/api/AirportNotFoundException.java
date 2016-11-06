package com.crossover.trial.weather.api;

/**
 * Must be thrown when operation try to store data for absent airport.
 * @author Ilya
 *
 */
public class AirportNotFoundException extends WeatherException {
    private static final long serialVersionUID = 1L;
    
    private String iataCode;

    public AirportNotFoundException(String iataCode) {
        super();
        this.iataCode = iataCode;
    }

    public String getIataCode() {
        return iataCode;
    }
}
