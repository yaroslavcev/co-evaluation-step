package com.crossover.trial.weather.service;

import com.crossover.trial.weather.WeatherQueryStatistic;

public interface WeatherQueryStatisticService {
	void recordQuery(String iata, Double radius);
	WeatherQueryStatistic getStatisitc();
}
