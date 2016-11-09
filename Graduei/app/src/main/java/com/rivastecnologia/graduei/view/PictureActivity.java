package com.rivastecnologia.graduei.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.rivastecnologia.graduei.R;
import com.rivastecnologia.graduei.controller.util.ImageItem;

import java.util.ArrayList;


public class PictureActivity extends AppCompatActivity implements PictureFragment.GetDataInterface{

    Fragment localImagePicassoFragment;

    ArrayList<ImageItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_image);
        localImagePicassoFragment = PictureFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction
                .replace(R.id.localImageContainer, localImagePicassoFragment, null)
                .commit();

        Intent i = getIntent();
        list = (ArrayList<ImageItem>) i
                .getSerializableExtra("randomList");

        if(list==null){
            getSupportActionBar().setTitle("Fotos");
        }else{
            getSupportActionBar().setTitle("Fotos Encontradas");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    private void finishActivity() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public ArrayList<ImageItem> getDataList() {
        return list;
    }
}
