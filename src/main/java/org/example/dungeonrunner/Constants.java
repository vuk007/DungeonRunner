package org.example.dungeonrunner;

import javafx.scene.paint.Color;

public class Constants {
    public static final int EMPTY = 0;
    public static final int WALL  = 1;
    public static final int EXIT  = 2;
    public static final int OCT  = 3;
    public static final int SPIKE  = 4;
    public static final int KEY  = 5;
    public static final int START = 6;

    public static final int[][] MAP1 = {
            {1,1,1,1,1,1,1,1,1},
            {1,4,6,5,1,0,0,0,1},
            {1,0,1,0,0,0,1,0,1},
            {1,0,1,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,1,1,0,1,1,0,1,1},
            {1,0,0,0,3,0,0,2,1},
            {1,1,1,1,1,1,1,1,1},
    };

    public static final int[][] MAP2 = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,6,0,0,1,0,0,5,0,1},
            {1,0,1,0,1,0,1,1,0,1},
            {1,0,1,0,0,0,1,0,0,1},
            {1,0,1,1,1,1,1,0,1,1},
            {1,4,0,0,0,0,0,0,3,1},
            {1,1,1,0,1,1,0,1,1,1},
            {1,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1},
    };

    public static final int[][] MAP3 = {
            {1,1,1,1,1,1,1},
            {1,5,0,0,0,0,1},
            {1,0,1,1,1,0,1},
            {1,0,1,4,6,0,1},
            {1,0,1,0,1,0,1},
            {1,0,0,1,3,2,1},
            {1,1,1,1,1,1,1},
    };

    public static final int[][][] MAPS = { MAP1, MAP2, MAP3 };
    public static final String[] MAP_NAMES = { "Tamnica I", "Tamnica II", "Tamnica III" };

    // ovo se postavlja u MainMenu pre pokretanja igre
    public static int[][] CURRENT_MAP = MAP1;

    public static final int SCREEN_WIDTH  = 1000;
    public static final int SCREEN_HEIGHT = 800;

    public static final double CELL_SIZE      = 2.0;
    public static final double WALL_HEIGHT    = 2.0;
    public static final double SLAB_THICKNESS = 0.05;

    public static final Color WALL_DIFFUSE_COLOR  = Color.rgb ( 110, 110, 110 );
    public static final Color WALL_SPECULAR_COLOR = Color.rgb ( 40,  40,  40 );
    public static final Color EXIT_DIFFUSE_COLOR  = Color.rgb ( 0, 200,  80 );
    public static final Color EXIT_SPECULAR_COLOR = Color.rgb ( 0,  80,  30 );

    public static final Color AMBIENT_LIGHT_COLOR = Color.rgb ( 50, 45, 40 );
    public static final Color POINT_LIGHT_COLOR   = Color.rgb ( 255, 200, 120 );

    public static final double CAMERA_NEAR_CLIP     = 0.05;
    public static final double CAMERA_FAR_CLIP      = 500.0;
    public static final double CAMERA_FIELD_OF_VIEW = 75.0;

    public static final double PLAYER_START_X        = 3.5;
    public static final double PLAYER_START_Y        = 2.5;
    public static final double PLAYER_MOVE_SPEED     = 0.005*2;
    public static final double PLAYER_ROTATION_SPEED = 0.01*2;
    public static final double PLAYER_RADIUS         = 0.25;
}