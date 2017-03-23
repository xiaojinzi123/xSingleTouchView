package com.move.xsingletouchviewproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.move.xsingletouchview.XSingleTouchView;

public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        XSingleTouchView x = (XSingleTouchView) findViewById(R.id.x);
        x.setOnDbClickListener(new XSingleTouchView.OnDbClickListener() {
            @Override
            public void onDbClick(View v) {
                Toast.makeText(MainAct.this, "双击", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
