package com.helloworld.golf.dk.helloworld.Aggregators;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class FileAggregator {

    private BufferedWriter bufferedWriter;

    private FastVector attributes;
    private FastVector classes;
    private Instances data;
    private File resultsFile;
    private ProgressDialog dialogBox;
    private Handler guiHandler;
    private String thisClass;

    public FileAggregator() {
    }

    public void createArff(String className, Context context) {
        thisClass = className;

        resultsFile = new File(context.getExternalFilesDir(null), "train-data-" + className + ".arff");

        attributes = new FastVector();

        attributes.addElement(new Attribute("min"));
        attributes.addElement(new Attribute("max"));
        attributes.addElement(new Attribute("mean"));
        attributes.addElement(new Attribute("stdDev"));
        classes = new FastVector();
        classes.addElement("walking");
        classes.addElement("running");
        classes.addElement("standing");
        classes.addElement("biking");

        attributes.addElement(new Attribute("movementType", classes));
        data = new Instances("detectMovementType", attributes, 128);

    }

    public void addValue(StatisticsData statisticsData) {
        double movementType = classes.indexOf(thisClass);
        data.add(new Instance(1.0D, new double[]{
                statisticsData.getMin(), statisticsData.getMax(), statisticsData.getMean(), statisticsData.getStdDev(), movementType
        }));
    }

    public void writeFile(final Context context) {
        guiHandler = new Handler();

        try {
            if (resultsFile.exists()) {
                resultsFile.delete();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(resultsFile, true));

            List<StatisticsData> results = MovementAggregator.getInstance().getResults();
            Iterator<StatisticsData> resultsIterator = results.iterator();

            while (resultsIterator.hasNext()) {
                StatisticsData currentElement = resultsIterator.next();
                addValue(currentElement);
            }

            bufferedWriter.write(data.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            guiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,
                            "File export completed - located in " + context.getFilesDir(),
                            Toast.LENGTH_SHORT).show();
                }

            });
            return;
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

}
