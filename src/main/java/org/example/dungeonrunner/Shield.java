package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class Shield implements PickUp{

    private int row , column;
    private Group shield = new Group();
    private boolean picked_up = false;

    public Group getShield() {return shield;}
    private Rotate rotate_y = new Rotate();
    private double rotate = 0;
    private boolean up = false;
    private double height;
    Shield(int row, int column) {
        double cx = column * 2 + 1;
        double cz = row * 2 + 1;
        this.row = row;
        this.column = column;

        double cy = 0;
        Cylinder c_outer = new Cylinder(0.13,0.05);
        PhongMaterial p_material = new PhongMaterial(Color.BLACK);

        c_outer.setTranslateX(cx);
        c_outer.setTranslateY(cy);
        c_outer.setTranslateZ(cz);
        c_outer.setMaterial(p_material);
        shield.getChildren().add(c_outer);

        Cylinder c = new Cylinder(0.1,0.052);
        PhongMaterial mat = new PhongMaterial(Color.BLUE);
        c.setMaterial(mat);

        c.setTranslateX(cx);
        c.setTranslateY(cy);
        c.setTranslateZ(cz);
        shield.getChildren().add(c);
        shield.setRotate(90);
        rotate_y.setAxis(Rotate.X_AXIS);
        shield.getTransforms().add(rotate_y);
        this.height = c.getTranslateY();
        rotate_y.setPivotX(cx);
        rotate_y.setPivotZ(cz);
        rotate_y.setPivotY(0);
    }

    @Override
    public Group getHitBox() {
        return getShield();
    }

    @Override
    public void update(double dt, Player player) {
        double rotation_speed = 75;
        rotate += rotation_speed *dt;
        rotate = rotate % 360;
        rotate_y.setAngle(rotate);

        double vertical_speed = 0.5;
        if(up){
            height -= vertical_speed *dt;
            if(height < -0.35){
                up = false;
            }
            shield.setTranslateY(height);
        }
        else {
            height += vertical_speed *dt;
            if(height > 0.25){
                up = true;
            }
            shield.setTranslateY(height);
        }
        if (column==(int)player.getPositionX() && row==(int)player.getPositionY()){
            this.picked_up = true;
            System.out.println("POKUPLJEN");
        }
    }

    @Override
    public boolean isPicked_up() {
        return picked_up;
    }
}
