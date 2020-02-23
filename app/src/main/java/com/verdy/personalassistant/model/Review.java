package com.verdy.personalassistant.model;

import android.content.ContentValues;

import com.verdy.personalassistant.dao.DAO;

public class Review implements DAO {
    public int topicId;
    public String date;
    public boolean checked;

    public Review() {
    }

    public Review(int topicId, String date, int checked) {
        this.topicId = topicId;
        this.date = date;
        this.checked = checked == 1;
    }

    public Review(int topicId, String date) {
        this.topicId = topicId;
        this.date = date;
    }

    @Override
    public String getTable() {
        return Metadata.REVIEW_TABLE;
    }

    @Override
    public void setContentValues(ContentValues ct) {
        ct.put(Metadata.REVIEW_TOPIC_ID, topicId);
        ct.put(Metadata.REVIEW_DATE, date);
    }

    @Override
    public void setUpdateContent(ContentValues content) {
    }

    @Override
    public String getWhereClauseUpdate() {
        return null;
    }

}
