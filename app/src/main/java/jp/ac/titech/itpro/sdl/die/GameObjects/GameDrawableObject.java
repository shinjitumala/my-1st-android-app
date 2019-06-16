package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;

import jp.ac.titech.itpro.sdl.die.GameState;

public abstract class GameDrawableObject {
    // position on the map
    public int[] position;
    public boolean passable;

    public GameDrawableObject(int initial_x, int initial_y){
        position = new int[] {initial_x, initial_y};
    }

    // can be drawn in both day and night form
    public abstract void draw(GameState game_state, Canvas cavnas, Paint paint);

    protected int get_draw_x(GameState game_state){
        return game_state.X_START + game_state.BLOCK_SIZE * position[0];
    }
    protected int get_draw_y(GameState game_state){
        return game_state.Y_START + game_state.BLOCK_SIZE * position[1];
    }
}
