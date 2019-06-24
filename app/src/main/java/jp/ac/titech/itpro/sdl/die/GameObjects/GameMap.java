package jp.ac.titech.itpro.sdl.die.GameObjects;

import android.util.Log;

public class GameMap {
    // debugging purposes
    private static String TAG = GameMap.class.getSimpleName();

    // map data
    public int[] size;
    private Level level;

    public GameMap() {

    }

    /* map */
    // 0: Tile
    // 1: Wall
    // -x: teleporter to stage x
    /* map top */
    // 0: none
    // 1: you, initial position
    // 2: Gravipigs
    // 3: EvilWall
    // 4: DoorOfTilt
    // 5: CubeOfRage
    public enum Level {
        ONE(
            // map size
            10, 10,
            // map data
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1,-1, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 0, 1, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            // map data top
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 5, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 0, 2, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 3, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        );

        private int[] size;
        private int[][] map_init;
        private int[][] map_init_top;

        Level(int... args){
            if(args.length < 2){
                error();
                return;
            }
            size = new int[] {args[0], args[1]};
            if(args.length != 2 + 2 * size[0] * size[1]){
                error();
                return;
            }
            map_init = new int[size[0]][size[1]];
            for(int i = 0; i < size[0]; i++){
                for(int j = 0; j < size[1]; j++){
                    map_init[i][j] = args[size[0] * j + i + 2];
                }
            }
            map_init_top = new int[size[0]][size[1]];
            for(int i = 0; i < size[0]; i++){
                for(int j = 0; j < size[1]; j++){
                    map_init_top[i][j] = args[size[0] * j + i + 2 + size[0] * size[1]];
                }
            }
        }

        private void error(){
            Log.e(TAG, "GameMap init data is corrupt. GameMap will be initialized with empty map.");
            size = new int[] {4, 4};
            map_init = new int[][]
                    {
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                    };
        }

    }

    public GameMap(Level level){
        this.size = level.size;
        this.level = level;
    }

    public void set_level(Level level){
        this.level = level;
        this.size = level.size;
    }

    public void set_level(int i){
        switch(i){
            case 1: set_level(Level.ONE); break;
        }
    }

    public int get_init_map(int i, int j){
        return level.map_init[i][j];
    }

    public int get_init_map_top(int i, int j){
        return level.map_init_top[i][j];
    }
}
