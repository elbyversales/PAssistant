package com.verdy.personalassistant.model;

import android.content.ContentValues;

import com.verdy.personalassistant.dao.DAO;

public class ReviewReminder implements DAO {
    public int reviewId;
    public int topicId;
    public String date;
    public String topicName;
    public String topicNote;
    public String subjectName;

    public ReviewReminder(int reviewId, String date, String topicName, String topicNote, String subjectName) {
        this.reviewId = reviewId;
        this.date = date;
        this.topicName = topicName;
        this.topicNote = topicNote;
        this.subjectName = subjectName;
    }

    public ReviewReminder(int reviewId, int topicId, String date) {
        this.reviewId = reviewId;
        this.topicId = topicId;
        this.date = date;
    }

    @Override
    public String getTable() {
        return Metadata.REVIEW_REMINDER_TABLE;
    }

    @Override
    public void setContentValues(ContentValues ct) {
        ct.put(Metadata.REVIEW_REMINDER_REVIEW_ID, reviewId);
        ct.put(Metadata.REVIEW_DATE, date);
        ct.put(Metadata.REVIEW_TOPIC_ID, topicId);
    }

    @Override
    public void setUpdateContent(ContentValues content) {

    }

    @Override
    public String getWhereClauseUpdate() {
        return null;
    }
}
