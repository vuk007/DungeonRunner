package org.example.dungeonrunner;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HudManager {

    private Group hudTimer;
    private Group hudHealth;

    private Text timeText;
    private Text healthText;

    private StackPane root;

    public HudManager(StackPane root){
        this.root = root;
        createHUD();
    }

    private void createHUD(){
        hudTimer = new Group();
        hudHealth = new Group();

        Rectangle timeBox = new Rectangle(140,40);
        timeBox.setArcWidth(10);
        timeBox.setArcHeight(10);
        timeBox.setFill(Color.rgb(50,50,50,0.4));
        timeBox.setStroke(Color.WHITE);

        Rectangle healthBox = new Rectangle(80,40);
        healthBox.setArcWidth(10);
        healthBox.setArcHeight(10);
        healthBox.setFill(Color.rgb(50,50,50,0.4));
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

        hudTimer.setTranslateX((double)Constants.SCREEN_WIDTH/2-timeBox.getWidth()/2-10);
        hudTimer.setTranslateY(-(double)Constants.SCREEN_HEIGHT/2+20);

        hudHealth.setTranslateX(-(double)Constants.SCREEN_WIDTH/2+50);
        hudHealth.setTranslateY(-(double)Constants.SCREEN_HEIGHT/2+20);

        hudTimer.getChildren().addAll(timeBox,timeText);
        hudHealth.getChildren().addAll(healthBox,healthText);

        root.getChildren().addAll(hudTimer,hudHealth);
    }

    public void update(double time,int hp){
        timeText.setText(String.format("Time: %.2f",time));
        healthText.setText("HP: "+hp);
    }

    public void showEndMessage(String message){
        Group overlay = new Group();

        Rectangle bg = new Rectangle(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        bg.setFill(Color.rgb(0,0,0,0.6));

        Text txt = new Text(message);
        txt.setFont(Font.font(40));
        txt.setFill(Color.WHITE);
        txt.setTranslateX(Constants.SCREEN_WIDTH/2-100);
        txt.setTranslateY(Constants.SCREEN_HEIGHT/2);

        overlay.getChildren().addAll(bg,txt);
        root.getChildren().add(overlay);
    }
}