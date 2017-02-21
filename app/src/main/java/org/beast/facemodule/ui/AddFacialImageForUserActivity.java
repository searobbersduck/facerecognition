package org.beast.facemodule.ui;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weidongzhang.personalindentificationui.FaceModuleActivity;
import com.example.weidongzhang.personalindentificationui.R;
import com.example.weidongzhang.personalindentificationui.UserImageAdapter;

import org.beast.voice.VoicePerson;
import org.facemodule.FaceLandMarker;
import org.facemodule.FaceRecognizer;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weidong.zhang on 2016/5/14.
 */
public class AddFacialImageForUserActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String    TAG                 = "OCVSample::Activity";

    private JavaCameraView mOpenCvFrontCameraView;

    private Mat mRgba;
    private Mat mGray;
    private Mat mDetectRGB;

    private boolean mIsPhotoing = false;
    private boolean mIsRegestering = false;
    private boolean mIsPredicting = false;

    private int iPredict = 1000;


    // Obj
    FaceLandMarker mFaceLandMarker;
    FaceRecognizer mFaceRecognizer;


    // handler
    private static Handler imageViewerHandler = new Handler();

    // viewer
    ImageView mImageViewer;

    ListView lv;
    long iid;

    List<Map<Integer, String>> mUserList = new ArrayList<Map<Integer,String>>();

    private void SetPhotoing(boolean b)
    {
        mIsPhotoing = b;
        Button but = (Button)findViewById(R.id.button_Photo__AddFacialImageForUserActivity);
        but.setEnabled(!b);
    }

    private void SetRegistering()
    {
        mIsRegestering = !mIsRegestering;
        Button but = (Button)findViewById(R.id.button_Register__AddFacialImageForUserActivity);
        if(mIsRegestering)
        {
            but.setText("Registering");
        }
        else{
            but.setText("Register");
        }
    }

    private void SetPredicting()
    {
        mIsPredicting = !mIsPredicting;
        Button but = (Button)findViewById(R.id.button_Predict__AddFacialImageForUserActivity);
        if(mIsPredicting)
        {
            but.setText("Predicting");
        }
        else{
            but.setText("Predict");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facemodule_add_facial_image_for_user);

        GridView gridview = (GridView) findViewById(R.id.gridView_AddFacialImageForUserActivity);
        String userFolder = new String("/storage/sdcard0/Pictures/facerec/" + ((EditText)findViewById(R.id.userName_AddFacialImageForUserActivity)).getText().toString());
        gridview.setAdapter(new AddFacialImageForUser_ImageAdapter(this, userFolder));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(AddFacialImageForUserActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mImageViewer = (ImageView)findViewById(R.id.imageView_Photo_AddFacialImageForUserActivity_Photo);


        // initialize opencv
        mOpenCvFrontCameraView = (JavaCameraView) findViewById(R.id.java_surface_front_view1);
        mOpenCvFrontCameraView.setCvCameraViewListener(this);
        mOpenCvFrontCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvFrontCameraView.enableView();
        //mOpenCvFrontCameraView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // initialize object
        mFaceLandMarker = new FaceLandMarker();
        mFaceRecognizer = new FaceRecognizer();



        List<Map<Integer, String>> user = new ArrayList<Map<Integer,String>>();

        int iLabel = 3;
        File fRecPath = new File("/storage/sdcard0/Pictures/facerec");
        if(fRecPath.exists() && fRecPath.isDirectory())
        {
            File[] userPath = fRecPath.listFiles();
            for(File f:userPath)
            {
                Map<Integer,String> map = new HashMap<Integer,String>();
                map.put(iLabel, f.getName());
                mUserList.add(map);
                ++iLabel;
            }
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int iuser = 0;
        for (Map<Integer,String> ui : mUserList) {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("name", ui.get(iuser+3));
            map1.put("id", iuser);
            list.add(map1);
            ++iuser;
        }

        lv = (ListView) findViewById(R.id.listView_AddFacialImageForUserActivity_UserList);
        SimpleAdapter adapter = new SimpleAdapter(AddFacialImageForUserActivity.this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                iid = adapter.getItemIdAtPosition(position);


                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mImageView.setBackgroundColor(1000001);
                        GridView gridview = (GridView) findViewById(R.id.gridView_AddFacialImageForUserActivity);
                        String userFolder = new String("/storage/sdcard0/Pictures/facerec/" + mUserList.get((int)iid).values().toArray()[0]);
                        gridview.setAdapter(new AddFacialImageForUser_ImageAdapter(AddFacialImageForUserActivity.this, userFolder));


                        ((EditText) findViewById(R.id.userName_AddFacialImageForUserActivity)).setText(mUserList.get((int)iid).values().toArray()[0].toString());


                    }
                });

            }


        });

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvFrontCameraView != null)
            mOpenCvFrontCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            //mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvFrontCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        mDetectRGB = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        mDetectRGB.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        System.out.println("heheheheheheheheehheheheh!!!!!");
        long timebegin = System.currentTimeMillis();

        mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
