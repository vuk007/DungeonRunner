package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

public class Hearth implements PickUp {

    private static final double SIZE = 0.35;
    private static final double FLOAT_HEIGHT = 0;
    private static final double HEAL_AMOUNT = 1;

    private final int column;
    private final int row;
    private final Group node;
    private boolean pickedUp = false;
    private double bobTime = 0;

    public Hearth(int column, int row) {
        this.column = column;
        this.row = row;
        this.node = buildHeartMesh();
        placeAt(FLOAT_HEIGHT);
    }

    private Group buildHeartMesh() {
        PhongMaterial heartMaterial = new PhongMaterial();
        heartMaterial.setDiffuseColor(Color.rgb(220, 20, 60));
        heartMaterial.setSpecularColor(Color.rgb(255, 140, 160));

        int segments = 32;
        double scale = SIZE / 16.0;
        double depth = SIZE * 0.5;
        double halfDepth = depth / 2.0;

        int pointCount = segments * 2 + 2;
        float[] points = new float[pointCount * 3];

        // prednji i zadnji prsten po obodu srca + centralne tacke za oba lica
        for (int i = 0; i < segments; i++) {
            double t = i * 2 * Math.PI / segments;
            double hx = 16 * Math.pow(Math.sin(t), 3) * scale;
            double hy = -(13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t)) * scale;

            points[i * 3]     = (float) hx;
            points[i * 3 + 1] = (float) hy;
            points[i * 3 + 2] = (float) halfDepth;
        }
        int frontCenter = segments;
        points[frontCenter * 3] = 0f;
        points[frontCenter * 3 + 1] = 0f;
        points[frontCenter * 3 + 2] = (float) halfDepth;

        int backRingStart = segments + 1;
        for (int i = 0; i < segments; i++) {
            points[(backRingStart + i) * 3]     = points[i * 3];
            points[(backRingStart + i) * 3 + 1] = points[i * 3 + 1];
            points[(backRingStart + i) * 3 + 2] = (float) -halfDepth;
        }
        int backCenter = backRingStart + segments;
        points[backCenter * 3] = 0f;
        points[backCenter * 3 + 1] = 0f;
        points[backCenter * 3 + 2] = (float) -halfDepth;

        float[] texCoords = new float[]{0f, 0f};

        List<Integer> faces = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(frontCenter); faces.add(0);
            faces.add(i);           faces.add(0);
            faces.add(next);        faces.add(0);
        }

        // zadnje lice (obrnut redosled za suprotnu normalu)
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(backCenter);            faces.add(0);
            faces.add(backRingStart + next);  faces.add(0);
            faces.add(backRingStart + i);     faces.add(0);
        }

        // bocni zidovi (spajaju prednji i zadnji prsten)
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(i);                    faces.add(0);
            faces.add(next);                 faces.add(0);
            faces.add(backRingStart + next);  faces.add(0);

            faces.add(i);                    faces.add(0);
            faces.add(backRingStart + next); faces.add(0);
            faces.add(backRingStart + i);    faces.add(0);
        }

        int[] faceArray = faces.stream().mapToInt(Integer::intValue).toArray();

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faceArray);

        MeshView heartView = new MeshView(mesh);
        heartView.setMaterial(heartMaterial);
        heartView.setCullFace(CullFace.NONE);

        return new Group(heartView);
    }

    private void placeAt(double y) {
        double cx = column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        double cz = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        node.getTransforms().setAll(
                new Translate(cx, y, cz)
        );
    }

    @Override
    public void update(double dt, Player player) {
        if (pickedUp) return;
        bobTime += dt;
        double bobOffset = Math.sin(bobTime * 2.0) * 0.1;
        double spinDegrees = (bobTime * 60) % 360;

        double cx = column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        double cz = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        node.getTransforms().setAll(
                new Translate(cx, FLOAT_HEIGHT + bobOffset, cz),
                new Rotate(spinDegrees, Rotate.Y_AXIS)
        );

        if ((int)player.getPositionY() == row && (int)player.getPositionX() == column && player.getHp() < 3)  {
            pickedUp = true;
            player.heal(HEAL_AMOUNT);
        }
    }

    @Override
    public boolean isPicked_up() {
        return pickedUp;
    }

    @Override
    public Group getHitBox() {
        return node;
    }

    public Node getHearth() {
        return node;
    }
}