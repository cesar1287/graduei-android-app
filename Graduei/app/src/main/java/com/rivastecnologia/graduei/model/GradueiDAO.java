package com.rivastecnologia.graduei.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rivastecnologia.graduei.R;
import com.rivastecnologia.graduei.controller.domain.User;
import com.rivastecnologia.graduei.controller.util.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class GradueiDAO extends SQLiteOpenHelper {

    private static final String DATABASE = "bd_graduei";
    private static final int VERSAO = 4;
    private static final String TABELA_USER = "user";
    private static final String TABELA_PICTURE = "picture";

    public GradueiDAO(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql_user = "CREATE TABLE " + TABELA_USER + "(" +
                "id TEXT PRIMARY KEY, " +
                "email TEXT," +
                "name TEXT NOT NULL, " +
                "password TEXT, " +
                "profile_pic TEXT, " +
                "telefone TEXT" +
                ");";

        String sql_pic = "CREATE TABLE " + TABELA_PICTURE + "(" +
                "id TEXT, " +
                "url_pic TEXT, " +
                "PRIMARY KEY ( id, url_pic)"+
                ");";
        try {
            db.execSQL(sql_user);
            db.execSQL(sql_pic);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql_user = "DROP TABLE IF EXISTS " + TABELA_USER;
        String sql_pic = "DROP TABLE IF EXISTS " + TABELA_PICTURE;
        db.execSQL(sql_user);
        db.execSQL(sql_pic);
        onCreate(db);
    }

    public void insert(User user) {
        ContentValues cv = new ContentValues();
        cv.put("id", user.getId());
        cv.put("email", user.getEmail());
        cv.put("name", user.getNome());
        cv.put("profile_pic", user.getProfilePicURL());

        getWritableDatabase().insert(TABELA_USER, null, cv);
    }

    public boolean isUserCreated(String id) {
        String sql = "SELECT id FROM " + TABELA_USER + " WHERE id = ?;";
        String[] args = {String.valueOf(id)};
        final Cursor cursor = getReadableDatabase().rawQuery(sql, args);

        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public void update(User user) {

        ContentValues cv = new ContentValues();
        cv.put("id", user.getId());
        cv.put("email", user.getEmail());
        cv.put("name", user.getNome());
        cv.put("telefone", user.getTelefone());
        cv.put("password", user.getSenha());
        cv.put("profile_pic", user.getProfilePicURL());

        String args[] = {user.getId()};
        getWritableDatabase().update(TABELA_USER, cv, "id=?", args);
    }

    public User getUserbyID(String id) {

        String sql = "SELECT * FROM " + TABELA_USER + " WHERE id = ?";
        String[] args = {id};
        final Cursor cursor = getReadableDatabase().rawQuery(sql, args);
        User user = new User();

        while(cursor.moveToNext()){

            user.setNome(cursor.getString(cursor.getColumnIndex("name")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
            user.setSenha(cursor.getString(cursor.getColumnIndex("password")));
        }
        cursor.close();

        return user;
    }

    public void insertPicture(String id, String url){
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("url_pic", url);

        getWritableDatabase().insert(TABELA_PICTURE, null, cv);
    }

    public List<ImageItem> getUserPicture(String id){
        List<ImageItem> pics = new ArrayList<>();

        String sql = "SELECT * FROM " + TABELA_PICTURE + " WHERE id = ?";
        String[] args = {id};
        final Cursor cursor = getReadableDatabase().rawQuery(sql, args);

        while(cursor.moveToNext()){

            ImageItem item = new ImageItem(R.drawable.logo, cursor.getString(cursor.getColumnIndex("url_pic")));
            pics.add(item);
        }

        cursor.close();
        return pics;
    }
}

