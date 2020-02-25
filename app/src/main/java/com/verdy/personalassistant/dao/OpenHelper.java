package com.verdy.personalassistant.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaScannerConnection;
import android.util.Log;

import com.verdy.personalassistant.util.MApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenHelper extends SQLiteOpenHelper {
    private static final String TAG = OpenHelper.class.getSimpleName();
    private static final int SCHEMA_VERSION = 1;
    static final String DB_NAME = "PA.db";
    private static OpenHelper instance;

    private OpenHelper(Context context) {
        super(context,DB_NAME,null,SCHEMA_VERSION);
        Log.d(TAG,"OpenHelper constructor called: " + DB_NAME);
        MediaScannerConnection.scanFile(MApp.mAppContext, new String[] {DB_NAME}, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            loadSQLFrom(db);
        }
        catch(Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            loadSQLFrom(db);
        }
        catch(Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    private void loadSQLFrom(SQLiteDatabase db) {
        List<String> statements = getDDLStatementsFrom();
        try{
            db.beginTransaction();
            for(String stmt: statements){
                Log.d(TAG,"Executing Statement:" + stmt);
                db.execSQL(stmt);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }


    }
    private List<String> getDDLStatementsFrom() {
        ArrayList<String> statements = new ArrayList<>();
        String s = getStringFromAssetFile();
        for (String stmt: s.split(";")) {
            if (stmt.contains("CREATE TRIGGER"))
                stmt += "; END";
            statements.add(stmt);
        }
        return statements;
    }


    private String getStringFromAssetFile() {
        try {
            Context ctx = MApp.mAppContext;
            AssetManager am = ctx.getAssets();
            InputStream is = am.open("PASchema.sql");
            String s = convertStreamToString(is);
            is.close();
            return s;
        }
        catch (IOException x) {
            throw new RuntimeException("Sorry not able to read filename:" + "PASchema.sql",x);
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while (i != -1) {
            baos.write(i);
            i = is.read();
        }
        return baos.toString();
    }


    static synchronized OpenHelper getInstance(){
        if (instance == null) {
            instance = new OpenHelper(MApp.mAppContext);
        }
        return instance;
    }

}
