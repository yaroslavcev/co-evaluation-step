package com.crossover.trial.weather;

import java.util.Map;

public class WeatherQueryStatistic {
	private int datasize;
	private Map<String, Double> iataFrequency;
	private int[] radiusFrequency;

	public int getDatasize() {
		return datasize;
	}

	public void setDatasize(int datasize) {
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
