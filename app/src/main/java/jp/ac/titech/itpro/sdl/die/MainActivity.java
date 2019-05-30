package jp.ac.titech.itpro.sdl.die;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener, Spinner.OnItemSelectedListener, CheckBox.OnCheckedChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView glView;
    private SimpleRenderer renderer;

    private Cube cube;
    private Pyramid pyramid;
    private Sword sword;

    // UI elements
    // seek bar
    private SeekBar seekBarX, seekBarY, seekBarZ, auto_speed_x, auto_speed_y, auto_speed_z;
    private Spinner auto_x, auto_y, auto_z;
    private CheckBox resist;

    // sensor for resist feature
    private SensorManager sensor_manager;
    private Sensor sensor_gyroscope;

    // variables for resist feature
    private boolean is_resist = false;
    private long last_time;
    private double[] last_rot = new double[3];

    // locks to prevent data races
    private boolean is_touching_seekbar = false;

    // automatic rotation status
    private int[] auto_rot = new int[3];
    private int[] auto_rot_speed = {0, 0, 0};

    // automatic rotation
    private Thread auto_rot_t = new Thread() {
        public void run() {
            while(true) {
                if(!is_touching_seekbar) {
                    for (int i = 0; i < 3; i++) {
                        switch (auto_rot[i]) {
                            case 1: // increment
                                last_rot[i] = (last_rot[i] + auto_rot_speed[i] + 1 + 360) % 360;
                                break;
                            case 2: // decrement
                                last_rot[i] = (last_rot[i] - auto_rot_speed[i] - 1 + 360) % 360;
                                break;
                            default: // static
                                // do nothing
                                break;
                        }
                    }
                    if(!is_resist){
                        seekBarX.setProgress((int) last_rot[0]);
                        seekBarY.setProgress((int) last_rot[1]);
                        seekBarZ.setProgress((int) last_rot[2]);
                    }
                }
                try {
                    sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.gl_view);
        seekBarX = findViewById(R.id.seekbar_x);
        seekBarY = findViewById(R.id.seekbar_y);
        seekBarZ = findViewById(R.id.seekbar_z);
        seekBarX.setMax(360);
        seekBarY.setMax(360);
        seekBarZ.setMax(360);
        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);
        seekBarZ.setOnSeekBarChangeListener(this);

        auto_x = findViewById(R.id.auto_x);
        auto_y = findViewById(R.id.auto_y);
        auto_z = findViewById(R.id.auto_z);
        auto_x.setOnItemSelectedListener(this);
        auto_y.setOnItemSelectedListener(this);
        auto_z.setOnItemSelectedListener(this);

        resist = findViewById(R.id.resist);
        resist.setOnCheckedChangeListener(this);

        auto_speed_x = findViewById(R.id.seekbar_x_auto);
        auto_speed_y = findViewById(R.id.seekbar_y_auto);
        auto_speed_z = findViewById(R.id.seekbar_z_auto);
        auto_speed_x.setMax(10);
        auto_speed_y.setMax(10);
        auto_speed_z.setMax(10);
        auto_speed_x.setOnSeekBarChangeListener(this);
        auto_speed_y.setOnSeekBarChangeListener(this);
        auto_speed_z.setOnSeekBarChangeListener(this);

        renderer = new SimpleRenderer();
        cube = new Cube();
        pyramid = new Pyramid();
        sword = new Sword();
        renderer.setObj(cube);
        glView.setRenderer(renderer);

        // initialize sensors
        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensor_manager == null){
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        sensor_gyroscope = sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor_gyroscope == null){
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show();
        }

        auto_rot_t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        glView.onResume();
        sensor_manager.registerListener(this, sensor_gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        glView.onPause();
        sensor_manager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
        case R.id.menu_cube:
            renderer.setObj(cube);
            break;
        case R.id.menu_pyramid:
            renderer.setObj(pyramid);
            break;
            case R.id.menu_sword:
            renderer.setObj(sword);
            break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_x:
                renderer.rotateObjX(progress);
                break;
            case R.id.seekbar_y:
                renderer.rotateObjY(progress);
                break;
            case R.id.seekbar_z:
                renderer.rotateObjZ(progress);
                break;
            case R.id.seekbar_x_auto:
                auto_rot_speed[0] = progress;
                break;
            case R.id.seekbar_y_auto:
                auto_rot_speed[1] = progress;
                break;
            case R.id.seekbar_z_auto:
                auto_rot_speed[2] = progress;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekbar_x:
            case R.id.seekbar_y:
            case R.id.seekbar_z:
                is_touching_seekbar = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekbar_x:
            case R.id.seekbar_y:
            case R.id.seekbar_z:
                is_touching_seekbar = false;
                last_rot[0] = seekBarX.getProgress();
                last_rot[1] = seekBarY.getProgress();
                last_rot[2] = seekBarZ.getProgress();
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if(!is_resist || is_touching_seekbar) return;

        long time = event.timestamp;
        double[] rot = new double[3];

        rot[0] = last_rot[0] - event.values[0] * 180 / Math.PI * (time - last_time) / 1000000000;
        rot[1] = last_rot[1] - event.values[1] * 180 / Math.PI * (time - last_time) / 1000000000;
        rot[2] = last_rot[2] - event.values[2] * 180 / Math.PI * (time - last_time) / 1000000000;

        rot[0] = (rot[0] + 360) % 360;
        rot[1] = (rot[1] + 360) % 360;
        rot[2] = (rot[2] + 360) % 360;

        seekBarX.setProgress((int) Math.round(rot[0]));
        seekBarY.setProgress((int) Math.round(rot[1]));
        seekBarZ.setProgress((int) Math.round(rot[2]));

        last_rot = rot;
        last_time = time;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){
        Log.d(TAG, "onAccuracyChanged: accuracy = " + accuracy);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: position = " + position + ", id = " + id);
        switch(parent.getId()){
            case R.id.auto_x:
                auto_rot[0] = (int) id;
            break;
            case R.id.auto_y:
                auto_rot[1] = (int) id;
            break;
            case R.id.auto_z:
                auto_rot[2] = (int) id;
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: isChecked = " + isChecked);
        is_resist = isChecked;
    }
}
