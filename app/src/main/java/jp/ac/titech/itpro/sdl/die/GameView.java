package jp.ac.titech.itpro.sdl.die;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ac.titech.itpro.sdl.die.GameObjects.*;

import static java.lang.System.exit;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    // for debugging purposes
    private String TAG = GameView.class.getSimpleName();

    // used by the game loop
    private Paint paint = new Paint();

    // drawing thread controller
    private SurfaceHolder surface_holder;
    private Thread thread;
    private volatile boolean allowed_to_draw = false;
    private volatile boolean loading = true;
    private boolean surface_created = false;
    private static final int FRAME_TIME = (int) (1000.0 / 60.0);
    private Context context;

    // game data
    private GameState game_state;
    private GameMap game_map = new GameMap();

    // entities
    private You you;
    private ArrayList<GameDrawableObject> game_objects;
    private ArrayList<GameDrawableObject> game_objects_top;
    private ArrayList<GameDrawableObject> game_objects_all;

    private static GameView gv;

    // Constructors
    public GameView(Context context){
        super(context);
        initialize_game(context);
    }

    public GameView(Context context, AttributeSet attribute_set){
        super(context, attribute_set);
        initialize_game(context);
    }

    public GameView(Context context, AttributeSet attribute_set, int def_style_attribute){
        super(context, attribute_set, def_style_attribute);
        initialize_game(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, AttributeSet attribute_set, int def_style_attribute, int def_style_resource){
        super(context, attribute_set, def_style_attribute, def_style_resource);
        initialize_game(context);
    }

    private void initialize_game(Context context){
        // surface view initialization
        this.context = context;
        surface_holder = this.getHolder();
        surface_holder.addCallback(this);
        setFocusable(true);

        // initialize game state
        this.game_state = new GameState((AppCompatActivity) context);
        gv = this;

        // load the first level
        load_map(1);
    }

    private void load_map(int level){
        pause();
        loading = true;
        // load map from rom
        game_map.set_level(level);

        // initialize game objects
        game_objects = new ArrayList<>();
        game_objects_top = new ArrayList<>();
        game_objects_all = new ArrayList<>();

        // initialize game objects
        for(int i = 0; i < game_map.size[0]; i++)
            for(int j = 0; j < game_map.size[1]; j++){
                switch(game_map.get_init_map(i, j)){
                    case 0: // Tiles
                        game_objects.add(new Tiles(i, j));
                        break;
                    case 1: // Wall
                        game_objects.add(new Wall(i, j));
                        break;
                    default:
                        int k = game_map.get_init_map(i, j);
                        if(k < 0){
                            Portal p = new Portal(i, j);
                            game_objects.add(p);
                            p.level = -k;
                        }
                    break;
                }
            }

        // initialize top game objects
        for(int i = 0; i < game_map.size[0]; i++)
            for(int j = 0; j < game_map.size[1]; j++){
                switch(game_map.get_init_map_top(i, j)){
                    case 1: // you
                        you = new You(i, j);
                        break;
                    case 2: // Gravpigs
                        game_objects_top.add(new Gravipigs(i, j));
                        break;
                    case 3: // EvilWall
                        game_objects_top.add(new EvilWall(i, j));
                        break;
                    case 4: // Door of Tilt
                        game_objects_top.add(new DoorOfTilt(i, j));
                        break;
                    case 5: // CubeOfRage
                        game_objects_top.add(new CubeOfRage(i, j));
                        break;
                }
            }

        game_objects_all.addAll(game_objects); game_objects_all.addAll(game_objects_top);
        loading = false;
        resume();
    }

    // main game loop
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run(){
        long frame_start;
        long frame;

        /*
         * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing thread
         * (AOSP - Issue 58385)
         */
        if (android.os.Build.BRAND.equalsIgnoreCase("google") && android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") && android.os.Build.MODEL.equalsIgnoreCase("Nexus 7")) {
            Log.w(TAG, "Sleep 500ms (Device: Asus Nexus 7)");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        while(allowed_to_draw){
            if(!surface_holder.getSurface().isValid() || surface_holder == null){
                return;
            }

            frame_start = System.nanoTime();
            Canvas canvas = surface_holder.lockCanvas();
            if(canvas != null){
                try{
                    synchronized (surface_holder){
                        game_state.update();

                        update(canvas);
                        you.draw(game_state, canvas, paint);
                    }
                } finally {
                    surface_holder.unlockCanvasAndPost(canvas);
                }
            }

            frame = (System.nanoTime() - frame_start) / 1000000;
            if(frame < FRAME_TIME){
                try{
                    Thread.sleep(FRAME_TIME - frame);
                } catch (InterruptedException e){
                    Log.e(TAG, "Was interrupted while sleeping.");
                }
            }
        }
    }

    // game logic
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void update(Canvas canvas){
        // draw background
        int color;
        switch(game_state.get_light_level()){
            case LOW:
                color = Color.rgb(29, 65, 111);
            break;
            case MEDIUM:
                color = Color.rgb(228, 98, 7);
            break;
            case HIGH:
                color = Color.rgb(91, 153, 233);
            break;
            default:
                color = Color.YELLOW;
            break;
        }
        canvas.drawColor(color);

        // draw game objects
        for(GameDrawableObject x : game_objects){
            x.draw(game_state, canvas, paint);
        }

        // draw top level objects
        for(GameDrawableObject x : game_objects_top){
            x.draw(game_state, canvas, paint);
        }

        // poll accelerometer and gyroscope events
        update_events(accelerometer(game_state), gyroscope(game_state));
    }

    // interpret

    @Override
    public void surfaceCreated(SurfaceHolder surface_holder) {
        Log.d(TAG, "onSurfaceCreated");
        this.surface_holder = surface_holder;
        pause();
        surface_created = true;
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
        // safeguard
        if(width == 0 || height == 0){
            return;
        }

        // recalculate size of blocks
        game_state.BLOCK_SIZE = Math.min(width / game_map.size[0], height / game_map.size[1]);
        game_state.X_START = (width - game_state.BLOCK_SIZE * game_map.size[0]) / 2;
        game_state.Y_START = (height - game_state.BLOCK_SIZE * game_map.size[1]) / 2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "onSurfaceDestroyed");
        surface_created = false;
    }

    // resume game
    public synchronized void resume(){;
        Log.d(TAG, "resume");
        game_state.resume();
        if(surface_created && thread == null){
            thread = new Thread(this, "drawer");
            allowed_to_draw = true;
            thread.start();
        }
    }

    // pause game
    public synchronized void pause(){
        game_state.pause();
        Log.d(TAG, "pause");
        if(thread != null) {
            allowed_to_draw = false;
            try{
                thread.join();
            }catch(Exception e) {
                Log.e(TAG, "Error joining draw thread.");
            }
            thread = null;
        }
    }

    // touch events
    float[] touch_start = new float[2], touch_end = new float[2];
    private static final float MINIMUM_DISTANCE = 100;
    @SuppressLint("ClickableViewAccessibility")
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
                if(Math.sqrt(Math.pow(touch_end[0] - touch_start[0], 2) + Math.pow(touch_end[1] - touch_start[1], 2)) > MINIMUM_DISTANCE)
                    swipe();
            break;
        }
        return true;
    }

    private void swipe(){
        float dx = touch_end[0] - touch_start[0];
        float dy = touch_end[1] - touch_start[1];

        if(Math.abs(dx) > Math.abs(dy)){
            if (dx > 0 && check_passable(you.position[0] + 1, you.position[1], 2)) {
                you.position[0]++; you.direction = 3;
            } else if(!(dx > 0) && check_passable( you.position[0] - 1, you.position[1], 0)) {
                you.position[0]--; you.direction = 1;
            }
        }else
            if(dy > 0 && check_passable(you.position[0], you.position[1] + 1, 1)) {
                you.position[1]++; you.direction = 2;
            }else if(!(dy > 0) && check_passable(you.position[0], you.position[1] - 1, 3)) {
                you.position[1]--; you.direction = 0;
            }

        Portal p = null;
        boolean flag = false;
        for(GameDrawableObject x : game_objects_all){
            if(x.getClass() == Portal.class){
                p = (Portal) x;
                if(Arrays.equals(p.position, you.position)) {
                    flag = true;
                    break;
                }
            }
        }

        if(flag) {
            load_map(p.level);
        }
    }

    // direction: right, up, left, down
    private boolean check_passable(int x, int y, int direction){
        for(GameDrawableObject penis : game_objects_all){
            if(penis.position[0] == x && penis.position[1] == y && !penis.passable[direction])
                return false;
        }
        return true;
    }

    // image loader
    public static Drawable load_image(int id){
        if(gv == null) Log.e("fatal", "OH NO!");
        Bitmap bitmap = BitmapFactory.decodeResource(gv.getResources(), id);
        return new BitmapDrawable(bitmap);
    }

    // detect accelerometer events
    // 0: none, 1: up, 2: left, 3: down, 4: right, 5: forward, 6: backward
    private int last_accel = 0;
    private int counter_accel = 0;
    private static final int COUNTER_ACCEL = 60;
    public int accelerometer(GameState game_state){
        switch(game_state.get_acceleration()){
            case STATIONARY:
                if(counter_accel >= COUNTER_ACCEL) {
                    counter_accel = 0;
                    last_accel = 0;
                }
                counter_accel++;
                return 0;
            case UP:
                if(last_accel == 3) {
                    last_accel = 0;
//                    Log.d(TAG, "down!");
                    return 5;
                } else {
                    last_accel = 1;
                    return 0;
                }
            case LEFT:
                if(last_accel == 4) {
                    last_accel = 0;
//                    Log.d(TAG, "right!");
                    return 3;
                } else {
                    last_accel = 2;
                    return 0;
                }
            case DOWN:
                if(last_accel == 1) {
                    last_accel = 0;
//                    Log.d(TAG, "up!");
                    return 6;
                } else {
                    last_accel = 3;
                    return 0;
                }
            case RIGHT:
                if(last_accel == 2){
                    last_accel = 0;
//                    Log.d(TAG, "left!");
                    return 1;
                } else {
                    last_accel = 4;
                    return 0;
                }
            case FORWARD:
                if(last_accel == 6){
                    last_accel = 0;
//                    Log.d(TAG, "backward!");
                    return 2;
                } else {
                    last_accel = 5;
                    return 0;
                }
            case BACKWARD:
                if(last_accel == 5){
                    last_accel = 0;
//                    Log.d(TAG, "forward!");
                    return 4;
                } else {
                    last_accel = 6;
                    return 0;
                }
            default:
                return 0;
        }

    }

    public void update_events(int direction, GameState.Rotation rot){
        for(GameDrawableObject x : game_objects_all){
            if(x.graviton){
                switch(direction){
                    case 4:
                        if (check_passable(x.position[0],x.position[1] - 1, 3)) x.position[1]--;
                    break;
                    case 1:
                        if (check_passable(x.position[0] - 1, x.position[1], 0)) x.position[0]--;
                    break;
                    case 2:
                        if (check_passable(x.position[0], x.position[1] + 1, 1)) x.position[1]++;
                    break;
                    case 3:
                        if (check_passable(x.position[0] + 1, x.position[1], 2)) x.position[0]++;
                    break;
                }
            } else if(x.rage){
                CubeOfRage tmp = (CubeOfRage) x;
                switch(rot){
                    case TURN_LEFT: tmp.left(); break;
                    case TURN_RIGHT: tmp.right(); break;
                    case TURN_FORWARD: tmp.up(); break;
                    case TURN_BACKWARD: tmp.down(); break;
                }
            }
        }
    }

    private GameState.Rotation last_rot = GameState.Rotation.STATIC;
    public GameState.Rotation gyroscope(GameState game_state){
        if(last_rot != game_state.get_rotation()){
            last_rot = game_state.get_rotation();
            return game_state.get_rotation();
        }
        last_rot = game_state.get_rotation();
        return GameState.Rotation.STATIC;
    }

    public void recalibrate(){
        game_state.recalibrate();
    }
}
