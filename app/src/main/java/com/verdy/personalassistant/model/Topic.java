package com.verdy.personalassistant.model;

import android.content.ContentValues;

import com.verdy.personalassistant.dao.DAO;

import java.util.ArrayList;

public class Topic implements DAO {
    public int _id;
    public int auxReviewId;
    public int subjectId;
    public String subjectName;
    public String name;
    public String notes;
    public ArrayList<String> reviewDates;

    public Topic() { }

    public Topic(int _id, String name, String notes) {
        this._id = _id;
        this.name = name;
        this.notes = notes;
    }

    public static Topic getTopicForReview(final int auxReviewId, final String name, final String notes, final String subjectName){
        Topic topic = new Topic();
        topic.auxReviewId = auxReviewId;
        topic.name = name;
        topic.notes = notes;
        topic.subjectName = subjectName;
        return topic;
    }

    @Override
    public String getTable() {
        return Metadata.TOPIC_TABLE;
    }

    @Override
    public void setContentValues(ContentValues ct) {
        ct.put(Metadata.COLUMN_NAME, name);
        ct.put(Metadata.TOPIC_NOTE, notes);
        ct.put(Metadata.SUBJECT_ID, subjectId);
    }

    @Override
    public void setUpdateContent(ContentValues ct) {
        ct.put(Metadata.COLUMN_NAME, name);
        ct.put(Metadata.TOPIC_NOTE, notes);
    }

    @Override
    public String getWhereClauseUpdate() {
        return "_id = " + this._id;
    }

    @Override
    public String toString() {
        return name;
    }

}
