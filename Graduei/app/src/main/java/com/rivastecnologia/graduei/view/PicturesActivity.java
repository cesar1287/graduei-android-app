package com.rivastecnologia.graduei.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.rivastecnologia.graduei.R;

/**
 * The Class GallarySample.
 */
public class PicturesActivity extends AppCompatActivity {

    /** The images. */
    private ArrayList<String> images;
    GridView gallery;
    String pic;

    private static final String PREF_NAME = "LoginActivityPreferences";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        gallery = (GridView) findViewById(R.id.galleryGridView);
        registerForContextMenu(gallery);

        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != images && !images.isEmpty()) {
                    /*Toast.makeText(
                            getApplicationContext(),
                            "position " + position + " " + images.get(position),
                            Toast.LENGTH_SHORT).show();*/
                    Intent intent = new Intent(PicturesActivity.this, VisualizationActivity.class);
                    intent.putExtra("foto", images.get(position));
                    startActivity(intent);
                }
            }
        });

        gallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pic = images.get(position);
                return false;
            }
        });

        getSupportActionBar().setTitle("Fotos");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

            SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();

            final MenuItem careta = menu.add("Marcar como \"CARETA\"");
            careta.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("careta", pic);
                editor.apply();
                Toast.makeText(PicturesActivity.this, "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem direito = menu.add("Marcar como \"PERFIL DIREITO\"");
        direito.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("direito", pic);
                editor.apply();
                Toast.makeText(PicturesActivity.this, "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem frontal = menu.add("Marcar como \"FRONTAL\"");
        frontal.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("frontal", pic);
                editor.apply();
                Toast.makeText(PicturesActivity.this, "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem esquerdo = menu.add("Marcar como \"PERFIL ESQUERDO\"");
        esquerdo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("esquerdo", pic);
                editor.apply();
                Toast.makeText(PicturesActivity.this, "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    /**
     * The Class ImageAdapter.
     */
    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            picturesView = new ImageView(context);

            Glide.with(context).load(images.get(position))
                    .placeholder(R.drawable.logo)
                    .centerCrop()
                    .into(picturesView);
            picturesView.setAdjustViewBounds(true);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<>();
            String absolutePathOfImage;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, orderBy);

            column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
            return listOfAllImages;
        }
    }
}