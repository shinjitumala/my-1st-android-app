package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;

import jp.ac.titech.itpro.sdl.die.GameState;
import jp.ac.titech.itpro.sdl.die.GameView;
import jp.ac.titech.itpro.sdl.die.R;

// main character class
public class You extends GameDrawableObject {
    // up, left, down, right
    public int direction;
    private static Drawable[] images = new Drawable[]{
            GameView.load_image(R.drawable.you_up),
            GameView.load_image(R.drawable.you_left),
            GameView.load_image(R.drawable.you_down),
            GameView.load_image(R.drawable.you_right)
    };

    public You(int initial_x, int initial_y){
        super(initial_x, initial_y);
        passable = new boolean[] {true, true, true, true};
        direction = 0; // up
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        Drawable image = images[direction];
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }
}
