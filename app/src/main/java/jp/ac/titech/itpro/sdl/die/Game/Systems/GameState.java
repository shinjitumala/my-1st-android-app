package jp.ac.titech.itpro.sdl.die.Game.Systems;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import jp.ac.titech.itpro.sdl.die.Game.GameView;

public class GameState {
    private static String TAG = GameState.class.getSimpleName();

    // parameters used for drawing
    public int BLOCK_SIZE, X_START, Y_START;

    private GameSensors game_sensors;
    private GameView game_view;

    private Rotation rotation;
    private static final float TURN_ON_THRESHOLD = 2.5f;
    private static final int TURN_COOLDOWN = 60;
    volatile int turn_cooldown;

    private LightLevel light_level;
    private static final int LOW_LIGHT_THRESHOLD = 40;
    private static final int HIGH_LIGHT_THRESHOLD = 150;

    private Acceleration acceleration;
    private static final float TRIGGER_ACCELERATION_THRESHOLD = 2.5f;
    private static final int ACCELERATION_COOLDOWN = 60;
    volatile int acceleration_cooldown;

    private Orientation orientation;
    private static final int HALF_ORIENTATION_THRESHOLD = 10;
    private static final int FULL_ORIENTATION_THRESHOLD = 20;

    public GameState(AppCompatActivity main_activity, GameView game_view){
        game_sensors = new GameSensors(main_activity, this);
        this.game_view = game_view;
    }

    void update_orientation(){
        // handle orientation
        boolean y_plus_z = game_sensors.get_orientation()[1] >= game_sensors.get_orientation()[2];
        boolean y_minus_z = -game_sensors.get_orientation()[1] <= game_sensors.get_orientation()[2];
        double maximum = Math.max(Math.abs(game_sensors.get_orientation()[1]), Math.abs(game_sensors.get_orientation()[2]));
        if(maximum <= HALF_ORIENTATION_THRESHOLD)
            orientation = Orientation.FACE_TOP;
        else if(maximum >= FULL_ORIENTATION_THRESHOLD){
            if(y_plus_z && y_minus_z) orientation = Orientation.FACE_UP;
            if(y_plus_z && !y_minus_z) orientation = Orientation.FACE_RIGHT;
            if(!y_plus_z && !y_minus_z) orientation = Orientation.FACE_DOWN;
            if(!y_plus_z && y_minus_z) orientation = Orientation.FACE_LEFT;
        }else{
            if(y_plus_z && y_minus_z) orientation = Orientation.FACE_UP_HALF;
            if(y_plus_z && !y_minus_z) orientation = Orientation.FACE_RIGHT_HALF;
            if(!y_plus_z && !y_minus_z) orientation = Orientation.FACE_DOWN_HALF;
            if(!y_plus_z && y_minus_z) orientation = Orientation.FACE_LEFT_HALF;
        }
    }

    void update_acceleration() {
        // handle acceleration
        if(
            acceleration != Acceleration.STATIONARY &&
            Math.abs(game_sensors.get_acceleration()[0]) < TRIGGER_ACCELERATION_THRESHOLD &&
            Math.abs(game_sensors.get_acceleration()[1]) < TRIGGER_ACCELERATION_THRESHOLD &&
            Math.abs(game_sensors.get_acceleration()[2]) < TRIGGER_ACCELERATION_THRESHOLD
        ) {
            acceleration = Acceleration.STATIONARY;
        } else if (acceleration_cooldown < 0){
            double max = Math.max(Math.abs(game_sensors.get_acceleration()[0]), Math.max(Math.abs(game_sensors.get_acceleration()[1]), Math.abs(game_sensors.get_acceleration()[2])));
            if(max == Math.abs(game_sensors.get_acceleration()[0])) {
                if (game_sensors.get_acceleration()[0] > TRIGGER_ACCELERATION_THRESHOLD) {
                    acceleration = Acceleration.RIGHT;
                    send_acceleration_event();
                }
                if (game_sensors.get_acceleration()[0] < -TRIGGER_ACCELERATION_THRESHOLD){
                    acceleration = Acceleration.LEFT;
                    send_acceleration_event();
                }
            }
            if(max == Math.abs(game_sensors.get_acceleration()[1])){
                if (game_sensors.get_acceleration()[1] > TRIGGER_ACCELERATION_THRESHOLD){
                    acceleration = Acceleration.FORWARD;
                    send_acceleration_event();
                }
                if (game_sensors.get_acceleration()[1] < -TRIGGER_ACCELERATION_THRESHOLD){
                    acceleration = Acceleration.BACKWARD;
                    send_acceleration_event();
                }
            }
            if(max == Math.abs(game_sensors.get_acceleration()[2])){
                if(game_sensors.get_acceleration()[2] > TRIGGER_ACCELERATION_THRESHOLD){
                    acceleration = Acceleration.UP;
                    send_acceleration_event();
                }
                if(game_sensors.get_acceleration()[2] < -TRIGGER_ACCELERATION_THRESHOLD){
                    acceleration = Acceleration.DOWN;
                    send_acceleration_event();
                }
            }
        }
    }

