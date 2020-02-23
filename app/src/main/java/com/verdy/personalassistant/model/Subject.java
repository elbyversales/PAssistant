package com.verdy.personalassistant.model;

import android.content.ContentValues;

import com.verdy.personalassistant.dao.DAO;

public class Subject implements DAO {
    public int _id;
    public String name;

    public Subject(int _id, String name) {
        this._id = _id;
        this.name = name;
    }

    @Override
    public String getTable() {
        return Metadata.SUBJECT_TABLE;
    }

    @Override
    public void setContentValues(ContentValues ct) {
        ct.put(Metadata.COLUMN_NAME, name);
    }

    @Override
    public void setUpdateContent(ContentValues content) {
    }

    @Override
    public String getWhereClauseUpdate() {
        return "_id = " + _id;
    }
}
