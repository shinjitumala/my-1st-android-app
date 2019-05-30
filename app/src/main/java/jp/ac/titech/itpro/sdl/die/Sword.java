package jp.ac.titech.itpro.sdl.die;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

public class Sword implements Obj {

    private final static float[] VERTICES = {
        // point 1
         0.0f,   1.0f,   0.0f,
         0.1f,   0.7f,   0.0f,
         0.0f,   0.7f,   0.2f,
        // point 2
         0.0f,   1.0f,   0.0f,
         0.0f,   0.7f,   0.2f,
        -0.1f,   0.7f,   0.0f,
        // point 3
         0.0f,   1.0f,   0.0f,
        -0.1f,   0.7f,   0.0f,
         0.0f,   0.7f,  -0.2f,
        // point 4
         0.0f,   1.0f,   0.0f,
         0.0f,   0.7f,  -0.2f,
         0.1f,   0.7f,   0.0f,

        // edge 1
         0.1f,   0.7f,   0.0f,
         0.0f,   0.7f,   0.2f,
         0.05f,  0.0f,   0.0f,
         0.0f,   0.0f,   0.1f,
        // edge 2
         0.0f,   0.7f,   0.2f,
        -0.1f,   0.7f,   0.0f,
         0.0f,   0.0f,   0.1f,
        -0.05f,  0.0f,   0.0f,
        // edge 3
        -0.1f,   0.7f,   0.0f,
         0.0f,   0.7f,  -0.2f,
        -0.05f,  0.0f,   0.0f,
         0.0f,   0.0f,  -0.1f,
        // edge 4
         0.0f,   0.7f,  -0.2f,
         0.1f,   0.7f,   0.0f,
         0.0f,   0.0f,  -0.1f,
         0.05f,  0.0f,   0.0f,

        // guard top
         0.0f,   0.0f,  -0.4f,
         0.2f,   0.0f,   0.0f,
        -0.2f,   0.0f,   0.0f,
         0.0f,   0.0f,   0.4f,
        // guard bottom
         0.0f,  -0.1f,  -0.4f,
         0.2f,  -0.1f,   0.0f,
        -0.2f,  -0.1f,   0.0f,
         0.0f,  -0.1f,   0.4f,
        // guard 1
         0.0f,   0.0f,  -0.4f,
         0.0f,  -0.1f,  -0.4f,
         0.2f,   0.0f,   0.0f,
         0.2f,  -0.1f,   0.0f,
        // guard 2
         0.0f,   0.0f,  -0.4f,
         0.0f,  -0.1f,  -0.4f,
        -0.2f,   0.0f,   0.0f,
        -0.2f,  -0.1f,   0.0f,
        // guard 3
         0.0f,   0.0f,   0.4f,
         0.0f,  -0.1f,   0.4f,
        -0.2f,   0.0f,   0.0f,
        -0.2f,  -0.1f,   0.0f,
        // guard 4
         0.0f,   0.0f,   0.4f,
         0.0f,  -0.1f,   0.4f,
         0.2f,   0.0f,   0.0f,
         0.2f,  -0.1f,   0.0f,

        // grip 1
         0.05f, -0.1f,   0.0f,
         0.0f,  -0.1f,   0.05f,
         0.05f, -0.4f,   0.0f,
         0.0f,  -0.4f,   0.05f,
        // grip 2
         0.05f, -0.1f,   0.0f,
         0.0f,  -0.1f,  -0.05f,
         0.05f, -0.4f,   0.0f,
         0.0f,  -0.4f,  -0.05f,
        // grip 3
        -0.05f, -0.1f,   0.0f,
         0.0f,  -0.1f,  -0.05f,
        -0.05f, -0.4f,   0.0f,
         0.0f,  -0.4f,  -0.05f,
        // grip 4
        -0.05f, -0.1f,   0.0f,
         0.0f,  -0.1f,   0.05f,
        -0.05f, -0.4f,   0.0f,
         0.0f,  -0.4f,   0.05f,

        // pommel top
        -0.1f,  -0.4f,   0.0f,
         0.0f,  -0.4f,  -0.1f,
         0.1f,  -0.4f,   0.0f,
         0.0f,  -0.4f,   0.1f,
        // pommel 1
        -0.1f,  -0.4f,   0.0f,
         0.0f,  -0.4f,  -0.1f,
         0.0f,  -0.5f,   0.0f,
        // pommel 2
         0.0f,  -0.5f,   0.0f,
         0.0f,  -0.4f,  -0.1f,
         0.1f,  -0.4f,   0.0f,
        // pommel 3
         0.0f,  -0.5f,   0.0f,
         0.1f,  -0.4f,   0.0f,
         0.0f,  -0.4f,   0.1f,
        // pommel 4
         0.0f,  -0.5f,   0.0f,
         0.0f,  -0.4f,   0.1f,
        -0.1f,  -0.4f,   0.0f,
    };

    private final FloatBuffer vbuf;

    Sword() {
        vbuf = ByteBuffer
                .allocateDirect(VERTICES.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vbuf.put(VERTICES);
        vbuf.position(0);
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vbuf);

        // point
        gl.glNormal3f(2, 0.3f, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glNormal3f(-2, 0.3f, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 3, 3);
        gl.glNormal3f(-2, 0.3f, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 6, 3);
        gl.glNormal3f(2, 0.3f, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 9, 3);

        // edge
        gl.glNormal3f(2, -0.1f, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
        gl.glNormal3f(-2, -0.1f, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        gl.glNormal3f(-2, -0.1f, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
        gl.glNormal3f(2, -0.1f, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 24, 4);

        // guard
        gl.glNormal3f(0, 1,0);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 28, 4);
        gl.glNormal3f(0,-1,0);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 32, 4);
        gl.glNormal3f(2, 0, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 36, 4);
        gl.glNormal3f(-2, 0, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 40, 4);
        gl.glNormal3f(-2, 0, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 44, 4);
        gl.glNormal3f(2, 0, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 48, 4);

        // grip
        gl.glNormal3f(1, 0, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 52, 4);
        gl.glNormal3f(1, 0, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 56, 4);
        gl.glNormal3f(-1, 0, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 60, 4);
        gl.glNormal3f(-1, 0, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 64, 4);

        // pommel
        gl.glNormal3f(0, 1, 0);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 68, 4);
        gl.glNormal3f(-1, -1, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 72, 3);
        gl.glNormal3f(1, -1, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 75, 3);
        gl.glNormal3f(1, -1, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 78, 3);
        gl.glNormal3f(-1, -1, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 81, 3);
    }
}
