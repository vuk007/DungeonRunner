package org.example.dungeonrunner;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.*;

public class DungeonRunner extends Application {

    private DungeonMap map;

    public DungeonMap getMap() {
        return map;
    }
    private HudManager hud;
    private Minimap minimap;
    private boolean gameOver = false;
    private StackPane rootPane;
    private Player player;
    private Group world;
    private PerspectiveCamera camera;
    private Group cameraMount;
    private PointLight torch;
    private AnimationTimer timer;
    private List<Trap> traps = new ArrayList<>();
    private List<Key> keys = new ArrayList<>();
    private List<Switch> switches = new ArrayList<>();
    private List<PickUp> pickUps = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private List<Guard> guards = new ArrayList<>();
    private boolean key_picked_up = false;
    private Sphere shieldVisual;
    public boolean isKey_picked_up() {
        return key_picked_up;
    }
    private int[] exit = {0, 0};
    public List<Trap> getTraps() {
        return traps;
    }

    public List<Door> getDoors() {
        return doors;
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


    private double lastHearthSpawn = 0;
    private double lastShieldSpawn = 0;
    private double lastPotionSpawn = 0;

    private int[] freePosition() {
        Random random = new Random();
        List<int[]> emptySpaces = new ArrayList<>();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                if (map.get(c, r) == Constants.EMPTY) {
                    emptySpaces.add(new int[]{r, c});
                }
            }
        }
        if (emptySpaces.isEmpty()) return null;
        return emptySpaces.get(random.nextInt(emptySpaces.size()));
    }

    public void spawn_hearth() {
        int[] pos = freePosition();
        if (pos == null) return;

        Hearth h = new Hearth(pos[1], pos[0]); // (column,row)
        world.getChildren().add(h.getHearth());
        pickUps.add(h);
    }

    public void spawn_shield() {
        int[] pos = freePosition();
        if (pos == null) return;

        Shield s = new Shield(pos[0], pos[1]);
        world.getChildren().add(s.getShield());
        pickUps.add(s);
    }

    public void spawn_potion() {
        int[] pos = freePosition();
        if (pos == null) return;

        Potion p = new Potion(pos[1], pos[0]);
        world.getChildren().add(p.getNode());
        pickUps.add(p);
    }

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

    private void updateLight(double dt) {
        flickerPhase += dt * 10E-9 ;

        double flicker = 0.55 * Math.sin(flickerPhase * 1.7)
                + 0.30 * Math.sin(flickerPhase * 4.1 + 1.3)
                + 0.15 * Math.sin(flickerPhase * 9.7 + 2.6);

        double intensity = 0.5 + 0.6 * flicker;

        c = Constants.POINT_LIGHT_COLOR.deriveColor(0, 1.0, intensity, 1.0);

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
                case E:
                {
                    int [] a = player.activateNearbySwitch(switches);

                    if(a!=null){

                        for(Door d:doors){

                            if(d.getRow()==a[0] &&
                                    d.getCol()==a[1]){

                                d.open();
                                break;
                            }
                        }
                    }
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


    @Override
    public void start ( Stage stage ) {
        this.world = new Group ( );
        map = new DungeonMap(Constants.CURRENT_MAP);
        generateSwitches();
        DungeonBuilder builder =
                new DungeonBuilder(map, world, traps, keys, switches, doors);
        player = builder.build();
        var patrol1 = List.of(Constants.GUARD_PATHS[Constants.index][0]);
        guards.add(new Guard(patrol1, 1.2)); // 1.2 celije/sekundi
        for(Guard g : guards){
            world.getChildren().add(g.getNode());
        }
        System.out.println(world.getChildren().size());
        exit = builder.getExit();
        setupLighting ( );
        setupCamera ( );
        SubScene gamescene = new SubScene (
                this.world,
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                true,
                SceneAntialiasing.BALANCED
        );
        gamescene.setCamera ( this.camera );

        setupInput ( gamescene );

        this.timer = new AnimationTimer ( ) {

            long last = 0;
            long last_update = 0;
            long start_time = 0;

            @Override
            public void handle(long now) {
                if (start_time == 0) {
                    start_time = now;
                    last = now;
                }

                time = (now - start_time) * 1E-9;
                player.update(DungeonRunner.this);

                updateCameraMount();
                updateTorch();
                minimap.update(player);
                if ((now - last_update) * 1E-9 > 3) {
                    updateTraps(now - last_update);
                    last_update = now;
                    player.update(DungeonRunner.this);
                }
                hud.update(time, player.getHp());
                if (!gameOver && player.getHp() <= 0) {
                    gameOver = true;
                    timer.stop();
                    hud.showEndMessage("Izgubili ste!");
                }

                if (!gameOver && player.isAtExit(DungeonRunner.this)) {
                    gameOver = true;
                    timer.stop();
                    hud.showEndMessage("Pobegli ste!");
                }


                if (player.isAtExit(DungeonRunner.this)) {
                    timer.stop();
                }
                double dt = (now - last) * 1E-9;
                for (Key k : keys) {
                    k.update(dt);
                }

                if (time - lastHearthSpawn >= 5 && pickUps.stream().filter(p -> p instanceof Hearth).count() < 2) {
                    spawn_hearth();
                    lastHearthSpawn = time;
                }

                if (time - lastShieldSpawn >= 5 && pickUps.stream().filter(p -> p instanceof Shield).count() < 2) {
                    spawn_shield();
                    lastShieldSpawn = time;
                }

                if (time - lastPotionSpawn >= 5 && pickUps.stream().filter(p -> p instanceof Potion).count() < 2) {
                    spawn_potion();
                    lastPotionSpawn = time;
                }

                for (Key k : keys) {
                    k.update(dt);
                }

                for(Guard g : guards){
                    g.update(dt);

                    if(!player.isShielded() &&
                            g.collidesWith(player.getPositionX(), player.getPositionY(), Constants.PLAYER_RADIUS + 0.3)){

                        player.hp_decrease();
                        player.resetToStart();
                        break;
                    }
                }

                for (PickUp p : pickUps) {
                    p.update(dt, player);
                    if (p.isPicked_up()) {
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
                tourch_light_animation t = new tourch_light_animation();
                t.start();
                if (key_picked_up != key_picked_up_next) {
                    updateExit();
                    key_picked_up = key_picked_up_next;
                    minimap.removeKey();
                    minimap.showExit();
                    minimap.update(player);
                }
            }
        };


        StackPane root = new StackPane();
        this.rootPane = root;


        minimap = new Minimap(map);
        root.getChildren().addAll(gamescene, minimap.getNode());

        hud = new HudManager(root);
        Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        timer.start();

        stage.setTitle ( "Beg iz tamnice" );
        stage.setScene ( scene );
        stage.setResizable ( false );
        stage.show ( );
        gamescene.requestFocus();
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


    private static class Point{

        int row;
        int col;

        Point(int row,int col){
            this.row=row;
            this.col=col;
        }

        @Override
        public boolean equals(Object o){
            if(this==o) return true;
            if(!(o instanceof Point)) return false;
            Point p=(Point)o;
            return row==p.row && col==p.col;
        }

        @Override
        public int hashCode(){
            return Objects.hash(row,col);
        }
    }

    private List<Point> reachableCells(DungeonMap map,
                                       int startRow,
                                       int startCol,
                                       Set<Point> openedDoors) {

        List<Point> result = new ArrayList<>();
        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[map.getRows()][map.getCols()];
        q.add(new Point(startRow,startCol));
        visited[startRow][startCol] = true;
        int[] dr = {-1,1,0,0};
        int[] dc = {0,0,-1,1};
        while(!q.isEmpty()){
            Point cur = q.poll();
            if(map.get(cur.col,cur.row) == Constants.EMPTY) {
                result.add(cur);
            }
            for(int i=0;i<4;i++){
                int nr = cur.row + dr[i];
                int nc = cur.col + dc[i];
                if(nr < 0 || nr >= map.getRows()
                        || nc < 0 || nc >= map.getCols())
                    continue;


                if(visited[nr][nc])
                    continue;


                int tile = map.get(nc,nr);


                if(tile == Constants.WALL)
                    continue;


                if(tile == Constants.DOOR &&
                        !openedDoors.contains(new Point(nr,nc)))
                    continue;


                visited[nr][nc] = true;
                q.add(new Point(nr,nc));
            }
        }


        return result;
    }

    public void generateSwitches(){
        Random random = new Random();
        int[] start = map.findStart();
        Set<Point> openedDoors = new HashSet<>();
        for(int r=0;r<map.getRows();r++){
            for(int c=0;c<map.getCols();c++){
                if(map.get(c,r)!=Constants.DOOR)
                    continue;
                List<Point> available = reachableCells(map, start[0], start[1], openedDoors);

                available.removeIf(p -> !map.hasNearbyWall(p.row,p.col));

                if(available.isEmpty())
                    throw new RuntimeException("Nema mesta za switch");


                Point p = available.get(random.nextInt(available.size()));
                Switch sw =
                        new Switch(p.row, p.col, r, c, map);
                switches.add(sw);
                map.set(
                        p.col,
                        p.row,
                        Constants.SWITCH
                );


                openedDoors.add(
                        new Point(r,c)
                );
            }
        }
    }
}