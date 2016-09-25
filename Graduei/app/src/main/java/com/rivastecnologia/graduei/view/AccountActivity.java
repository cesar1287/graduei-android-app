package com.rivastecnologia.graduei.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.rivastecnologia.graduei.R;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    Bundle infosFacebook;
    Bundle infosGoogle;
    String name, email, id, profilePic;

    EditText eName, eEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        eName = (EditText) findViewById(R.id.editTextName);
        eEmail = (EditText) findViewById(R.id.editTextEmail);

        infosFacebook = getIntent().getBundleExtra("infosFacebook");
        if(infosFacebook!=null) {
            name = infosFacebook.get("name").toString();
            email = infosFacebook.get("email").toString();
            id = infosFacebook.get("idFacebook").toString();
            profilePic = infosFacebook.get("profile_pic").toString();
        }

        infosGoogle= getIntent().getBundleExtra("infosGoogle");
        if(infosGoogle!=null) {
            name = infosGoogle.get("name").toString();
            email = infosGoogle.get("email").toString();
            id = infosGoogle.get("id").toString();
            profilePic = infosGoogle.get("profile_pic").toString();
        }

        loadDataToEditText();

        getSupportActionBar().setTitle("Meus Dados");
    }

    private void loadDataToEditText() {
        eName.setText(name);
        eEmail.setText(email);
    }
}
