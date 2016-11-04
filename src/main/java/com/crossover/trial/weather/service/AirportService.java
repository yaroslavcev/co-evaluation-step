package com.crossover.trial.weather.service;

import java.util.List;

import com.crossover.trial.weather.api.AirportData;

public interface AirportService {
	List<AirportData> getAllAirports();
	
	void add(AirportData airport);
	
	void remove(AirportData airport);
}
