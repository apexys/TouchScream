package de.fh_kiel.robotics.touchscream.core;

import javafx.geometry.Point2D;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class FingerDetection implements Camera.CameraListener {

    private FingerDetection(){}
    private static final FingerDetection FINGERDETECTION = new FingerDetection();
    public static FingerDetection instance(){
        return FINGERDETECTION;
    }


    public interface DetectionListener{
        void newBlobs(List<Blob> aBlobs, Mat baseImage);
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

    private volatile int mThreshold = 127, mMinSize = 127, mMaxRadius = 127, mMaxMoveRange = 127;
    private volatile double mDecay = 0.03;
    private Mat mImageZero;
    private boolean mImageZeroNeedsReset = false;

    private List<Blob> mOldBlobs = new CopyOnWriteArrayList<>();

    private class FingerFinder implements Runnable {

        private final int mThisStep;
        Mat mFrame;

        public FingerFinder(int aStep, Mat aFrame) {
            mThisStep = aStep;
            mFrame = aFrame;
        }

        @Override
        public void run() {
            System.err.println("\t\tStarted " + mThisStep + "/" + getStep());
            processImage();
            nextStep();
            System.err.println("\t\t\tFinished " + getStep());
        }

        private void processImage(){
            try {
                if (getImageZero() == null || doesImageZeroNeedReset()) {
                    try {
                        Thread.sleep(500, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setImageZero(mFrame);
                    setImageZeroNeedsReset(false);
                    System.err.println(mThisStep + " Image reset");
                    return;
                }

                Mat vImageZero = new Mat();
                getImageZero().copyTo(vImageZero);

                //TODO: Image things that are parrallel doable

                List<Point2D> points = new ArrayList<>(1000);

                System.err.println(mThisStep + " " + "Cols: " + mFrame.cols());

                //Find pixels over thresh value
                for (int y = 0; y < mFrame.rows(); y++) {
                    for (int x = 0; x < mFrame.cols(); x++) {
                        boolean inBlob = false;
                        double baseValue = vImageZero.get(y,x)[0]; //TODO: If image size changes, this breaks
                        double imgValue = mFrame.get(y,x)[0];
                        double data = (imgValue - baseValue);

                        for(Blob b : mOldBlobs){
                            if(b.getCenter().distance(x,y) < mMinSize && data > getThreshold()){ //TODO: Possibly increase radius to account for movement
                                inBlob = true;
                                break;
                            }
                        }

                        if(!inBlob){
                            vImageZero.put(y,x, Math.max(Math.min(baseValue + ((imgValue -   baseValue) * mDecay), 255), 0));
                        };


                        if (data > getThreshold()) {
                            points.add(new Point2D(x, y));
                        }
                    }
                }



                Mat diffImage = new Mat();
                Core.subtract(mFrame, getImageZero(), diffImage);

                if(points.size() > 50000){
                    return;
                }

                System.err.println(mThisStep + " " + "Found " + points.size() + " points");

                List<Blob> foundBlobs = new CopyOnWriteArrayList<>();
                if(points.size() > 0) {
                    foundBlobs.add(new Blob(points.remove(points.size() - 1)));

                    points.stream().forEach(p -> {
                        foundBlobs.stream()
                                .filter(blob -> blob.getCenter().distance(p) < mMaxRadius)
                                .findAny()
                                .orElseGet(() -> {
                                    Blob b = new Blob();
                                    foundBlobs.add(b);
                                    return b;
                                })
                                .addPoint(p);
                    });
                }

                List<Blob> validBlobs = foundBlobs.stream().filter(b -> b.getPoints().size() > mMinSize).collect(Collectors.toList());

                System.err.println(mThisStep + " " + "Found " + foundBlobs.size() + " blobs");

                //#####################################################################################################
                //AB HIER IN SEQUENCE
                //#####################################################################################################
                while (FingerDetection.instance().getStep() + 1 != mThisStep) {
                    try {
                        Thread.sleep(0, 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //TODO: Stuff that needs to be in Order

                validBlobs.forEach(blob -> {
                    Optional<Blob> vOldBlob = mOldBlobs.stream()
                            .filter(oldblob -> oldblob.getId() != -1)
                            .filter(oldblob -> oldblob.getCenter().distance(blob.getCenter()) < mMaxMoveRange)
                            .sorted((a,b) -> (int)( a.getCenter().distance(blob.getCenter()) - b.getCenter().distance(blob.getCenter())))
                            .findFirst();
                    if(vOldBlob.isPresent()){
                        blob.setId(vOldBlob.get().getId());
                        vOldBlob.get().setId(-1);
                    }else{
                        blob.setNewId();
                    }
                });

                mOldBlobs = new CopyOnWriteArrayList<>(validBlobs);


                setImageZero(vImageZero);

                mListener.forEach(l -> {
                    l.newBlobs(new ArrayList<>(validBlobs), diffImage);
                });




            } catch (Exception vException ){
                vException.printStackTrace();
            }
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

    public Mat getImageZero() {
        synchronized (FingerDetection.instance()) {
            return mImageZero;
        }
    }

    public void setImageZero(Mat aImageZero) {
        synchronized (FingerDetection.instance()) {
            mImageZero = aImageZero;
        }
    }

    public boolean doesImageZeroNeedReset() {
        return mImageZeroNeedsReset;
    }

    public void setImageZeroNeedsReset(boolean aImageZeroNeedsReset) {
        this.mImageZeroNeedsReset = aImageZeroNeedsReset;
    }

    public void setMaxMoveRange(int aMaxMoveRange) {
        mMaxMoveRange = aMaxMoveRange;
    }

}
