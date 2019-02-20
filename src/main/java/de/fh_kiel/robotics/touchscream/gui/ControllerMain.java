package de.fh_kiel.robotics.touchscream.gui;

import de.fh_kiel.robotics.touchscream.core.Camera;
import de.fh_kiel.robotics.touchscream.util.JavaFX;
import de.fh_kiel.robotics.touchscream.util.OpenCVToJava;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.opencv.core.Mat;

public class ControllerMain implements Camera.CameraListener {

    @FXML
    public Pane fxImagePane;
    @FXML
    public Button fxCameraToggle;
    @FXML
    public TextField fxFPS;
    @FXML
    public TextField fxCameraId;
    @FXML
    private ImageView fxVideo;

    public void initialize() {
        fxVideo.fitWidthProperty().bind(fxImagePane.widthProperty());
        fxVideo.fitHeightProperty().bind(fxImagePane.heightProperty());
    }

    private boolean isRunning = false;
    public void toggleVideo(MouseEvent mouseEvent) {
        if(!Camera.instance().registerListener(this)) {
            Camera.instance().unregisterListener(this);
        }
    }

    @Override
    public void newImage(Mat aImage) {
        Image imageToShow = OpenCVToJava.mat2Image(aImage);
        JavaFX.updateImageView(fxVideo, imageToShow);
    }

    public void toggleCamera(ActionEvent actionEvent) {
        if(!Camera.instance().isCameraActive()) {
            Camera.instance().registerListener(this);
            Camera.instance().setCameraId(Integer.parseInt(fxCameraId.getText()));
            Camera.instance().start();
            fxCameraToggle.setText("Stop");
        } else {
            Camera.instance().close();
            Camera.instance().unregisterListener(this);
            fxCameraToggle.setText("Start");
        }
    }

    public void changeFPS(ActionEvent actionEvent) {
        Camera.instance().setFPS(Integer.parseInt(fxFPS.getText()));
    }
}
