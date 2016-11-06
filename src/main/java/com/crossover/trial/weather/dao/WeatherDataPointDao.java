package com.crossover.trial.weather.dao;

import java.util.Collection;

import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;

/**
 * DataPoint DAO object providing access to manipulating weather data.
 * @author Ilya
 *
 */
public interface WeatherDataPointDao {
    /**
     * Update weather data for specified airport.
     * @param iataCode IATA code of the airport
     * @param dataPoint data to save
     * @throws AirportNotFoundException when airport with specified IATA code does not exist
     */
    void updateWeather(String iataCode, DataPoint dataPoint) throws AirportNotFoundException;

    /**
     * Count data point which was updated later than the since parameter. 
     * @param since time in milliseconds since UTC epoch
     * @return count count of matching data points
     */
    long countDataPoints(long since);

    /**
     * Return all data points available for specified IATA code.
     * @param iata IATA code
     * @return collection of data points
     */
    Collection<DataPoint> getAllForAirport(String iata);
}
