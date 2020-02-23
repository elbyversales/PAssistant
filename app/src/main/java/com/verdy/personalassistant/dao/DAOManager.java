package com.verdy.personalassistant.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DAOManager {

    private static SQLiteDatabase db = OpenHelper.getInstance().getWritableDatabase();

    static int save(final DAO dao, final ContentValues data){
        dao.setContentValues(data);
        return (int) db.insert(dao.getTable(), null, data);
    }

    public static int save(final DAO dao){
        ContentValues data = new ContentValues();
        dao.setContentValues(data);
        return  (int) db.insert(dao.getTable(), null, data);
    }

    public static boolean update(DAO item){
        establishDC();
        final ContentValues data = new ContentValues();
        item.setUpdateContent(data);
        final int rows = db.update(item.getTable(), data, item.getWhereClauseUpdate(),null );
        return rows > 0;
    }

    public static boolean delete(final DAO item){
        establishDC();
        return db.delete(item.getTable(), item.getWhereClauseUpdate(), null) > 0;
    }

    static Cursor select(final String selectClause){
        establishDC();
        return db.rawQuery(selectClause, null);
    }

    public static void execute(final String sql){
        db.execSQL(sql);
    }

    private static void establishDC(){
        if(db == null) db = OpenHelper.getInstance().getWritableDatabase();
    }

    static void beginTransaction(){
        db.beginTransaction();
    }

    static void endTransaction(){
        db.endTransaction();
    }

    static void setTransactionSuccessful(){
        db.setTransactionSuccessful();
    }

}
