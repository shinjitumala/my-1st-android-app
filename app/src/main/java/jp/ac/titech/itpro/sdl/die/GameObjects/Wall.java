package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.GameState;
import jp.ac.titech.itpro.sdl.die.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class Wall extends GameDrawableObject {
    public Wall(int initial_x, int initial_y){
        super(initial_x, initial_y);
    }

    private static Drawable image = GameView.load_image(R.drawable.wall);

    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }
}
