package com.crossover.trial.weather.api;

import java.util.Map;

public class WeatherQueryStatistic {
	private long datasize;
	private Map<String, Double> iataFrequency;
	private int[] radiusFrequency;

	public long getDatasize() {
		return datasize;
	}

	public void setDatasize(long datasize) {
		this.datasize = datasize;
	}

	public Map<String, Double> getIataFrequency() {
		return iataFrequency;
	}

	public void setIataFrequency(Map<String, Double> iataFrequency) {
		this.iataFrequency = iataFrequency;
	}

	public int[] getRadiusFrequency() {
		return radiusFrequency;
	}

	public void setRadiusFrequency(int[] radiusFrequency) {
		this.radiusFrequency = radiusFrequency;
	}
}
