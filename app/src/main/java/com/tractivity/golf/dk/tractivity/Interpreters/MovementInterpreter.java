package com.tractivity.golf.dk.tractivity.Interpreters;

import android.content.Context;
import android.content.res.AssetManager;

import com.tractivity.golf.dk.tractivity.Models.StatisticsData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.beans.Classifier;

public class MovementInterpreter {
    private J48 classifier;
    private AssetManager assetManager;

    public MovementInterpreter(Context context) throws IOException, ClassNotFoundException {
        assetManager = context.getApplicationContext().getAssets();
        this.classifier = new J48();
        ObjectInputStream ois = new ObjectInputStream(assetManager.open("MovementTypeModel.model"));

        classifier = (J48) ois.readObject();
        ois.close();
    }

    public String classify(StatisticsData statisticsData, Double speed) throws Exception {
        FastVector attributes = new FastVector();
        FastVector classes = new FastVector();


        attributes.addElement(new Attribute("min"));
        attributes.addElement(new Attribute("max"));
        attributes.addElement(new Attribute("mean"));
        attributes.addElement(new Attribute("stdDev"));
        attributes.addElement(new Attribute("speed"));

        classes.addElement("walking");
        classes.addElement("running");
        classes.addElement("standing");
        classes.addElement("biking");

        attributes.addElement(new Attribute("@@class@@", classes));


        attributes.addElement(new Attribute("movementType", classes));
        Instances unpredicted = new Instances("TestInstances", attributes, 1);
        unpredicted.setClassIndex(unpredicted.numAttributes() - 1);

        Instance data = new Instance(1.0D, new double[]{
                statisticsData.getMin(), statisticsData.getMax(), statisticsData.getMean(), statisticsData.getStdDev(), speed
        });
        data.setDataset(unpredicted);

        double tNum = this.classifier.classifyInstance(data);
        this.classifier.getOptions();
        int classIndex = (int) tNum;

        return convertToClass(classIndex);
    }

    public String convertToClass(int input){

        switch (input){
            case 0: return "walking";
            case 1: return "running";
            case 2: return "standing";
            case 3: return "biking";
            default: return "WTF";
        }

    }
}
