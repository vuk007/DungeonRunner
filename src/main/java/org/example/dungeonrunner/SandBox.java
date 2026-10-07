package org.example.dungeonrunner;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SandBox extends Application {

    private final Group world = new Group();

    private final PerspectiveCamera camera =
            new PerspectiveCamera(true);

    private final Group cameraHolder =
            new Group(camera);

    private final PointLight light =
            new PointLight(Color.WHITE);

    private double x = 20;
    private double z = 20;

    private double angle = 0;

    private final Set<KeyCode> keys =
            new HashSet<>();

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
        view.setCullFace(CullFace.NONE);
        return view;
    }
    @Override
    public void start(Stage stage) {

        buildFloor();


        // world.getChildren().add(new Door(...).getDoor());
        // world.getChildren().add(new Spikes(5,5).getSpikes());
        // world.getChildren().add(buildPillar());
//        List<int[]> patrol1 = List.of(
//                new int[]{0,0},
//                new int[]{0,0}
//        );
//        Guard g = new Guard(
//                patrol1,0
//        );
        MeshView g = buildPillar(0,0);
        g.getTransforms().add(new Translate(0,-2,0));
        world.getChildren().add(g);
        camera.setNearClip(0.1);
        camera.setFarClip(1000);
        camera.setFieldOfView(75);

        world.getChildren().add(cameraHolder);
        world.getChildren().add(light);

        updateCamera();

        SubScene subScene =
                new SubScene(
                        world,
                        1200,
                        800,
                        true,
                        SceneAntialiasing.BALANCED
                );

        subScene.setFill(Color.rgb(25,25,25));
        subScene.setCamera(camera);

        Group root = new Group(subScene);

        Scene scene =
                new Scene(root);

        scene.setOnKeyPressed(e ->
                keys.add(e.getCode()));

        scene.setOnKeyReleased(e ->
                keys.remove(e.getCode()));

        new AnimationTimer() {

            long last;

            @Override
            public void handle(long now) {

                if(last==0){
                    last=now;
                    return;
                }

                double dt=(now-last)/1e9;
                last=now;

                update(dt);
            }

        }.start();

        stage.setScene(scene);
        stage.setTitle("Sandbox");
        stage.show();

        subScene.requestFocus();
    }

    private void update(double dt){

        double speed = 8 * dt;
        double rotSpeed = 90 * dt;

        if(keys.contains(KeyCode.LEFT))
            angle -= rotSpeed;

        if(keys.contains(KeyCode.RIGHT))
            angle += rotSpeed;

        double dx=Math.sin(Math.toRadians(angle));
        double dz=Math.cos(Math.toRadians(angle));

        if(keys.contains(KeyCode.W)){
            x+=dx*speed;
            z+=dz*speed;
        }

        if(keys.contains(KeyCode.S)){
            x-=dx*speed;
            z-=dz*speed;
        }

        if(keys.contains(KeyCode.A)){
            x+=dz*speed;
            z-=dx*speed;
        }

        if(keys.contains(KeyCode.D)){
            x-=dz*speed;
            z+=dx*speed;
        }

        updateCamera();
    }

    private void updateCamera(){

        cameraHolder.getTransforms().setAll(

                new Translate(
                        x,
                        -1,
                        z
                ),

                new Rotate(
                        angle,
                        Rotate.Y_AXIS
                )
        );

        light.getTransforms().setAll(

                new Translate(
                        x,
                        -1,
                        z
                )
        );
    }

    private void buildFloor(){

        Box floor =
                new Box(
                        200,
                        0.2,
                        200
                );

        floor.setTranslateX(100);
        floor.setTranslateY(0.1);
        floor.setTranslateZ(100);

        PhongMaterial mat =
                new PhongMaterial(
                        Color.DARKGRAY
                );

        floor.setMaterial(mat);

        world.getChildren().add(floor);
    }

    public static void main(String[] args){
        launch(args);
    }
}