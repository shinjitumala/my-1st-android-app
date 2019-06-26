package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class Tiles extends GameDrawableObject {
    public Tiles(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        passable = new boolean[] {true, true, true, true};
    }

    private static final Drawable image = GameView.load_image(R.drawable.tile);

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }
}
