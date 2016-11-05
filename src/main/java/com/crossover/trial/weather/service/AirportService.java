package com.crossover.trial.weather.service;

import java.util.Collection;
import java.util.List;

import com.crossover.trial.weather.api.AirportData;

public interface AirportService {
	Collection<AirportData> getAllAirports();
	
	void add(AirportData airport);
	
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

    AirportData findAirportData(String iata);
}
