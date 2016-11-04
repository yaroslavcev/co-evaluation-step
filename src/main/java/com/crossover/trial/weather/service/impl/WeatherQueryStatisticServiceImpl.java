package com.crossover.trial.weather.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import com.crossover.trial.weather.WeatherQueryStatistic;
import com.crossover.trial.weather.api.AirportData;
import com.crossover.trial.weather.api.AtmosphericInformation;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.WeatherQueryStatisticService;

public class WeatherQueryStatisticServiceImpl implements WeatherQueryStatisticService {
    public static final int RADIUS_HISTOGRAM_STEP = 10;
    
    private AirportService airportService;
    private AtmosphericInformationService atmosphericInformationService;
    
    private AtomicLong totalQueryCount = new AtomicLong();

    /**
     * Map iata code -> request count
     */
    private Map<String, AtomicLong> queryPerIataCounter = new ConcurrentHashMap<>();

    /**
     * Map radius -> request count
     */
    private Map<Double, AtomicInteger> queryPerRadiusCounter = new ConcurrentHashMap<>();
    
    @Inject
    public WeatherQueryStatisticServiceImpl(AirportService airportService,
            AtmosphericInformationService atmosphericInformationService) {
        this.airportService = airportService;
        this.atmosphericInformationService = atmosphericInformationService;
    }
    
    @Override
    public void recordQuery(String iata, Double radius) {
        totalQueryCount.incrementAndGet();
        queryPerIataCounter.computeIfAbsent(iata, (k) -> new AtomicLong()).incrementAndGet();
        queryPerRadiusCounter.computeIfAbsent(radius, (k) -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    public WeatherQueryStatistic getStatisitc() {
        WeatherQueryStatistic stat = new WeatherQueryStatistic();
       
        int datasize = 0;
        for (AtmosphericInformation ai : atmosphericInformationService.getAllAtmosphericInformation()) {
            // we only count recent readings
            if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPressure() != null
                    || ai.getPrecipitation() != null || ai.getTemperature() != null || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        
        stat.setDatasize(datasize);
        
        Map<String, Double> iataFrequency = computeIataFrequency();
        stat.setIataFrequency(iataFrequency);

        int[] hist = computeRadiusHistogram(RADIUS_HISTOGRAM_STEP);
        stat.setRadiusFrequency(hist);

        return stat;
    }

    /**
     * Compute histogram of requests per radius for provided interval.
     * 
     * @param interval
     *            interval to be used to divide radiuses to bins.
     * @return
     */
    private int[] computeRadiusHistogram(int interval) {
        if (queryPerRadiusCounter.isEmpty()) {
            return new int[0];
        }
        
        Double maxRadius = queryPerRadiusCounter.keySet().stream().max(Double::compare).orElse(null);
        int maxBin = maxRadius.intValue() / interval + 1;
        
        int[] hist = new int[maxBin];
        
        for (Entry<Double, AtomicInteger> e : queryPerRadiusCounter.entrySet()) {
            int i = e.getKey().intValue() / interval;
            hist[i] += e.getValue().get();
        }
        
        return hist;
    }

    /**
     * Compute fraction of request for each IATA code from total request count.
     * 
     * @return
     */
    private Map<String, Double> computeIataFrequency() {
        Map<String, Double> iataFrequency = new HashMap<>();
        
        for (AirportData airport : airportService.getAllAirports()) {
            String iata = airport.getIata();
            double iataFraction = 0;
            if (!queryPerIataCounter.isEmpty()) {
                AtomicLong atomicCount = queryPerIataCounter.get(iata);
                double countForIata = (atomicCount == null) ? 0 : atomicCount.get();

                iataFraction = countForIata / totalQueryCount.get();
            }

            iataFrequency.put(iata, iataFraction);
        }

        return iataFrequency;
    }
}
