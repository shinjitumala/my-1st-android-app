package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.GameState;
import jp.ac.titech.itpro.sdl.die.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class Portal extends GameDrawableObject {
    public Portal(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        passable = new boolean[] {true, true, true, true};
    }

    private static Drawable image = GameView.load_image(R.drawable.portal);
    public int level;

    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }
}