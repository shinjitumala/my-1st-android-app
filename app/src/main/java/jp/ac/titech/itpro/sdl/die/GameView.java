package jp.ac.titech.itpro.sdl.die;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayDeque;
import java.util.Queue;

import jp.ac.titech.itpro.sdl.die.GameObjects.GameDrawableObject;
import jp.ac.titech.itpro.sdl.die.GameObjects.Map;
import jp.ac.titech.itpro.sdl.die.GameObjects.Wall;
import jp.ac.titech.itpro.sdl.die.GameObjects.You;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    // for debugging purposes
    private String TAG = GameView.class.getSimpleName();

    // used by the game loop
    private Paint paint = new Paint();
    private SurfaceHolder surface_holder = this.getHolder();
    private Thread thread;

    // game data
    private GameState game_state;

    private Map map;

    // entities
    private You you;
    private Queue<GameDrawableObject> game_objects = new ArrayDeque<>();

    public GameView(Context context, GameState game_state){
        super(context);
        surface_holder.addCallback(this);
        this.game_state = game_state;

        // initialize map
        map = new Map(Map.Level.ONE);

        // initialize game objects
        for(int i = 0; i < map.size[0]; i++)
            for(int j = 0; j < map.size[1]; j++){
                switch(map.get_map_state(i, j)){
                    case 1:
                        game_objects.add(new Wall(i, j));
                    break;
                    case 2:
                        you = new You(i, j);
                    break;
                }
            }
    }
    public GameView(Context context, AttributeSet as){
        super(context, as);
        Log.e(TAG, "hmmm...");
    }

    private void init(){

    }

    // main game loop
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run(){
        while(thread != null){
            if(!surface_holder.getSurface().isValid()){
                continue;
            }
            Canvas tmp_canvas = surface_holder.lockCanvas();
            if(tmp_canvas != null){
                game_state.update();

                update(tmp_canvas);
                you.draw(game_state, tmp_canvas, paint);

                surface_holder.unlockCanvasAndPost(tmp_canvas);
            }
        }
    }

    // game logic
    private void update(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(32f);
        for(int i = 0; i < map.size[0]; i++){
            for(int j = 0; j < map.size[1]; j++){
                switch(map.get_map_state(i, j)){
                    case 0:
                        paint.setColor(Color.GRAY);
                    break;
                    case 1:
                        paint.setColor(Color.BLACK);
                    break;
                    case 2:
                        paint.setColor(Color.RED);
                    break;
                    case 3:
                        paint.setColor(Color.GREEN);
                    break;
                }
                Rect tmp_rect = new Rect(
                        game_state.X_START + game_state.BLOCK_SIZE * i,
                        game_state.Y_START + game_state.BLOCK_SIZE * j,
                        game_state.X_START + game_state.BLOCK_SIZE * i + game_state.BLOCK_SIZE,
                        game_state.Y_START + game_state.BLOCK_SIZE * j + game_state.BLOCK_SIZE
                );
                canvas.drawRect(tmp_rect, paint);
            }
        }
        for(GameDrawableObject x : game_objects){
            x.draw(game_state, canvas, paint);
        }
    }

    // interpret

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "onSurfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "onSurfaceChanged");

        // recalculate size of blocks
        game_state.BLOCK_SIZE = Math.min(width / map.size[0], height / map.size[1]);
        game_state.X_START = (width - game_state.BLOCK_SIZE * map.size[0]) / 2;
        game_state.Y_START = (height - game_state.BLOCK_SIZE * map.size[1]) / 2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "onSurfaceDestroyed");
    }

    // resume game
    public void resume(){
        Log.d(TAG, "resume");
        if(thread != null){
            thread.start();
        }else{
            thread = new Thread(this);
            thread.start();
        }
    }

    // pause game
    public void pause(){
        Log.d(TAG, "pause");
        thread = null;
    }

    // touch events
    float[] touch_start = new float[2], touch_end = new float[2];
    @Override
    public boolean onTouchEvent(MotionEvent event){
//        Log.d(TAG, "onTouchEvent");
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touch_start[0] = event.getX();
                touch_start[1] = event.getY();
            break;
            case MotionEvent.ACTION_UP:
                touch_end[0] = event.getX();
                touch_end[1] = event.getY();
                swipe();
            break;
        }
        return true;
    }

    private void swipe(){
        float dx = touch_end[0] - touch_start[0];
        float dy = touch_end[1] - touch_start[1];

        if(Math.abs(dx) > Math.abs(dy)){
            if (dx > 0 && check_passable(you.position[0], you.position[1], you.position[0] + 1, you.position[1]))
                you.position[0]++;
            else if(!(dx > 0) && check_passable(you.position[0], you.position[1], you.position[0] - 1, you.position[1]))
                you.position[0]--;
        }else
            if(dy > 0 && check_passable(you.position[0], you.position[1], you.position[0], you.position[1] + 1))
                you.position[1]++;
            else if(!(dy > 0) && check_passable(you.position[0], you.position[1], you.position[0], you.position[1] - 1))
                you.position[1]--;
    }

    private boolean check_passable(int start_x, int start_y, int end_x, int end_y){
        for(GameDrawableObject x : game_objects){
            if(x.position[0] == end_x && x.position[1] == end_y && !x.passable)
                return false;
        }
        return true;
    }
}