//
//
//        if(ii > 0)
//        {
//            //Imgcodecs.imwrite("storage/sdcard0/Pictures/facerec"+File.separator+"dlibtest.png", mDetectRGB);
//        }
//
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//            //mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//
//        }
//
//
        if(mIsPhotoing)
        {
            Mat matTemp = new Mat();
            System.out.println("Bitmap size Frame size is: " + mRgba.cols() + "    " + mRgba.rows());
            Imgproc.cvtColor(mRgba, matTemp, Imgproc.COLOR_BGRA2BGR, 3);

            long ii  = mFaceLandMarker.GetDetectFacesTest1(matTemp, mDetectRGB);
            System.out.println("Face detected by dlib number is: " + ii);
            if(ii > 0)
            {
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mImageView.setBackgroundColor(1000001);
                        SetPhotoing(false);
                        Bitmap bm = Bitmap.createBitmap(mDetectRGB.cols(), mDetectRGB.rows(), Bitmap.Config.RGB_565);
                        System.out.println("Bitmap size mDetectRGB is: " + mDetectRGB.cols() + "      " + mDetectRGB.rows());
                        System.out.println("Bitmap size BM is: " + bm.getWidth() + "      " + bm.getHeight());
                        Utils.matToBitmap(mDetectRGB, bm);
                        mImageViewer.setImageBitmap(bm);
                    }
                });
                System.out.println(GetPhotoName());
                Imgcodecs.imwrite(GetPhotoName(), mDetectRGB);
            }
        }


        if(mIsRegestering)
        {
            Mat matTemp = new Mat();
            System.out.println("Bitmap size Frame size is: " + mRgba.cols() + "    " + mRgba.rows());
            Imgproc.cvtColor(mRgba, matTemp, Imgproc.COLOR_BGRA2BGR, 3);


            long ii  = mFaceLandMarker.GetDetectFacesTest1(matTemp, mDetectRGB);
            System.out.println("Face detected by dlib number is: " + ii);
            if(ii > 0)
            {
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mImageView.setBackgroundColor(1000001);
                        SetPhotoing(false);
                        Bitmap bm = Bitmap.createBitmap(mDetectRGB.cols(), mDetectRGB.rows(), Bitmap.Config.RGB_565);
                        System.out.println("Bitmap size mDetectRGB is: " + mDetectRGB.cols() + "      " + mDetectRGB.rows());
                        System.out.println("Bitmap size BM is: " + bm.getWidth() + "      " + bm.getHeight());
                        Utils.matToBitmap(mDetectRGB, bm);
                        mImageViewer.setImageBitmap(bm);
                    }
                });
                System.out.println(GetPhotoName());
                Imgcodecs.imwrite(GetPhotoName(), mDetectRGB);
            }
        }


        if(mIsPredicting)
        {
            Mat matTemp = new Mat();
            System.out.println("Bitmap size Frame size is: " + mRgba.cols() + "    " + mRgba.rows());
            Imgproc.cvtColor(mRgba, matTemp, Imgproc.COLOR_BGRA2BGR, 3);

            long ii  = mFaceLandMarker.GetDetectFacesTest1(matTemp, mDetectRGB);
            System.out.println("Face detected by dlib number is: " + ii);
            Imgproc.cvtColor(mDetectRGB, mGray, Imgproc.COLOR_BGR2GRAY, 1);
            if(ii > 0)
            {
                iPredict = mFaceRecognizer.Predict(mGray);
                System.out.println("Oh, my god: " + iPredict);
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mImageView.setBackgroundColor(1000001);
                        if(iPredict < 500)
                        {
                            TextView tv = (TextView) findViewById(R.id.textView_PredictUser__AddFacialImageForUserActivity);
                            tv.setText("Oh, you're user: " + mUserList.get(iPredict-3).get(iPredict));
                        }else
                        {
                            TextView tv = (TextView) findViewById(R.id.textView_PredictUser__AddFacialImageForUserActivity);
                            tv.setText("Oh, you're user: " + "Unkonwn");
                        }
                    }
                });
            }
        }

