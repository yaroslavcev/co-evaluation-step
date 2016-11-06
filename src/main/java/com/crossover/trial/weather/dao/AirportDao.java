package com.crossover.trial.weather.dao;

import java.util.Collection;
import java.util.List;

import com.crossover.trial.weather.api.AirportData;

/**
 * Airport DAO object providing access to manipulating airport data.
 * @author Ilya
 *
 */
public interface AirportDao {
    /**
     * Return all stored airports.
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
     * Find airport by its IATA code.
     * 
     * @param iata the IATA code to find
     * @return airport data if airport with specified IATA code exists null
     *         otherwise
     */
    AirportData findAirport(String iata);

    /**
     * Find all airports that are located in specified rectangle.
     * @param lowerLeftLatitudeInDegrees latitude in degrees
     * @param lowerLeftLongitudeInDegrees longitude in degrees
     * @param upperRightLatitudeInDegrees latitude in degrees
     * @param upperRightLongitudeInDegrees longitude in degrees
     * @return list of matching airports
     */
    List<AirportData> findAllAirportInsideRectangle(double lowerLeftLatitudeInDegrees,
            double lowerLeftLongitudeInDegrees, double upperRightLatitudeInDegrees,
            double upperRightLongitudeInDegrees);
}
