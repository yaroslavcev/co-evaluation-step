package com.crossover.trial.weather.api;

import java.util.Map;

/**
 * Digest of gathered statistic related to query endpoint usage. 
 * @author Ilya
 *
 */
public class WeatherQueryStatistic {
	private long datasize;
	private Map<String, Double> iataFrequency;
	private int[] radiusFrequency;

	/**
	 * Return count of DataPoints updated during last 24 hours.
	 * @return count
	 */
	public long getDatasize() {
		return datasize;
	}

	public void setDatasize(long datasize) {
		this.datasize = datasize;
	}

	/**
	 * Return fraction of requests for each airport.
	 * @return map IATA code -> fraction of requests
	 */
	public Map<String, Double> getIataFrequency() {
		return iataFrequency;
	}

	public void setIataFrequency(Map<String, Double> iataFrequency) {
		this.iataFrequency = iataFrequency;
	}

	/**
	 * Return histogram of requests per radius bin to 10 km.
	 * @return histogram
	 */
	public int[] getRadiusFrequency() {
		return radiusFrequency;
	}

	public void setRadiusFrequency(int[] radiusFrequency) {
		this.radiusFrequency = radiusFrequency;
	}
}
