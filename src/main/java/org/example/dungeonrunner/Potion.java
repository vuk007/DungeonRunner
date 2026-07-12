package org.example.dungeonrunner;

import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Potion implements PickUp {
    private static final double SIZE = 0.45;
    private static final double FLOAT_HEIGHT = 0;
    private static final double HEAL_AMOUNT = 1;

    private final int column;
    private final int row;
    private final Group node;
    private final Group hitBox;
    private boolean pickedUp = false;
    private double bobTime = 0;
    private PointLight glow;
    public Potion(int column, int row) {
        this.column = column;
        this.row = row;
        this.node = new Group();
        this.hitBox = new Group();
        double cx = column * 2 + 1; double cz = row * 2 + 1;
        buildPot(cx, cz);
    }

    private void buildPot(double cx, double cz) {
        double R = 0.3;

        Sphere body = new Sphere(R);
        body.setScaleY(0.9);
        PhongMaterial bodyMat = new PhongMaterial(Color.rgb(225, 235, 245, 0.35)); // staklo
        bodyMat.setSpecularColor(Color.rgb(255, 255, 255, 0.9));
        bodyMat.setSpecularPower(60);
        body.setCullFace(CullFace.FRONT);
        body.setMaterial(bodyMat);
        body.setTranslateY(-(R * 0.9));

        double bodyTop = -2 * R * 0.9;

        Sphere liquid = new Sphere(R * 0.9);
        liquid.setScaleY(0.85);
        PhongMaterial liquidMat = new PhongMaterial(Color.rgb(150, 40, 200, 0.95));
        liquidMat.setSpecularColor(Color.rgb(200, 200, 15, 0.7));
        liquid.setMaterial(liquidMat);
        liquid.setTranslateY(-(R * 0.9) + 0.04);

        Sphere bubble1 = napraviDno(0.045, cx + 0.12, bodyTop * 0.45, cz + 0.05, Color.WHITE);
        Sphere bubble2 = napraviDno(0.025, cx - 0.08, bodyTop * 0.6, cz - 0.05,Color.WHITE);
        Sphere bubble3 = napraviDno(0.06, cx + 0.05, bodyTop * 0.3, cz + 0.1, Color.WHITE);


        glow = new PointLight(Color.rgb(180, 90, 220));
        glow.setTranslateY(bodyTop * 0.5);
        glow.setMaxRange(2);
        glow.setConstantAttenuation(1);
        glow.setLinearAttenuation(0.245);
        glow.setQuadraticAttenuation(0.4);


        Cylinder collar = new Cylinder(0.16, 0.07);
        PhongMaterial collarMat = new PhongMaterial(Color.rgb(110, 160, 230));
        collarMat.setSpecularColor(Color.WHITE);
        collar.setMaterial(collarMat);
        collar.setTranslateY(bodyTop + 0.04);

        Cylinder neck = new Cylinder(0.09, 0.2);
        neck.setMaterial(bodyMat);
        double neckTop = bodyTop + 0.04 - 0.2;
        neck.setTranslateY((bodyTop + 0.04 + neckTop) / 2);

        MeshView cork = napraviCep();
        cork.setTranslateY(neckTop);

        Sphere corkKnob = new Sphere(0.08);
        corkKnob.setMaterial(new PhongMaterial(Color.rgb(200, 150, 100))); // svetlija drvenkasta
        corkKnob.setTranslateY(neckTop - 0.16);


        node.getChildren().addAll(body, liquid, bubble1, bubble2, bubble3,
                collar, neck, cork,glow);

        node.setTranslateX(cx);
        node.setTranslateZ(cz);
        node.setTranslateY(FLOAT_HEIGHT);
        node.setScaleX(SIZE);
        node.setScaleY(SIZE);
        node.setScaleZ(SIZE);
        node.setRotate(Math.random() * 360);
    }

    private Sphere napraviDno(double radius, double cx, double y, double cz , Color c) {
        Sphere s = new Sphere(radius);
        PhongMaterial mat = new PhongMaterial(Color.rgb(255, 230, 80));
        mat.setSpecularColor(c);
        s.setMaterial(mat);
        s.setTranslateY(y);
        return s;
    }


    private MeshView napraviCep() {
        int sides = 24;
        List<Float> points = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> faces = new ArrayList<>();

        double angleStep = 360.0 / sides;
        double corkBase = 0.13;

        for (int i = 0; i < sides; i++) {
            double rad = Math.toRadians(i * angleStep);
            float x = (float) (corkBase * Math.cos(rad));
            float z = (float) (corkBase * Math.sin(rad));
            points.add(x); points.add(0F); points.add(z);
        }
        points.add(0F); points.add(-0.16F); points.add(0F);
        points.add(0F); points.add(0F); points.add(0F);

        for (int i = 0; i < sides; i++) {
            texCoords.add(i / (float) sides);
            texCoords.add(0F);
        }
        texCoords.add(0.5F); texCoords.add(1F);
        texCoords.add(0.5F); texCoords.add(0.5F);

        int apexIndex = sides;
        int capCenterIndex = sides + 1;

        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            faces.add(apexIndex); faces.add(i); faces.add(next);
        }
        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            faces.add(capCenterIndex); faces.add(next); faces.add(i);
        }

        TriangleMesh mesh = new TriangleMesh();
        float[] pointsArr = new float[points.size()];
        for (int i = 0; i < points.size(); i++) pointsArr[i] = points.get(i);
        mesh.getPoints().setAll(pointsArr);

        float[] texArr = new float[texCoords.size()];
        for (int i = 0; i < texCoords.size(); i++) texArr[i] = texCoords.get(i);
        mesh.getTexCoords().setAll(texArr);

        int[] faceArr = new int[faces.size() * 2];
        for (int i = 0; i < faces.size(); i++) {
            faceArr[i * 2] = faces.get(i);
            faceArr[i * 2 + 1] = faces.get(i);
        }
        mesh.getFaces().setAll(faceArr);

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.rgb(120, 70, 30)));
        meshView.setCullFace(CullFace.BACK);
        return meshView;
    }

    @Override
    public Group getHitBox() {
        return hitBox;
    }

    @Override
    public void update(double dt, Player player) {
        if (pickedUp) return;

        bobTime += dt;
        double bobOffset = Math.sin(bobTime * 2) * 0.08;
        node.setTranslateY(FLOAT_HEIGHT + bobOffset);
        hitBox.setTranslateY(FLOAT_HEIGHT + bobOffset);
        node.setRotate(node.getRotate() + dt * 20);

        if (column==(int)player.getPositionX() && row==(int)player.getPositionY()) {
            pickUp(player);
        }
    }

    private void pickUp(Player player) {
        pickedUp = true;
        player.drunk();

        ScaleTransition shrink = new ScaleTransition(Duration.millis(200), node);
        shrink.setToX(0); shrink.setToY(0); shrink.setToZ(0);
        shrink.setOnFinished(e -> {
            node.setVisible(false);
            hitBox.setVisible(false);
            node.getChildren().remove(glow);
        });
        shrink.play();
    }

    @Override
    public boolean isPicked_up() {
        return pickedUp;
    }

    public Group getNode() {
        return node;
    }
}