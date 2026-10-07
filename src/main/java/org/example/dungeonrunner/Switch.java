package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Switch{

    Group switchGroup = new Group();
    private Box base;
    private Box base_metal;

    private int doorRow;
    private int doorCol;

    private int row;
    private int col;

    private boolean activated=false;


    public Switch(int row,int col, int doorRow, int doorCol,DungeonMap map){

        this.row=row;
        this.col=col;

        this.doorRow=doorRow;
        this.doorCol=doorCol;

        base =new Box(0.15,0.3,0.2);
        base_metal =new Box(0.05,0.2,0.25);

        PhongMaterial material=new PhongMaterial();
        material.setDiffuseColor(Color.BURLYWOOD);
        base.setMaterial(material);

        PhongMaterial material_metal=new PhongMaterial();
        material_metal.setDiffuseColor(Color.rgb(150,150,160));
        material_metal.setSpecularColor(Color.WHITE);
        material_metal.setSpecularPower(80);
        base_metal.setMaterial(material_metal);


        double x = col * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        double z = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        double px = x, pz = z;
        int rotate= 0;
        if(map.isWall(row-1,col)){
            pz = z - Constants.CELL_SIZE/2.0 + 0.05;
            System.out.println("1");
        }
        else if(map.isWall(row+1,col)){
            pz = z + Constants.CELL_SIZE/2.0 - 0.05;
            System.out.println("2");
        }
        else if(map.isWall(row,col-1)){
            px = x - Constants.CELL_SIZE/2.0 + 0.05;
            System.out.println("3");
            rotate=90;
        }
        else if(map.isWall(row,col+1)){
            px = x + Constants.CELL_SIZE/2.0 - 0.05;
            rotate = 90;
            System.out.println("4");
        }

        Translate placement = new Translate(px, -0.2, pz);
        base.getTransforms().add(placement);
        base.getTransforms().add(new Rotate(rotate,Rotate.Y_AXIS));
        base_metal.getTransforms().add(placement);
        base_metal.getTransforms().add(new Rotate(rotate,Rotate.Y_AXIS));

        switchGroup.getChildren().addAll(base,base_metal);
    }

    public boolean activate(){
        if(activated)
            return false;

        activated=true;
        PhongMaterial material=new PhongMaterial();
        material.setDiffuseColor(Color.GREEN);
        base.setMaterial(material);
        return true;
    }


    public Node getNode(){
        return switchGroup;
    }


    public int getX(){
        return col;
    }


    public int getY(){
        return row;
    }


    public int getDoorRow(){
        return doorRow;
    }


    public int getDoorCol(){
        return doorCol;
    }
}
