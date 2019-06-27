package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;

public class EvilWallNot extends EvilWall {
    public EvilWallNot(int initial_x, int initial_y) {
        super(initial_x, initial_y);
    }

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        switch(game_state.get_light_level()){
            case HIGH:
                passable = new boolean [] {true, true, true, true};
                break;
            case MEDIUM:
                image.setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);
                image.setBounds(get_rekt(game_state));
                image.draw(canvas);
                passable = new boolean [] {false, false, false, false};
                break;
            case LOW:
                image.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                image.setBounds(get_rekt(game_state));
                image.draw(canvas);
                passable = new boolean [] {false, false, false, false};
                break;
        }
    }
}
