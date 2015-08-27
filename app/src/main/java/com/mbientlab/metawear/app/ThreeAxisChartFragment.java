/*
 * Copyright 2015 MbientLab Inc. All rights reserved.
 *
 * IMPORTANT: Your use of this Software is limited to those specific rights
 * granted under the terms of a software license agreement between the user who
 * downloaded the software, his/her employer (which must be your employer) and
 * MbientLab Inc, (the "License").  You may not use this Software unless you
 * agree to abide by the terms of the License which can be found at
 * www.mbientlab.com/terms . The License limits your use, and you acknowledge,
 * that the  Software may not be modified, copied or distributed and can be used
 * solely and exclusively in conjunction with a MbientLab Inc, product.  Other
 * than for the foregoing purpose, you may not use, reproduce, copy, prepare
 * derivative works of, modify, distribute, perform, display or sell this
 * Software and/or its documentation for any purpose.
 *
 * YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
 * PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
 * MBIENTLAB OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT, NEGLIGENCE,
 * STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE
 * THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED
 * TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST
 * PROFITS OR LOST DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY,
 * SERVICES, OR ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY
 * DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 *
 * Should you have any questions regarding your right to use this Software,
 * contact MbientLab Inc, at www.mbientlab.com.
 */

package com.mbientlab.metawear.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiConfiguration;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.data.CartesianFloat;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by etsai on 8/19/2015.
 */
public abstract class ThreeAxisChartFragment extends SensorFragment {

    public abstract void onDataReceived(float[] points);

    private final ArrayList<Entry> xAxisData= new ArrayList<>(), yAxisData= new ArrayList<>(), zAxisData= new ArrayList<>();
    private final String dataType, streamKey;
    private final float samplePeriod;
    protected final AsyncOperation.CompletionHandler<RouteManager> dataStreamManager= new AsyncOperation.CompletionHandler<RouteManager>() {
        @Override
        public void success(RouteManager result) {
            streamRouteManager= result;
            result.subscribe(streamKey, new RouteManager.MessageHandler() {
                @Override
                public void process(Message message) {
                    final CartesianFloat spin = message.getData(CartesianFloat.class);

                    LineData data = chart.getData();

                    data.addXValue(String.format("%.2f", sampleCount * samplePeriod));
                    data.addEntry(new Entry(spin.x(), sampleCount), 0);
                    data.addEntry(new Entry(spin.y(), sampleCount), 1);
                    data.addEntry(new Entry(spin.z(), sampleCount), 2);

//                    Timber.d("Accelerometer:" + spin.x());
//                    Log.d("TAG", "Accelerometer:" + spin.x());

                    float accel_x = Float.valueOf(spin.x());
                    float accel_y = Float.valueOf(spin.y());
                    float accel_z = Float.valueOf(spin.z());

                    float[] points = new float[] {accel_x, accel_y, accel_z, 0, 0, 0};

                    onDataReceived(points);

                    sampleCount++;
                }
            });
        }
    };

    protected ThreeAxisChartFragment(String dataType, String chartDescription, String sensor, String streamKey, float min, float max, float sampleFreq) {
        super(sensor, R.layout.fragment_sensor, min, max);
        this.dataType= dataType;
        this.streamKey= streamKey;
        this.samplePeriod= 1 / sampleFreq;
        this.chartDescription= chartDescription;
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void clean() {

    }

    @Override
    protected String saveData() {
        final String CSV_HEADER = String.format("time,x-%s,y-%s,z-%s%n", dataType, dataType, dataType);
        String filename = String.format("%s_%tY%<tm%<td-%<tH%<tM%<tS%<tL.csv", sensor, Calendar.getInstance());

        try {
            FileOutputStream fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(CSV_HEADER.getBytes());

            LineData data = chart.getLineData();
            LineDataSet xSpinDataSet = data.getDataSetByIndex(0), ySpinDataSet = data.getDataSetByIndex(1),
                    zSpinDataSet = data.getDataSetByIndex(2);
            for (int i = 0; i < data.getXValCount(); i++) {
                fos.write(String.format("%.3f,%.3f,%.3f,%.3f%n", i * samplePeriod,
                        xSpinDataSet.getEntryForXIndex(i).getVal(),
                        ySpinDataSet.getEntryForXIndex(i).getVal(),
                        zSpinDataSet.getEntryForXIndex(i).getVal()).getBytes());
            }
            fos.close();
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void resetData(boolean clearData) {
        if (clearData) {
            sampleCount = 0;
            chartXValues.clear();
            xAxisData.clear();
            yAxisData.clear();
            zAxisData.clear();
        }

        ArrayList<LineDataSet> spinAxisData= new ArrayList<>();
        spinAxisData.add(new LineDataSet(xAxisData, "x-" + dataType));
        spinAxisData.get(0).setColor(Color.RED);
        spinAxisData.get(0).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(yAxisData, "y-" + dataType));
        spinAxisData.get(1).setColor(Color.GREEN);
        spinAxisData.get(1).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(zAxisData, "z-" + dataType));
        spinAxisData.get(2).setColor(Color.BLUE);
        spinAxisData.get(2).setDrawCircles(false);

        LineData data= new LineData(chartXValues);
        for(LineDataSet set: spinAxisData) {
            data.addDataSet(set);
        }
        data.setDrawValues(false);
        chart.setData(data);
    }
}
