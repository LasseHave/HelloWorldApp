package com.helloworld.golf.dk.helloworld.Models;

public class StatisticsData {
	private float min;
	private float max;
	private float stdDev;

	public StatisticsData(float min, float max, float stdDev) {
		this.min = min;
		this.max = max;
		this.stdDev = stdDev;
	}

	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
	public float getStdDev() {
		return stdDev;
	}
	public void setStdDev(float stdDev) {
		this.stdDev = stdDev;
	}
}

