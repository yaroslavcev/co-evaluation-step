package com.crossover.trial.weather.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AirportNotFoundException;
import com.crossover.trial.weather.api.DataPoint;
import com.crossover.trial.weather.api.DataPointType;
import com.crossover.trial.weather.dao.AirportDao;
import com.crossover.trial.weather.dao.WeatherDataPointDao;

import jersey.repackaged.com.google.common.base.Objects;

/**
 * In Memory implementation of DAO level that stores everything in java structures.
 * @author Ilya
 *
 */
public class InMemoryStoreImpl implements AirportDao, WeatherDataPointDao {
    /**
     * All data points. It uses compound key (iataCode, dataPointType)
     */
    private ConcurrentMap<DataPointKey, DataPoint> allDataPoints = new ConcurrentHashMap<>();

    /**
     * Data points per airport cache.
     */
    private ConcurrentMap<String, Map<DataPointType, DataPoint>> dataPointsPerAirport = new ConcurrentHashMap<>();
 
    /**
     * All airports map. iata code -> airport.
     */
    private ConcurrentMap<String, AirportData> airports = new ConcurrentHashMap<>();
    
    private AirportSpatialIndex airportsSpatialIndex = new AirportSpatialIndex();
    
    /**
     * Read write lock per airport.
     */
    private ConcurrentMap<String, ReadWriteLock> airportLocks = new ConcurrentHashMap<>();
    
    /**
     * Lock for all airport data.
     */
    private ReadWriteLock allAirportsLock = new ReentrantReadWriteLock();
    
    @Override
    public void updateWeather(String iataCode, DataPoint dataPoint) throws AirportNotFoundException {
        ifAirportExistExecuteUnderLock(iataCode, () -> {
            DataPointType dataPointType = dataPoint.getType();
            getDataPointsForAirport(iataCode).put(dataPointType, dataPoint);
            
            DataPointKey dpKey = new DataPointKey(iataCode, dataPointType);
            allDataPoints.put(dpKey, dataPoint);
        });
    }
    
    private Map<DataPointType, DataPoint> getDataPointsForAirport(String iataCode) {
        return dataPointsPerAirport.computeIfAbsent(iataCode, (o) -> new ConcurrentHashMap<>());
    }
    
    /**
     * Execute an action only if airport exists. Action will be executed under read lock.
     * @param iataCode airport to lock
     * @param action action to execute
     */
    private void ifAirportExistExecuteUnderLock(String iataCode, Runnable action) throws AirportNotFoundException {
        ReadWriteLock lock = getLock(iataCode);
        if (lock == null) {
            throw new AirportNotFoundException(iataCode);
        }
        lock.readLock().lock();
        try {
            if (!isAirportExists(iataCode)) {
                throw new AirportNotFoundException(iataCode);
            }
            action.run();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private boolean isAirportExists(String iataCode) {
        return airports.containsKey(iataCode);
    }
    
    private ReadWriteLock getLock(String iataCode) {
        return airportLocks.get(iataCode);
    }
    
    private ReadWriteLock getOrCreateLock(String iataCode) {
        return airportLocks.computeIfAbsent(iataCode, (o) -> new ReentrantReadWriteLock());
    }
    
    @Override
    public long countDataPoints(long since) {
        return allDataPoints.values().stream().filter(dp -> dp.getLastUpdateTime() > since).count();
    }

    @Override
    public Collection<DataPoint> getAllForAirport(String iataCode) {
        return Collections.unmodifiableCollection(getDataPointsForAirport(iataCode).values());
    }

    @Override
    public Collection<AirportData> getAllAirports() {
        return Collections.unmodifiableCollection(airports.values());
    }

    @Override
    public void add(AirportData airport) {
        allAirportsLock.writeLock().lock();
        
        try {
            String iata = airport.getIata();
            ReadWriteLock airportLock = getOrCreateLock(iata);
            airportLock.writeLock().lock();
            try {
                airports.put(iata, airport);
                addToSpatialIndex(airport);
            } finally {
                airportLock.writeLock().unlock();
            }
        } finally {
            allAirportsLock.writeLock().unlock();
        }
    }

    private void addToSpatialIndex(AirportData airport) {
        airportsSpatialIndex.add(airport);
    }

    @Override
    public void remove(String iata) {
        allAirportsLock.writeLock().lock();
        ReadWriteLock rwLock = getOrCreateLock(iata);
        rwLock.writeLock().lock();
        try {
            AirportData airport = airports.remove(iata);
            removeFromSpatialIndex(airport);
            removeDataPoints(iata);
            
            airportLocks.remove(iata);
        } finally {
            allAirportsLock.writeLock().unlock();
            rwLock.writeLock().unlock();
        }
    }
    
    private void removeFromSpatialIndex(AirportData airport) {
        if (airport == null) {
            return;
        }
        airportsSpatialIndex.remove(airport);
    }
    
    /**
     * Remove all data points for specified airport. Must be executed under write lock.
     * @param iata IATA code
     */
    private void removeDataPoints(String iata) {
        Map<DataPointType, DataPoint> dataPoints = dataPointsPerAirport.remove(iata);
        if (dataPoints == null) {
            return;
        }
        for (DataPoint dataPoint : dataPoints.values()) {
            DataPointKey dpKey = new DataPointKey(iata, dataPoint.getType());
            allDataPoints.remove(dpKey);
        }
    }
    
    @Override
    public AirportData findAirport(String iata) {
        return airports.get(iata);
    }

    @Override
    public List<AirportData> findAllAirportInsideRectangle(double lowerLeftLatitudeInDegrees,
            double lowerLeftLongitudeInDegrees, double upperRightLatitudeInDegrees,
            double upperRightLongitudeInDegrees) {
        allAirportsLock.readLock().lock();
        try {
            return airportsSpatialIndex.findAllAirportInsideRectangle(lowerLeftLatitudeInDegrees,
                    lowerLeftLongitudeInDegrees, upperRightLatitudeInDegrees, upperRightLongitudeInDegrees);
        } finally {
            allAirportsLock.readLock().unlock();
        }
    }
    
    private static class DataPointKey {
        private final String iataCode;
        private final DataPointType dataPointType;

        public DataPointKey(String iataCode, DataPointType dataPointType) {
            this.iataCode = iataCode;
            this.dataPointType = dataPointType;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(iataCode, dataPointType);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            
            if (obj == null)
                return false;
            
            DataPointKey other = (DataPointKey) obj;
            return Objects.equal(this.iataCode, other.iataCode)
                    && Objects.equal(this.dataPointType, other.dataPointType);
        }
    }
}
