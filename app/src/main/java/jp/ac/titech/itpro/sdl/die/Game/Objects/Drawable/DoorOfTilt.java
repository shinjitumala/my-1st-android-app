package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class DoorOfTilt extends GameDrawableObject {
    public DoorOfTilt(int initial_x, int initial_y) {
        super(initial_x, initial_y);
    }
    private static Drawable[] images = new Drawable[]{
            GameView.load_image(R.drawable.dot_top),
            GameView.load_image(R.drawable.dot_up),
            GameView.load_image(R.drawable.dot_up_half),
            GameView.load_image(R.drawable.dot_down),
            GameView.load_image(R.drawable.dot_down_half),
            GameView.load_image(R.drawable.dot_left),
            GameView.load_image(R.drawable.dot_left_half),
            GameView.load_image(R.drawable.dot_right),
            GameView.load_image(R.drawable.dot_right_half)
    };

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        switch(game_state.get_orientation()){
            case FACE_TOP:
                passable = new boolean[] {false, false, false, false};
                break;
            case FACE_RIGHT:
                passable = new boolean[] {true, false, false, false};
                break;
            case FACE_UP:
                passable = new boolean[] {false, true, false, false};
                break;
            case FACE_LEFT:
                passable = new boolean[] {false, false, true, false};
                break;
            case FACE_DOWN:
                passable = new boolean[] {false, false, false, true};
                break;
        }
        Drawable image = images[game_state.get_orientation().id];
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }
}
