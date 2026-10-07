package org.example.dungeonrunner;


import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Translate;

import java.util.List;


public class DungeonBuilder {


    private DungeonMap map;
    private Group world;


    private List<Trap> traps;
    private List<Key> keys;
    private List<Switch> switches;
    private List<Door> doors;


    private int[] exit = {0,0};


    private PhongMaterial wallMaterial =
            new PhongMaterial();



    public DungeonBuilder(
            DungeonMap map,
            Group world,
            List<Trap> traps,
            List<Key> keys,
            List<Switch> switches,
            List<Door> doors
    ){

        this.map = map;
        this.world = world;
        this.traps = traps;
        this.keys = keys;
        this.switches = switches;
        this.doors = doors;
        prepareMaterials();

    }
    private void prepareMaterials(){
        wallMaterial.setDiffuseColor(Constants.WALL_DIFFUSE_COLOR);
        wallMaterial.setSpecularColor(Constants.WALL_SPECULAR_COLOR);
        wallMaterial.setDiffuseMap(new Image(DungeonRunner.class.getResourceAsStream("/com/example/begiztamnice/bricks.jpg")));

    }

    public Player build(){
        buildFloorAndCeiling();
        Player player =
                buildObjects();
        return player;

    }
    private void buildFloorAndCeiling(){
        int rows = map.getRows();
        int cols = map.getCols();
        double width =
                cols*Constants.CELL_SIZE;
        double depth =
                rows*Constants.CELL_SIZE;

        PhongMaterial floorMaterial = new PhongMaterial();floorMaterial.setDiffuseColor(Color.rgb(60,40,20));
        Box floor = new Box(width, Constants.SLAB_THICKNESS, depth);
        floor.getTransforms().add(new Translate(width/2, Constants.WALL_HEIGHT/2 +Constants.SLAB_THICKNESS/2, depth/2));
        floor.setMaterial(floorMaterial);
        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(Color.rgb(25,25,45));
        Box ceiling = new Box(width, Constants.SLAB_THICKNESS, depth);

        ceiling.getTransforms().add(new Translate(width/2, -Constants.WALL_HEIGHT/2 -Constants.SLAB_THICKNESS/2, depth/2));
        ceiling.setMaterial(ceilingMaterial);
        world.getChildren().addAll(floor, ceiling);

    }
    private Player buildObjects(){
        Player player=null;
        for(int row=0;row<map.getRows();row++){
            for(int col=0;col<map.getCols();col++){
                int tile =
                        map.get(col,row);
                switch(tile){
                    case Constants.WALL:
                    case Constants.EXIT:
                        buildWall(col,row);
                        if(tile==Constants.EXIT){
                            exit[0]=row;
                            exit[1]=col;
                        }
                        break;
                    case Constants.DOOR:
                        boolean vertical = map.isDoorVertical(row, col);
                        if(!vertical && !map.isDoorHorizontal(row,col))
                            break;
                        Door door = new Door(row, col, doors.size(), vertical);
                        doors.add(door);
                        world.getChildren().add(door.getDoor());
                        break;
                    case Constants.OCT:
                        world.getChildren().add(buildPillar(col, row));
                        break;
                    case Constants.SPIKE:
                        Spikes spike = new Spikes(col, row);
                        traps.add(spike);
                        world.getChildren().add(spike.getSpikes());
                        break;
                    case Constants.KEY:
                        Key key = new Key(row, col);
                        keys.add(key);
                        world.getChildren().add(key.getKey());
                        break;
                    case Constants.SWITCH:
                        for(Switch sw:switches){
                            if(sw.getX()==col &&
                                    sw.getY()==row){
                                world.getChildren().add(sw.getNode());
                                break;
                            }
                        }
                        break;
                    case Constants.START:
                        player = new Player(row+0.5, col+0.5);
                        break;
                }

            }

        }
        return player;
    }
    private void buildWall(
            int column,
            int row
    ){
        Box wall =
                new Box(
                        Constants.CELL_SIZE,
                        Constants.WALL_HEIGHT,
                        Constants.CELL_SIZE
                );
        wall.getTransforms().add(new Translate(column*Constants.CELL_SIZE +Constants.CELL_SIZE/2,
                                0,
                                row*Constants.CELL_SIZE +Constants.CELL_SIZE/2));
        wall.setMaterial(wallMaterial);
        world.getChildren().add(wall);
    }
    private MeshView buildPillar(int column, int row){
        double cx = column*2+1;
        double cz = row*2+1;
        double s=0.6;
        double hy=1;

        float[] points={
                (float)cx,-1,(float)cz,
                (float)(cx-s),0,(float)(cz-s),
                (float)(cx+s),0,(float)(cz-s),
                (float)(cx+s),0,(float)(cz+s),
                (float)(cx-s),0,(float)(cz+s),
                (float)cx,1,(float)cz

        };
        float[] tex={
                0.5f,0,
                0,1,
                1,1,
                0.5f,1,
                0,0,
                1,0
        };
        int[] faces={
                0,0,2,2,1,1,
                0,0,3,2,2,1,
                0,0,4,2,3,1,
                0,0,1,2,4,1,
                5,3,1,4,2,5,
                5,3,1,5,4,4,
                5,3,2,4,3,5,
                5,3,3,4,4,5

        };
        TriangleMesh mesh = new TriangleMesh();mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(tex);
        mesh.getFaces().setAll(faces);
        MeshView view = new MeshView(mesh);
        view.setMaterial(wallMaterial);
        view.setCullFace(CullFace.NONE);
        return view;
    }

    public int[] getExit(){
        return exit;
    }


}