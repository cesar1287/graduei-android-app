package com.rivastecnologia.graduei.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.rivastecnologia.graduei.R;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setTitle("Meus Dados");

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }
}
