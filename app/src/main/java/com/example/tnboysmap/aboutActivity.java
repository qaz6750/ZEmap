package com.example.tnboysmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class aboutActivity extends AppCompatActivity {

    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        update= (Button) findViewById(R.id.update); //找到组件

        update.setOnClickListener(new View.OnClickListener() { //为组件设置点击事件
            @Override
            public void onClick(View v) {
                Toast.makeText(aboutActivity.this,"这好像还没有开发完成!",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
