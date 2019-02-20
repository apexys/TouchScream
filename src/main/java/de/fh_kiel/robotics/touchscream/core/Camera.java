package de.fh_kiel.robotics.touchscream.core;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class Camera {

    private Camera(){}
    private static final Camera CAMERA = new Camera();
    public static Camera instance(){
        return CAMERA;
    }

    public interface CameraListener{
        void newImage(Mat aImage);
    }

    private List<CameraListener> mListener = new CopyOnWriteArrayList<>();
    public boolean registerListener( CameraListener aListener ){
        if(!mListener.contains(aListener)){
            mListener.add(aListener);
            return true;
        }
        return false;
    }
    public boolean unregisterListener( CameraListener aListener ){
        return mListener.remove(aListener);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    private int mFPS = 30;
    private boolean mCameraActive = false;
    private ScheduledExecutorService mTimer;

    private int mCameraId = 0;
    private VideoCapture mCapture = new VideoCapture();

    private volatile int mSizeOfFrameX = 640, mSizeOfFrameY=480;

    Runnable mFrameGrabber = new Runnable() {

        @Override
        public void run()
        {
            Mat mFrame = grabFrame();
            mListener.forEach(l->l.newImage(mFrame));
        }
    };

    public int getSizeOfFrameX() {
        return mSizeOfFrameX;
    }

    public int getSizeOfFrameY() {
        return mSizeOfFrameY;
    }

    public boolean isCameraActive() {
        return mCameraActive;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public void setCameraId(int aCameraId) {
        mCameraId = aCameraId;
    }

    public void setFPS(int aFPS ){
        mFPS = Math.min(Math.max(aFPS,1),1000);
        stopCapturingFrames();
        startCapturingFrames();
    }

    public int getFPS(){
        return mFPS;
    }

    private void startCapturingFrames(){
        if (this.mCapture.isOpened())
        {

            this.mTimer = Executors.newSingleThreadScheduledExecutor();
            this.mTimer.scheduleAtFixedRate(mFrameGrabber, 0, Math.max(1000/mFPS, 5), TimeUnit.MILLISECONDS);

        }
        else
        {
            System.err.println("Impossible to open the camera connection...");
        }
    }

    private void stopCapturingFrames(){
        if (this.mTimer !=null && !this.mTimer.isShutdown())
        {
            try
            {
                this.mTimer.shutdown();
                this.mTimer.awaitTermination(1000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("Exception in stopping the frame mCapture, trying to release the camera now... " + e);
            }
        }
    }

    private Mat grabFrame()
    {
        Mat vFrame = new Mat();

        if (this.mCapture.isOpened())
        {
            try
            {
                this.mCapture.read(vFrame);

                if (!vFrame.empty())
                {
                    cvtColor(vFrame, vFrame, COLOR_BGR2GRAY);

                    mSizeOfFrameX = vFrame.cols();
                    mSizeOfFrameY = vFrame.rows();
                }

            }
            catch (Exception e)
            {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }



        return vFrame;
    }

    private void stopAcquisition()
    {
        stopCapturingFrames();

        if (this.mCapture.isOpened())
        {
            this.mCapture.release();
        }
        this.mCameraActive = false;
    }


    public void start()
    {
        if (!this.mCameraActive)
        {
            this.mCapture.open(mCameraId);
            this.mCameraActive = true;
            startCapturingFrames();

        }
        else
        {
            this.mCameraActive = false;
            this.stopAcquisition();
        }
    }

    public void close()
    {
        this.stopAcquisition();
    }

}
