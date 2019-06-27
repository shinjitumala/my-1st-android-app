package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;

public class GravipigsNot extends Gravipigs {
    public GravipigsNot(int initial_x, int initial_y) {
        super(initial_x, initial_y);
    }

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        image.setBounds(get_rekt(game_state));
        invert(image);
        image.draw(canvas);
        light_mask(canvas, game_state);
        if(game_state.get_light_level() == GameState.LightLevel.LOW){
            image_overlay.setBounds(get_rekt(game_state));
            invert(image_overlay);
            image_overlay.draw(canvas);
        }
    }
}
