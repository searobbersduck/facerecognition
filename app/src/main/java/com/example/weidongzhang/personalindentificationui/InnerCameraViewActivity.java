package com.example.weidongzhang.personalindentificationui;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Created by weidong.zhang on 2016/5/6.
 */
public class InnerCameraViewActivity extends Activity{

    private Camera mCamera = null;
    private CameraView mCameraView = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_camera_view);

        try{
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {

//            int currentAPIVersion = Build.VERSION.SDK_INT;
//            if(currentAPIVersion > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//            {
//                mCamera.startFaceDetection();
//            }

            Camera.Parameters params = mCamera.getParameters();

            // start face detection only *after* preview has started
            if (params.getMaxNumDetectedFaces() > 0){
                // camera supports face detection, so can start it:
                //mCamera.startFaceDetection();
                //System.out.println("face detect is opened!!!!");
            }

            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        //btn to close the application
        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }
}
