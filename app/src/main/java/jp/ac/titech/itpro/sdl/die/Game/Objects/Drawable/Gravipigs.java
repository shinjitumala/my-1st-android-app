package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class Gravipigs extends GameDrawableObject {
    public Gravipigs(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        graviton = true;
    }

    private static final Drawable image = GameView.load_image(R.drawable.gravpig);
    private static final Drawable image_overlay = GameView.load_image(R.drawable.gravpig_night);

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
        if(game_state.get_light_level() == GameState.LightLevel.LOW){
            image_overlay.setBounds(get_rekt(game_state));
            image_overlay.draw(canvas);
        }
    }
}
