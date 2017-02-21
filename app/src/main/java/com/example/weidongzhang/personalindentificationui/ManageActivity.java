package com.example.weidongzhang.personalindentificationui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.facemodule.FaceRecognizer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by weidong.zhang on 2016/5/6.
 */
public class ManageActivity extends AppCompatActivity {


    FaceRecognizer mFaceRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_module);

        mFaceRecognizer = new FaceRecognizer();
        mFaceRecognizer.Test();
    }

    public void onClickButton_ManageActivity_Photo(View viw)
    {
        File filePicStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        EditText userNameEdit = (EditText)findViewById(R.id.edit_TextManageActivity_UserName);
        String userName = userNameEdit.getText().toString();
        System.out.println(userName);

        //File subFiles[] = filePicStorage.listFiles();
        File faceFile = new File(filePicStorage.toString()+File.separator+"att_faces");

        File subFiles[] = faceFile.listFiles();

        ArrayList<String> listUserName = new ArrayList<String>();
        ArrayList<String> listPhotoPath = new ArrayList<String>();

        for(File f:subFiles)
        {
            if(f.isDirectory())
            {
                for(File ff:f.listFiles())
                {
                    if(!ff.isDirectory())
                    {
                        listUserName.add(f.getName());
                        listPhotoPath.add(ff.toString());
                    }
                }
            }
        }


        for(String s:listUserName)
        {
            System.out.println("User Name is: " + s);
        }

        for(String s:listPhotoPath)
        {
            System.out.println("Photo path is: " + s);
        }

        System.out.println("Face file path: " + faceFile.toString());
    }
}
