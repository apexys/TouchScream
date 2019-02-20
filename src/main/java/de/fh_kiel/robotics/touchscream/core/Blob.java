package de.fh_kiel.robotics.touchscream.core;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Blob {

    private static int IDCOUNTER = 0;

    private Point2D mCenter;
    private final List<Point2D> mPoints = new ArrayList<>(1000);

    private int mId = -1;

    public Blob(int aPointX, int aPointY){
        this(new Point2D(aPointX,aPointY));
    }

    public Blob(Point2D aPoint){
        mPoints.add(aPoint);
        mCenter = aPoint;
    }

    public Point2D getCenter() {
        return mCenter;
    }

    private void calculateCenter(){
        mCenter = mPoints.stream().reduce(new Point2D(0,0), Point2D::add).multiply(1.0/mPoints.size());
    }

    public List<Point2D> getPoints() {
        return mPoints;
    }

    public void addPoint(int aPointX, int aPointY) {
        addPoint(new Point2D(aPointX,aPointY));
    }

    public void addPoint(Point2D aPoint) {
        mPoints.add(aPoint);
        calculateCenter();
    }

    public int getId() {
        return mId;
    }

    public void setId(int aId) {
        mId = aId;
    }

    public void setNewId() {
        mId = IDCOUNTER++;
    }
}
