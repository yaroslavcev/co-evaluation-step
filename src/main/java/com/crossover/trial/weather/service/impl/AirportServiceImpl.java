package com.crossover.trial.weather.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.util.GeoLocation;

/**
 * AirportService implementation that uses GeoLocation to calculate MBR for
 * radius. This implementation suppose on AirportDao effectively implement
 * findAllAirportInsideRectangle method.
 * 
 * @author Ilya
 *
 */
public class AirportServiceImpl implements AirportService {
    /** earth radius in KM. */
    public static final double R = 6372.8;
    
    private static final Logger LOG = LoggerFactory.getLogger(AirportServiceImpl.class);
            
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
        LOG.debug("Finding adjacent airports for [{}] in radius {} km", iata, radius);
        
        AirportData centralAirport = findAirportData(iata);
        if (centralAirport == null) {
            LOG.debug("Can't find central airport [{}]", iata);
            return Collections.emptyList();
        }
        
        GeoLocation centralAirportGeo = GeoLocation.fromDegrees(centralAirport.getLatitude(),
                centralAirport.getLongitude());
        
        //Step 1. Find Minimum bounding rectangle https://en.wikipedia.org/wiki/Minimum_bounding_rectangle
        GeoLocation[] bounds = centralAirportGeo.boundingCoordinates(radius, R);
        GeoLocation lowerLeftBound = bounds[0]; 
        GeoLocation upperRightBound = bounds[1];
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found bounds {} for airport [{}] with location {} and radius {}", Arrays.toString(bounds), iata,
                    centralAirportGeo, radius);
        }
        
        //Step 2. Find all airport inside the MBR
        List<AirportData> airportFromRectangle = airportDao.findAllAirportInsideRectangle(
                lowerLeftBound.getLatitudeInDegrees(), lowerLeftBound.getLongitudeInDegrees(),
                upperRightBound.getLatitudeInDegrees(), upperRightBound.getLongitudeInDegrees());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Airports {} in the rectangle {} ", airportFromRectangle, Arrays.toString(bounds));
        }
        
        //Step 3. Exclude airports that doesn't belong to specified circle 
        List<AirportData> result = new ArrayList<>(airportFromRectangle.size());
        for (AirportData currAirport : airportFromRectangle) {
            GeoLocation currAirportGeo = GeoLocation.fromDegrees(currAirport.getLatitude(), currAirport.getLongitude());
            if (centralAirportGeo.distanceTo(currAirportGeo, R) <= radius) {
                result.add(currAirport);
            }
        }
        
        LOG.debug("Airports {} in radius {} for [{}]", result, radius, iata);
        
        return result;
    }

    @Override
    public AirportData findAirportData(String iata) {
        return airportDao.findAirport(iata);
    }
}
