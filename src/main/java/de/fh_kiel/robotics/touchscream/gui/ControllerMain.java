package de.fh_kiel.robotics.touchscream.gui;

import de.fh_kiel.robotics.touchscream.core.Blob;
import de.fh_kiel.robotics.touchscream.core.Camera;
import de.fh_kiel.robotics.touchscream.core.FingerDetection;
import de.fh_kiel.robotics.touchscream.util.JavaFX;
import de.fh_kiel.robotics.touchscream.util.OpenCVToJava;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class ControllerMain implements Camera.CameraListener, FingerDetection.DetectionListener {

    @FXML
    public Button fxCameraToggle;
    @FXML
    public TextField fxFPS;
    @FXML
    public TextField fxCameraId;


    @FXML
    public Pane fxCameraVideoPane;
    @FXML
    private ImageView fxCameraVideo;
    @FXML
    public Pane fxDetectionVideoPane;
    @FXML
    public Canvas fxDetectionVideo;
    @FXML
    public Pane fxCalibrationVideoPane;
    @FXML
    public ImageView fxCalibrationVideo;


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
    public TextField fxDetectionMaxMoveRange;
    @FXML
    public Slider fxDetectionThresholdSlider;
    @FXML
    public Slider fxDetectionMaxRadiusSlider;
    @FXML
    public Slider fxDetectionMinSizeSlider;
    @FXML
    public Slider fxDetectionDecaySlider;
    @FXML
    public Slider fxDetectionMaxMoveRangeSlider;


    @FXML
    public CheckBox fxCheckCamera;
    @FXML
    public CheckBox fxCheckTouch;
    @FXML
    public CheckBox fxCheckCalibration;

    private Stage mStage = null;

    public void setStage(Stage aStage) {
        mStage = aStage;
    }

    public void initialize() {
        fxCameraVideo.fitWidthProperty().bind(fxCameraVideoPane.widthProperty());
        fxCameraVideo.fitHeightProperty().bind(fxCameraVideoPane.heightProperty());
        fxDetectionVideo.widthProperty().bind(fxDetectionVideoPane.widthProperty());
        fxDetectionVideo.heightProperty().bind(fxDetectionVideoPane.heightProperty());
        fxCalibrationVideo.fitWidthProperty().bind(fxCalibrationVideoPane.widthProperty());
        fxCalibrationVideo.fitHeightProperty().bind(fxCalibrationVideoPane.heightProperty());

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
        fxDetectionMaxMoveRangeSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {

                FingerDetection.instance().setMaxMoveRange((int) fxDetectionMaxMoveRangeSlider.getValue());
                fxDetectionMaxMoveRange.setText(Integer.toString((int) fxDetectionMaxMoveRangeSlider.getValue()));

            }
        });
    }

    public void loadConfiguration(ActionEvent actionEvent) {
        Properties vProperties = new Properties();

        final FileChooser vFileChooser = new FileChooser();
        vFileChooser.setTitle("Load TouchScream Configuration");
        vFileChooser.setInitialDirectory(new File("."));
        File vFile = vFileChooser.showOpenDialog(mStage);
        if(vFile == null){
            return;
        }
        try( InputStream vPropertiesStream = new FileInputStream(vFile) ){
            vProperties.load( vPropertiesStream );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        String vCameraId = vProperties.getProperty("CameraId", fxCameraId.getText());
        fxCameraId.setText(vCameraId);
        Camera.instance().setCameraId(Integer.parseInt(vCameraId));
        String vCameraFPS = vProperties.getProperty("CameraFPS", fxFPS.getText());
        fxFPS.setText(vCameraFPS);
        Camera.instance().setFPS(Integer.parseInt(vCameraFPS));

        String vDetectionThreshold = vProperties.getProperty("DetectionThreshold", fxDetectionThreshold.getText());
        fxDetectionThreshold.setText(vDetectionThreshold);
        fxDetectionThresholdSlider.setValue(Double.parseDouble(vDetectionThreshold));
        FingerDetection.instance().setThreshold(Integer.parseInt(vDetectionThreshold));
        String vDetectionMaxRadius = vProperties.getProperty("DetectionMaxRadius", fxDetectionMaxRadius.getText());
        fxDetectionMaxRadius.setText(vDetectionMaxRadius);
        fxDetectionMaxRadiusSlider.setValue(Double.parseDouble(vDetectionMaxRadius));
        FingerDetection.instance().setMaxRadius(Integer.parseInt(vDetectionMaxRadius));
        String vDetectionMinNumberOfPixel = vProperties.getProperty("DetectionMinNumberOfPixel", fxDetectionMinSize.getText());
        fxDetectionMinSize.setText(vDetectionMinNumberOfPixel);
        fxDetectionMinSizeSlider.setValue(Double.parseDouble(vDetectionMinNumberOfPixel));
        FingerDetection.instance().setMinSize(Integer.parseInt(vDetectionMinNumberOfPixel));
        String vDetectionDecay = vProperties.getProperty("DetectionDecay", fxDetectionDecay.getText());
        fxDetectionDecay.setText(vDetectionDecay);
        fxDetectionDecaySlider.setValue(Double.parseDouble(vDetectionDecay));
        FingerDetection.instance().setDecay(Double.parseDouble(vDetectionDecay));
        String vDetectionMaxMovementRange = vProperties.getProperty("DetectionMaxMoveRange", fxDetectionMaxMoveRange.getText());
        fxDetectionMaxMoveRange.setText(vDetectionMaxMovementRange);
        fxDetectionMaxMoveRangeSlider.setValue(Double.parseDouble(vDetectionMaxMovementRange));
        FingerDetection.instance().setMaxMoveRange(Integer.parseInt(vDetectionMaxMovementRange));
    }

    public void saveConfiguration(ActionEvent actionEvent) {

        final FileChooser vFileChooser = new FileChooser();
        vFileChooser.setTitle("Save TouchScream Configuration");
        vFileChooser.setInitialDirectory(new File("."));
        File vFile = vFileChooser.showSaveDialog(mStage);
        if(vFile == null){
            return;
        }
        try (OutputStream out = new FileOutputStream( vFile );){
            Properties vProperties = new Properties();
            vProperties.setProperty("CameraId", fxCameraId.getText());
            vProperties.setProperty("CameraFPS", fxFPS.getText());

            vProperties.setProperty("DetectionThreshold", fxDetectionThreshold.getText());
            vProperties.setProperty("DetectionMaxRadius", fxDetectionMaxRadius.getText());
            vProperties.setProperty("DetectionMinNumberOfPixel", fxDetectionMinSize.getText());
            vProperties.setProperty("DetectionDecay", fxDetectionDecay.getText());
            vProperties.setProperty("DetectionMaxMoveRange", fxDetectionMaxMoveRange.getText());

            vProperties.store(out, "Configuration TouchScream");
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public void toggleCamera(ActionEvent actionEvent) {
        if(!Camera.instance().isCameraActive()) {
            Camera.instance().setCameraId(Integer.parseInt(fxCameraId.getText()));
            Camera.instance().start();
            fxCameraToggle.setText("Stop");
        } else {
            Camera.instance().close();
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

    public void setDetectionMaxMoveRange(ActionEvent actionEvent) {
        FingerDetection.instance().setMaxMoveRange(Integer.parseInt(fxDetectionMaxMoveRange.getText()));
        fxDetectionMaxMoveRangeSlider.setValue(Integer.parseInt(fxDetectionMaxMoveRange.getText()));
    }

    public void checkVideo(ActionEvent actionEvent) {
        if(fxCheckCamera.isSelected()){
            Camera.instance().registerListener(this);
            fxCameraVideo.setVisible(true);
            fxCameraVideoPane.setMaxHeight(10000);
        } else {
            fxCameraVideo.setVisible(false);
            fxCameraVideoPane.setMaxHeight(0);
            Camera.instance().unregisterListener(this);
        }
        if(fxCheckTouch.isSelected()){
            FingerDetection.instance().registerListener(this);
            fxDetectionVideo.setVisible(true);
            fxDetectionVideoPane.setMaxHeight(10000);
        } else {
            FingerDetection.instance().unregisterListener(this);
            fxDetectionVideo.setVisible(false);
            fxDetectionVideoPane.setMaxHeight(0);
        }
        if(fxCheckCalibration.isSelected()){
            fxCalibrationVideo.setVisible(true);
            fxCalibrationVideoPane.setMaxHeight(10000);
        } else {
            fxCalibrationVideo.setVisible(false);
            fxCalibrationVideoPane.setMaxHeight(0);
        }
    }


    public void toggleCameraVideo(MouseEvent mouseEvent) {
        if(!Camera.instance().registerListener(this)) {
            Camera.instance().unregisterListener(this);
        }
    }

    public void toggleDetectionVideo(MouseEvent mouseEvent) {
        if(!FingerDetection.instance().registerListener(this)) {
            FingerDetection.instance().unregisterListener(this);
        }

    }

    public void toggleCalibrationVideo(MouseEvent mouseEvent) {
        //TODO: all
    }

    @Override
    public void newImage(Mat aImage) {
        Image imageToShow = OpenCVToJava.mat2Image(aImage);
        JavaFX.updateImageView(fxCameraVideo, imageToShow);
        // TODO: remove
        JavaFX.updateImageView(fxCalibrationVideo, imageToShow);
    }

    @Override
    public void newBlobs(List<Blob> aBlobs, Mat aImageZero) {
        Runnable vPaintBlobsOnCanvas = new Runnable() {
            @Override
            public void run() {
                GraphicsContext vGC = fxDetectionVideo.getGraphicsContext2D();

                vGC.save();

                vGC.transform(fxDetectionVideo.getWidth()/Camera.instance().getSizeOfFrameX(), 0, 0,fxDetectionVideo.getHeight()/Camera.instance().getSizeOfFrameY(), 0, 0);

                vGC.drawImage(OpenCVToJava.mat2Image(aImageZero),0,0);

                aBlobs.forEach(b->{
                    vGC.setFill(Color.WHITE);
                    vGC.setStroke(Color.WHITE);
                    b.getPoints().forEach(p->vGC.fillRect(p.getX(),p.getY(),1,1));
                    vGC.setFill(Color.ORANGE);
                    vGC.setStroke(Color.ORANGE);
                    vGC.fillOval(b.getCenter().getX(), b.getCenter().getY(),2,2);
                    vGC.setFill(Color.LIME);
                    vGC.setStroke(Color.LIME);
                    vGC.fillText(Integer.toString(b.getId()),b.getCenter().getX(), b.getCenter().getY());
                });
                vGC.restore();
            }
        };
        JavaFX.runOnFXThread(vPaintBlobsOnCanvas);
    }

    public void resetBaseImage(ActionEvent actionEvent) {
        FingerDetection.instance().setImageZeroNeedsReset(true);
    }
}
