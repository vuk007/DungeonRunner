package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class Key {

    private Group key = new Group();
    private double rotate = 0;
    private double height = 0;
    private boolean up = true;
    public Group getKey() {
        return key;
    }

    private int row , column;
    public int getX(){return column;}
    public int getY(){return row;}
    public Key(int row, int column) {
        this.row = row;
        this.column = column;

        Box leva  = new Box(0.1,0.4,0.1);
        Box desna =  new Box(0.1,0.4,0.1);
        Box gornja =  new Box(0.5 , 0.1,0.1);
        Box zub1 =  new Box(0.2 , 0.1,0.1);
        Box zub2 =  new Box(0.1,0.1,0.1);
        Box drska =  new Box(0.1 ,0.6 , 0.1);
        drska.setTranslateY(0.6);
        zub2.setTranslateY(0.8);
        zub2.setTranslateX(0.1);
        zub1.setTranslateX(0.1);
        zub1.setTranslateY(0.6);
        leva.setTranslateX(0.2);
        leva.setTranslateY(0.2);
        desna.setTranslateX(-0.2);
        desna.setTranslateY(0.2);
        double cx = column * 2 + 1;
        double cz = row * 2 + 1;
        double cy = 0;



        key.getChildren().addAll(drska, zub2 , zub1, gornja,leva,desna);
        key.setTranslateX(cx);
        key.setTranslateY(cy);
        key.setTranslateZ(cz);
        for(Node s : key.getChildren()){
            PhongMaterial m  = new PhongMaterial(Color.GOLD);
            m.setSpecularColor(Color.YELLOW);
            ((Box)s).setMaterial(m);
        }
        key.setRotationAxis(Rotate.Y_AXIS);
        key.setScaleX(0.7);
        key.setScaleY(0.7);
        key.setScaleZ(0.7);
        this.height = key.getTranslateY();
    }

    public void update(double dt){

        double rotation_speed = 75;
        rotate += rotation_speed *dt;
        rotate = rotate % 360;
        key.setRotate(rotate);

        double vertical_speed = 0.5;
        if(up){
            height -= vertical_speed *dt;
            if(height < -0.35){
                up = false;
            }
            key.setTranslateY(height);
        }
        else {
            height += vertical_speed *dt;
            if(height > 0.25){
                up = true;
            }
            key.setTranslateY(height);
        }
    }
}
