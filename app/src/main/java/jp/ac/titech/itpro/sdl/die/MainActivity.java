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

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    GameView game_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        game_view = new GameView(this);
        setContentView(game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        game_view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        game_view.pause();
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
        return true;
    }

    private class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
        private Paint paint = new Paint();

        private SurfaceHolder surface_holder = this.getHolder();
        private Thread thread;

        private int state = 0;

        public GameView(Context context){
            super(context);
        }

        @Override
        public void run(){
            while(true){
                if(!surface_holder.getSurface().isValid()){
                    continue;
                }
                Canvas tmp_canvas = surface_holder.lockCanvas();
                if(tmp_canvas != null){
                    paint.setStyle(Paint.Style.FILL);
                    if(state == 0) {
                        paint.setColor(Color.RED);
                        state = 1;
                    }else{
                        paint.setColor(Color.GREEN);
                        state = 0;
                    }
                    tmp_canvas.drawPaint(paint);
                    surface_holder.unlockCanvasAndPost(tmp_canvas);
                    Log.d(TAG, "drawn!");
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        public void resume(){
            Log.d(TAG, "resume");
            if(thread != null){
                thread.start();
            }else{
                thread = new Thread(this);
                thread.start();
            }
        }

        public void pause(){
            Log.d(TAG, "pause");
            thread = null;
        }
    }
}
