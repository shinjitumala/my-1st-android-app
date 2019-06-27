package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;

public class CubeOfRageNot extends CubeOfRage {
    public CubeOfRageNot(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        top_face = top;
    }

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        Drawable image = top_face.image;
        invert(image);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
        light_mask(canvas, game_state);
        if(top_face.image_overlay != null && game_state.get_light_level() == GameState.LightLevel.LOW){
            top_face.image_overlay.setBounds(get_rekt(game_state));
            invert(top_face.image_overlay);
            top_face.image_overlay.draw(canvas);
        }
    }

    public void right() {if(top_face.r) top_face = top_face.right; update_pass();}
    public void left() {if(top_face.l) top_face = top_face.left; update_pass();}
    public void down() {if(top_face.d) top_face = top_face.down; update_pass();}
    public void up() {if(top_face.u) top_face = top_face.up; update_pass();}
}
