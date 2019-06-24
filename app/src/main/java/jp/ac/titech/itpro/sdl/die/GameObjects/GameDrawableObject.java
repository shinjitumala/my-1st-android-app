package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.GameState;

public abstract class GameDrawableObject {
    // position on the map
    public int[] position;

    // attributes
    public boolean[] passable; // right, up, left, down
    public boolean graviton; // true if affected by accelerometer.
    public boolean rage; // true if cube penis shit. fuck u

    public GameDrawableObject(int initial_x, int initial_y){
        position = new int[] {initial_x, initial_y};
        passable = new boolean[] {false, false, false, false};
        graviton = false;
        rage = false;
    }

    // can be drawn in both day and night form
    public abstract void draw(GameState game_state, Canvas canvas, Paint paint);

    protected int get_draw_x(GameState game_state){
        return game_state.X_START + game_state.BLOCK_SIZE * position[0];
    }
    protected int get_draw_y(GameState game_state){
        return game_state.Y_START + game_state.BLOCK_SIZE * position[1];
    }

    protected void light_mask(Drawable image, GameState game_state){
        switch(game_state.get_light_level()){
            case LOW:
                image.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                break;
            case MEDIUM:
                image.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                break;
            case HIGH:
                image.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    protected Rect get_rekt(GameState game_state){
        return new Rect(
                get_draw_x(game_state),
                get_draw_y(game_state),
                get_draw_x(game_state) + game_state.BLOCK_SIZE,
                get_draw_y(game_state) + game_state.BLOCK_SIZE
        );
    }

    protected Drawable tilt_select(Drawable[] images, GameState game_state){
        switch(game_state.get_orientation()){
            case FACE_TOP: return images[0];
            case FACE_UP: return images[1];
            case FACE_LEFT: return images[2];
            case FACE_DOWN: return images[3];
            case FACE_RIGHT: return images[4];
            case FACE_UP_HALF: return images[5];
            case FACE_LEFT_HALF: return images[6];
            case FACE_DOWN_HALF: return images[7];
            case FACE_RIGHT_HALF: return images[8];
            default: return null;
        }
    }
}
