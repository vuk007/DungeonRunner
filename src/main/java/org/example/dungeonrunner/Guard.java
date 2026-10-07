package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.List;

public class Guard {

    private Group guard;
    private Group model;      // sve delove modela nosi ova grupa, rotira se prema pravcu kretanja

    private Cylinder body;
    private Cylinder hat_top;
    private Sphere head;

    private Cylinder leftLeg;
    private Cylinder rightLeg;
    private Cylinder leftArm;
    private Cylinder rightArm;
    private Group leftSideRotation; //ruka + noga
    private Group rightSideRotation;//ruka + noga

    private List<int[]> path;   // putanja: niz tacaka {row,col}
    private int next = 1;
    private int direction = 1;  // 1 = napred, -1 = nazad

    private double positionX;
    private double positionY;

    private double speed; // kocke po sekundi

    private double facingAngle = 0;
    private double walkPhase = 0;


    public Guard(List<int[]> path, double speed){

        this.path = path;
        this.speed = speed;

        int[] start = path.get(0);
        this.positionY = start[0] + 0.5;
        this.positionX = start[1] + 0.5;

        build();
        updateVisual();
    }

    private void build(){

        guard = new Group();
        model = new Group();

        PhongMaterial skinMaterial = new PhongMaterial();
        skinMaterial.setDiffuseColor(Color.web("#2079A8FF"));
        skinMaterial.setSpecularColor(Color.web("#0722EDFF"));
        skinMaterial.setSpecularPower(40);

        PhongMaterial uniformMaterial = new PhongMaterial(Color.DARKBLUE);


        body = new Cylinder(0.25, 0.9);
        body.setMaterial(uniformMaterial);
        body.getTransforms().add(new Translate(0,0.2,0));


        head = new Sphere(0.2);
        head.setMaterial(skinMaterial);
        head.getTransforms().add(new Translate(0,-0.45,0));


        hat_top = new Cylinder(0.12,0.1);
        hat_top.setMaterial(uniformMaterial);
        hat_top.getTransforms().add(new Translate(0,-0.65,0));



        double legLength = 0.5;
        double armLength = 0.5;
        double legStart = 0.65; // donja ivica tela je otprilike ovde

        leftLeg = new Cylinder(0.06, legLength);
        leftLeg.setMaterial(uniformMaterial);
        leftLeg.getTransforms().add(new Translate(0,legLength/2.0,0));

        rightLeg = new Cylinder(0.06, legLength);
        rightLeg.setMaterial(uniformMaterial);
        rightLeg.getTransforms().add(new Translate(0,legLength/2.0,0));

        leftArm = new Cylinder(0.06, armLength);
        leftArm.setMaterial(uniformMaterial);
        leftArm.getTransforms().add(new Translate(-0.25 - (-0.1) - 0.03,-armLength,0));

        rightArm = new Cylinder(0.06, armLength);
        rightArm.setMaterial(uniformMaterial);
        rightArm.getTransforms().add(new Translate(+0.25 - 0.1 + 0.03,-armLength,0));

        leftSideRotation = new Group();
        leftSideRotation.getChildren().addAll(leftLeg , leftArm);
        leftSideRotation.getTransforms().add(new Translate(-0.1, legStart, 0));
        leftSideRotation.setRotationAxis(Rotate.X_AXIS);



        rightSideRotation = new Group();
        rightSideRotation.getChildren().addAll(rightLeg, rightArm);
        rightSideRotation.getTransforms().add(new Translate(0.1, legStart, 0));
        rightSideRotation.setRotationAxis(Rotate.X_AXIS);

        model.getChildren().addAll(
                body, head, hat_top,
                leftSideRotation, rightSideRotation
        );

        model.setRotationAxis(Rotate.Y_AXIS);

        guard.getChildren().add(model);
    }

    public void update(double dt){

        if(path.size() < 2) return;

        int[] target = path.get(next);
        double targetX = target[1] + 0.5;
        double targetY = target[0] + 0.5;

        double dx = targetX - positionX;
        double dy = targetY - positionY;
        double dist = Math.sqrt(dx*dx + dy*dy);

        double step = speed * dt;
        boolean moving = dist > 1e-6;
        if(dist < step || dist == 0){

            positionX = targetX;
            positionY = targetY;

            next += direction;

            if(next >= path.size()){
                next = path.size()-2;
                direction = -1;
            } else if(next < 0){
                next = Math.min(1, path.size()-1);
                direction = 1;
            }

        } else {
            positionX += dx/dist*step;
            positionY += dy/dist*step;
        }
        if(moving){
            walkPhase += dt * speed * 6.0;
            facingAngle = Math.toDegrees(Math.atan2(dx, dy));
        }

        updateVisual();
    }

    private void updateVisual(){

        Translate t = new Translate(
                positionX*Constants.CELL_SIZE,
                0,
                positionY*Constants.CELL_SIZE
        );

        guard.getTransforms().setAll(t);

        model.setRotate(facingAngle);

        double swing = Math.sin(walkPhase) * 30; // amplituda njihanja nogu u stepenima
        leftSideRotation.setRotate(swing);
        rightSideRotation.setRotate(-swing);
    }

    public Group getNode(){
        return guard;
    }

    public double getPositionX(){
        return positionX;
    }

    public double getPositionY(){
        return positionY;
    }

    public boolean collidesWith(double playerX, double playerY, double radius){

        double dx = playerX - positionX;
        double dy = playerY - positionY;

        return Math.sqrt(dx*dx+dy*dy) < radius;
    }
}