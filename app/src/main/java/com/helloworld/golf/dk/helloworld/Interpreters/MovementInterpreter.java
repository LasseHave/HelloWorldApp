package com.helloworld.golf.dk.helloworld.Interpreters;

import android.content.Context;
import android.content.res.AssetManager;

import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

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

    public String classify(StatisticsData statisticsData) throws Exception {
        FastVector attributes = new FastVector();
        FastVector classes = new FastVector();


        attributes.addElement(new Attribute("min"));
        attributes.addElement(new Attribute("max"));
        attributes.addElement(new Attribute("mean"));
        attributes.addElement(new Attribute("stdDev"));

        classes.addElement("walking");
        classes.addElement("running");
        classes.addElement("standing");
        classes.addElement("biking");


        attributes.addElement(new Attribute("movementType", classes));

        Instance data = new Instance(1.0D, new double[]{
                statisticsData.getMin(), statisticsData.getMax(), statisticsData.getMean(), statisticsData.getStdDev()
        });


        /*Instance ins = new Instance(4);
        ins.setDataset(new Instances());
        ins.setDataset();*/
        double tNum = this.classifier.classifyInstance(data);
        //Attribute attr =
        int classIndex = (int) tNum;
        return String.valueOf(classIndex);
    }
}
