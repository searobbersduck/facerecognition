package org.beast.facemodule.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.weidongzhang.personalindentificationui.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by weidong.zhang on 2016/5/14.
 */
public class AddFacialImageForUser_ImageAdapter extends BaseAdapter{

    private Context mContext;
    private String mUserPhotoPath;
    private ArrayList<String> mPhotosPath;

    public AddFacialImageForUser_ImageAdapter(Context c, String sPhotoPath) {
        mContext = c;
        mUserPhotoPath = sPhotoPath;

        mPhotosPath = new ArrayList<String>();
        File fileUserRoot = new File(sPhotoPath);
        if(fileUserRoot.exists())
        {
            File[] fileUserPhotos = fileUserRoot.listFiles();
            for(File f:fileUserPhotos)
            {
                mPhotosPath.add(f.toString());
            }
        }
    }

    public int getCount() {
        return mPhotosPath.size();
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

        //File f1 = new File("");
        Bitmap bm = BitmapFactory.decodeFile(mPhotosPath.get(position));
        imageView.setImageBitmap(bm);
        return imageView;
    }

}
