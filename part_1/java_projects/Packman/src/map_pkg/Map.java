package map_pkg;

import java.awt.Color;
import java.awt.Dimension;

public class Map {

    public static final int 		BLOCK_PIXEL_SIZE = 		24;
    public static final int 		NUM_OF_SIDE_BLOCKS = 	15;
    public static final int 		SCREEN_PIXEL_SIZE = 	NUM_OF_SIDE_BLOCKS * BLOCK_PIXEL_SIZE;
    public static final Dimension 	BOARD_PIXEL_DIMENSION = new Dimension(400, 400);
    public static final Color 		MAZE_COLOR = 			new Color(5, 100, 5);;
    public static final Color 		POINT_COLOR = 			new Color(192, 192, 0);
    public static final Color 		GHOST_PATH_COLOR = 		new Color(100, 100,100);
    
    public static final int 		WALL_LEFT = 			1;
    public static final int 		WALL_RIGHT = 			4;
    public static final int 		WALL_UP = 				2;
    public static final int 		WALL_DOWN = 			8;
    public static final int 		GAME_POINT = 			16;
    public static final int 		EMPTY_SPACE = 			15;
    public static final int 		POINTS_PIXEL_SIZE = 	2;
    
    
    public static short CONTENT[] = {
		19, 26, 22, -1, -1, 19, 26, 26, 26, 22, -1, -1, 19, 26, 22,
		21, -1, 17, 18, 26, 20, -1, -1, -1, 17, 26, 18, 20, -1, 21,
		25, 18, 16, 28, -1, 25, 22, -1, 19, 28, -1, 25, 16, 18, 28,
		-1, 17, 20, -1, -1, -1, 21, -1, 21, -1, -1, -1, 17, 20, -1,
		-1, 17, 20, -1, -1, -1, 17, 26, 20, -1, -1, -1, 17, 20, -1,
		19, 24, 16, 22, -1, 19, 20, -1, 17, 22, -1, 19, 16, 24, 22,
		21, -1, 25, 24, 18, 24, 20, -1, 17, 24, 18, 24, 28, -1, 21,
		21, -1, -1, -1, 21, -1, 17, 26, 20, -1, 21, -1, -1, -1, 21,
		21, -1, 19, 26, 16, 26, 20, -1, 17, 26, 16, 26, 22, -1, 21,
		17, 26, 20, -1, 21, -1, 21, -1, 21, -1, 21, -1, 17, 26, 20,
		21, -1, 21, -1, 21, -1, 17, 26, 20, -1, 21, -1, 21, -1, 21,
		17, 18, 20, -1, 21, -1, 21, -1, 21, -1, 21, -1, 17, 18, 20,
		17, 24, 16, 26, 24, 18, 28, -1, 25, 18, 24, 26, 16, 24, 20,
		21, -1, 21, -1, -1, 21, -1, -1, -1, 21, -1, -1, 21, -1, 21,
		25, 26, 28, -1, -1, 25, 26, 26, 26, 28, -1, -1, 25, 26, 28
    };
    
    public static short[] DATA = new short[NUM_OF_SIDE_BLOCKS * NUM_OF_SIDE_BLOCKS];
}
