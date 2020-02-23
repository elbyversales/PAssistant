package com.verdy.personalassistant.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.controller.MainActivity;
import com.verdy.personalassistant.dao.DAOManager;
import com.verdy.personalassistant.model.Subject;

/*topic_title_tag 0,
        save_topic 1,
        topic_name 2;*/

public class AddSubjectFragment extends DialogFragment implements  View.OnClickListener {
    public static String TAG = "add_subject_fragment";
    private EditText name;
    private MainActivity activity;

    public static AddSubjectFragment newInstance(){
        return new AddSubjectFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.add_subject, container, false);
        view.getChildAt(1).setOnClickListener(this);
        name = (EditText) view.getChildAt(2);
        return view;
    }

    private void insertNewTopic(){
        final String name = this.name.getText().toString();
        if(!name.isEmpty()){
            Subject subject = new Subject(0, name);
            subject._id = DAOManager.save(subject);
            activity.addSubject(subject);
            Toast.makeText(getActivity(), "Subject saved", Toast.LENGTH_SHORT).show();
            dismiss();
        }else{
            Toast.makeText(getActivity(), "The name can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        insertNewTopic();
    }


}
