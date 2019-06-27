package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.R;

public class Button extends GameDrawableObject {
    public Button(int initial_x, int initial_y, int id) {
        super(initial_x, initial_y);
        this.id = id;
        passable = new boolean[] {true, true, true, true};
    }

    protected static final Drawable[] images = new Drawable[]{
            GameView.load_image(R.drawable.button_unpressed),
            GameView.load_image(R.drawable.button_pressed)
    };

    // will open door with same id, if and only if all buttons with the id are pushed
    public final int id;
    public boolean pressed = false;

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        Rect rekt = get_rekt(game_state);
        Drawable image;
        if(pressed){
            image = images[1];
        } else {
            image = images[0];
        }
        image.setBounds(rekt);
        light_mask(image, game_state);
        image.draw(canvas);
        draw_num(this.id, canvas, game_state);
    }
}
