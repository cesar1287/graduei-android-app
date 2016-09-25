package com.rivastecnologia.graduei.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}

