package com.crossover.trial.weather.dao;

import java.util.Collection;
import java.util.List;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.util.GeoLocation;

public interface AirportDao {
	Collection<AirportData> getAllAirports();
	
	void add(AirportData airport);
	
	void remove(String iata);

    AirportData findAirportData(String iata);

    List<AirportData> findAllAirportInsideRectangle(double lowerLeftLatitudeInDegrees,
            double lowerLeftLongitudeInDegrees, double upperRightLatitudeInDegrees,
            double upperRightLongitudeInDegrees);
}
