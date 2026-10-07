package org.example.dungeonrunner;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Door {

    private Group door;
    private boolean open = false;
    private boolean opening = false;
    private int id;
    private int row;
    private int col;

    private double baseX;
    private double baseY;
    private double baseZ;

    public Door(int row, int col, int id, boolean vertical) {

        this.row = row;
        this.col = col;
        this.id = id;

        door = new Group();

        double doorHeight = Constants.WALL_HEIGHT;
        double frameThickness = 0.10;
        double plankThickness = 0.18;

        PhongMaterial frameMaterial = new PhongMaterial();
        frameMaterial.setDiffuseColor(Color.rgb(25, 15, 8));
        frameMaterial.setSpecularColor(Color.rgb(50, 35, 20));

        Box frame;
        if (vertical) {
            frame = new Box(frameThickness, doorHeight + 0.3, Constants.CELL_SIZE);
        } else {
            frame = new Box(Constants.CELL_SIZE, doorHeight + 0.3, frameThickness);
        }
        frame.setMaterial(frameMaterial);
        door.getChildren().add(frame);

        PhongMaterial plankMaterial = new PhongMaterial();
        plankMaterial.setDiffuseColor(Color.rgb(160, 100, 45));
        plankMaterial.setSpecularColor(Color.rgb(230, 190, 130));
        plankMaterial.setSpecularPower(15);

        int plankCount = 4;
        double gap = 0.04;
        double plankSpan = vertical ? Constants.CELL_SIZE * 0.9 : Constants.CELL_SIZE * 0.9;
        double plankSize = (plankSpan - gap * (plankCount - 1)) / plankCount;

        for (int i = 0; i < plankCount; i++) {
            double offset = -plankSpan / 2.0 + plankSize / 2.0 + i * (plankSize + gap);

            Box plank;
            if (vertical) {
                plank = new Box(plankThickness, doorHeight, plankSize);
                plank.getTransforms().add(new Translate(0, 0, offset));
            } else {
                plank = new Box(plankSize, doorHeight, plankThickness);
                plank.getTransforms().add(new Translate(offset, 0, 0));
            }
            plank.setMaterial(plankMaterial);
            door.getChildren().add(plank);
        }

        PhongMaterial metalMaterial = new PhongMaterial();
        metalMaterial.setDiffuseColor(Color.rgb(60, 60, 65));
        metalMaterial.setSpecularColor(Color.rgb(210, 210, 220));
        metalMaterial.setSpecularPower(70);

        double bandDepth = plankThickness + 0.03;
        double[] bandYOffsets = { -doorHeight * 0.3, doorHeight * 0.3 };
        for (double by : bandYOffsets) {
            Box band;
            if (vertical) {
                band = new Box(bandDepth, 0.08, Constants.CELL_SIZE * 0.85);
            } else {
                band = new Box(Constants.CELL_SIZE * 0.85, 0.08, bandDepth);
            }
            band.setMaterial(metalMaterial);
            band.getTransforms().add(new Translate(0, by, 0));
            door.getChildren().add(band);
        }

        Cylinder handle = new Cylinder(0.04, 0.25);
        handle.setMaterial(metalMaterial);
        if (vertical) {
            handle.getTransforms().addAll(
                    new Rotate(90, Rotate.X_AXIS),
                    new Translate(0, 0.15, -Constants.CELL_SIZE * 0.3)
            );
        } else {
            handle.getTransforms().addAll(
                    new Rotate(90, Rotate.Z_AXIS),
                    new Translate(Constants.CELL_SIZE * 0.3, 0.0, 0.15)
            );
        }
        door.getChildren().add(handle);

        baseX = col * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        baseY = 0;
        baseZ = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        door.getTransforms().add(new Translate(baseX, baseY, baseZ));
    }

    public int getId() {
        return id;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        if (open || opening) return;
        opening = true;

        Translate translate = new Translate(baseX, baseY, baseZ);
        door.getTransforms().setAll(translate);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1.0), door);
        slide.setByY(-Constants.WALL_HEIGHT);

        slide.setOnFinished(e -> {
            open = true;
            opening = false;
        });
        slide.play();
    }

    public Group getDoor() {
        return door;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}