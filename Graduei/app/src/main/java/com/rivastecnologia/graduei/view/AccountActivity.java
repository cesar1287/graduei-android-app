package com.rivastecnologia.graduei.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rivastecnologia.graduei.R;
import com.rivastecnologia.graduei.controller.domain.User;
import com.rivastecnologia.graduei.model.GradueiDAO;

public class AccountActivity extends AppCompatActivity {

    Bundle infosFacebook;
    Bundle infosGoogle;
    String name, email, id, profilePic;

    EditText eName, eEmail, eTelefone, eSenha, eConfirmaSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        eName = (EditText) findViewById(R.id.editTextName);
        eEmail = (EditText) findViewById(R.id.editTextEmail);
        eTelefone = (EditText) findViewById(R.id.editTextPhone);
        eSenha = (EditText) findViewById(R.id.editTextPassword);
        eConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmPass);

        infosFacebook = getIntent().getBundleExtra("infosFacebook");
        if(infosFacebook!=null) {
            name = infosFacebook.get("name").toString();
            email = infosFacebook.get("email").toString();
            id = infosFacebook.get("id").toString();
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

        Button btSalvar = (Button) findViewById(R.id.buttonSave);
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(eName.getText().toString().isEmpty()||
                    eEmail.getText().toString().isEmpty()||
                    eTelefone.getText().toString().isEmpty()||
                    eSenha.getText().toString().isEmpty()||
                    eConfirmaSenha.getText().toString().isEmpty()){
                Toast.makeText(AccountActivity.this, "Todos os campos são obrigatórios", Toast.LENGTH_LONG).show();
            }else if(!eSenha.getText().toString().equals(eConfirmaSenha
                      .getText().toString())){
                Toast.makeText(AccountActivity.this, "As senhas não batem, digite-as novamente", Toast.LENGTH_LONG).show();
                eSenha.getText().clear();
                eConfirmaSenha.getText().clear();
                eSenha.requestFocus();
            }else {
                User user = new User();
                GradueiDAO dao = new GradueiDAO(AccountActivity.this);
                user.setId(id);
                user.setNome(name);
                user.setEmail(email);
                user.setProfilePicURL(profilePic);
                user.setTelefone(eTelefone.getText().toString());
                user.setSenha(eSenha.getText().toString());
                dao.update(user);
                Toast.makeText(AccountActivity.this, "Dados salvados com sucesso", Toast.LENGTH_LONG).show();
                finish();
            }
            }
        });

        getSupportActionBar().setTitle("Meus Dados");
    }

    private void loadDataToEditText() {

        User user = new User();
        GradueiDAO dao = new GradueiDAO(this);
        user.setId(id);
        if(!dao.isUserCreated(user.getId())){
            eName.setText(name);
            eEmail.setText(email);
        }else{
            user = dao.getUserbyID(id);

            eName.setText(user.getNome());
            eEmail.setText(user.getEmail());
            eTelefone.setText(user.getTelefone());
            eSenha.setText(user.getSenha());
        }
        dao.close();
    }
}
