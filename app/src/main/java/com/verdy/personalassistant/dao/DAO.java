package com.verdy.personalassistant.dao;

import android.content.ContentValues;

public interface DAO {

    String getTable();
    void setContentValues(ContentValues ct);
    void setUpdateContent(ContentValues content);
    String getWhereClauseUpdate();

}
