package com.rivastecnologia.graduei.view;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rivastecnologia.graduei.R;
import com.rivastecnologia.graduei.controller.util.BitmapTransform;
import com.rivastecnologia.graduei.controller.util.ImageItem;
import com.rivastecnologia.graduei.controller.util.SquareImageView;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class PictureFragment extends Fragment {
    View view;
    MyAdapter adapter;
    private int width;
    GridView gridView;
    private ImageLoader mImageLoader;
    DisplayImageOptions mDisplayImageOptions;
    ImageLoadingListener mImageLoadingListenerImpl;
    private List<ImageItem> items = new ArrayList<>();
    String pic;
    private static final String PREF_NAME = "LoginActivityPreferences";

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

    public PictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mImageLoader = ImageLoader.getInstance();
        initImageLoader(getActivity());
        int defaultImageId = R.drawable.logo;

        width = displaymetrics.widthPixels / 4;
        adapter = new MyAdapter(getActivity());
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 16;
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImageId)
                .showImageForEmptyUri(defaultImageId)
                .showImageOnFail(defaultImageId)
                .decodingOptions(opt)
                .cacheInMemory(true)
                .cacheOnDisk(true)  //Set the downloaded image be cached in SD card or not.
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //Set which encoding type will be used for displaying images.
                .bitmapConfig(Bitmap.Config.RGB_565) //Set which decoding type will be used for parsing images.
                .build();
        mImageLoadingListenerImpl = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_picture_activity, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        registerForContextMenu(gridView);
        gridView.setAdapter(adapter);
        getItemList();
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        SharedPreferences sp = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        final MenuItem careta = menu.add("Marcar como \"CARETA\"");
        careta.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("careta", pic);
                editor.apply();
                Toast.makeText(getActivity(), "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem direito = menu.add("Marcar como \"PERFIL DIREITO\"");
        direito.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("direito", pic);
                editor.apply();
                Toast.makeText(getActivity(), "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem frontal = menu.add("Marcar como \"FRONTAL\"");
        frontal.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("frontal", pic);
                editor.apply();
                Toast.makeText(getActivity(), "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        final MenuItem esquerdo = menu.add("Marcar como \"PERFIL ESQUERDO\"");
        esquerdo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                editor.putString("esquerdo", pic);
                editor.apply();
                Toast.makeText(getActivity(), "Foto marcada com sucesso", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        MenuItem recortar = menu.add("Recortar Imagem");
        recortar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                UCrop.of(Uri.fromFile(new File(pic)), Uri.fromFile(new File(pic)))
                        .withMaxResultSize(maxWidth, maxHeight)
                        .useSourceImageAspectRatio()
                        .start(getActivity());
                return false;
            }
        });

        MenuItem deletar = menu.add("Deletar imagem");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                File file = new File(pic);
                if(delete(file)) {
                    deleteFileFromMediaStore(getActivity().getContentResolver(),file);
                    Toast.makeText(getActivity(), "Foto deletada com sucesso", Toast.LENGTH_LONG).show();
                    items.clear();
                    items.addAll(insertData());
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), "Erro ao deletar foto", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            MediaScannerConnection.scanFile(getActivity(),
                    new String[] { new File(resultUri.getPath()).toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage","ok");
                            items.clear();
                            items.addAll(insertData());
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), "Erro ao recortar imagem", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean delete(File path) {
        boolean result = true;
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File child : path.listFiles()) {
                    result &= delete(child);
                }
                result &= path.delete(); // Delete empty directory.
            }
            if (path.isFile()) {
                result &= path.delete();
            }
            if (!result) {
                Log.e("Delete", "Delete failed;");
            }
            return result;
        } else {
            Log.e("Delete", "File does not exist.");
            return false;
        }
    }

    public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            String canonicalPath;
            try {
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                canonicalPath = file.getAbsolutePath();
            }
            final Uri uri = MediaStore.Files.getContentUri("external");
            final int result = contentResolver.delete(uri,
                    MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
            if (result == 0) {
                final String absolutePath = file.getAbsolutePath();
                if (!absolutePath.equals(canonicalPath)) {
                    contentResolver.delete(uri,
                            MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                }
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.gridview_imageview_item, viewGroup, false);
                holder = new ViewHolder();
                holder.imgQueue = (SquareImageView) view.findViewById(R.id.picture);
                holder.imgQueue.setMaxHeight(width);
                holder.imgQueue.setMaxWidth(width);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageItem item = (ImageItem) getItem(i);
            final String mpath = item.imagePath;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Click on  Path : ", mpath);
                    if (null != items && !items.isEmpty()) {
                    /*Toast.makeText(
                            getApplicationContext(),
                            "position " + position + " " + images.get(position),
                            Toast.LENGTH_SHORT).show();*/
                        Intent intent = new Intent(getActivity(), VisualizationActivity.class);
                        intent.putExtra("foto", mpath);
                        startActivity(intent);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    pic = mpath;
                    return false;
                }
            });

            Picasso.with(getActivity())
                    .load(new File(item.imagePath))
                    .placeholder(R.drawable.logo)
                    .transform(new BitmapTransform(width, width))
                    .resize(width, width)
                    .centerCrop()
                    .into(holder.imgQueue);
            return view;
        }
    }

    public class ViewHolder {
        SquareImageView imgQueue;
    }

    public void allScan() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(Environment.getExternalStorageDirectory()); // out is your output file
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);

        } else {
            getActivity().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    public void getItemList() {
        allScan();
        items = insertData();
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    private List<ImageItem> insertData() {
        List<ImageItem> items = new ArrayList<>();
        try {
            final String[] columns = {Images.Media.DATA,
                    Images.Media._ID};
            final String orderBy = Images.Media._ID;

            Cursor imagecursor = getActivity().managedQuery(
                    Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    int dataColumnIndex = imagecursor
                            .getColumnIndex(Images.Media.DATA);
                    items.add(new ImageItem(R.drawable.logo, imagecursor.getString(dataColumnIndex)));
                }
//                Log.d("items : "+items.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(items);
        return items;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageLoader != null) {
            mImageLoader.clearMemoryCache();
            mImageLoader.clearDiskCache();
        }
    }

}
