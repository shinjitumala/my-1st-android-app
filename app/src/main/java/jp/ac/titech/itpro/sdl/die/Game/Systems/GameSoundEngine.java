package jp.ac.titech.itpro.sdl.die.Game.Systems;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class GameSoundEngine {
    private static final String TAG = GameSoundEngine.class.getSimpleName();
    private static final int MAX_SOUNDS = 8;

    private final AudioManager audio_manager;
    private final SoundPool sounds;
    private final Map<String, Integer> sound_map;
    private final Context context;

    public GameSoundEngine(Context context) {
        this.audio_manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.sounds = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        this.sound_map = new HashMap<>();
        this.context = context;

        // load all sounds
        //load_sound("debug", R.raw.debug);
    }

    private void load_sound(String key, int id){
        sound_map.put(key, sounds.load(context, id, 1));
    }

    public void play_sound(String key){
        if(sound_map.get(key) != null){
            Log.d(TAG, "Could not find sound: '" + key + "'.");
            return;
        }
        //noinspection ConstantConditions
        sounds.play(
                sound_map.get(key),
                audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC),
                audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC),
                1, 0, 1);
    }
}
