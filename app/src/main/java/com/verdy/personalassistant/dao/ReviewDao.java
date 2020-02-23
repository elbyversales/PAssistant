package com.verdy.personalassistant.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.verdy.personalassistant.model.Review;
import com.verdy.personalassistant.model.ReviewReminder;
import com.verdy.personalassistant.model.Topic;

import java.util.ArrayList;

public class ReviewDao extends DaoImplBase {

    public static ArrayList<? extends DAO> getReviews(final ConditionFlags condition, final int _id, final String date){
        try{
            cursor = _id == 0? DAOManager.select(condition.getSelectClause(date)) : DAOManager.select(condition.getSelectClause(_id));
            return testForNotEmptyCursorByMovingToFirst()? extractFromCursor(condition, cursor): null;
        }finally {
            closeCursor();
        }
    }

    private static ArrayList<DAO> extractFromCursor(final ConditionFlags condition, final Cursor c){
        int listSize = c.getCount();
        ArrayList<DAO> elements = new ArrayList<>(listSize);
        for (int index = 0; index < listSize; index ++, c.moveToNext()){
            elements.add(condition.getElementFromC(c));
        }
        return elements;
    }


    public static void insertReminders(final ArrayList<ReviewReminder> reviews){
        try{
            ContentValues data = new ContentValues();
            DAOManager.beginTransaction();
            for(ReviewReminder review : reviews){
                DAOManager.save(review, data);
                data.clear();
            }
            DAOManager.setTransactionSuccessful();
        }finally {
            DAOManager.endTransaction();
        }

    }

    public enum ConditionFlags{
        REVIEWS_BY_TOPIC("SELECT _id, date, checked FROM review WHERE topic_id = "){
            @Override
            DAO getElementFromC(final Cursor c){
                return new Review(c.getInt(0), c.getString(1), c.getInt(2));
            }
        },
        TOPICS_TO_REMINDER_BY_DAY("SELECT  rr.review_id, t.name, t.note, s.name FROM review_reminder rr JOIN topic t ON t._id = rr.topic_id JOIN subject s ON t.subject_id = s._id   WHERE rr.date = '%s'"){
            @Override
            DAO getElementFromC(final Cursor c){
                return Topic.getTopicForReview(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
            }
        },
        REVIEWS_TO_REMINDER("SELECT _id, topic_id, date FROM review WHERE date = '%s'"){
            @Override
            DAO getElementFromC(final Cursor c){
                return new ReviewReminder(c.getInt(0), c.getInt(1), c.getString(2));
            }
        },
        FAILED_REVIEWS ("SELECT rr.review_id, rr.date, t.name, t.note, s.name FROM review_reminder rr JOIN topic t ON t._id = rr.topic_id JOIN subject s ON t.subject_id = s._id WHERE rr.date < '%s'"){
            @Override
            DAO getElementFromC(final Cursor c){
                return new ReviewReminder(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4));
            }
        };

        private String SELECT_CLAUSE;

        ConditionFlags(final String SELECT_CLAUSE){
            this.SELECT_CLAUSE = SELECT_CLAUSE;
        }

        String getSelectClause(String elementDate){
            return String.format(SELECT_CLAUSE, elementDate);
        }

        String getSelectClause(int elementId){
            return SELECT_CLAUSE + elementId;
        }

        DAO getElementFromC(final Cursor c){
            return null;
        }
    }

}
