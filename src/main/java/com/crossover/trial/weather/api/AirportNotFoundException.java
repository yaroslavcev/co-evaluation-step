package com.crossover.trial.weather.api;

public class AirportNotFoundException extends Exception {
    private String iataCode;

    public AirportNotFoundException(String iataCode) {
        super();
        this.iataCode = iataCode;
    }

    public String getIataCode() {
        return iataCode;
    }
}
