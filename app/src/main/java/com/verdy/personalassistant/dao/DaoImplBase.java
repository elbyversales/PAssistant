package com.verdy.personalassistant.dao;

import android.database.Cursor;

abstract class DaoImplBase {
    static Cursor cursor;

    static void closeCursor(){
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
            cursor = null;
        }
    }

    static boolean testForNotEmptyCursorByMovingToFirst(){
        return cursor != null && cursor.moveToFirst();
    }

}
