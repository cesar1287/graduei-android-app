package com.rivastecnologia.graduei.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rivastecnologia.graduei.controller.domain.User;

public class GradueiDAO extends SQLiteOpenHelper {

    private static final String DATABASE = "bd_graduei";
    private static final int VERSAO = 1;
    private static final String TABELA = "user";

    public GradueiDAO(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABELA + "(" +
                "id TEXT PRIMARY KEY, " +
                "email TEXT," +
                "name TEXT NOT NULL, " +
                "password TEXT, " +
                "profile_pic TEXT, " +
                "telefone TEXT" +
                ");";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABELA;
        db.execSQL(sql);
        onCreate(db);
    }

    public void insert(User user) {
        ContentValues cv = new ContentValues();
        cv.put("id", user.getId());
        cv.put("email", user.getEmail());
        cv.put("name", user.getNome());
        cv.put("profile_pic", user.getProfilePicURL());

        getWritableDatabase().insert(TABELA, null, cv);
    }

    public boolean isUserCreated(String id) {
        String sql = "SELECT id FROM " + TABELA + " WHERE id = ?;";
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
        getWritableDatabase().update(TABELA, cv, "id=?", args);
    }

    public User getUserbyID(String id) {

        String sql = "SELECT * FROM " + TABELA + " WHERE id = ?";
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
}

