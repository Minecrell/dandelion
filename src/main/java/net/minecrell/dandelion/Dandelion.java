package net.minecrell.dandelion;

import static com.google.common.base.Preconditions.checkState;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dandelion extends Application {

    private static Dandelion instance;

    public static Dandelion getInstance() {
        checkState(instance != null, "Dandelion has not been initialized");
        return instance;
    }

    private Stage primaryStage;
    private Scene scene;

    public Dandelion() {
        checkState(instance == null, "Dandelion was already initialized");
        instance = this;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("main.fxml"));
        Parent root = loader.load();

        scene = new Scene(root);
        primaryStage.setTitle("Dandelion");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
