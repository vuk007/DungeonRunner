package org.example.dungeonrunner;

import javafx.scene.Group;

public interface PickUp {
    Group getHitBox();
    void update (double dt , Player player );
    boolean isPicked_up();
}
