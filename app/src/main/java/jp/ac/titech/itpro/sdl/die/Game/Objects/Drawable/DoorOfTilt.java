package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class DoorOfTilt extends GameDrawableObject {
    public DoorOfTilt(int initial_x, int initial_y) {
        super(initial_x, initial_y);
    }
    private static final Drawable[] images = new Drawable[]{
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

    private static final Paint paint = new Paint();
    private static int color = 255;
    private static boolean color_shift = true;
    private static final int COLOR_SHIFT_SPEED = 50;
    static {
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        Drawable image = images[game_state.get_orientation().id];
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);

        if(game_state.get_light_level() == GameState.LightLevel.LOW){
            if(color > 255 - COLOR_SHIFT_SPEED) color_shift = false;
            else if(color < COLOR_SHIFT_SPEED) color_shift = true;
            color = (color_shift) ? color + COLOR_SHIFT_SPEED : color - COLOR_SHIFT_SPEED;
            paint.setARGB(125, color, color, 255);
            paint.setMaskFilter(new BlurMaskFilter(game_state.BLOCK_SIZE * 0.2f, BlurMaskFilter.Blur.NORMAL));
        }

        float x_offset, y_offset, x_size, y_size;
        switch(game_state.get_orientation()){
            case FACE_TOP:
                passable = new boolean[] {false, false, false, false};
                x_offset = 0; y_offset = 0; x_size = game_state.BLOCK_SIZE; y_size = game_state.BLOCK_SIZE;
                break;
            case FACE_RIGHT:
                passable = new boolean[] {true, false, false, false};
                x_offset = game_state.BLOCK_SIZE * 0.5f; y_offset = 0; x_size = game_state.BLOCK_SIZE * 0.3f; y_size = game_state.BLOCK_SIZE;
                break;
            case FACE_UP:
                passable = new boolean[] {false, true, false, false};
                x_offset = 0; y_offset = -game_state.BLOCK_SIZE * 0.5f; x_size = game_state.BLOCK_SIZE; y_size = game_state.BLOCK_SIZE * 0.3f;
                break;
            case FACE_LEFT:
                passable = new boolean[] {false, false, true, false};
                x_offset = -game_state.BLOCK_SIZE * 0.5f; y_offset = 0; x_size = game_state.BLOCK_SIZE * 0.3f; y_size = game_state.BLOCK_SIZE;
                break;
            case FACE_DOWN:
                passable = new boolean[] {false, false, false, true};
                x_offset = 0; y_offset = game_state.BLOCK_SIZE * 0.5f; x_size = game_state.BLOCK_SIZE; y_size = game_state.BLOCK_SIZE * 0.3f;
                break;
            case FACE_RIGHT_HALF:
                x_offset = game_state.BLOCK_SIZE * 0.3f; y_offset = 0; x_size = game_state.BLOCK_SIZE * 0.5f; y_size = game_state.BLOCK_SIZE;
                break;
            case FACE_UP_HALF:
                x_offset = 0; y_offset = -game_state.BLOCK_SIZE * 0.3f; x_size = game_state.BLOCK_SIZE; y_size = game_state.BLOCK_SIZE * 0.5f;
                break;
            case FACE_LEFT_HALF:
                x_offset = -game_state.BLOCK_SIZE * 0.3f; y_offset = 0; x_size = game_state.BLOCK_SIZE * 0.5f; y_size = game_state.BLOCK_SIZE;
                break;
            case FACE_DOWN_HALF:
                x_offset = 0; y_offset = game_state.BLOCK_SIZE * 0.3f; x_size = game_state.BLOCK_SIZE; y_size = game_state.BLOCK_SIZE * 0.5f;
                break;
            default:
                x_offset = 0; y_offset = 0; x_size = 0; y_size = 0;
                break;
        }
        if(game_state.get_light_level() == GameState.LightLevel.LOW) {
            RectF rekt = new RectF(
                    get_draw_x(game_state) + game_state.BLOCK_SIZE * 0.5f + x_offset - x_size / 2,
                    get_draw_y(game_state) + game_state.BLOCK_SIZE * 0.5f + y_offset - y_size / 2,
                    get_draw_x(game_state) + game_state.BLOCK_SIZE * 0.5f + x_offset + x_size / 2,
                    get_draw_y(game_state) + game_state.BLOCK_SIZE * 0.5f + y_offset + y_size / 2
            );
            canvas.drawOval(rekt, paint);
        }
    }
}
