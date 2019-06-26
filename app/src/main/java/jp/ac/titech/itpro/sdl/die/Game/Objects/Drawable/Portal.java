package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class Portal extends GameDrawableObject {
    public Portal(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        passable = new boolean[] {true, true, true, true};
    }

    private static final Drawable image = GameView.load_image(R.drawable.portal);
    public int level;
    private static final Paint paint = new Paint();
    private static int color_g = 255;
    private static boolean color_shift = true;
    private static final int COLOR_SHIFT_SPEED = 25;
    static {
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(125, 255, 255, 0);
    }

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);

        if(game_state.get_light_level() == GameState.LightLevel.LOW){
            if(color_g > 255 - COLOR_SHIFT_SPEED) color_shift = false;
            else if(color_g < 100 + COLOR_SHIFT_SPEED) color_shift = true;
            color_g = (color_shift) ? (color_g + COLOR_SHIFT_SPEED) : (color_g - COLOR_SHIFT_SPEED);
            paint.setARGB(125, 255, color_g, 0);
            paint.setMaskFilter(new BlurMaskFilter(game_state.BLOCK_SIZE * 0.5f, BlurMaskFilter.Blur.NORMAL));
            RectF rekt = new RectF(
                    get_draw_x(game_state) - game_state.BLOCK_SIZE * 0.5f,
                    get_draw_y(game_state) - game_state.BLOCK_SIZE * 0.5f,
                    get_draw_x(game_state) + game_state.BLOCK_SIZE * 1.5f,
                    get_draw_y(game_state) + game_state.BLOCK_SIZE * 1.5f
            );
            canvas.drawOval(rekt, paint);
        }
    }
}
