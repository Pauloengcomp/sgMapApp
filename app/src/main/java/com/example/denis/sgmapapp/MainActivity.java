package com.example.denis.sgmapapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button config =(Button) findViewById(R.id.bt_config);
        Button naveg =(Button) findViewById(R.id.bt_nav);
        Button credt =(Button) findViewById(R.id.bt_credit);
        Button sai =(Button) findViewById(R.id.bt_sair);
        config.setOnClickListener(this);
        naveg.setOnClickListener(this);
        credt.setOnClickListener(this);
        sai.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch (view.getId()) {
            case R.id.bt_config:
                i=new Intent(this,ConfigActivity.class);
                startActivity(i);
                break;
            case R.id.bt_nav:
                break;
            case R.id.bt_credit:
                i=new Intent(this,CreditosActivity.class);
                startActivity(i);
                break;
            case R.id.bt_sair:
                finish();
                break;
        }
    }
}
