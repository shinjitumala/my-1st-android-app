package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.R;

public class Door extends GameDrawableObject {
    public Door(int initial_x, int initial_y, int id) {
        super(initial_x, initial_y);
        this.id = id;
    }

    protected static final Drawable[] images = new Drawable[]{
            GameView.load_image(R.drawable.door_close),
            GameView.load_image(R.drawable.door_open)
    };

    // this door will open if all buttons with this id is pressed
    public final int id;
    public boolean open = false;

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        Rect rekt = get_rekt(game_state);
        Drawable image;
        if(open){
            passable = new boolean[] {true, true, true, true};
            image = images[1];
        } else {
            passable = new boolean[] {false, false, false, false};
            image = images[0];
        }
        image.setBounds(rekt);
        light_mask(image, game_state);
        image.draw(canvas);
        draw_num(this.id, canvas, game_state);
    }
}