//        if(bPhotoing)
//        {
//            bPhotoing = false;
//            imageViewerHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    //mImageView.setBackgroundColor(1000001);
//                    Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(mRgba, bm);
//                    mImageView.setImageBitmap(bm);
//                }
//            });
//        }
//
//        MatOfRect faces = new MatOfRect();
//
//        if (mDetectorType == JAVA_DETECTOR) {
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//        }
////        else if (mDetectorType == NATIVE_DETECTOR) {
////            if (mNativeDetector != null)
////                mNativeDetector.detect(mGray, faces);
////        }
//        else {
//            Log.e(TAG, "Detection method is not selected!");
//        }
//
//        Rect[] facesArray = faces.toArray();
////        for (int i = 0; i < facesArray.length; i++)
////            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//
//        for (int i = 0; i < facesArray.length; i++)
//        {
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//            if(i == 0 && bPhotoing)
//            {
//                bPhotoing = false;
//                m1 = new Mat(mRgba, facesArray[0]);
//                Imgproc.resize(m1, m1, new Size(300,300));
//                imageViewerHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //mImageView.setBackgroundColor(1000001);
//                        Bitmap bm = Bitmap.createBitmap(m1.cols(), m1.rows(), Bitmap.Config.ARGB_8888);
//                        //Bitmap bm = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//                        Utils.matToBitmap(m1, bm);
//                        mImageView.setImageBitmap(bm);
//                    }
//                });
//                File fPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                Log.i("opencv_path", fPath.getPath());
//                String sTimeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
//                Imgcodecs.imwrite("storage/sdcard0/Pictures/facerec"+File.separator+sTimeStamp+".png", m1);
//            }
//
//            if(i == 0 && bRecognition)
//            {
//                Mat m2 = new Mat(mGray, facesArray[0]);
//                Imgproc.resize(m2, m2, new Size(300,300));
//                //int iRes = 0;
//                int iRes = mNativeFaceRecognizer.predict1(m2);
//                if(iPreviousLabel == iRes)
//                {
//                    ++iFaceRecognitionCnt;
//                }
//                else
//                {
//                    iPreviousLabel = iRes;
//                    iFaceRecognitionCnt = 0;
//                }
//                EditText txt = (EditText)findViewById(R.id.editText);
//                if(iFaceRecognitionCnt >= 0)
//                {
//                    iFaceRecognitionCnt = 0;
//                    if(iRes == 9)
//                    {
//                        imageViewerHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                EditText txt = (EditText) findViewById(R.id.editText);
//                                txt.setText("opencv_whose_face: Haijun\n");
//                            }
//                        });
//                        //txt.setText("opencv_whose_face: Haijun\n");
//                        Log.i("opencv_whose_face:", "Haijun\n");
//                    }
//                    else if(iRes == 20)
//                    {
//                        imageViewerHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                EditText txt = (EditText) findViewById(R.id.editText);
//                                txt.setText("opencv_whose_face: Changhe\n");
//                            }
//                        });
//                        Log.i("opencv_whose_face:", "Changhe\n");
//                    }
//                    else
//                    {
//                        imageViewerHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                EditText txt = (EditText) findViewById(R.id.editText);
//                                txt.setText("opencv_whose_face: Gui zhi dao\n");
//                            }
//                        });
//                        Log.i("opencv_whose_face:", "Gui zhi dao\n");
//                    }
//                }
//            }
//        }


//        long timeEnd = System.currentTimeMillis();
//
//        System.out.println("Face detected by dlib number is: " + (timeEnd - timebegin) + "ms");

        //return mDetectRGB;

        return mRgba;
    }


    private String GetPhotoName()
    {
        //File fPath = new File("/storage/sdcard0/Pictures");//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fPath = new String("/storage/sdcard0/Pictures/facerec");

        String sTimeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String userName = ((EditText)findViewById(R.id.userName_AddFacialImageForUserActivity)).getText().toString();
        //File newFile = new File(fPath+"/facerec/"+userName+File.separator+sTimeStamp+".png");
        //return (new String(fPath+"/facerec/"+userName+File.separator+sTimeStamp+".png"));

        File f = new File(fPath,userName);
        if(!f.exists())
        {
            f.mkdir();
        }

        return (new String(f.getPath()+File.separator+sTimeStamp+".png"));
        //return (new String(fPath+"/facerec/"+sTimeStamp+".png"));
        //Imgcodecs.imwrite("storage/sdcard0/Pictures/facerec"+File.separator+sTimeStamp+".png", m1);
    }

    public void onClickButton_AddFacialImageForUserActivity_Photo(View view)
    {
        EditText userName = (EditText)findViewById(R.id.userName_AddFacialImageForUserActivity);
        String sName = userName.toString();

        if(!sName.isEmpty())
        {
            SetPhotoing(true);
        }
    }

    public void onClickButton_AddFacialImageForUserActivity_Register(View view)
    {
        SetRegistering();
    }

    public void onClickButton_AddFacialImageForUserActivity_Train(View view)
    {
        mFaceRecognizer.Train();
    }

    public void onClickButton_AddFacialImageForUserActivity_Predict(View view)
    {
        SetPredicting();
    }

}
