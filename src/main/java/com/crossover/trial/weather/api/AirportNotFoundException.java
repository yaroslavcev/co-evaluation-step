package com.crossover.trial.weather.api;

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
