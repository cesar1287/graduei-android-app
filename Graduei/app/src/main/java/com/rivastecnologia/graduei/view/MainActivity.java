package com.rivastecnologia.graduei.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.rivastecnologia.graduei.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "MainActivity";

    Bundle infosFacebook;
    Bundle infosGoogle;
    String nome, email, id, profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if(AccessToken.getCurrentAccessToken()!=null){
            //processLoginFacebook(AccessToken.getCurrentAccessToken());
        }else if (opr.isDone()) {
            //Log.d(TAG, "Got cached sign-in");
            //GoogleSignInResult result = opr.get();
            //handleSignInResult(result);
        }else{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        infosFacebook = getIntent().getBundleExtra("infosFacebook");
        if(infosFacebook!=null) {
            nome = infosFacebook.get("name").toString();
            email = infosFacebook.get("email").toString();
            id = infosFacebook.get("idFacebook").toString();
            profilePic = infosFacebook.get("profile_pic").toString();
        }

        infosGoogle= getIntent().getBundleExtra("infosGoogle");
        if(infosGoogle!=null) {
            nome = infosGoogle.get("name").toString();
            email = infosGoogle.get("email").toString();
            id = infosGoogle.get("id").toString();
            profilePic = infosGoogle.get("profile_pic").toString();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.action_bar_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btPictures = (Button) findViewById(R.id.btLoadPictures);
        btPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Carregando fotos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            if(infosFacebook!=null) {
                Intent irParaATelaMeusDados = new Intent(this, AccountActivity.class);
                irParaATelaMeusDados.putExtra("infosFacebook", infosFacebook);
                startActivity(irParaATelaMeusDados);
            }else{
                Intent irParaATelaMeusDados = new Intent(this, AccountActivity.class);
                irParaATelaMeusDados.putExtra("infosGoogle", infosGoogle);
                startActivity(irParaATelaMeusDados);
            }
        } else if (id == R.id.nav_pictures) {
            startActivity(new Intent(this, PicturesActivity.class));
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage(R.string.message_logout)
                    .setPositiveButton(R.string.yes_logout, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Saindo...", Toast.LENGTH_SHORT).show();
                            Thread mThread = new Thread(){
                                @Override
                                public void run() {
                                    //logout Google
                                    signOut();
                                    //logout Facebook
                                    LoginManager.getInstance().logOut();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish(); // dispose do java
                                }
                            };
                            mThread.start();
                        }
                    })
                    .setNegativeButton(R.string.no_logout, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //nothing to do
                    }
                });
    }
}
