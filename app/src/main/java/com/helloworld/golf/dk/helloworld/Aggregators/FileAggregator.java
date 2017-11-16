package com.helloworld.golf.dk.helloworld.Aggregators;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class FileAggregator {

    private File resultsFile;
    private ProgressDialog dialogBox;
    private Handler guiHandler;

    public FileAggregator() {
    }

    public void saveExternalFile(final Context context) {
        guiHandler = new Handler();
        Log.v("PATH", Environment.getExternalStorageDirectory().getAbsolutePath());
        Thread saveThread = new Thread() {
            @Override
            public void run() {
                List<StatisticsData> results = AccelerationAggregator.getInstance().getResults();

                try {
                    File path = context.getExternalFilesDir(null);
                    File file = new File(path, "my-file-name2.txt");
                    FileOutputStream f = new FileOutputStream(file);

                    PrintWriter pw = new PrintWriter(f);

                    // Results are scanned and written on the file..
                    Iterator<StatisticsData> resultsIterator = results.iterator();
                    int resultsCounter = 0;

                    pw.println("AccelerometerData");
                    pw.println("Sliding window dimension: 128 samples\n");

                    while (resultsIterator.hasNext()) {

                        StatisticsData currentElement = resultsIterator.next();

                        // Writing to file (special first case, with extra line
                        switch(resultsCounter % 3) {

                            case 0:		pw.println("Sliding window no. " + ((int) resultsCounter / 3 + 1));
                                        pw.println("X -> Min: " + currentElement.getMin() +
                                        ";\tMax: " + currentElement.getMax() + ";\tStd Dev: " + currentElement.getStdDev() + ";");
                                        break;

                            case 1:		pw.println("Y -> Min: " + currentElement.getMin() +
                                        ";\tMax: " + currentElement.getMax() + ";\tStd Dev: " + currentElement.getStdDev() + ";");
                                        break;

                            case 2:		pw.println("Linear acceleration - Z dimension -> Min: " + currentElement.getMin() +
                                        ";\tMax: " + currentElement.getMax() + ";\tStd Dev: " + currentElement.getStdDev() + ";\n");
                                        break;
                        }
                        resultsCounter ++;
                    }

                    pw.flush();
                    pw.close();
                    f.close();

                    // The GUI handler is started to update the GUI
                    guiHandler.post(new Runnable () {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "File export completed - located in " + context.getFilesDir(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    });

                } catch(Exception e){
                    e.printStackTrace();
                    guiHandler.post(new Runnable () {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "error",
                                    Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            }
        };
        saveThread.start();
    }
}
