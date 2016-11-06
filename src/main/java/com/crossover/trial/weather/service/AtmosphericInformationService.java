package com.crossover.trial.weather.service;

import java.util.List;

import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;

/**
 * Service providing mechanism for retrieving and updating atmospheric information.
 * @author Ilya
 *
 */
public interface AtmosphericInformationService {
    /**
     * Count atmospheric information which was updated later than the since parameter. 
     * @param since time in milliseconds since UTC epoch
     * @return count count of matching data points
     */
    long countAtmosphericInformation(long since);
    
    /**
     * Get atmospheric information available for specified airport.
     * @param iata IATA code of the airport
     * @return atmospheric information
     */
    AtmosphericInformation getForAirport(String iata);
    
    /**
     * Get atmospheric information for specified airport and its adjacent airports in specified radius.
     * @param iata IATA code of the airport
     * @param radius radius in km
     * @return list of atmospheric information
     */
    List<AtmosphericInformation> getForRadius(String iata, double radius);

    /**
     * Update weather data for specified airport.
     * @param iataCode IATA code of the airport
     * @param dataPointType type of data to be updated
     * @param dataPoint data to save
     * @throws AirportNotFoundException when airport with specified IATA code does not exist
     */
    void updateWeather(String iataCode, DataPointType dataPointType, DataPoint dataPoint)
            throws AirportNotFoundException;
}
