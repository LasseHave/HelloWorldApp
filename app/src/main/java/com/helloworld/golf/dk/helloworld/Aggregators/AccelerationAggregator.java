package com.helloworld.golf.dk.helloworld.Aggregators;
import com.helloworld.golf.dk.helloworld.Models.Acceleration;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class AccelerationAggregator {

    private List<StatisticsData> results;
    private DescriptiveStatistics statistics;

    private static AccelerationAggregator singleton = new AccelerationAggregator( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private AccelerationAggregator() {
        results = new ArrayList<>();
    }

    /* Static 'instance' method */
    public static AccelerationAggregator getInstance( ) {
        return singleton;
    }

    public List<StatisticsData> getResults() {

        return results;
    }

    public void processData(List<Acceleration> dataToProcess) {

        for(int i = 0; i < 3; i++) { // Calc stat for each acceleration axis

            statistics = new DescriptiveStatistics(readyForStat(dataToProcess, i));

            StatisticsData tempResult = new StatisticsData(
                    (float) statistics.getMin(),
                    (float) statistics.getMax(),
                    (float) statistics.getStandardDeviation());
            results.add(tempResult);

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
