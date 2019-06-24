package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import jp.ac.titech.itpro.sdl.die.GameState;
import jp.ac.titech.itpro.sdl.die.GameView;
import jp.ac.titech.itpro.sdl.die.R;

public class CubeOfRage extends GameDrawableObject {
    public CubeOfRage(int initial_x, int initial_y) {
        super(initial_x, initial_y);
        rage = true;

        // initialize the cube
        top.is_entrance = true;
        top.left = left; top.right = right; top.up = up; top.down = down;
        bottom.left = right; bottom.right = left; bottom.up = down; bottom.down = up;
        up.left = left; up.right = right; up.down = top; up.up = bottom;
        down.left = left; down.right = right; down.down = bottom; down.up = top;
        left.up = up; left.down = down; left.left = bottom; left.right = top;
        right.up = up; right.down = down; right.left = top; right.right = bottom;

        top_face = top;
    }
    private Face top    = new Face(GameView.load_image(R.drawable.cor_top)),
                bottom  = new Face(GameView.load_image(R.drawable.cor_bottom)),
                left    = new Face(GameView.load_image(R.drawable.cor_left)),
                right   = new Face(GameView.load_image(R.drawable.cor_right)),
                up      = new Face(GameView.load_image(R.drawable.cor_up)),
                down    = new Face(GameView.load_image(R.drawable.cor_down));

    // 立方体の面を表現している
    private class Face{
        public Face left, right, up, down;
        public boolean is_entrance;
        public Drawable image;

        public Face(Drawable image){this.image = image;}
    }

    private Face top_face;

    @Override
    public void draw(GameState game_state, Canvas canvas, Paint paint) {
        Drawable image = top_face.image;
        light_mask(image, game_state);
        image.setBounds(get_rekt(game_state));
        image.draw(canvas);
    }

    public void right() {top_face = top_face.left; update_pass();}
    public void left() {top_face = top_face.right; update_pass();}
    public void down() {top_face = top_face.up; update_pass();}
    public void up() {top_face = top_face.down; update_pass();}

    public void update_pass(){
        passable[0] = top_face.right.is_entrance;
        passable[1] = top_face.up.is_entrance;
        passable[2] = top_face.left.is_entrance;
        passable[3] = top_face.down.is_entrance;
    }
}
