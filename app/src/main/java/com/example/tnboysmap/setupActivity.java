package com.example.tnboysmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class setupActivity extends AppCompatActivity {



    private Button button3;
    private Button about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        about = (Button) findViewById(R.id.about);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(setupActivity.this, aboutActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
        });




    }
}
