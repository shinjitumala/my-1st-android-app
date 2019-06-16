package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.util.Log;

public class Map {
    // debugging purposes
    private static String TAG = Map.class.getSimpleName();

    // map data
    public final int[] size;
    private int[][] map_state;

    /* map state */
    // 0: empty space
    // 1: wall
    // 2: starting position of 'You'
    // 3: position of goal

    public enum Level {
        ONE(
            // map size
            10, 10,
            // map data
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 3, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 2, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        );

        private int[] size;
        private int[][] map_initial_state;

        Level(int... args){
            if(args.length < 2){
                error();
                return;
            }
            size = new int[] {args[0], args[1]};
            if(args.length != 2 + size[0] * size[1]){
                error();
                return;
            }
            map_initial_state = new int[size[0]][size[1]];
            for(int i = 0; i < size[0]; i++){
                for(int j = 0; j < size[1]; j++){
                    map_initial_state[i][j] = args[size[0] * j + i + 2];
                }
            }
        };

        private void error(){
            Log.e(TAG, "Map init data is corrupt. Map will be initialized with empty map.");
            size = new int[] {4, 4};
            map_initial_state = new int[][]
                    {
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                    };
        }

        public int[] get_size() {return size;}
        public int[][] get_map_initial_state() {return map_initial_state;}
    }

    public Map(Level level){
        size = level.get_size();
        map_state = level.get_map_initial_state();
    }

    public int get_map_state(int i, int j){
        return map_state[i][j];
    }

    public void set_map_state(int i, int j, int state){
        map_state[i][j] = state;
    }
}
