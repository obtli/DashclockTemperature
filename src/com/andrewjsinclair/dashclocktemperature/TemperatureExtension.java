/**
 *    Copyright 2013 Andrew Sinclair
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewjsinclair.dashclocktemperature;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;


public class TemperatureExtension extends DashClockExtension {
    private float mMeasuredTemperature = 0.0f;
    private float mDisplayedTemperature = 0.0f;
    private float mHumidity = 0.0f;
    private String mTempUnits = DEGREES + "C";

    protected static final String PREF_LABEL = "temp_units";
    private static final char DEGREES = (char)0x00B0;

    @Override
    public void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        initSensors();
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fahrenheit = sp.getBoolean(PREF_LABEL, true);


        if (!fahrenheit) {
            mDisplayedTemperature = mMeasuredTemperature;
            mTempUnits = DEGREES + "C";
        } else {
            mDisplayedTemperature =  ((9.0f * mMeasuredTemperature) / 5.0f + 32.0f);
            mTempUnits = DEGREES + "F";
        }

        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(R.drawable.device_access_brightness_low)
                .status(makeStatusString())
                .expandedTitle(makeTitleString())
        );

    }

    private String makeStatusString() {
        StringBuilder s = new StringBuilder();
        s.append(makeTemperatureShortString());
        s.append("/");
        s.append(makeHumidityShortString());
        return s.toString();
    }


    private String makeTitleString() {
        StringBuilder s = new StringBuilder();
        s.append(makeTemperatureString());
        s.append("\n");
        s.append(makeHumidityString());
        return s.toString();
    }

    private String makeTemperatureShortString() {
        return new String(String.format("%.0f", mDisplayedTemperature) + mTempUnits);
    }

    private String makeTemperatureString() {
        return new String("Temperature: " +
                String.format("%.1f", mDisplayedTemperature) + mTempUnits);
    }

    private String makeHumidityString() {
        return new String("Relative Humidity: " + String.format("%.1f", mHumidity) + "%");
    }

    private String makeHumidityShortString() {
        return new String(String.format("%.0f", mHumidity) + "%");
    }


    /** Setup listeners for the chosen sensors.
     * TODO: Provide logic to choose sensors in preferences.
     */
    private void initSensors() {
        SensorManager manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        initTemperatureSensor(manager);
        initHumiditySensor(manager);
    }

    private void initTemperatureSensor(SensorManager manager) {
        Sensor temperatureSensor = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (temperatureSensor != null) {
            manager.registerListener(
                    SensorListener,
                    temperatureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void initHumiditySensor(SensorManager manager) {
        Sensor humiditySensor = manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(humiditySensor != null) {
            manager.registerListener(
                    SensorListener,
                    humiditySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    private final SensorEventListener SensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Accuracy is unimportant for this Extension
            return;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(event.sensor.getType()) {
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    mMeasuredTemperature =  event.values[0];
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    mHumidity = event.values[0];
                    break;
                default:
                    break;
            }
        }

    };

}
