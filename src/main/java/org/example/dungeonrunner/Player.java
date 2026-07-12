package org.example.dungeonrunner;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class Player {
    private double positionX;
    private double startX;
    private double startY;
    private double positionY;
    private double directionX;
    private double directionY;
    private boolean moveForward;
    private boolean moveBackward;
    private boolean rotateLeft;
    private boolean rotateRight;
    private boolean invers_controls = false;
    private int hp = 3;

    public int getHp() {
        return hp;
    }

    public void hp_decrease() {
        if (shielded) return;
        if (this.hp > 0) this.hp--; }
    public Player ( double startX, double startY ) {
        this.startX = startX;
        this.startY = startY;
        this.positionX = startX;
        this.positionY = startY;
        this.directionX =  1.0;
        this.directionY =  0.0;
    }

    public double getPositionX ( ) { return this.positionX; }
    public double getPositionY ( ) { return this.positionY; }
    public double getDirectionX ( ) { return this.directionX; }
    public double getDirectionY ( ) { return this.directionY; }

    public void setMoveForward  ( boolean newValue ) { this.moveForward  = newValue; }
    public void setMoveBackward ( boolean newValue ) { this.moveBackward = newValue; }
    public void setRotateLeft   ( boolean newValue ) { this.rotateLeft   = newValue; }
    public void setRotateRight  ( boolean newValue ) { this.rotateRight  = newValue; }

    public void update ( DungeonRunner game ) {
        double speed = Constants.PLAYER_MOVE_SPEED;
        double speed_rotation = Constants.PLAYER_ROTATION_SPEED;
        if(invers_controls){
            speed *= -1;
            speed_rotation *= -1;
        }
        if ( this.moveForward ) {
            double newX = this.positionX + this.directionX * speed;
            double newY = this.positionY + this.directionY * speed;

            if ( canMoveTo ( newX, positionY, game ) ) {
                this.positionX = newX;
            }

            if ( canMoveTo ( positionX, newY, game ) ) {
                this.positionY = newY;
            }
        }

        if ( this.moveBackward ) {
            double newX = this.positionX - this.directionX * speed;
            double newY = this.positionY - this.directionY * speed;

            if ( canMoveTo ( newX, positionY, game ) ) {
                this.positionX = newX;
            }

            if ( canMoveTo ( positionX, newY, game ) ) {
                this.positionY = newY;
            }
        }

        if ( this.rotateLeft ) {
            rotate ( speed_rotation );
        }
        if ( this.rotateRight ) {
            rotate ( -speed_rotation );
        }
    }

    public boolean isAtExit ( DungeonRunner game ) {
        return game.getMap().get ( ( int ) this.positionX, ( int ) this.positionY ) == Constants.EXIT;
    }
    private boolean canMoveTo ( double x, double y, DungeonRunner game ) {
        return isFree ( ( int ) ( x + Constants.PLAYER_RADIUS ), ( int ) ( y + Constants.PLAYER_RADIUS ), game )
            && isFree ( ( int ) ( x + Constants.PLAYER_RADIUS ), ( int ) ( y - Constants.PLAYER_RADIUS ), game )
            && isFree ( ( int ) ( x - Constants.PLAYER_RADIUS ), ( int ) ( y + Constants.PLAYER_RADIUS ), game )
            && isFree ( ( int ) ( x - Constants.PLAYER_RADIUS ), ( int ) ( y - Constants.PLAYER_RADIUS ), game );
    }

    private boolean isFree ( int x, int y, DungeonRunner game ) {
        DungeonMap map = game.getMap();
        int tile = map.get ( x, y );
        if (tile == Constants.SPIKE){
            for(Trap t : game.getTraps()){
                if(t.getColumn() == x && t.getRow() == y && t.getY() == 0){
                    if (!shielded) {
                        positionX = startX;
                        positionY = startY;
                        hp_decrease();
                        return false;
                    }
                    return true;
                }
            }
            return true;
        } else if (tile == Constants.EXIT && !game.isKey_picked_up()) {
            return false;
        } else if (tile == Constants.KEY) {
            game.setKey_picked_up_next();
            return true;
        } else{
            return tile == Constants.EMPTY || tile == Constants.EXIT||tile==Constants.START ;
        }


    }
    private boolean shielded = false;
    private double shieldTimeRemaining = 0;

    public boolean isShielded() {
        return shielded;
    }

    public void activateShield(double durationSeconds) {
        this.shielded = true;
        this.shieldTimeRemaining = durationSeconds;
    }

    public void updateShield(double dt) {
        if (shielded) {
            shieldTimeRemaining -= dt;
            if (shieldTimeRemaining <= 0) {
                shielded = false;
                shieldTimeRemaining = 0;
            }
        }
    }

    private void rotate ( double angle ) {
        Point2D newDirection = new Rotate ( Math.toDegrees ( angle ) ).transform ( this.directionX, this.directionY );
        this.directionX = newDirection.getX ( );
        this.directionY = newDirection.getY ( );
    }

    public void drunk(){
        invers_controls = true;
        AnimationTimer a = new AnimationTimer() {
            double start = 0;
            @Override
            public void handle(long l) {
                if (start == 0) start = l;
                if(1e-9*(l - start) > 3) {
                    invers_controls = false;
                    stop();
                }
            }
        };
        a.start();
    }

    public void heal(double healAmount) {
        this.hp += (int) healAmount;
        if(hp >= 3) hp = 3;
    }
}