package com.crossover.trial.weather.service;

import java.util.Collection;

import com.crossover.trial.weather.api.AirportData;

/**
 * Service providing mechanism for retrieving and updating airport data.
 * 
 * @author Ilya
 *
 */
public interface AirportService {
    /**
     * Return all stored airports.
     * 
     * @return all stored airports
     */
    Collection<AirportData> getAllAirports();

    /**
     * Add airport.
     * 
     * @param airport airport data to be added
     */
    void add(AirportData airport);

    /**
     * Remove airport.
     * 
     * @param iata IATA code of the airport to be removed
     */
    void remove(String iata);

    /**
     * Returns airport with specified IATA code and all adjacent airports within
     * specified radius.
     * 
     * @param iata IATA code of the airport
     * @param radius radius to find adjacent airports
     * @return list of airports data
     */
    Collection<AirportData> findAllAirportsInRadius(String iata, double radius);

    /**
     * Find airport by its IATA code.
     * 
     * @param iata the IATA code to find
     * @return airport data if airport with specified IATA code exists null
     *         otherwise
     */
    AirportData findAirportData(String iata);
}
