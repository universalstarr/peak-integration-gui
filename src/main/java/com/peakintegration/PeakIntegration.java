package com.peakintegration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PeakIntegration extends Application {
    private PeakIntegrationModel model;
    public PeakIntegration(){
        model = new PeakIntegrationModel();
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        Stage stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(PeakIntegration.class.getResource("spectrum.fxml"));
        Parent root = fxmlLoader.load();
        PeakIntegrationController controller = fxmlLoader.getController();
        model.addObserver(controller);
        controller.setUp();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("PeakIntegration!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}