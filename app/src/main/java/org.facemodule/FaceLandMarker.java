package org.facemodule;

import org.opencv.core.Mat;

import java.io.File;

/**
 * Created by weidong.zhang on 2016/5/16.
 */
public class FaceLandMarker {

    private long mNativeObj = 0;

    public FaceLandMarker()
    {
        mNativeObj = nativeCreateObject();
    }

    public void Release()
    {
        nativeDestroyObject(mNativeObj);
    }

    public int GetDetectedFacesNum(Mat img)
    {
        return nativeDetectedFacesNum(mNativeObj, img.getNativeObjAddr());
    }

    public long GetDetectFacesTest(Mat img)
    {
        String landmarkPath = "";
        // If landmark exits in sdcard, then use it
        if (new File(Constants.getFaceShapeModelPath()).exists()) {
            landmarkPath = Constants.getFaceShapeModelPath();
        }

        return nativeDetectedFaces(mNativeObj, img.getNativeObjAddr(), landmarkPath);
    }


    public long GetDetectFacesTest1(Mat img, Mat res)
    {
        String landmarkPath = "";
        // If landmark exits in sdcard, then use it
        if (new File(Constants.getFaceShapeModelPath()).exists()) {
            landmarkPath = Constants.getFaceShapeModelPath();
        }

        return nativeDetectedFaces1(mNativeObj, img.getNativeObjAddr(), res.getNativeObjAddr(), landmarkPath);
    }

//    public Mat GetDetectFaces(Mat img)
//    {
//        String landmarkPath = "";
//        // If landmark exits in sdcard, then use it
//        if (new File(Constants.getFaceShapeModelPath()).exists()) {
//            landmarkPath = Constants.getFaceShapeModelPath();
//        }
//
//        return nativeDetectedFaces(mNativeObj, img.getNativeObjAddr(), landmarkPath);
//    }

    static {
        System.loadLibrary("FaceLandMarker");
    }

    private static native long nativeCreateObject();
    private static native int nativeDetectedFacesNum(long thiz, long inputImages);
    private static native long nativeDetectedFaces(long thiz, long inputImages, String landMarkFile);
    private static native long nativeDetectedFaces1(long thiz, long inputImages, long res, String landMarkFile);
    private static native void nativeDestroyObject(long thiz);
}