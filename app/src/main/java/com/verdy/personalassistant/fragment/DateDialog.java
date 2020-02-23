package com.verdy.personalassistant.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.verdy.personalassistant.util.FormatterUtility;

import java.util.Calendar;
import java.util.Date;

public class DateDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnDateSelectedListener mCallBack;

    public void setSelectedDateListener(OnDateSelectedListener listener){
        mCallBack = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
        datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
        return datePicker;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month++;
        mCallBack.onDateSelected(FormatterUtility.formatDate(year, month, day));
    }

}
