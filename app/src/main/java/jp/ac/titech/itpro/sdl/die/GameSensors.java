package jp.ac.titech.itpro.sdl.die;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

/*
    Class that gets all the sensor values.
 */
public class GameSensors implements SensorEventListener {
    // game state tracker
    GameState game_state;

    // sensors
    private SensorManager sensor_manager;
    private Sensor sensor_gyroscope, sensor_light, sensor_accelerometer, sensor_orientation;
    public double[] rotation = new double[3];
    public double illumination;
    public double[] acceleration = new double[3];
    public double[] orientation = new double[3];
    private double[] orientation_adjustment = new double[3];

    public GameSensors(AppCompatActivity main_activity, GameState game_state){
        this.game_state = game_state;

        // initialize sensors
        sensor_manager = (SensorManager) main_activity.getSystemService(SENSOR_SERVICE);
        if(sensor_manager == null){
            Toast.makeText(main_activity, main_activity.getString(R.string.string_no_sensor_manager), Toast.LENGTH_LONG).show();
        }

        sensor_gyroscope = sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor_gyroscope == null){
            Toast.makeText(main_activity, main_activity.getString(R.string.string_no_gyroscope), Toast.LENGTH_LONG).show();
        }
        sensor_light = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(sensor_light == null){
            Toast.makeText(main_activity, main_activity.getString(R.string.string_no_light_sensor), Toast.LENGTH_LONG).show();
        }
        sensor_accelerometer = sensor_manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensor_accelerometer == null){
            Toast.makeText(main_activity, main_activity.getString(R.string.string_no_accelerometer), Toast.LENGTH_LONG).show();
        }
        sensor_orientation = sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor_orientation == null){
            Toast.makeText(main_activity, main_activity.getString(R.string.string_no_orientation_sensor), Toast.LENGTH_LONG).show();
        }

        recalibrate();
    }

    // called when an sensor change event is received
    @Override
    public void onSensorChanged(SensorEvent event) {
//         Log.d(TAG, "onSensorChanged");
        switch(event.sensor.getType()){
            case Sensor.TYPE_GYROSCOPE:
                rotation[0] = event.values[0];
                rotation[1] = event.values[1];
                rotation[2] = event.values[2];
//                Log.d(TAG, "onSensorChanged: rotation = " + Arrays.toString(rotation));
                break;
            case Sensor.TYPE_LIGHT:
                illumination = event.values[0];
                // low: <=40, 150 <= high
//                Log.d(TAG, "onSensorChanged: illumination = " + illumination);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                acceleration[0] = event.values[0];
                acceleration[1] = event.values[1];
                acceleration[2] = event.values[2];
//                Log.d(TAG, "onSensorChanged: acceleration = " + Arrays.toString(acceleration));
                break;
            case Sensor.TYPE_ORIENTATION:
                orientation[0] = event.values[0] - orientation_adjustment[0];
                orientation[1] = event.values[1] - orientation_adjustment[1];
                orientation[2] = event.values[2] - orientation_adjustment[2];
//                Log.d(TAG, "onSensorChanged: orientation = " + Arrays.toString(orientation));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // called when app is resumed
    public void resume(){
        sensor_manager.registerListener(this, sensor_gyroscope, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_light, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_orientation, SensorManager.SENSOR_DELAY_GAME);
    }

    // called when app is paused
    public void pause(){
        sensor_manager.unregisterListener(this);
    }

    // recalibrate controls
    public void recalibrate(){
        orientation_adjustment[0] = orientation[0] + orientation_adjustment[0];
        orientation_adjustment[1] = orientation[1] + orientation_adjustment[1];
        orientation_adjustment[2] = orientation[2] + orientation_adjustment[2];
    }
}
