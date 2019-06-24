package jp.ac.titech.itpro.sdl.die;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    // surface view for drawing
    GameView game_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize drawing canvas
        game_view = new GameView(this);
        setContentView(game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game_view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        game_view.pause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_recalibrate:
                game_view.recalibrate();
            break;
        }
        return true;
    }
}
