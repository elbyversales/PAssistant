package com.verdy.personalassistant.dao;

import com.verdy.personalassistant.model.Topic;

import java.util.ArrayList;

public class TopicDao extends DaoImplBase {

    private static ArrayList<Topic> topics = new ArrayList<>();

    public static ArrayList<Topic> getTopics(final int subjectId){
        try{
            final String select = "SELECT _id, name, note FROM topic WHERE subject_id = " + subjectId;
            cursor = DAOManager.select(select);
            testForNotEmptyCursorByMovingToFirst();
            setTopicsFromCursor();
            return topics ;
        }finally {
            closeCursor();
        }
    }

    private static void setTopicsFromCursor(){
        int listSize = cursor.getCount();
        topics.clear();
        for (int index = 0; index < listSize; index ++, cursor.moveToNext()){
            topics.add(new Topic(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }
    }

}
