package de.fh_kiel.robotics.touchscream;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;

public class Main extends Application implements Runnable{

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private ImageView imgpane;

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("MainGui.fxml"));

        final Scene scene = new Scene(root, 800, 600);


        primaryStage.setTitle("TouchScream");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();

    }

    @Override
    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        // 1 for next camera
        int i = 0;
        try {

            Mat frame = new Mat();
            while (true) {
                camera.read(frame);
                MatOfByte byteMat = new MatOfByte();
                Imgcodecs.imencode(".bmp", frame, byteMat);
                Image im = new Image(new ByteArrayInputStream(byteMat.toArray()));
                imgpane.setImage(im);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
