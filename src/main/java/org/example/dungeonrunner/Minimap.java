package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Minimap {

    private Group minimap;
    private Circle player;
    private Line direction;
    private Circle key;
    private Circle exit;
    private DungeonMap map;
    private double cell = 15;

    public Minimap(DungeonMap map){
        this.map = map;
        create();
    }

    private void create(){
        minimap = new Group();

        System.out.println("Minimap size: " + (map.getCols()*cell+10) + " x " + (map.getRows()*cell+10));
        System.out.println("Minimap translateX/Y: " + minimap.getTranslateX() + ", " + minimap.getTranslateY());

        Rectangle background = new Rectangle(map.getCols()*cell+10,map.getRows()*cell+10);
        background.setFill(Color.rgb(0,0,0,0.55));
        background.setStroke(Color.WHITE);

        minimap.getChildren().add(background);

        for(int y=0;y<map.getRows();y++){
            for(int x=0;x<map.getCols();x++){

                Rectangle tile = new Rectangle(x*cell+5,y*cell+5,cell,cell);

                int value = map.get(x,y);

                if(value==Constants.WALL || value==Constants.EXIT){
                    tile.setFill(Color.DARKGRAY);
                }else{
                    tile.setFill(Color.TRANSPARENT);
                }

                tile.setStroke(Color.rgb(70,70,70));
                minimap.getChildren().add(tile);

                if(value==Constants.KEY){
                    key = new Circle(x*cell+cell/2+5,y*cell+cell/2+5,3,Color.YELLOW);
                    minimap.getChildren().add(key);
                }

                if(value==Constants.EXIT){
                    exit = new Circle(x*cell+cell/2+5,y*cell+cell/2+5,3,Color.GREEN);
                }
            }
        }

        player = new Circle(0,0,4,Color.WHITE);
        direction = new Line();
        direction.setStroke(Color.WHITE);
        direction.setStrokeWidth(2);

        minimap.getChildren().addAll(direction, player);

        double margin = 20;

        minimap.setTranslateX(0);
        minimap.setTranslateY(Constants.SCREEN_HEIGHT/2.0 - (map.getRows()*cell+10)/2.0 - margin);
    }

    public Group getNode(){
        return minimap;
    }

    public void update(Player player){
        double x = player.getPositionX()*cell+5;
        double y = player.getPositionY()*cell+5;

        this.player.setCenterX(x);
        this.player.setCenterY(y);

        direction.setStartX(x);
        direction.setStartY(y);
        direction.setEndX(x+player.getDirectionX()*10);
        direction.setEndY(y+player.getDirectionY()*10);
    }

    public void removeKey(){
        if(key !=null){minimap.getChildren().remove(key);}
    }

    public void showExit(){
        if(exit !=null){minimap.getChildren().add(exit);}
    }
}