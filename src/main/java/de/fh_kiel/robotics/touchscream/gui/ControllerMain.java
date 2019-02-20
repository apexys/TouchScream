package de.fh_kiel.robotics.touchscream.gui;

import de.fh_kiel.robotics.touchscream.core.Camera;
import de.fh_kiel.robotics.touchscream.core.FingerDetection;
import de.fh_kiel.robotics.touchscream.util.JavaFX;
import de.fh_kiel.robotics.touchscream.util.OpenCVToJava;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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


    @FXML
    public Button fxTouchDetectionToggle;
    @FXML
    public TextField fxDetectionThreshold;
    @FXML
    public TextField fxDetectionMaxRadius;
    @FXML
    public TextField fxDetectionMinSize;
    @FXML
    public TextField fxDetectionDecay;
    @FXML
    public Slider fxDetectionThresholdSlider;
    @FXML
    public Slider fxDetectionMaxRadiusSlider;
    @FXML
    public Slider fxDetectionMinSizeSlider;
    @FXML
    public Slider fxDetectionDecaySlider;



    public void initialize() {
        fxVideo.fitWidthProperty().bind(fxImagePane.widthProperty());
        fxVideo.fitHeightProperty().bind(fxImagePane.heightProperty());

        fxDetectionThresholdSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {

                FingerDetection.instance().setThreshold((int) fxDetectionThresholdSlider.getValue());
                fxDetectionThreshold.setText(Integer.toString((int) fxDetectionThresholdSlider.getValue()));

            }
        });
        fxDetectionMaxRadiusSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {

                FingerDetection.instance().setMaxRadius((int) fxDetectionMaxRadiusSlider.getValue());
                fxDetectionMaxRadius.setText(Integer.toString((int) fxDetectionMaxRadiusSlider.getValue()));

            }
        });
        fxDetectionMinSizeSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {

                FingerDetection.instance().setMinSize((int) fxDetectionMinSizeSlider.getValue());
                fxDetectionMinSize.setText(Integer.toString((int) fxDetectionMinSizeSlider.getValue()));

            }
        });
        fxDetectionDecaySlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {

                FingerDetection.instance().setDecay(fxDetectionDecaySlider.getValue());
                fxDetectionDecay.setText(Double.toString(fxDetectionDecaySlider.getValue()));

            }
        });
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

    public void toggleDetection(ActionEvent actionEvent) {
        if(!Camera.instance().registerListener(FingerDetection.instance())) {
            Camera.instance().unregisterListener(FingerDetection.instance());
            fxTouchDetectionToggle.setText("Start");
        } else {
            fxTouchDetectionToggle.setText("Stop");
        }
    }

    public void setDetectionThreshold(ActionEvent actionEvent) {
        FingerDetection.instance().setThreshold(Integer.parseInt(fxDetectionThreshold.getText()));
        fxDetectionThresholdSlider.setValue(Integer.parseInt(fxDetectionThreshold.getText()));
    }

    public void setDetectionMaxRadius(ActionEvent actionEvent) {
        FingerDetection.instance().setMaxRadius(Integer.parseInt(fxDetectionMaxRadius.getText()));
        fxDetectionMaxRadiusSlider.setValue(Integer.parseInt(fxDetectionMaxRadius.getText()));
    }

    public void setDetectionMinSize(ActionEvent actionEvent) {
        FingerDetection.instance().setMinSize(Integer.parseInt(fxDetectionMinSize.getText()));
        fxDetectionMinSizeSlider.setValue(Integer.parseInt(fxDetectionMinSize.getText()));
    }

    public void setDetectionDecay(ActionEvent actionEvent) {
        FingerDetection.instance().setDecay(Double.parseDouble(fxDetectionDecay.getText()));
        fxDetectionDecaySlider.setValue(Double.parseDouble(fxDetectionDecay.getText()));
    }
}
