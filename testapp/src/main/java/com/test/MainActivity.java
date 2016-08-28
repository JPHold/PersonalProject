package com.test;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView imgV_test_colorFilter;
    ColorFilterLayout customV_test_colorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        imgV_test_colorFilter = (ImageView) findViewById(R.id.imgV_test_colorFilter);
//        customV_test_colorFilter = (ColorFilterLayout) findViewById(R.id.customV_test_colorFilter);

        imgV_test_colorFilter.setColorFilter(Color.BLACK);
//        customV_test_colorFilter.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
    }
}
