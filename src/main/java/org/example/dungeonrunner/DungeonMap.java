package org.example.dungeonrunner;

public class DungeonMap {
    private int map[][];

    public DungeonMap ( int map[][] ) {
        this.map = new int[map.length][map[0].length];

        for ( int i = 0; i < map.length; i++ ) {
            for ( int j = 0; j < map[i].length; j++ ) {
                this.map[i][j] = map[i][j];
            }
        }
    }

    public int getRows ( ) {
        return this.map.length;
    }

    public int getCols ( ) {
        return this.map[0].length;
    }

    public int get ( int x, int y ) {
        return this.map[y][x];
    }


    public int[] findStart(){

        for(int r = 0; r < map.length; r++){
            for(int c = 0; c < map[0].length; c++){

                if(map[r][c] == Constants.START)
                    return new int[]{r,c};

            }
        }

        throw new RuntimeException("START nije pronadjen.");
    }

    public void set(int x, int y, int value){
        this.map[y][x] = value;
    }

    public boolean isWall(int row,int col){

        if(row < 0 || row >= getRows()
                || col < 0 || col >= getCols())
            return false;

        return get(col,row) == Constants.WALL;
    }

    public boolean isDoorVertical(int row,int col){

        return isWall(row-1,col)
                &&
                isWall(row+1,col);
    }

    public boolean isDoorHorizontal(int row,int col){

        return isWall(row,col-1)
                &&
                isWall(row,col+1);
    }

    public boolean hasNearbyWall(int row,int col){
        return isWall(row-1,col) || isWall(row+1,col) || isWall(row,col-1) || isWall(row,col+1);
    }
}