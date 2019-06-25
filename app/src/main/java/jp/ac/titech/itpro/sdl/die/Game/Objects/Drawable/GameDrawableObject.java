package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;

public abstract class GameDrawableObject {
    // position on the map
    public final int[] position;

    // attributes
    boolean[] passable; // right, up, left, down
    boolean graviton; // true if affected by accelerometer.
    boolean rage; // true if cube penis shit. fuck u

    public GameDrawableObject(int initial_x, int initial_y){
        position = new int[] {initial_x, initial_y};
        passable = new boolean[] {false, false, false, false};
        graviton = false;
        rage = false;
    }

    // called when the object is being drawn
    public abstract void draw(GameState game_state, Canvas canvas);

    int get_draw_x(GameState game_state){
        return game_state.X_START + game_state.BLOCK_SIZE * position[0];
    }
    int get_draw_y(GameState game_state){
        return game_state.Y_START + game_state.BLOCK_SIZE * position[1];
    }

    void light_mask(Drawable image, GameState game_state){
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

    Rect get_rekt(GameState game_state){
        return new Rect(
                get_draw_x(game_state),
                get_draw_y(game_state),
                get_draw_x(game_state) + game_state.BLOCK_SIZE,
                get_draw_y(game_state) + game_state.BLOCK_SIZE
        );
    }

    public boolean[] passable() {
        return passable;
    }
}
