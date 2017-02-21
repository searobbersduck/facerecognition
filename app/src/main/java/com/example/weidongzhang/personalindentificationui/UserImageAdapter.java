package com.example.weidongzhang.personalindentificationui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by weidong.zhang on 2016/5/13.
 */
public class UserImageAdapter extends BaseAdapter {

    private Context mContext;

    public UserImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        File filePicStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

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

        ArrayList<String> faceFiles = new ArrayList<String>();
        File facerecFile = new File(filePicStorage.toString()+File.separator+"facerec");
        File faceSubFiles[] = facerecFile.listFiles();
        for(File f:faceSubFiles)
        {
            faceFiles.add(f.toString());
        }

        //File f1 = new File("");
        Bitmap bm = BitmapFactory.decodeFile(faceFiles.get(position));
        imageView.setImageBitmap(bm);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };


}
