package com.example.weidongzhang.personalindentificationui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by weidong.zhang on 2016/5/5.
 */
public class ListViewTestActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_test);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new UserImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ListViewTestActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
