package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

// main character class
public class You extends GameDrawableObject {
    // up, left, down, right
    public int direction;
    private static final Drawable[] images = new Drawable[]{
            GameView.load_image(R.drawable.you_up),
            GameView.load_image(R.drawable.you_left),
            GameView.load_image(R.drawable.you_down),
            GameView.load_image(R.drawable.you_right)
    };
    private static final Drawable[] images_torch = new Drawable[]{
            GameView.load_image(R.drawable.torch_up),
            GameView.load_image(R.drawable.torch_left),
            GameView.load_image(R.drawable.torch_down),
            GameView.load_image(R.drawable.torch_right)
    };

    public You(int initial_x, int initial_y){
        super(initial_x, initial_y);
        direction = 0; // up
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(GameState game_state, Canvas canvas) {
        // draw main character
        Drawable image = images[direction];
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);

        // draw torch
        if(game_state.get_light_level() == GameState.LightLevel.LOW){
            Drawable overlay = images_torch[direction];
            Rect rekt = new Rect(
                    get_draw_x(game_state) - game_state.BLOCK_SIZE,
                    get_draw_y(game_state) - game_state.BLOCK_SIZE,
                    get_draw_x(game_state) + 2 * game_state.BLOCK_SIZE,
                    get_draw_y(game_state) + 2 * game_state.BLOCK_SIZE);
            overlay.setBounds(rekt);
            overlay.draw(canvas);
        }

        // draw serif
        if(serif_life > 0) {
            if(serif_life == SERIF_FADE_FRAMES){
                serif_fade = SERIF_FADE_FRAMES;
            }else if(serif_life < SERIF_FADE_FRAMES){
                serif_fade--;
            }else if(serif_fade < SERIF_FADE_FRAMES){
                serif_fade++;
            }

            float i = (float) serif_fade / SERIF_FADE_FRAMES;
            paint_fill.setAlpha(Math.round(SERIF_OPACITY * i));
            paint_outline.setAlpha(Math.round(255 * i));
            paint_serif.setAlpha(Math.round(255 * i));

            float serif_length = paint_serif.measureText(serif);
            float offset = (float) game_state.BLOCK_SIZE / SERIF_Y_OFFSET_FACTOR;
            Path path = new Path();
            float x = get_draw_x(game_state) + (float) game_state.BLOCK_SIZE / 2;
            float y = get_draw_y(game_state) - offset;
            path.moveTo(x, y);
            path.rLineTo(offset, -offset);
            path.rLineTo(serif_length / 2 - offset + SERIF_MARGIN, 0);
            path.rLineTo(0, -(SERIF_MARGIN * 2 + SERIF_TEXT_SIZE));
            path.rLineTo(-(serif_length + SERIF_MARGIN * 2), 0);
            path.rLineTo(0, SERIF_MARGIN * 2 + SERIF_TEXT_SIZE);
            path.rLineTo(serif_length / 2 - offset + SERIF_MARGIN, 0);
            path.rLineTo(offset, offset);
            path.rLineTo(offset, -offset);
            canvas.drawPath(path, paint_fill);
            canvas.drawPath(path, paint_outline);
            canvas.drawText(serif, x - serif_length / 2, y - offset - SERIF_MARGIN, paint_serif);
            serif_life--;
        }
    }

    // code for talking
    private static Paint paint_outline = new Paint(), paint_fill = new Paint(), paint_serif = new Paint();

    private final static float SERIF_Y_OFFSET_FACTOR = 10;
    private final static float SERIF_MARGIN = 16;
    private final static float SERIF_TEXT_SIZE = 32;
    private final static int SERIF_FADE_FRAMES = 3;
    private final static float SERIF_OPACITY = 80;
    private String serif = "うんこぶりぶり";
    private int serif_life = 0;
    private int serif_fade = 0;
    public void talk(String s, float duration_seconds){
        serif = s;
        serif_life = Math.round(duration_seconds * 10);
        serif_fade = 0;
    }

    static {
        // initialize paint brushes
        paint_outline.setColor(Color.BLACK);
        paint_outline.setStrokeWidth(16f);
        paint_outline.setStyle(Paint.Style.STROKE);
        paint_outline.setAntiAlias(true);

        paint_fill.setColor(Color.WHITE);
        paint_fill.setStyle(Paint.Style.FILL);

        paint_serif.setTextSize(SERIF_TEXT_SIZE);
        paint_serif.setAntiAlias(true);
    }
}
