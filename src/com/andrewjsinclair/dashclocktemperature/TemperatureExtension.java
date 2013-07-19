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
	private float temperature = (float) 0.0;
	public static final String PREF_LABEL = "temp_units";

	@Override
	public void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
		SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		Sensor AmbientTemperatureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		
		if(AmbientTemperatureSensor != null){
		      mySensorManager.registerListener(
		        AmbientTemperatureSensorListener, 
		        AmbientTemperatureSensor, 
		        SensorManager.SENSOR_DELAY_NORMAL);
		}
		setUpdateWhenScreenOn(true);
	}
	
	@Override
	protected void onUpdateData(int reason) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fahrenheit = sp.getBoolean(PREF_LABEL, true);
        String tempUnitsOutput = (char)0x00B0 + "C";
        int convertedTemp = 0;

        
        if(!fahrenheit) {
        	convertedTemp = (int) temperature;
        	tempUnitsOutput = (char)0x00B0 + "C";
        } else {
        	convertedTemp =  (int)((9 * temperature) / 5 + 32.0f);
        	tempUnitsOutput = (char)0x00B0 + "F";
        }
        
		publishUpdate(new ExtensionData()
	    .visible(true)
	    .icon(R.drawable.device_access_brightness_low)
	    .status(String.format("%d", convertedTemp) + tempUnitsOutput)
	    .expandedTitle("Temperature " + String.format("%d", convertedTemp) + tempUnitsOutput )	    
		);

	}
	

	
   private final SensorEventListener AmbientTemperatureSensorListener
    = new SensorEventListener(){

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
   // TODO Auto-generated method stub
  
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
   if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
	   
    TemperatureExtension.this.temperature =  event.values[0];
   }
  }

   };

}
