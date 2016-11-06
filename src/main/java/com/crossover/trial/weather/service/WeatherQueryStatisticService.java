package com.crossover.trial.weather.service;

import com.crossover.trial.weather.api.WeatherQueryStatistic;

/**
 * Service providing mechanism for retrieving and updating statistic of query endpoint usage.
 * @author Ilya
 *
 */
public interface WeatherQueryStatisticService {
    /**
     * Store record about incoming request with IATA code and radius.
     * @param iata the IATA code
     * @param radius the radius
     */
	void recordQuery(String iata, Double radius);
	
	/**
	 * Return statistic data. {@link com.crossover.trial.weather.api.WeatherQueryStatistic}
	 * @return statistic data
	 */
	WeatherQueryStatistic getStatisitc();
}
