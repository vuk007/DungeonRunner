package org.example.dungeonrunner;

public interface Trap {
    double getColumn();
    double getRow();
    double getY();
    void update(long dt);
}
