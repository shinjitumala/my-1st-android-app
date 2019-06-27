package jp.ac.titech.itpro.sdl.die.Game.Systems;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.ac.titech.itpro.sdl.die.R;

public class GameSoundEngine {
    private static final String TAG = GameSoundEngine.class.getSimpleName();
    private static final int MAX_SOUNDS = 128;
    private static final Random random = new Random();

    private final AudioManager audio_manager;
    private final SoundPool sounds;
    private final Map<String, ArrayList<Integer>> sound_map;
    private final Context context;

    private MediaPlayer music_player;
    private static int[] music_ids = new int[]{
            R.raw.fpr_music_0,
            R.raw.fpr_music_1,
            R.raw.fpr_music_2,
            R.raw.fpr_music_3,
            R.raw.fpr_music_4,
            R.raw.fpr_music_5,
    };

    public GameSoundEngine(Context context) {
        this.audio_manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.sounds = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        this.sound_map = new HashMap<>();
        this.context = context;

        // load all sounds
        load_sound("bump", R.raw.bump_0);
        load_sound("bump", R.raw.bump_1);
        load_sound("bump", R.raw.bump_2);
        load_sound("bump", R.raw.bump_3);

        load_sound("portal", R.raw.portal_0);
        load_sound("portal", R.raw.portal_1);
        load_sound("portal", R.raw.portal_2);
        load_sound("portal", R.raw.portal_3);

        load_sound("say", R.raw.talk_0);
        load_sound("say", R.raw.talk_1);
        load_sound("say", R.raw.talk_2);
        load_sound("say", R.raw.talk_3);
        load_sound("say", R.raw.talk_4);
        load_sound("say", R.raw.talk_5);
        load_sound("say", R.raw.talk_6);
        load_sound("say", R.raw.talk_7);
        load_sound("say", R.raw.talk_8);
        load_sound("say", R.raw.talk_9);

    }

    private void load_sound(String key, int id){
        if(!sound_map.containsKey(key)){
            ArrayList<Integer> i = new ArrayList<>();
            i.add(sounds.load(context, id, 1));
            sound_map.put(key, i);
        } else {
            sound_map.get(key).add(sounds.load(context, id, 1));
        }
    }

    public void play_sound(String key){
        if(!sound_map.containsKey(key)){
            Log.d(TAG, "Could not find sound: '" + key + "'.");
            return;
        }
        ArrayList<Integer> list = sound_map.get(key);
        int id = list.get(random.nextInt(list.size()));
        //noinspection ConstantConditions
        sounds.play(
                id,
                audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC),
                audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC),
                1, 0, 1);
    }

    public void start_music(){
        stop_music();
        music_player = MediaPlayer.create(context, get_music());
        int i = audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC) / 3;
        music_player.setVolume(i, i);
        music_player.start();
        music_player.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        start_music();
                    }
                }
        );
    }

    public void stop_music(){
        if(music_player != null) music_player.release();
        music_player = null;
    }

    private int get_music(){
        int i = random.nextInt(6);
        return music_ids[i];
    }
}
