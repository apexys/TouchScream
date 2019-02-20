package de.fh_kiel.robotics.touchscream.core;

import javafx.geometry.Point2D;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class FingerDetection implements Camera.CameraListener {

    private FingerDetection(){}
    private static final FingerDetection FINGERDETECTION = new FingerDetection();
    public static FingerDetection instance(){
        return FINGERDETECTION;
    }

    public interface DetectionListener{
        void newBlobs(List<Blob> aBlobs);
    }

    private List<DetectionListener> mListener = new CopyOnWriteArrayList<>();
    public boolean registerListener( DetectionListener aListener ){
        if(!mListener.contains(aListener)){
            mListener.add(aListener);
            return true;
        }
        return false;
    }
    public boolean unregisterListener( DetectionListener aListener ){
        return mListener.remove(aListener);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    private int mStep = -1;
    private int mStepCounter = 0;

    @Override
    public void newImage(Mat aImage) {
        FingerFinder vFinder = null;
        synchronized (FingerDetection.instance()) {
            vFinder = new FingerFinder(mStepCounter++, aImage);
        }
        new Thread(vFinder).start();
    }

    public int getStep() {
        synchronized (FingerDetection.instance()) {
            return mStep;
        }
    }

    public void nextStep() {
        synchronized (FingerDetection.instance()) {
            mStep++;
        }
    }

    private int mThreshold = 127, mMinSize = 127, mMaxRadius = 127;
    private double mDecay = 0.03;

    private List<Blob> mOldBlobs = new ArrayList<>();

    private class FingerFinder implements Runnable {

        int mThisStep;
        Mat mFrame;

        public FingerFinder(int aStep, Mat aFrame) {
            mThisStep = aStep;
            mFrame = aFrame;
        }

        @Override
        public void run() {

            System.err.println("\t\tStarted " + mThisStep + "/" + getStep());
            //TODO: Image things that are parrallel doable
            try { Thread.sleep((int)(Math.random()*1000),100); } catch (InterruptedException e) { e.printStackTrace(); }

            while(FingerDetection.instance().getStep()+1 != mThisStep){ try { Thread.sleep(0,100); } catch (InterruptedException e) { e.printStackTrace(); } }

            //TODO: Stuff that needs to be in Order
            try { Thread.sleep((int)(Math.random()*100),100); } catch (InterruptedException e) { e.printStackTrace(); }

            mListener.forEach(l->{System.err.println("SendBlobsToListeners!"); List<Blob> vTestBlobs = new ArrayList<>(); vTestBlobs.add(new Blob(new Point2D(100,100))); vTestBlobs.add(new Blob(new Point2D(300,100))); l.newBlobs(vTestBlobs);});
            nextStep();
            System.err.println("Finished " + getStep());
        }
    };

    public int getThreshold() {
        return mThreshold;
    }

    public void setThreshold(int aThreshold) {
        synchronized (FingerDetection.instance()) {
            mThreshold = aThreshold;
        }
    }

    public int getMinSize() {
        return mMinSize;
    }

    public void setMinSize(int aMinSize) {
        synchronized (FingerDetection.instance()) {
            mMinSize = aMinSize;
        }
    }

    public int getMaxRadius() {
        return mMaxRadius;
    }

    public void setMaxRadius(int aMaxRadius) {
        synchronized (FingerDetection.instance()) {
            mMaxRadius = aMaxRadius;
        }
    }

    public double getDecay() {
        return mDecay;
    }

    public void setDecay(double aDecay) {
        synchronized (FingerDetection.instance()) {
            mDecay = aDecay;
        }
    }
}
