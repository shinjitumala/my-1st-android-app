package jp.ac.titech.itpro.sdl.die;

import android.support.v7.app.AppCompatActivity;

public class GameState {
    private static String TAG = GameState.class.getSimpleName();

    // parameters used for drawing
    public int BLOCK_SIZE, X_START, Y_START;

    private GameSensors game_sensors;

    private Rotation rotation;
    private static final float TURN_ON_THRESHOLD = 2.5f;
    private static final float TURN_OFF_THRESHOLD = 1.0f;

    private LightLevel light_level;
    private static final int LOW_LIGHT_THRESHOLD = 40;
    private static final int HIGH_LIGHT_THRESHOLD = 150;

    private Acceleration acceleration;
    private static final float TRIGGER_ACCELERATION_THRESHOLD = 2.5f;
    private static final float UNTRIGGER_ACCELERATION_THRESHOLD = 1.0f;

    private Orientation orientation;
    private static final int HALF_ORIENTATION_THRESHOLD = 10;
    private static final int FULL_ORIENTATION_THRESHOLD = 20;

    public GameState(AppCompatActivity main_activity){
        game_sensors = new GameSensors(main_activity, this);
    }

    public void update(){
        // handle rotation
//        Rotation tmp3 = rotation;
        if(
                rotation != Rotation.STATIC &&
                Math.abs(game_sensors.rotation[0]) < TURN_OFF_THRESHOLD &&
                Math.abs(game_sensors.rotation[1]) < TURN_OFF_THRESHOLD &&
                Math.abs(game_sensors.rotation[2]) < TURN_OFF_THRESHOLD
        )
            rotation = Rotation.STATIC;
        else {
            double max = Math.max(Math.abs(game_sensors.rotation[0]), Math.max(Math.abs(game_sensors.rotation[1]), Math.abs(game_sensors.rotation[2])));
            if(max == Math.abs(game_sensors.rotation[0])) {
                if (game_sensors.rotation[0] > TURN_ON_THRESHOLD)
                    rotation = Rotation.TURN_BACKWARD;
                if (game_sensors.rotation[0] < -TURN_ON_THRESHOLD)
                    rotation = Rotation.TURN_FORWARD;
            }
            if(max == Math.abs(game_sensors.rotation[1])){
                if (game_sensors.rotation[1] > TURN_ON_THRESHOLD)
                    rotation = Rotation.TURN_RIGHT;
                if (game_sensors.rotation[1] < -TURN_ON_THRESHOLD)
                    rotation = Rotation.TURN_LEFT;
            }
            if(max == Math.abs(game_sensors.rotation[2])){
                if(game_sensors.rotation[2] > TURN_ON_THRESHOLD)
                    rotation = Rotation.ROTATE_RIGHT;
                if(game_sensors.rotation[2] < -TURN_ON_THRESHOLD)
                    rotation = Rotation.ROTATE_LEFT;
            }
        }
//        if(tmp3 != rotation)
//            Log.d(TAG, "rotation = " + rotation.name());
        
        // handle illumination
//        LightLevel tmp0 = light_level;
        if(game_sensors.illumination <= LOW_LIGHT_THRESHOLD)
            light_level = LightLevel.LOW;
        else if(game_sensors.illumination >= HIGH_LIGHT_THRESHOLD)
            light_level = LightLevel.HIGH;
        else
            light_level = LightLevel.MEDIUM;
//        if(tmp0 != light_level)
//            Log.d(TAG, "light_level = " + light_level.name());

        // handle acceleration
//        Acceleration tmp2 = acceleration;
        if(
                acceleration != Acceleration.STATIONARY &&
                Math.abs(game_sensors.acceleration[0]) < UNTRIGGER_ACCELERATION_THRESHOLD &&
                Math.abs(game_sensors.acceleration[1]) < UNTRIGGER_ACCELERATION_THRESHOLD &&
                Math.abs(game_sensors.acceleration[2]) < UNTRIGGER_ACCELERATION_THRESHOLD
        )
            acceleration = Acceleration.STATIONARY;
        else {
            double max = Math.max(Math.abs(game_sensors.acceleration[0]), Math.max(Math.abs(game_sensors.acceleration[1]), Math.abs(game_sensors.acceleration[2])));
            if(max == Math.abs(game_sensors.acceleration[0])) {
                if (game_sensors.acceleration[0] > TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.RIGHT;
                if (game_sensors.acceleration[0] < -TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.LEFT;
            }
            if(max == Math.abs(game_sensors.acceleration[1])){
                if (game_sensors.acceleration[1] > TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.FORWARD;
                if (game_sensors.acceleration[1] < -TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.BACKWARD;
            }
            if(max == Math.abs(game_sensors.acceleration[2])){
                if(game_sensors.acceleration[2] > TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.UP;
                if(game_sensors.acceleration[2] < -TRIGGER_ACCELERATION_THRESHOLD)
                    acceleration = Acceleration.DOWN;
            }
        }
//        if(tmp2 != acceleration)
//            Log.d(TAG, "acceleration = " + acceleration.name());

        // handle orientation
//        Orientation tmp1 = orientation;
        boolean y_plus_z = game_sensors.orientation[1] >= game_sensors.orientation[2];
        boolean y_minus_z = -game_sensors.orientation[1] <= game_sensors.orientation[2];
        double maximum = Math.max(Math.abs(game_sensors.orientation[1]), Math.abs(game_sensors.orientation[2]));
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
//        if(tmp1 != orientation)
//            Log.d(TAG, "orientation = " + orientation.name());
    }

    // rotation
    public enum Rotation{
        STATIC,
        TURN_FORWARD, TURN_BACKWARD,
        TURN_LEFT, TURN_RIGHT,
        ROTATE_RIGHT, ROTATE_LEFT
    }
    public Rotation get_rotation(){return rotation;}

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
    public Acceleration get_acceleration(){return acceleration;}

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
}
