package com.verdy.personalassistant.dao;

import com.verdy.personalassistant.model.Subject;

import java.util.ArrayList;

public class SubjectDao extends DaoImplBase {

    public static ArrayList<Subject>getSubjects(){
        try{
            cursor = DAOManager.select("SELECT _id, name FROM subject");
            return testForNotEmptyCursorByMovingToFirst()? getSubjectFromC() : null;
        }finally {
            closeCursor();
        }
    }

    private static ArrayList<Subject> getSubjectFromC(){
        final int listSize = cursor.getCount();
        final ArrayList<Subject> subjects = new ArrayList<>(listSize);
        for (int index = 0; index < listSize; index ++, cursor.moveToNext()){
            subjects.add(new Subject(cursor.getInt(0), cursor.getString(1)));
        }
        return subjects;
    }
}
