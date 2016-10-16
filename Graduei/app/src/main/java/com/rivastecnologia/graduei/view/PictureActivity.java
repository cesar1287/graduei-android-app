package com.rivastecnologia.graduei.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rivastecnologia.graduei.R;


public class PictureActivity extends AppCompatActivity {

    Fragment localImagePicassoFragment;
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

        getSupportActionBar().setTitle("Fotos");
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
}
