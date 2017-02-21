package org.facemodule;

import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by weidong.zhang on 2016/5/13.
 */
public class FaceRecognizer {
//    public FaceRecognizer(int type){
//        mNativeObj = nativeCreateObject(type);
//    }
//
//    public void release(){
//        nativeDestroyObject(mNativeObj);
//        mNativeObj = 0;
//    }
//
////    public void train(Vector<Mat> inputImages, int[] inputLabels){
////        long l1[] = {1,2,3,4,5};
////        int i1[] = {1,2,3,4,5};
////        nativeTrain(mNativeObj, l1, i1);
////    }
//
//    public void train(){
//        long l1[] = {1,2,3,4,5};
//        int i1[] = {1,2,3,4,5};
//        nativeTrain(mNativeObj, l1, i1);
//    }
//
//    public int predict()
//    {
//        return nativePredict(mNativeObj);
//    }
//
//    public int predict1(Mat img)
//    {
//        return nativePredict1(mNativeObj, img.getNativeObjAddr());
//    }
//
//    public long sayHi(){
//        return mNativeObj;
//    }
//
//    private long mNativeObj = 0;

//    private static native long nativeCreateObject(int iType);
//    private static native void nativeTrain(long thiz, long[] inputImages, int[] inputLabels);
//    private static native void nativeUpdate(long thiz, long[] inputImages, int[] inputLabels);
//    private static native int nativePredict(long thiz);
//    private static native int nativePredict1(long thiz, long inputImage);
//    private static native void nativeDestroyObject(long thiz);

    public FaceRecognizer()
    {
        mNativeObj = nativeCreateObject(0);
    }


    public void Release()
    {

    }

    public void Test() {
        String[] ss = {
        "aa", "bb", "cc", "dd", "ee",
        };
        long ii = nativeStringArray(ss);
        System.out.println("ssssssssssss" + ii);
    }

    public void Train()
    {
        ArrayList<String> sUPhotos = new ArrayList<String>();
        ArrayList<Integer> sUPhotosLabel = new ArrayList<Integer>();
        int iLabel = 3;
        File fRecPath = new File("/storage/sdcard0/Pictures/facerec");
        if(fRecPath.exists() && fRecPath.isDirectory())
        {
            File[] userPath = fRecPath.listFiles();
            for(File f:userPath)
            {
                if(f.isDirectory())
                {
                    File[] userPhotos = f.listFiles();
                    for(File ff:userPhotos)
                    {
                        if(!ff.isDirectory())
                        {
                            sUPhotos.add(ff.toString());
                            sUPhotosLabel.add(iLabel);
                        }
                    }
                }
                ++iLabel;
            }
        }

        int [] iiLabel= new int[sUPhotosLabel.size()];
        for(int i = 0; i < sUPhotosLabel.size(); ++i)
        {
            iiLabel[i] = sUPhotosLabel.get(i).intValue();
        }

        String[] ssPhotos = new String[sUPhotos.size()];
        for(int i = 0; i < sUPhotos.size(); ++i)
        {
            ssPhotos[i] = sUPhotos.get(i);
        }

        nativeTrain(mNativeObj, ssPhotos, iiLabel);
    }

    public int Predict(Mat img)
    {
        return nativePredict(mNativeObj, img.getNativeObjAddr());
    }

    long mNativeObj = 0;

    static
    {
        System.loadLibrary("FaceRecognizer");
    }

    private static native long nativeCreateObject(int iType);
    private static native long nativeStringArray(String[] stringArr);
    private static native void nativeDestroyObject(long thiz);
    private static native void nativeTrain(long thiz, String[] inputImages, int[] inputLabels);
    private static native void nativeUpdate(long thiz, String[] inputImages, int[] inputLabels);
    private static native int nativePredict(long thiz, long inputImage);
    //private static native int nativePredict1(long thiz, long inputImage);
}
