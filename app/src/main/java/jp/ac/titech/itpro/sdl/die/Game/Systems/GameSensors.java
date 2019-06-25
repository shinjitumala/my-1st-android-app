package jp.ac.titech.itpro.sdl.die.Game.Systems;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import jp.ac.titech.itpro.sdl.die.R;

import static android.content.Context.SENSOR_SERVICE;

/*
    Class that gets all the sensor values.
 */
public class GameSensors implements SensorEventListener {
    // sensors
    private SensorManager sensor_manager;
    private Sensor sensor_gyroscope, sensor_light, sensor_accelerometer, sensor_orientation;

    private double[] get_rotation = new double[3];
    private double illumination;
    private double[] acceleration = new double[3];
    private double[] orientation = new double[3];
    private double[] orientation_adjustment = new double[3];

    private GameState game_state;

    GameSensors(AppCompatActivity main_activity, GameState game_state){
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
        switch(event.sensor.getType()){
            case Sensor.TYPE_GYROSCOPE:
                get_rotation[0] = event.values[0];
                get_rotation[1] = event.values[1];
                get_rotation[2] = event.values[2];
                if(game_state.turn_cooldown >= 0) game_state.turn_cooldown--;
                game_state.update_rotation();
            break;
            case Sensor.TYPE_LIGHT:
                illumination = event.values[0];
                game_state.update_illumination();
            break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                acceleration[0] = event.values[0];
                acceleration[1] = event.values[1];
                acceleration[2] = event.values[2];
                if(game_state.acceleration_cooldown >=0) game_state.acceleration_cooldown--;
                game_state.update_acceleration();
            break;
            case Sensor.TYPE_ORIENTATION:
                orientation[0] = event.values[0] - orientation_adjustment[0];
                orientation[1] = event.values[1] - orientation_adjustment[1];
                orientation[2] = event.values[2] - orientation_adjustment[2];
                game_state.update_orientation();
            break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // called when app is resumed
    void resume(){
        sensor_manager.registerListener(this, sensor_gyroscope, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_light, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_orientation, SensorManager.SENSOR_DELAY_GAME);
    }

    // called when app is paused
    void pause(){
        sensor_manager.unregisterListener(this);
    }

    // recalibrate controls
    void recalibrate(){
        orientation_adjustment[0] = orientation[0] + orientation_adjustment[0];
        orientation_adjustment[1] = orientation[1] + orientation_adjustment[1];
        orientation_adjustment[2] = orientation[2] + orientation_adjustment[2];
    }

    // prevent external sensor data editing
    double[] get_rotation() {
        return get_rotation;
    }
    double get_illumination() {
        return illumination;
    }
    double[] get_acceleration() {
        return acceleration;
    }
    double[] get_orientation() {
        return orientation;
    }
}
