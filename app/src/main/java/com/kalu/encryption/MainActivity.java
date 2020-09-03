package com.kalu.encryption;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lib.kalu.unlock.gesture.GestureView;
import lib.kalu.unlock.gesture.GestureresultView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 指纹
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 手势
        GestureView gestureView = findViewById(R.id.button2);
        gestureView.setOnGestureChangeListener(new GestureView.OnGestureChangeListener() {
            @Override
            public void onResult(String result) {

            }

            @Override
            public void onChange(String result) {
                GestureresultView gestureresultView = findViewById(R.id.button1);
                gestureresultView.postInvalidate(result);
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onStart() {

            }
        });
    }
}
