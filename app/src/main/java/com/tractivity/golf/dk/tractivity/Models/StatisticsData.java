package com.tractivity.golf.dk.tractivity.Models;

public class StatisticsData {
	private float min;
	private float max;
	private float mean;
	private float stdDev;

	public StatisticsData(float min, float max, float mean, float stdDev) {
		this.min = min;
		this.max = max;
		this.mean = mean;
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
	public float getMean() {
		return mean;
	}
	public void setMean(float mean) {
		this.mean = mean;
	}
	public float getStdDev() {

		return stdDev;
	}
	public void setStdDev(float stdDev) {
		this.stdDev = stdDev;
	}
}

