package com.crossover.trial.weather.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.util.GeoLocation;

public class AirportServiceImpl implements AirportService {
    /** earth radius in KM */
    public static final double R = 6372.8;
    
	AirportDao airportDao;
	
	@Inject
	public AirportServiceImpl(AirportDao airportDao) {
	    this.airportDao = airportDao;
	}
	
	@Override
	public Collection<AirportData> getAllAirports() {
		return airportDao.getAllAirports();
	}

	@Override
	public void add(AirportData airport) {
		airportDao.add(airport);
	}

	@Override
	public void remove(String iata) {
		airportDao.remove(iata);
	}

    @Override
    public List<AirportData> findAllAirportsInRadius(String iata, double radius) {
        AirportData centralAirport = findAirportData(iata);
        if (centralAirport == null) {
            return Collections.emptyList();
        }
        
        GeoLocation centralAirportGeo = GeoLocation.fromDegrees(centralAirport.getLatitude(),
                centralAirport.getLongitude());
        
        //Step 1. Find Minimum bounding rectangle https://en.wikipedia.org/wiki/Minimum_bounding_rectangle
        GeoLocation[] bounds = centralAirportGeo.boundingCoordinates(radius, R);
        GeoLocation lowerLeftBound = bounds[0]; 
        GeoLocation upperRightBound = bounds[1];
        
        //Step 2. Find all airport inside the MBR
        List<AirportData> airportFromRectangle = airportDao.findAllAirportInsideRectangle(
                lowerLeftBound.getLatitudeInDegrees(), lowerLeftBound.getLongitudeInDegrees(),
                upperRightBound.getLatitudeInDegrees(), upperRightBound.getLongitudeInDegrees());
        
        //Step 3. Exclude airports that doesn't belong to specified circle 
        List<AirportData> result = new ArrayList<>(airportFromRectangle.size());
        for (AirportData currAirport : airportFromRectangle) {
            GeoLocation currAirportGeo = GeoLocation.fromDegrees(currAirport.getLatitude(), currAirport.getLongitude());
            if (centralAirportGeo.distanceTo(currAirportGeo, R) <= radius) {
                result.add(currAirport);
            }
        }
        
        return result;
    }

    @Override
    public AirportData findAirportData(String iata) {
        return airportDao.findAirport(iata);
    }
}
