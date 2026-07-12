package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Spikes implements Trap{

    private double column,y, row;

    @Override
    public double getY() {
        return y;
    }

    public double getColumn() {
        return column;
    }

    public double getRow() {
        return row;
    }

    private boolean up = false; // 1 dole ,0 gore
    private double height = 0.8;
    public Group spikes;
    private MeshView meshView;

    public Group getSpikes() {
        return spikes;
    }

    public MeshView getMeshView() {return meshView;}
    Spikes(double column, double row) {

        this.column = column;
        this.row = row;
        this.y = 0;
        spikes = new Group();

        double d = 0.3;
        if(100*Math.random()< 50) up =true;
        spikes.getChildren().add(buildPillar(-d, -d));
        spikes.getChildren().add(buildPillar(d, -d));
        spikes.getChildren().add(buildPillar(-d, d));
        spikes.getChildren().add(buildPillar(d, d));
        update(0);
    }

    private MeshView buildPillar(double xOffset, double zOffset) {
        double cx = column*2+1 + xOffset;
        double cz = row*2+1 + zOffset;

        double s = 0.1;
        double hy = this.y + height;

        float[] points = new float[]{
                (float)cx, (float)(1-hy), (float)cz,
                (float)(cx - s), (float)(1), (float)(cz - s),
                (float)(cx + s), (float)(1), (float)(cz - s),
                (float)(cx + s), (float)(1), (float)(cz + s),
                (float)(cx - s), (float)(1), (float)(cz + s)
        };

        float[] texCoords = new float[]{
                0.5F,0.0F,
                0.0F,1.0F,
                1.0F,1.0F,
                1.0F,0.0F,
                0.0F,0.0F
        };

        int[] faces = new int[]{
                0,0, 4,1, 3,2,
                0,0, 3,2, 2,3,
                0,0, 2,3, 1,4,
                0,0, 1,4, 4,1,
                1,4, 2,3, 3,2,
                1,4, 3,2, 4,1
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        MeshView view = new MeshView(mesh);

        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(Color.web("#252424FF"));
        mat.setSpecularColor(Color.rgb(180, 180, 180));
        mat.setSpecularPower(20);

        view.setMaterial(mat);
        view.setCullFace(CullFace.NONE);

        return view;
    }
    @Override
    public void update(long dt) {
        System.out.println("AAAAA");
        if(up){
            up = false;
            spikes.setTranslateY(30);
            y = 30;
        }
        else{
            up = true;
            spikes.setTranslateY(0);
            y=0;
        }

    }
}
