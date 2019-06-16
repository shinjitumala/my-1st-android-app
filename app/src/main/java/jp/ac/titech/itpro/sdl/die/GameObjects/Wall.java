package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import jp.ac.titech.itpro.sdl.die.GameState;

public class Wall extends GameDrawableObject {
    public Wall(int initial_x, int initial_y){
        super(initial_x, initial_y);
        passable = false;
    }

    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        switch(game_state.get_light_level()){
            case LOW:
                paint.setColor(Color.BLACK);
            break;
            case MEDIUM:
                paint.setColor(Color.DKGRAY);
            break;
            case HIGH:
                paint.setColor(Color.GRAY);
            break;
        }
        Rect tmp = new Rect(
                get_draw_x(game_state),
                get_draw_y(game_state),
                get_draw_x(game_state) + game_state.BLOCK_SIZE,
                get_draw_y(game_state) + game_state.BLOCK_SIZE
        );
        canvas.drawRect(tmp, paint);
    }
}
