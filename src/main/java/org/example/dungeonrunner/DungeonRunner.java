package org.example.dungeonrunner;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class DungeonRunner extends Application {

    private DungeonMap map;

    public DungeonMap getMap() {
        return map;
    }
    private boolean gameOver = false;
    private StackPane rootPane;
    private Player player;
    private Group world;
    private PerspectiveCamera camera;
    private Group cameraMount;
    private PointLight torch;
    private AnimationTimer timer;
    private int power=0;
    private Group vreme;
    private List<Trap> traps = new ArrayList<>();
    private List<Key> keys = new ArrayList<>();
    private List<PickUp> pickUps = new ArrayList<>();
    private boolean key_picked_up = false;
    private Sphere shieldVisual;
    public boolean isKey_picked_up() {
        return key_picked_up;
    }
    private int[] exit = {0, 0};
    public List<Trap> getTraps() {
        return traps;
    }

    private boolean key_picked_up_next = false;

    public void setKey_picked_up_next() {
        this.key_picked_up_next = true;
        for ( Key k: keys){
            if((int)player.getPositionX() == k.getX()
                    &&
            (int)player.getPositionY() == k.getY())
            {this.world.getChildren().remove(k.getKey());}
        }

    }

    private PhongMaterial wallMaterial = new PhongMaterial ( );

    private MeshView buildPillar(int column, int row) {
        double cx = (double)column * 2.0 + 1.0;
        double cz = (double)row * 2.0 + 1.0;
        double s = 0.6;
        double hy = 1.0;
        float[] points = new float[]{
                (float)cx, (float)(-hy), (float)cz,
                (float)(cx - s), 0.0F, (float)(cz - s),
                (float)(cx + s), 0.0F, (float)(cz - s),
                (float)(cx + s), 0.0F, (float)(cz + s),
                (float)(cx - s), 0.0F, (float)(cz + s),
                (float)cx, (float)hy, (float)cz};
        float[] texCoords = new float[]{
                0.5F, 0.0F,
                0.0F, 1.0F,
                1.0F, 1.0F,
                0.5F, 1.0F,
                0.0F, 0.0F,
                1.0F, 0.0F};
        int[] faces = new int[]{0, 0, 2, 2, 1, 1, 0, 0, 3, 2, 2, 1, 0, 0, 4, 2, 3, 1, 0, 0, 1, 2, 4, 1, 5, 3, 1, 4, 2, 5, 5, 3, 2, 4, 3, 5, 5, 3, 3, 4, 4, 5, 5, 3, 4, 4, 1, 5};
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);
        MeshView view = new MeshView(mesh);
        view.setMaterial(this.wallMaterial);
        view.setCullFace(CullFace.NONE);
        return view;
    }

    Color c = Color.rgb(
            (int)(Constants.POINT_LIGHT_COLOR.getRed() * 255),
            (int)(Constants.POINT_LIGHT_COLOR.getGreen() * 255),
            (int)(Constants.POINT_LIGHT_COLOR.getBlue() * 255)
    );
    {
        baseR = (int)(Constants.POINT_LIGHT_COLOR.getRed() * 255);
        baseG = (int)(Constants.POINT_LIGHT_COLOR.getGreen() * 255);
        baseB = (int)(Constants.POINT_LIGHT_COLOR.getBlue() * 255);
    }

    private double flickerPhase = 0;
    private int baseR, baseG, baseB;

    private class tourch_light_animation extends AnimationTimer {
        private long lastTime = 0;

        @Override
        public void handle(long l) {
            if (start == 0) {
                start = l;
                lastTime = l;
            }
            double dt = (l - lastTime) * 1E-9;
            lastTime = l;
            time = (l - start) * 1E-9;
            updateLight(dt);
        }
    }
    private long start = 0;
    private double time = 0;
    private void buildDungeon ( ) {

        wallMaterial.setDiffuseColor ( Constants.WALL_DIFFUSE_COLOR );
        wallMaterial.setSpecularColor ( Constants.WALL_SPECULAR_COLOR );
        wallMaterial.setDiffuseMap(new Image(DungeonRunner.class.getResourceAsStream ("/com/example/begiztamnice/bricks.jpg")));
        PhongMaterial exitMaterial = new PhongMaterial();
        exitMaterial.setDiffuseColor ( Constants.EXIT_DIFFUSE_COLOR );
        exitMaterial.setSpecularColor ( Constants.EXIT_SPECULAR_COLOR );

        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(Color.rgb(60, 40, 20));

        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(Color.rgb(25, 25, 45));

        this.map = new DungeonMap ( Constants.CURRENT_MAP );


        int    rows       = this.map.getRows ( );
        int    columns    = this.map.getCols ( );
        double totalWidth = columns * Constants.CELL_SIZE;
        double totalDepth = rows * Constants.CELL_SIZE;

        Box floor = new Box ( totalWidth, Constants.SLAB_THICKNESS, totalDepth );
        Translate floorTranslate = new Translate (
                totalWidth / 2.0,
                Constants.WALL_HEIGHT / 2.0 + Constants.SLAB_THICKNESS / 2.0,
                totalDepth / 2.0
        );
        floor.getTransforms ( ).add ( floorTranslate );
        floor.setMaterial ( floorMaterial );

        Box ceiling = new Box ( totalWidth, Constants.SLAB_THICKNESS, totalDepth );
        Translate ceilingTranslate = new Translate (
                totalWidth / 2.0,
                -Constants.WALL_HEIGHT / 2.0 - Constants.SLAB_THICKNESS / 2.0,
                totalDepth / 2.0
        );
        ceiling.getTransforms ( ).add ( ceilingTranslate );
        ceiling.setMaterial ( ceilingMaterial );
        tourch_light_animation t = new tourch_light_animation();
        t.start();
        this.world.getChildren ( ).addAll ( floor, ceiling );

        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                int tile = this.map.get ( column, row );
                if ( tile == Constants.WALL || tile == Constants.EXIT ) {
                    Box wall = new Box ( Constants.CELL_SIZE, Constants.WALL_HEIGHT, Constants.CELL_SIZE );

                    Translate wallTranslate = new Translate (
                            column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0,
                            0,
                            row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0
                    );
                    wall.getTransforms ( ).add ( wallTranslate );
                    wall.setMaterial (  wallMaterial );
                    if(tile==Constants.EXIT){exit[0] = row ; exit[1] = column;}
                    this.world.getChildren().add ( wall );
                } else if (tile == Constants.OCT) {
                    this.world.getChildren().add(buildPillar(column,row));
                } else if (tile == Constants.SPIKE) {
                    Spikes s = new Spikes(column,row);
                    traps.add( s );
                    this.world.getChildren().add(s.getSpikes());
                } else if (tile == Constants.KEY) {
                    Key key = new Key(row,column);
                    this.keys.add(key);
                    this.world.getChildren().add(key.getKey());
                } else if (tile == Constants.START) {
                    this.player = new Player ( row+0.5, column +0.5 );
                }
            }
        }
        Potion s = new Potion(1,1);
        pickUps.add(s);
        this.world.getChildren().add(s.getNode());
    }

    private Group minimap;
    private Circle playerMarker;
    private Line directionLine;
    private Circle keyMarker;
    private Circle exitMarker;

    private void createMinimap(){
        minimap = new Group();
        double cell = 15;
        Rectangle background = new Rectangle(map.getCols() * cell + 10, map.getRows() * cell + 10);
        background.setFill(Color.rgb(0,0,0,0.55));
        background.setStroke(Color.WHITE);
        minimap.getChildren().add(background);
        for(int y = 0; y < map.getRows(); y++){
            for(int x = 0; x < map.getCols(); x++){
                Rectangle tile = new Rectangle(x * cell + 5, y * cell + 5, cell, cell);
                int value = map.get(x,y);
                if(value == Constants.WALL || value == Constants.EXIT ){
                    tile.setFill(Color.DARKGRAY);
                }
                else{
                    tile.setFill(Color.TRANSPARENT);
                }
                tile.setStroke(Color.rgb(70,70,70));
                minimap.getChildren().add(tile);
                if(value == Constants.KEY){
                    keyMarker = new Circle(
                            x * cell + cell/2 + 5,
                            y * cell + cell/2 + 5,
                            3,
                            Color.YELLOW
                    );
                    minimap.getChildren().add(keyMarker);
                }
                if(value == Constants.EXIT){
                    exitMarker = new Circle(x * cell + cell/2 + 5, y * cell + cell/2 + 5, 3, Color.GREEN);
                }

            }
        }



        playerMarker = new Circle(0, 0, 4, Color.WHITE);


        directionLine = new Line();
        directionLine.setStroke(Color.WHITE);
        directionLine.setStrokeWidth(2);

        minimap.getChildren().addAll(directionLine, playerMarker);
        minimap.setTranslateX(Constants.SCREEN_WIDTH - map.getCols()*cell - 30);
        minimap.setTranslateY(Constants.SCREEN_HEIGHT - map.getRows()*cell - 30);
    }


    private void updateMinimap(){
        double cell = 15;
        double x = player.getPositionX() * cell + 5;
        double y = player.getPositionY() * cell + 5;
        playerMarker.setCenterX(x);
        playerMarker.setCenterY(y);
        directionLine.setStartX(x);
        directionLine.setStartY(y);
        directionLine.setEndX(x + player.getDirectionX()*10);
        directionLine.setEndY(y + player.getDirectionY()*10);
    }
    private void updateLight(double dt) {
        flickerPhase += dt;

        double flicker = 0.55 * Math.sin(flickerPhase * 1.7)
                + 0.30 * Math.sin(flickerPhase * 4.1 + 1.3)
                + 0.15 * Math.sin(flickerPhase * 9.7 + 2.6);
        double intensity = 0.5 + 0.6 * flicker;
        int r = (int) Math.clamp(baseR * intensity, 0, 255);
        int g = (int) Math.clamp(baseG * intensity, 0, 255);
        int b = (int) Math.clamp(baseB * intensity, 0, 255);
        c = Color.rgb(r, g, b);
        this.torch.setColor(c);
    }
    private void setupLighting ( ) {
        AmbientLight ambient = new AmbientLight ( Constants.AMBIENT_LIGHT_COLOR );

        this.torch = new PointLight ( Constants.POINT_LIGHT_COLOR );
        this.torch.setMaxRange ( Constants.CELL_SIZE * 6 );

        this.world.getChildren ( ).addAll ( ambient, torch );
    }

    private void setupCamera ( ) {
        this.camera = new PerspectiveCamera ( true );

        this.camera.setNearClip ( Constants.CAMERA_NEAR_CLIP );
        this.camera.setFarClip ( Constants.CAMERA_FAR_CLIP );
        this.camera.setFieldOfView ( Constants.CAMERA_FIELD_OF_VIEW );

        this.cameraMount = new Group ( this.camera );

        this.world.getChildren ( ).add ( cameraMount );

        updateCameraMount ( );
    }

    private void setupInput ( SubScene scene ) {
        scene.setOnKeyPressed ( event -> {
            switch ( event.getCode ( ) ) {
                case UP: {
                    this.player.setMoveForward ( true );
                    break;
                }
                case DOWN: {
                    this.player.setMoveBackward ( true );
                    break;
                }
                case LEFT: {
                    this.player.setRotateLeft ( true );
                    break;
                }
                case RIGHT: {
                    this.player.setRotateRight ( true );
                    break;
                }
            }
        } );

        scene.setOnKeyReleased ( event -> {
            switch ( event.getCode ( ) ) {
                case UP: {
                    this.player.setMoveForward ( false );
                    break;
                }
                case DOWN: {
                    this.player.setMoveBackward ( false );
                    break;
                }
                case LEFT: {
                    this.player.setRotateLeft ( false );
                    break;
                }
                case RIGHT: {
                    this.player.setRotateRight ( false );
                    break;
                }
            }
        } );
    }

    private void updateCameraMount ( ) {
        Translate camerMountTranslate = new Translate (
                this.player.getPositionX( ) * Constants.CELL_SIZE,
                0,
                this.player.getPositionY( ) * Constants.CELL_SIZE
        );

        Rotate camerMountRotate = new Rotate (
                Math.toDegrees ( Math.atan2 ( this.player.getDirectionX ( ), this.player.getDirectionY ( ) ) ),
                Rotate.Y_AXIS
        );

        this.cameraMount.getTransforms ( ).setAll (
                camerMountTranslate,
                camerMountRotate
        );
    }
    private void updateTorch() {
        Translate torchTranslate = new Translate (
                this.player.getPositionX( ) * Constants.CELL_SIZE,
                0,
                this.player.getPositionY( ) * Constants.CELL_SIZE
        );

        this.torch.getTransforms ( ).setAll ( torchTranslate );
    }


    private Text timeText;
    private Text healthText;
    @Override
    public void start ( Stage stage ) {

        this.world = new Group ( );
        buildDungeon ( );
        setupLighting ( );
        setupCamera ( );
        createMinimap();
        SubScene gamescene = new SubScene (
                this.world,
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                true,
                SceneAntialiasing.BALANCED
        );
        gamescene.setCamera ( this.camera );

        setupInput ( gamescene );

        Group hudtimer = new Group();


        Group hudhealth = new Group();

        Group hudmap = new Group();
        hudmap.getChildren().add(minimap);


        this.timer = new AnimationTimer ( ) {

            long last = 0;
            long last_update = 0;
            long start_time = 0;
            @Override
            public void handle ( long now ) {
                if (start_time ==0){
                    start_time = now;
                    last = now ;
                }

                time = (now - start_time) * 1E-9;
                player.update ( DungeonRunner.this );

                updateCameraMount ( );
                updateTorch ( );
                if((now - last_update) * 1E-9 > 3){
                    updateTraps(now - last_update);
                    last_update = now;
                    player.update(DungeonRunner.this);
                }
                updateHud( );
                if ( !gameOver && player.getHp() <= 0 ) {
                    gameOver = true;
                    timer.stop ( );
                    showEndMessage("Izgubili ste!");
                }

                if ( !gameOver && player.isAtExit ( DungeonRunner.this ) ) {
                    gameOver = true;
                    timer.stop ( );
                    showEndMessage("Pobegli ste!");
                }


                if ( player.isAtExit ( DungeonRunner.this ) ) {
                    timer.stop ( );
                }
                double dt = (now - last)*1E-9;
                for(Key k : keys) {
                    k.update(dt);
                }
                for (PickUp p : pickUps){
                    p.update(dt , player);
                    if(p.isPicked_up()){
                        pickUps.remove(p);
                        world.getChildren().remove(p.getHitBox());
                        if (p instanceof Shield) {
                            activatePlayerShield(10.0); // trajanje štita u sekundama, podesi po želji
                        }
                        break;

                    }
                }
                player.updateShield(dt);
                updateShieldVisual();
                last = now;
                if(key_picked_up != key_picked_up_next){
                    updateExit();
                    key_picked_up = key_picked_up_next;
                    if(keyMarker != null){
                        minimap.getChildren().remove(keyMarker);
                    }


                    if(exitMarker != null){
                        minimap.getChildren().add(exitMarker);
                    }
                }
                updateMinimap();
            }
        };




        StackPane root = new StackPane();
        this.rootPane = root;
        root.getChildren().addAll(gamescene, hudtimer, hudhealth,hudmap);

        Scene scene = new Scene(
                root,
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT
        );
        timer.start ( );


        /* BOX ZA VREME */
        Rectangle timeBox = new Rectangle(140, 40);
        timeBox.setArcWidth(10);
        timeBox.setArcHeight(10);
        timeBox.setFill(Color.rgb(50, 50, 50, 0.4));
        timeBox.setStroke(Color.WHITE);

        Rectangle healthBox = new Rectangle(80, 40);
        healthBox.setArcWidth(10);
        healthBox.setArcHeight(10);
        healthBox.setFill(Color.rgb(50, 50, 50, 0.4));
        healthBox.setStroke(Color.WHITE);



        timeText = new Text("Time: 00:00");
        timeText.setFont(Font.font(20));
        timeText.setFill(Color.WHITE);

        timeText.setTranslateX(10);
        timeText.setTranslateY(25);

        healthText = new Text("HP: 3");
        healthText.setFont(Font.font(20));
        healthText.setFill(Color.WHITE);

        healthText.setTranslateX(10);
        healthText.setTranslateY(25);


        hudtimer.setTranslateX((double) Constants.SCREEN_WIDTH /2 - timeBox.getWidth()/2 - 10);
        hudtimer.setTranslateY((double) -Constants.SCREEN_HEIGHT /2 + timeBox.getHeight()/2 + 10);
        hudtimer.getChildren().addAll(timeBox, timeText, healthBox ,healthText);

        hudhealth.setTranslateX((double) -Constants.SCREEN_WIDTH /2 + healthBox.getWidth()/2 + 10);
        hudhealth.setTranslateY((double) -Constants.SCREEN_HEIGHT /2 + healthBox.getHeight()/2 + 10);
        hudhealth.getChildren().addAll(healthBox,healthText);

        hudmap.setTranslateY(Constants.SCREEN_HEIGHT/2 - minimap.getChildren().size());
        stage.setTitle ( "Beg iz tamnice" );
        stage.setScene ( scene );
        stage.setResizable ( false );
        stage.show ( );
        gamescene.requestFocus();
    }

    private void showEndMessage(String message) {
        StackPane overlay = new StackPane();
        Rectangle bg = new Rectangle(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        bg.setFill(Color.rgb(0, 0, 0, 0.6));

        Text msg = new Text(message);
        msg.setFont(Font.font(40));
        msg.setFill(Color.WHITE);

        overlay.getChildren().addAll(bg, msg);
        rootPane.getChildren().add(overlay);
    }

    private void activatePlayerShield(double durationSeconds) {
        player.activateShield(durationSeconds);
        if (shieldVisual == null) {
            shieldVisual = new Sphere(Constants.PLAYER_RADIUS * Constants.CELL_SIZE * 2);
            PhongMaterial shieldMaterial = new PhongMaterial();
            shieldMaterial.setDiffuseColor(Color.rgb(60, 120, 255, 0.1));
            shieldMaterial.setSpecularColor(Color.rgb(150, 200, 255, 0.3));
            shieldVisual.setMaterial(shieldMaterial);
            shieldVisual.setCullFace(CullFace.NONE);
            shieldVisual.setMouseTransparent(true);
        }
        if (!world.getChildren().contains(shieldVisual)) {
            world.getChildren().add(shieldVisual);
        }
    }

    private void updateShieldVisual() {
        if (shieldVisual == null) return;
        if (player.isShielded()) {
            Translate t = new Translate(
                    player.getPositionX() * Constants.CELL_SIZE,
                    0,
                    player.getPositionY() * Constants.CELL_SIZE
            );
            shieldVisual.getTransforms().setAll(t);
            if (!world.getChildren().contains(shieldVisual)) {
                world.getChildren().add(shieldVisual);
            }
        } else {
            world.getChildren().remove(shieldVisual);
        }
    }
    private void updateTraps( long dt) {
        for (Trap t : traps){
            t.update(0);
        }
    }

    private void updateExit() {
        Box wall = new Box ( Constants.CELL_SIZE, Constants.WALL_HEIGHT, Constants.CELL_SIZE );

        Translate wallTranslate = new Translate (
                exit[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0,
                0,
                exit[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0
        );
        PhongMaterial exitMaterial = new PhongMaterial();
        exitMaterial.setDiffuseColor ( Constants.EXIT_DIFFUSE_COLOR );
        exitMaterial.setSpecularColor ( Constants.EXIT_SPECULAR_COLOR );
        wall.getTransforms ( ).add ( wallTranslate );
        wall.setMaterial ( exitMaterial );
        this.world.getChildren().add ( wall );
    }
    private void updateHud() {
        String time_txt = String.format("Time: %.2f" , time );
        timeText.setText( time_txt );
        healthText.setText("HP: " + player.getHp());
    }

}