package jp.ac.titech.itpro.sdl.die.Game;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import jp.ac.titech.itpro.sdl.die.Game.Objects.*;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.CubeOfRage;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.DoorOfTilt;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.EvilWall;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.GameDrawableObject;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.Gravipigs;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.Portal;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.Tiles;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.Wall;
import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.You;
import jp.ac.titech.itpro.sdl.die.Game.Systems.GameSoundEngine;
import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    // for debugging purposes
    private String TAG = GameView.class.getSimpleName();

    // drawing thread controller
    private SurfaceHolder surface_holder;
    private Thread thread;
    private volatile boolean allowed_to_draw = false;
    private boolean surface_created = false;
    private static final int FRAME_TIME = (int) (1000.0 / 10.0);
    private int width, height;

    // game data
    private GameState game_state;
    private GameMap game_map = new GameMap();
    private GameSoundEngine game_sound_engine;
    private Scenario scenario;

    // entities
    private You you;
    private ArrayList<GameDrawableObject> game_objects;
    private ArrayList<GameDrawableObject> game_objects_top;
    private ArrayList<GameDrawableObject> game_objects_all;
    private ArrayList<GameDrawableObject> game_objects_gravitons;
    private ArrayList<GameDrawableObject> game_objects_rages;
    private ArrayList<GameDrawableObject> game_objects_portals;

    public static GameView gv;

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
        surface_holder = this.getHolder();
        surface_holder.addCallback(this);
        setFocusable(true);

        // initialize game state
        this.game_state = new GameState((AppCompatActivity) context, this);
        this.game_sound_engine = new GameSoundEngine(context);
        this.scenario = new Scenario(game_sound_engine);
        gv = this;

        // load the first level
        load_map(1);
    }

    private void load_map(int level){
        pause();
        // load map from rom
        game_map.set_level(level);

        // initialize game objects
        game_objects = new ArrayList<>();
        game_objects_top = new ArrayList<>();
        game_objects_all = new ArrayList<>();
        game_objects_gravitons = new ArrayList<>();
        game_objects_rages = new ArrayList<>();
        game_objects_portals = new ArrayList<>();

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
                            game_objects_top.add(p);
                            p.level = -k;
                            game_objects_portals.add(p);
                        }
                    break;
                }
            }

        // initialize top game objects
        for(int i = 0; i < game_map.size[0]; i++)
            for(int j = 0; j < game_map.size[1]; j++){
                switch(game_map.get_init_map_top(i, j)){
                    case 1: // you
                        you = new You(i, j, game_sound_engine);
                        scenario.you(you, game_map.get_level());
                        break;
                    case 2: // Gravipigs
                        Gravipigs g = new Gravipigs(i, j);
                        game_objects_top.add(g);
                        game_objects_gravitons.add(g);
                        break;
                    case 3: // EvilWall
                        game_objects_top.add(new EvilWall(i, j));
                        break;
                    case 4: // Door of Tilt
                        game_objects_top.add(new DoorOfTilt(i, j));
                        break;
                    case 5: // CubeOfRage
                        CubeOfRage c = new CubeOfRage(i, j);
                        game_objects_top.add(c);
                        game_objects_rages.add(c);
                        break;
                }
            }

        game_objects_all.addAll(game_objects); game_objects_all.addAll(game_objects_top);
        update_drawing_scale();
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
                    update(canvas);
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
        switch (game_state.get_light_level()) {
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
        for (GameDrawableObject x : game_objects) {
            x.draw(game_state, canvas);
        }

        // draw top level objects
        for (GameDrawableObject x : game_objects_top) {
            x.draw(game_state, canvas);
        }

        // draw player
        you.draw(game_state, canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surface_holder) {
        Log.d(TAG, "onSurfaceCreated");
        this.surface_holder = surface_holder;
        pause();
        surface_created = true;
        resume();
        game_sound_engine.start_music();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
        // safeguard
        if(width == 0 || height == 0){
            return;
        }
        this.width = width;
        this.height = height;
        update_drawing_scale();
    }

    public void update_drawing_scale(){
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
    public synchronized void resume(){
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
                else{
                    scenario.tap();
                }
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
            } else game_sound_engine.play_sound("bump");
        }else
            if(dy > 0 && check_passable(you.position[0], you.position[1] + 1, 1)) {
                you.position[1]++; you.direction = 2;
            }else if(!(dy > 0) && check_passable(you.position[0], you.position[1] - 1, 3)) {
                you.position[1]--; you.direction = 0;
            } else game_sound_engine.play_sound("bump");

        Portal p = null;
        boolean flag = false;
        for(GameDrawableObject x : game_objects_portals){
            p = (Portal) x;
            if(Arrays.equals(p.position, you.position)) {
                flag = true;
                break;
            }
        }

        if(flag) {
            game_sound_engine.play_sound("portal");
            load_map(p.level);
        }
    }

    // direction: right, up, left, down
    // returns true if it is possible for a object in (x, y) to go in the 'direction
    private boolean check_passable(int x, int y, int direction){
        for(GameDrawableObject penis : game_objects_all){
            if(penis.position[0] == x && penis.position[1] == y && !penis.passable()[direction])
                return false;
        }
        return you.position[0] != x || you.position[1] != y || you.passable()[direction];
    }

    // image loader
    public static Drawable load_image(int id){
        if(gv == null) Log.e("fatal", "OH NO!");
        Bitmap bitmap = BitmapFactory.decodeResource(gv.getResources(), id);
        return new BitmapDrawable(bitmap);
    }

    public void acceleration_event(GameState.Acceleration acceleration){
        for(GameDrawableObject x : game_objects_gravitons){
            switch(acceleration){
                case FORWARD:
                    if (check_passable(x.position[0],x.position[1] - 1, 3)) x.position[1]--;
                    break;
                case LEFT:
                    if (check_passable(x.position[0] - 1, x.position[1], 0)) x.position[0]--;
                    break;
                case BACKWARD:
                    if (check_passable(x.position[0], x.position[1] + 1, 1)) x.position[1]++;
                    break;
                case RIGHT:
                    if (check_passable(x.position[0] + 1, x.position[1], 2)) x.position[0]++;
                    break;
            }
        }
    }

    public void rotation_event(GameState.Rotation rotation){
        for(GameDrawableObject x : game_objects_rages){
            CubeOfRage tmp = (CubeOfRage) x;
            switch(rotation){
                case TURN_LEFT: tmp.left(); break;
                case TURN_RIGHT: tmp.right(); break;
                case TURN_FORWARD: tmp.up(); break;
                case TURN_BACKWARD: tmp.down(); break;
            }
        }
    }

    public void recalibrate(){
        game_state.recalibrate();
    }
}
