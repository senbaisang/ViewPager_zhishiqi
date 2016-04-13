package com.sally.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.view.View.OnClickListener;

public class WelcomeActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtn1;
    private Button mBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mBtn1 = (Button) findViewById(R.id.id_btn1);
        mBtn2 = (Button) findViewById(R.id.id_btn2);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn1: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.id_btn2: {
                Intent intent = new Intent(this, MainActivity2.class);
                startActivity(intent);
            }
            break;
        }
    }
}
