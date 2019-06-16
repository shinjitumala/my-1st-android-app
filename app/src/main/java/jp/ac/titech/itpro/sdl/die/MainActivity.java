package jp.ac.titech.itpro.sdl.die;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    // canvas for drawing
    GameView game_view;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // initialize sensors
        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensor_manager == null){
            Toast.makeText(this, getString(R.string.string_no_sensor_manager), Toast.LENGTH_LONG).show();
            return;
        }

        sensor_gyroscope = sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor_gyroscope == null){
            Toast.makeText(this, getString(R.string.string_no_gyroscope), Toast.LENGTH_LONG).show();
            return;
        }
        sensor_light = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(sensor_light == null){
            Toast.makeText(this, getString(R.string.string_no_light_sensor), Toast.LENGTH_LONG).show();
            return;
        }
        sensor_accelerometer = sensor_manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(sensor_accelerometer == null){
            Toast.makeText(this, getString(R.string.string_no_accelerometer), Toast.LENGTH_LONG).show();
            return;
        }
        sensor_orientation = sensor_manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor_orientation == null){
            Toast.makeText(this, getString(R.string.string_no_orientation_sensor), Toast.LENGTH_LONG).show();
            return;
        }

        // initialize drawing canvas
        game_state = new GameState(this);
        game_view = new GameView(this, game_state);
        setContentView(game_view);
        recalibrate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        game_view.resume();
        sensor_manager.registerListener(this, sensor_gyroscope, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_light, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensor_manager.registerListener(this, sensor_orientation, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        game_view.pause();
        sensor_manager.unregisterListener(this);
    }

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
        Log.d(TAG, "onAccuracyChanged");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "onOptionsItemSelected");
        switch(item.getItemId()){
            case R.id.menu_recalibrate:
                recalibrate();
            break;
        }
        return true;
    }

    // recalibrate controls
    private void recalibrate(){
        orientation_adjustment[0] = orientation[0] + orientation_adjustment[0];
        orientation_adjustment[1] = orientation[1] + orientation_adjustment[1];
        orientation_adjustment[2] = orientation[2] + orientation_adjustment[2];
    }
}
