package com.tractivity.golf.dk.tractivity.Aggregators;
import com.tractivity.golf.dk.tractivity.Models.Acceleration;
import com.tractivity.golf.dk.tractivity.Models.LocationObj;
import com.tractivity.golf.dk.tractivity.Models.StatisticsData;
import com.tractivity.golf.dk.tractivity.Widgets.GPSWidget;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class MovementAggregator {

    private List<StatisticsData> results;
    private List<Double> locationObjs;
    private DescriptiveStatistics statistics;

    public double lastSpeed;

    private static MovementAggregator singleton = new MovementAggregator( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private MovementAggregator() {
        results = new ArrayList<>();
        locationObjs = new ArrayList<>();
    }

    /* Static 'instance' method */
    public static MovementAggregator getInstance( ) {
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

        for(int i = 0; i < 3; i++) { // Calc stat for each acceleration axis

            statistics = new DescriptiveStatistics(readyForStat(dataToProcess, i));

            StatisticsData tempResult = new StatisticsData(
                    (float) statistics.getMin(),
                    (float) statistics.getMax(),
                    (float) statistics.getMean(),
                    (float) statistics.getStandardDeviation());
            results.add(tempResult);
            locationObjs.add(lastSpeed);
        }
    }

    private double[] readyForStat(List<Acceleration> input, int index)
    {
        double[] output = new double[input.size()];

        for (int i = 0; i < input.size(); i++)
        {
            output[i] = input.get(i).toDoubleArray()[index];
        }
        return output;
    }

}
