package com.idlerpg;

import com.idlerpg.controller.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private AppController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 720);
        scene.getStylesheets().add(Main.class.getResource("/view/css/app.css").toExternalForm());

        controller = loader.getController();
        stage.setTitle("Idle RPG Framework");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.setOnCloseRequest(event -> controller.shutdown());
        stage.show();
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
