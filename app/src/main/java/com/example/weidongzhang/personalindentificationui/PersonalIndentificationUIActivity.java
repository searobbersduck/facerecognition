package com.example.weidongzhang.personalindentificationui;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.facemodule.FaceRecognizer;

public class PersonalIndentificationUIActivity extends AppCompatActivity {

    private TextView tv = null;

    private static final int iRequestCodeFaceModule = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_indentification_ui);
        tv = (TextView)findViewById(R.id.textMainUIButton);

    }

    public void onClickButtonPlayVideo(View view)
    {
        Intent playVideo = new Intent(this, ListViewTestActivity.class);
        startActivity(playVideo);
    }

    public void onClickButtonStartCamera(View view)
    {
        Intent intent = new Intent(this, InnerCameraViewActivity.class);
        startActivity(intent);
    }

    public void onClickButtonFaceModule(View view)
    {
        //Intent intent = new Intent(this, )
        tv.setText("Face Module");
        Intent intent = new Intent(this, FaceModuleActivity.class);
        startActivityForResult(intent, iRequestCodeFaceModule);
    }

    public void onClickButtonASMModule(View view)
    {
        tv.setText("ASM Module");
    }


    public void onClickButtonVoiceModule(View view)
    {
        Intent intent = new Intent(this, VoiceRecognition1Activity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case iRequestCodeFaceModule:
            {
                tv.setText("Return from face module!");
            }
            break;
        }
    }
}
