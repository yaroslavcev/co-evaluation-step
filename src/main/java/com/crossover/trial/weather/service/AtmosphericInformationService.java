package com.crossover.trial.weather.service;

import java.util.List;

import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;

public interface AtmosphericInformationService {
    /**
     * Count atmospheric information which was updated later than the since parameter. 
     * @param since
     * @return count
     */
    long countAtmosphericInformation(long since);
    
    AtmosphericInformation getForAirport(String iata);
    
    List<AtmosphericInformation> getForRadius(String iata, double radius);

    void updateWeather(String iataCode, DataPointType dataPointType, DataPoint dataPoint)
            throws AirportNotFoundException;
}
