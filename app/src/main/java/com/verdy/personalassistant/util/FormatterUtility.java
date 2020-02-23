package com.verdy.personalassistant.util;

import java.util.Calendar;

public class FormatterUtility {
    public static String formatDate(int year, int month, int day){
        month ++;
        return year + "-" + (month < 10? "0" + month: month) + "-" + (day < 10 ? "0" + day : day);
    }

    public static String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        return formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
