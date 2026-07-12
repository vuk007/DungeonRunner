package org.example.dungeonrunner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu extends Application {

    private int selectedIndex = 0;
    private Text[] mapLabels;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgb(18,18,28);");

        Text title = new Text("BEG IZ TAMNICE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.WHITE);

        Text subtitle = new Text("Strelice LEVO/DESNO za izbor mape, ENTER za start");
        subtitle.setFont(Font.font(16));
        subtitle.setFill(Color.LIGHTGRAY);

        VBox mapList = new VBox(12);
        mapList.setAlignment(Pos.CENTER);
        mapLabels = new Text[Constants.MAPS.length];
        for (int i = 0; i < Constants.MAPS.length; i++) {
            Text label = new Text();
            label.setFont(Font.font(24));
            mapLabels[i] = label;
            mapList.getChildren().add(label);
        }

        root.getChildren().addAll(title, subtitle, mapList);

        Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        updateSelection();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    selectedIndex = (selectedIndex - 1 + Constants.MAPS.length) % Constants.MAPS.length;
                    updateSelection();
                    break;
                case RIGHT:
                    selectedIndex = (selectedIndex + 1) % Constants.MAPS.length;
                    updateSelection();
                    break;
                case ENTER:
                    startGame(stage);
                    break;
                default:
                    break;
            }
        });

        stage.setTitle("Beg iz tamnice - Meni");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void updateSelection() {
        for (int i = 0; i < mapLabels.length; i++) {
            if (i == selectedIndex) {
                mapLabels[i].setFill(Color.YELLOW);
                mapLabels[i].setText("> " + Constants.MAP_NAMES[i] + " <");
            } else {
                mapLabels[i].setFill(Color.WHITE);
                mapLabels[i].setText(Constants.MAP_NAMES[i]);
            }
        }
    }

    private void startGame(Stage stage) {
        Constants.CURRENT_MAP = Constants.MAPS[selectedIndex];
        DungeonRunner game = new DungeonRunner();
        game.start(stage); // reciklira isti stage, igra preuzima scenu
    }

    public static void main(String[] args) {
        launch(args);
    }
}