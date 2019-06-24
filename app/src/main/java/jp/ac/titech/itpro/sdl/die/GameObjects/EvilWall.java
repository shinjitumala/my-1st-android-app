package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.GameState;
import jp.ac.titech.itpro.sdl.die.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class EvilWall extends GameDrawableObject {
    public EvilWall(int initial_x, int initial_y) {
        super(initial_x, initial_y);
    }

    private static Drawable image = GameView.load_image(R.drawable.wall);

    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        switch(game_state.get_light_level()){
            case LOW:
                passable = new boolean [] {true, true, true, true};
                break;
            case MEDIUM:
                image.setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);
                image.setBounds(get_rekt(game_state));
                image.draw(canvas);
                passable = new boolean [] {false, false, false, false};
                break;
            case HIGH:
                image.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                image.setBounds(get_rekt(game_state));
                image.draw(canvas);
                passable = new boolean [] {false, false, false, false};
                break;
        }
    }
}
