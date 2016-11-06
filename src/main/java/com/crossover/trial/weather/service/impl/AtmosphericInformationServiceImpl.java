package com.crossover.trial.weather.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.dao.WeatherDataPointDao;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;

/**
 * Implementation of AtmosphericInformationService.
 * @author Ilya
 *
 */
public class AtmosphericInformationServiceImpl implements AtmosphericInformationService {
    
    @Inject
    WeatherDataPointDao weatherDataPointDao;
    
    @Inject
    private AirportService airportService;
    
    @Override
    public long countAtmosphericInformation(long since) {
        return weatherDataPointDao.countDataPoints(since);
    }
    
    @Override
    public AtmosphericInformation getForAirport(String iata) {
        Collection<DataPoint> dataPoints = weatherDataPointDao.getAllForAirport(iata);
        AtmosphericInformation res = new AtmosphericInformation();
        long lastUpdateTime = 0;
        for (DataPoint dataPoint : dataPoints) {
            dataPoint.getType().appendToAtmosphericInformation(res, dataPoint);
            if (dataPoint.getLastUpdateTime() > lastUpdateTime) {
                lastUpdateTime = dataPoint.getLastUpdateTime();
            }
        }
        res.setLastUpdateTime(lastUpdateTime);
        return res;
    }

    @Override
    public void updateWeather(String iataCode, DataPointType dataPointType, DataPoint dataPoint)
            throws AirportNotFoundException {
        if (dataPointType.check(dataPoint)) {
            weatherDataPointDao.updateWeather(iataCode, dataPoint);
        }
    }

    @Override
    public List<AtmosphericInformation> getForRadius(String iata, double radius) {
        Collection<AirportData> airports = airportService.findAllAirportsInRadius(iata, radius);
        if (airports.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<AtmosphericInformation> retval = new ArrayList<>(airports.size());
        for (AirportData airport : airports) {
            AtmosphericInformation ai = getForAirport(airport.getIata());
            
            retval.add(ai);
        }
        
        return retval;
    }
}
