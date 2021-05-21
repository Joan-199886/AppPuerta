package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        scene = new Scene(loadFXML("ViewServer"));
        primaryStage.setScene(scene);
        primaryStage.setMaxHeight(740);
        primaryStage.setMaxWidth(1220);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Servidor Puerta De Ingreso/Salida");
        primaryStage.resizableProperty().setValue(false);

        primaryStage.setOnCloseRequest(evt -> {
            System.exit(0);
        });
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }



    public static void main(String[] args) {
        launch();
    }

}