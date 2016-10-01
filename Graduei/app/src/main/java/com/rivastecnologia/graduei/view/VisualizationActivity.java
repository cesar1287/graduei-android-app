package com.rivastecnologia.graduei.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rivastecnologia.graduei.R;

public class VisualizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);

        getSupportActionBar().setTitle("Visualizar imagem");

        ImageView imageView = (ImageView) findViewById(R.id.imageViewVisu);

        Intent intent = getIntent();
        String url_foto = intent.getStringExtra("foto");

        Glide.with(this).load(url_foto)
                .placeholder(R.drawable.logo)
                .into(imageView);
    }
}
