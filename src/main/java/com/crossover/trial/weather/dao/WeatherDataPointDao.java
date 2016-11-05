package com.crossover.trial.weather.dao;

import java.util.Collection;

import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;

public interface WeatherDataPointDao {
    void updateWeather(String iataCode, DataPoint dataPoint) throws AirportNotFoundException;

    /**
     * Count data point which was updated later than the since parameter. 
     * @param since
     * @return count
     */
    long countDataPoints(long since);

    Collection<DataPoint> getAllForAirport(String iata);
}
