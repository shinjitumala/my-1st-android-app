package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import jp.ac.titech.itpro.sdl.die.GameState;

// main character class
public class You extends GameDrawableObject {

    public You(int initial_x, int initial_y){
        super(initial_x, initial_y);
        passable = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        if(game_state.get_light_level() == GameState.LightLevel.HIGH){
            paint.setColor(Color.BLUE);
            canvas.drawOval(get_draw_x(game_state), get_draw_y(game_state), get_draw_x(game_state) + game_state.BLOCK_SIZE, get_draw_y(game_state) + game_state.BLOCK_SIZE, paint);
        }
    }
}
