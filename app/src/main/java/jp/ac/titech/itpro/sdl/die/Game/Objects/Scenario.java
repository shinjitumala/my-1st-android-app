package jp.ac.titech.itpro.sdl.die.Game.Objects;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import jp.ac.titech.itpro.sdl.die.Game.Objects.Drawable.You;
import jp.ac.titech.itpro.sdl.die.Game.Systems.GameSoundEngine;

public class Scenario {
    private static final String TAG = Scenario.class.getSimpleName();
    private You you;
    private GameMap.Level level;
    private final GameSoundEngine game_sound_engine;
    private final Random random;
    private Thread thread;
    private Method trigger;

    // things to say when tapped
    private static final String[] tap = new String[] {
        "ポチッとな",
        "ヤッホー",
        "こんにちは！",
        "こんばんは！",
        "おはよう！",
        "権藤研ゼミ長くね？",
        "www.youtube.com/channel/UCwOW6jiu1szkwNfg7z_kFSA",
        "www.twitch.tv/funny_pig_run"
    };

    // level triggers
    private class trigger_thread implements Runnable{
        private final GameMap.Level level;
        private final Scenario scenario;

        trigger_thread(GameMap.Level level, Scenario scenario){
            this.level = level;
            this.scenario = scenario;
        }

        public void run(){
            try {
                initial_delay(level);
                trigger.invoke(scenario, level);
            }
            catch (InterruptedException ignore){}
            catch (InvocationTargetException ignore){}
            catch (IllegalAccessException e){
                Log.e(TAG, e.toString());
            }

        }
    }

    @SuppressWarnings("unused")
    public void level_ONE(GameMap.Level level) throws InterruptedException {
        say("...", 2, 2, level);
        say("ここは、どこだ？", 2, 2, level);
        say("昨日は酒を飲みすぎたな。", 3, 2, level);
        say("ヒント：スワイプで歩けるよ！", 4, 1, level);
        say("ヒント:ゴールは左上のポータルだよ！", 4, 0, level);
    }

    @SuppressWarnings("unused")
    public void level_TWO(GameMap.Level level) throws InterruptedException {
        say("なんかこの壁怪しいな...", 2, 2, level);
        say("夜になるまで待ってみるか...", 4, 2, level);
        say("ヒント：部屋を暗くすると、ゲームも夜になるよ！", 4, 0, level);
    }

    @SuppressWarnings("unused")
    public void level_THREE(GameMap.Level level) throws InterruptedException {
        say("おい！", 1, 0.3f, level);
        say("そこの豚！", 1.5f, 0.3f, level);
        say("邪魔だ！", 1.0f, 3, level);
        say("無視か？", 1.5f, 2, level);
        say("力づくで退かすしかないな", 2.5f, 2, level);
        say("ヒント：デバイスを振るとその方向に豚が動くよ！", 4, 0, level);
    }

    @SuppressWarnings("unused")
    public void level_FOUR(GameMap.Level level) throws InterruptedException {
        say("なんか、地面が傾くな", 2, 0.5f, level);
        say("いや、二日酔いじゃない", 2, 1, level);
        say("とりあえず、進み続けるか...", 2, 1, level);
        say("このポータル、入れそうだな", 2, 2, level);
        say("ヒント:デバイスを傾けると、ポータルを傾けられるよ！", 4, 0, level);
    }

    @SuppressWarnings("unused")
    public void level_FIVE(GameMap.Level level) throws InterruptedException {
        say("なんか邪魔なところに家を建てるな", 3, 0.5f, level);
        say("中を通してもらうしかない...", 2, 0.5f, level);
        say("なんか怖い雰囲気するけど。", 2, 2, level);
        say("ヒント：デバイスを勢いよく傾けると、家が傾くよ！", 4, 0, level);
    }

    public Scenario(GameSoundEngine game_sound_engine){
        this.game_sound_engine = game_sound_engine;
        this.random = new Random(System.nanoTime());
    }

    public void you(You you, GameMap.Level level){
        this.you = you;
        this.level = level;
        thread = null;

        try {
            trigger = Scenario.class.getMethod("level_" + level.name(), GameMap.Level.class);
            Runnable runnable = new trigger_thread(level, this);
            thread = new Thread(runnable, "Scenario");
            thread.start();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Trigger not implemented for level: '" + level.name() + "'.");
        }
    }

    public void tap(){
       int i = random.nextInt(tap.length);
       you.talk(tap[i], 1);
    }

    private void say(String s, float duration, float wait, GameMap.Level level) throws InterruptedException {
        if(this.level != level) throw new InterruptedException();
        you.talk(s, duration + 0.6f);
        if(this.level != level) throw new InterruptedException();
        Thread.sleep(Math.round((wait + duration) * 1000));
        if(this.level != level) throw new InterruptedException();
    }

    private void initial_delay(GameMap.Level level) throws InterruptedException {
        if(this.level != level) throw new InterruptedException();
        Thread.sleep(3000);
        if(this.level != level) throw new InterruptedException();
    }
}
