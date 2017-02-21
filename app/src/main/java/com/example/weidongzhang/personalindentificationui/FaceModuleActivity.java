package com.example.weidongzhang.personalindentificationui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.beast.facemodule.ui.AddFacialImageForUserActivity;

/**
 * Created by weidong.zhang on 2016/5/6.
 */
public class FaceModuleActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_module);
    }

    public void onClickButton_FaceModuleActivity_Manage(View view)
    {
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
        System.out.println("on button face activity manage!");
    }

    public void onClickButton_FaceModuleActivity_UserModule(View view)
    {
        Intent intent = new Intent(this, AddFacialImageForUserActivity.class);
        startActivity(intent);
    }
}
