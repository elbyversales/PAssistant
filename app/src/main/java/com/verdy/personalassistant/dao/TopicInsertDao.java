package com.verdy.personalassistant.dao;

import android.content.ContentValues;

import com.verdy.personalassistant.model.Review;
import com.verdy.personalassistant.model.Topic;
import com.verdy.personalassistant.util.FormatterUtility;

import java.util.ArrayList;

public class TopicInsertDao {
    private static ContentValues values = new ContentValues();

    public static void save(final Topic topic){
        try{
            DAOManager.beginTransaction();
            topic._id = DAOManager.save(topic, values);
            saveReviews(topic);
            DAOManager.setTransactionSuccessful();
        }finally {
            DAOManager.endTransaction();
        }
    }

    private static void saveReviews(final Topic topic){
        if(topic.reviewDates != null){
            final String todayDate = FormatterUtility.getTodaysDate();
            final int topicId = topic._id;
            ArrayList<String> dates = topic.reviewDates;
            final Review review = new Review();
            for(String reviewDate : dates){
                review.date = reviewDate;
                review.topicId = topicId;
                int reviewId =  DAOManager.save(review);
                processReviewForToday(todayDate, reviewDate,reviewId, topicId);
            }
        }
    }

    public static void processReviewForToday(final String todayDate, final String reviewDate, final int reviewId, final int topicId ){
        if(todayDate.equals(reviewDate)){
            DAOManager.execute("INSERT INTO review_reminder (review_id, date, topic_id) VALUES (" + reviewId + ", '" + todayDate + "' , " + topicId + ")");
        }
    }

}
