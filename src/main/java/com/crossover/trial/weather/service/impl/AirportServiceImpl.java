package com.crossover.trial.weather.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.service.AirportService;

public class AirportServiceImpl implements AirportService {
	@Inject
	AirportDao airportDao;
	
	@Override
	public List<AirportData> getAllAirports() {
		return airportDao.getAllAirports();
	}

	@Override
	public void add(AirportData airport) {
		airportDao.add(airport);
	}

	@Override
	public void remove(AirportData airport) {
		airportDao.remove(airport);
	}
}
