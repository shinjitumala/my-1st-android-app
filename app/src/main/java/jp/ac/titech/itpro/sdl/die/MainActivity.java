package jp.ac.titech.itpro.sdl.die;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.ac.titech.itpro.sdl.die.Game.GameView;

public class MainActivity extends AppCompatActivity {
    // surface view for the game
    private GameView game_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize drawing canvas
        game_view = new GameView(this);
        setContentView(game_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game_view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        game_view.pause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_recalibrate:
                game_view.recalibrate();
            break;
            case R.id.menu_reset:
                game_view.gv = null;
                game_view =  new GameView(this);
                setContentView(game_view);
            break;
        }
        return true;
    }
}
