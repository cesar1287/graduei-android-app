package com.rivastecnologia.graduei.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.rivastecnologia.graduei.controller.domain.User;
import com.rivastecnologia.graduei.controller.util.ImageItem;
import com.rivastecnologia.graduei.model.GradueiDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    Activity activity = this;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "MainActivity";
    private static final String PREF_NAME = "LoginActivityPreferences";

    public static final int REQUEST_IMAGE_CAPTURE_CARETA = 1;
    public static final int REQUEST_IMAGE_CAPTURE_FRONTAL = 2;
    public static final int REQUEST_IMAGE_CAPTURE_DIREITO = 3;
    public static final int REQUEST_IMAGE_CAPTURE_ESQUERDO = 4;
    private static final int MY_PERMISSIONS_REQUEST = 1234;

    Bundle infosFacebook, infosGoogle;
    String nome, email, id, profilePic, frontal, careta,
            perfil_direito, perfil_esquerdo, mCurrentPhotoPath;
    int executado;
    File photoFile;

    private List<ImageItem> items = new ArrayList<>();
    private List<ImageItem> randomItems = new ArrayList<>();

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        primeiraExecucao();

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
            Log.d(TAG, "Got cached sign-in facebook");
            processLoginFacebook(AccessToken.getCurrentAccessToken());
        }else if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in google");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.action_bar_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        executado = sp.getInt("execucao",-1);
        if(executado==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Deseja carregar agora suas fotos da galeria para o app?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Thread mThread = new Thread() {
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                            builder.setMessage("Fotos carregadas com sucesso, acesse o menu lateral e escolha a opção Fotos para" +
                                                    " visualizá-las")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //nothing
                                                        }
                                                    });
                                            builder.show();
                                        }
                                    });
                                }
                            };
                            mThread.start();
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.show();
        }

        id = sp.getString("id", "0");
        nome = sp.getString("nome","falhou");
        email = sp.getString("email","falhou");
        profilePic = sp.getString("profile_pic","falhou");

        View hView =  navigationView.getHeaderView(0);
        final ImageView nav_image = (ImageView)hView.findViewById(R.id.imageView);
        Glide.with(this).load(profilePic).asBitmap().into(new BitmapImageViewTarget(nav_image) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                nav_image.setImageDrawable(circularBitmapDrawable);
            }
        });
        TextView nav_nome = (TextView)hView.findViewById(R.id.header_name);
        nav_nome.setText(nome);
        TextView nav_email = (TextView)hView.findViewById(R.id.header_email);
        nav_email.setText(email);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPicturesProfile();

        getItemList();

        GradueiDAO dao = new GradueiDAO(this);
        randomItems = dao.getUserPicture(id);
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
            startActivity(new Intent(this, PictureActivity.class));
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
                                    SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.clear();
                                    editor.apply();
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
        }else if(id == R.id.nav_pictures_found){
            Intent intent = new Intent(this, PictureActivity.class);
            intent.putExtra("randomList", (Serializable) randomItems);
            startActivity(intent);
        }else if(id == R.id.nav_careta){
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_CARETA);
        }else if(id == R.id.nav_frontal){
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_FRONTAL);
        }else if(id == R.id.nav_direito){
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_DIREITO);
        }else if(id == R.id.nav_esquerdo){
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_ESQUERDO);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        GradueiDAO dao = new GradueiDAO(this);
        if (requestCode == REQUEST_IMAGE_CAPTURE_CARETA && resultCode == RESULT_OK) {
            galleryAddPic();
            String pic = photoFile.getAbsolutePath();
            Log.d(TAG, "onActivityResult: " + pic);
            editor.putString("careta", pic);
            editor.apply();
            dao.insertPicture(id,pic);
            dao.close();
            loadPicturesProfile();
        }else if (requestCode == REQUEST_IMAGE_CAPTURE_FRONTAL && resultCode == RESULT_OK) {
            galleryAddPic();
            String pic = photoFile.getAbsolutePath();
            Log.d(TAG, "onActivityResult: " + pic);
            editor.putString("frontal", pic);
            editor.apply();
            dao.insertPicture(id,pic);
            dao.close();
            loadPicturesProfile();
        }else if (requestCode == REQUEST_IMAGE_CAPTURE_DIREITO && resultCode == RESULT_OK) {
            galleryAddPic();
            String pic = photoFile.getAbsolutePath();
            Log.d(TAG, "onActivityResult: " + pic);
            editor.putString("direito", pic);
            editor.apply();
            dao.insertPicture(id,pic);
            dao.close();
            loadPicturesProfile();
        }else if (requestCode == REQUEST_IMAGE_CAPTURE_ESQUERDO && resultCode == RESULT_OK) {
            galleryAddPic();
            String pic = photoFile.getAbsolutePath();
            Log.d(TAG, "onActivityResult: " + pic);
            editor.putString("esquerdo", pic);
            editor.apply();
            dao.insertPicture(id,pic);
            dao.close();
            loadPicturesProfile();
        }
    }

    public void primeiraExecucao(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // Assume thisActivity is the current activity
            int permissionCheckWrite = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            int permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);

            // Here, thisActivity is the current activity
            if (permissionCheckWrite!= PackageManager.PERMISSION_GRANTED || permissionCheckCamera!=PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        }

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(!sp.contains("execucao")) {
            editor.putInt("execucao", 0);
            editor.apply();
        }else{
            editor.putInt("execucao", 1);
            editor.apply();
        }
    }

    /* -------------- FILES -----------------*/
    /*--------------- BEGIN -----------------*/

    public void loadPicturesProfile(){
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        frontal = sp.getString("frontal","falhou");
        perfil_direito = sp.getString("direito","falhou");
        careta = sp.getString("careta","falhou");
        perfil_esquerdo = sp.getString("esquerdo","falhou");

        ImageView iv_frontal = (ImageView) findViewById(R.id.frontal);
        Glide.with(iv_frontal.getContext())
                .load(frontal)
                .placeholder(R.drawable.logo)
                .into(iv_frontal);

        ImageView iv_direito = (ImageView) findViewById(R.id.perfil_direito);
        Glide.with(this)
                .load(perfil_direito)
                .placeholder(R.drawable.logo)
                .into(iv_direito);

        ImageView iv_careta = (ImageView) findViewById(R.id.careta);
        Glide.with(this)
                .load(careta)
                .placeholder(R.drawable.logo)
                .into(iv_careta);

        ImageView iv_esquerdo = (ImageView) findViewById(R.id.perfil_esquerdo);
        Glide.with(this)
                .load(perfil_esquerdo)
                .placeholder(R.drawable.logo)
                .into(iv_esquerdo);
    }

    public void getItemList() {
        allScan();
        items = insertData();
    }

    private List<ImageItem> insertData() {
        List<ImageItem> items = new ArrayList<>();
        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = this.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    items.add(new ImageItem(R.drawable.logo, imagecursor.getString(dataColumnIndex)));
                }
                //Log.i("teste","items : "+items.size());
            }

            imagecursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(items);
        return items;
    }

    public void allScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(Environment.getExternalStorageDirectory()); // out is your output file
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        } else {
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    /*--------------- END -----------------*/

    /* -------------- PICTURES -----------------*/
    /*--------------- BEGIN -----------------*/

    private void dispatchTakePictureIntent(int id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "dispatchTakePictureIntent: "+ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                Log.d(TAG, "dispatchTakePictureIntent: " + photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, id);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /*--------------- END -----------------*/

    /* -------------- LOGIN -----------------*/
    /*--------------- BEGIN -----------------*/

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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            processLoginGoogle(acct);
        }
    }

    private Bundle getGoogleData(GoogleSignInAccount account) {

        Bundle bundle = new Bundle();

        bundle.putString("profile_pic", account.getPhotoUrl().toString());
        bundle.putString("name", account.getDisplayName());
        bundle.putString("id", account.getId());
        bundle.putString("email", account.getEmail());

        return bundle;
    }

    private void processLoginGoogle(GoogleSignInAccount account){

        infosGoogle = getGoogleData(account);

        GradueiDAO dao = new GradueiDAO(this);
        user.setId(infosGoogle.get("id").toString());
        if(!dao.isUserCreated(user.getId())){
            user.setEmail(infosGoogle.get("email").toString());
            user.setNome(infosGoogle.get("name").toString());
            user.setProfilePicURL(infosGoogle.get("profile_pic").toString());
            dao.insert(user);
            dao.close();
        }
    }

    private void processLoginFacebook(AccessToken token){

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Get facebook data from login
                infosFacebook = getFacebookData(object);

                GradueiDAO dao = new GradueiDAO(MainActivity.this);
                user.setId(infosFacebook.get("id").toString());
                if(!dao.isUserCreated(user.getId())){
                    user.setEmail(infosFacebook.get("email").toString());
                    user.setNome(infosFacebook.get("name").toString());
                    user.setProfilePicURL(infosFacebook.get("profile_pic").toString());
                    dao.insert(user);
                    dao.close();
                }
            }

        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email"); // Parâmetros que pedimos ao facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("id", id);
            if (object.has("name")) {
                bundle.putString("name", object.getString("name"));
            }if (object.has("email")) {
                bundle.putString("email", object.getString("email"));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return bundle;
    }

    /*--------------- END -----------------*/
}
