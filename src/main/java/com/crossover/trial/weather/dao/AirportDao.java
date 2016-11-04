package com.crossover.trial.weather.dao;

import java.util.List;

import com.crossover.trial.weather.api.AirportData;

public interface AirportDao {
	List<AirportData> getAllAirports();
	
	void add(AirportData airport);
	
	void remove(AirportData airport);
}
