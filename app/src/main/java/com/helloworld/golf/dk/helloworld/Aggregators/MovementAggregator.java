package com.helloworld.golf.dk.helloworld.Aggregators;

import com.helloworld.golf.dk.helloworld.Models.Acceleration;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class MovementAggregator {

    private List<StatisticsData> results;
    private List<Double> locationObjs;
    private DescriptiveStatistics statistics;

    public double lastSpeed;

    private static MovementAggregator singleton = new MovementAggregator();

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private MovementAggregator() {
        results = new ArrayList<>();
        locationObjs = new ArrayList<>();
    }

    /* Static 'instance' method */
    public static MovementAggregator getInstance() {
        return singleton;
    }

    public List<StatisticsData> getResults() {

        return results;
    }

    public List<Double> getSpeeds() {

        return locationObjs;
    }

    public void resetResults() {
        results.clear();
    }

    public void processData(List<Acceleration> dataToProcess) {

        double[] norms = new double[dataToProcess.size()];

        for (int i = 0; i < dataToProcess.size(); i++) {
            norms[i] = (Math.sqrt(Math.pow(dataToProcess.get(i).getX(), 2) + Math.pow(dataToProcess.get(i).getY(), 2) + Math.pow(dataToProcess.get(i).getZ(), 2)));
        }
        statistics = new DescriptiveStatistics(norms);

        StatisticsData tempResult = new StatisticsData(
                (float) statistics.getMin(),
                (float) statistics.getMax(),
                (float) statistics.getMean(),
                (float) statistics.getStandardDeviation());
        results.add(tempResult);
        locationObjs.add(lastSpeed);
    }

}