    void update_illumination() {
        // handle illumination
        if(game_sensors.get_illumination() <= LOW_LIGHT_THRESHOLD)
            light_level = LightLevel.LOW;
        else if(game_sensors.get_illumination() >= HIGH_LIGHT_THRESHOLD)
            light_level = LightLevel.HIGH;
        else
            light_level = LightLevel.MEDIUM;
    }

    void update_rotation() {
        // handle rotation
        if(
                rotation != Rotation.STATIC &&
                Math.abs(game_sensors.get_rotation()[0]) < TURN_ON_THRESHOLD &&
                Math.abs(game_sensors.get_rotation()[1]) < TURN_ON_THRESHOLD &&
                Math.abs(game_sensors.get_rotation()[2]) < TURN_ON_THRESHOLD
        )
            rotation = Rotation.STATIC;
        else if(turn_cooldown < 0){
            double max = Math.max(Math.abs(game_sensors.get_rotation()[0]), Math.max(Math.abs(game_sensors.get_rotation()[1]), Math.abs(game_sensors.get_rotation()[2])));
            if(max == Math.abs(game_sensors.get_rotation()[0])) {
                if (game_sensors.get_rotation()[0] > TURN_ON_THRESHOLD) {
                    rotation = Rotation.TURN_BACKWARD;
                    send_rotation_event();
                }
                if (game_sensors.get_rotation()[0] < -TURN_ON_THRESHOLD){
                    rotation = Rotation.TURN_FORWARD;
                    send_rotation_event();
                }
            }
            if(max == Math.abs(game_sensors.get_rotation()[1])){
                if (game_sensors.get_rotation()[1] > TURN_ON_THRESHOLD){
                    rotation = Rotation.TURN_RIGHT;
                    send_rotation_event();
                }
                if (game_sensors.get_rotation()[1] < -TURN_ON_THRESHOLD){
                    rotation = Rotation.TURN_LEFT;
                    send_rotation_event();
                }
            }
            if(max == Math.abs(game_sensors.get_rotation()[2])){
                if(game_sensors.get_rotation()[2] > TURN_ON_THRESHOLD){
                    rotation = Rotation.ROTATE_RIGHT;
                    send_rotation_event();
                }
                if(game_sensors.get_rotation()[2] < -TURN_ON_THRESHOLD){
                    rotation = Rotation.ROTATE_LEFT;
                    send_rotation_event();
                }
            }
        }
    }

    // rotation
    public enum Rotation{
        STATIC,
        TURN_FORWARD, TURN_BACKWARD,
        TURN_LEFT, TURN_RIGHT,
        ROTATE_RIGHT, ROTATE_LEFT
    }

    // light levels
    public enum LightLevel {
        LOW,
        MEDIUM,
        HIGH
    }
    public LightLevel get_light_level(){return light_level;}

    // acceleration
    public enum Acceleration {
        STATIONARY,
        RIGHT, LEFT,
        FORWARD, BACKWARD,
        UP, DOWN
    }

    // orientation: which way cans should be facing
    public enum Orientation {
        FACE_TOP(0),
        FACE_UP(1), FACE_UP_HALF(2),
        FACE_DOWN(3), FACE_DOWN_HALF(4),
        FACE_LEFT(5), FACE_LEFT_HALF(6),
        FACE_RIGHT(7), FACE_RIGHT_HALF(8);

        public int id;
        Orientation(int id){
            this.id = id;
        }
    }
    public Orientation get_orientation(){return orientation;}

    public void resume(){
        game_sensors.resume();
        game_sensors.recalibrate();
    }

    public void pause(){
        game_sensors.pause();
    }

    public void recalibrate(){
        game_sensors.recalibrate();
    }

    private void send_acceleration_event(){
        acceleration_cooldown = ACCELERATION_COOLDOWN;
        game_view.acceleration_event(acceleration);
    }

    private void send_rotation_event(){
        turn_cooldown = TURN_COOLDOWN;
        game_view.rotation_event(rotation);
    }
}
