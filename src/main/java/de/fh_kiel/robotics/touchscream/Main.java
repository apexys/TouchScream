package de.fh_kiel.robotics.touchscream;

import de.fh_kiel.robotics.touchscream.core.Camera;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args) {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(Main.class.getClassLoader().getResource("MainGui.fxml"));

        final Scene scene = new Scene(root, 800, 600);


        primaryStage.setTitle("TouchScream");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Camera.instance().close();
    }
}
