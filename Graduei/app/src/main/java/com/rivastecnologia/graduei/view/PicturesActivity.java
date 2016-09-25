package com.rivastecnologia.graduei.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rivastecnologia.graduei.R;

public class PicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        getSupportActionBar().setTitle("Fotos");
    }
}
