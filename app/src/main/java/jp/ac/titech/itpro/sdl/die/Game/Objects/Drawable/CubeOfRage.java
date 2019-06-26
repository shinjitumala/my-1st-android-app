package jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import jp.ac.titech.itpro.sdl.die.Game.Systems.GameState;
import jp.ac.titech.itpro.sdl.die.Game.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class CubeOfRage extends GameDrawableObject {
    private static final Face top = new Face(GameView.load_image(R.drawable.cor_top), GameView.load_image(R.drawable.cor_top_night));
    private static final Face bottom = new Face(GameView.load_image(R.drawable.cor_bottom), null);
    private static final Face left = new Face(GameView.load_image(R.drawable.cor_left), GameView.load_image(R.drawable.cor_left_night));
    private static final Face right = new Face(GameView.load_image(R.drawable.cor_right), GameView.load_image(R.drawable.cor_right_night));
    private static final Face up = new Face(GameView.load_image(R.drawable.cor_up), GameView.load_image(R.drawable.cor_up_night));
    private static final Face down = new Face(GameView.load_image(R.drawable.cor_down), null);

    static {
        // initialize the cube
        top.is_entrance = true;
        top.left = left;        top.right = right;      top.up = up;        top.down = down;
        top.l = true;           top.r = true;           top.u = true;       top.d = true;

        bottom.left = right;    bottom.right = left;    bottom.up = down;   bottom.down = up;
        bottom.l = false;       bottom.r = false;       bottom.u = true;    bottom.d = true;

        up.left = left;         up.right = right;       up.down = top;      up.up = bottom;
        up.l = false;           up.r = false;           up.d = true;        up.u = true;

        down.left = left;       down.right = right;     down.down = bottom; down.up = top;
        down.l = false;         down.r = false;         down.d = true;      down.u = true;

        left.up = up;           left.down = down;       left.left = bottom; left.right = top;
        left.u = false;         left.d = false;         left.l = false;     left.r = true;

        right.up = up;          right.down = down;      right.left = top;   right.right = bottom;
        right.u = false;        right.d = false;        right.l = true;     right.r = false;
    }
    public CubeOfRage(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        rage = true;
        top_face = top;
    }

    // 立方体の面を表現している
    private static class Face{
        Face left, right, up, down;
        boolean l, r, u, d;
        boolean is_entrance;
        public final Drawable image, image_overlay;

        Face(Drawable image, Drawable image_overlay){
            this.image = image;
            this.image_overlay = image_overlay;
        }
    }

    private Face top_face;

    @Override
    public void draw(GameState game_state, Canvas canvas) {
        Drawable image = top_face.image;
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
        if(top_face.image_overlay != null && game_state.get_light_level() == GameState.LightLevel.LOW){
            top_face.image_overlay.setBounds(get_rekt(game_state));
            top_face.image_overlay.draw(canvas);
        }
    }

    public void right() {if(top_face.l) top_face = top_face.left; update_pass();}
    public void left() {if(top_face.r) top_face = top_face.right; update_pass();}
    public void down() {if(top_face.u) top_face = top_face.up; update_pass();}
    public void up() {if(top_face.d) top_face = top_face.down; update_pass();}

    private void update_pass(){
        passable[0] = top_face.right.is_entrance;
        passable[1] = top_face.up.is_entrance;
        passable[2] = top_face.left.is_entrance;
        passable[3] = top_face.down.is_entrance;
    }
}
