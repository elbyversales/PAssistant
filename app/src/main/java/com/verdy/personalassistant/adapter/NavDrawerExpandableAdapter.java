package com.verdy.personalassistant.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.model.Subject;
import com.verdy.personalassistant.util.Constant;

import java.util.ArrayList;

public class NavDrawerExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Subject> subjects;

    public NavDrawerExpandableAdapter(final Context context,final ArrayList<Subject> subjects){
        this.context = context;
        this.subjects = subjects;
    }

    @Override
    public NavOption getGroup(int groupPosition) {
        return NavOption.values()[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return 2;

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        NavOption navOption = getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_item_header, null);
        }
        TextView navName = (TextView)((ViewGroup)convertView).getChildAt(0);
        Drawable icon = context.getDrawable(navOption.iconId);
        icon.setBounds(0,0,50,50);
        navName.setCompoundDrawablesRelative(icon, null, null, null);
        navName.setText(navOption.name);
        return convertView;
    }

    @Override
    public Subject getChild(int groupPosition, int childPosititon) {
        return this.subjects.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String topicName = getChild(groupPosition, childPosition).name;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_child_item, null);
        }
        TextView txtListChild = (TextView) ((LinearLayout)convertView).getChildAt(0);
        txtListChild.setText(topicName);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition == 1 && subjects != null){
            return subjects.size();
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addNewSubject(final Subject subject){
        if(subjects == null){
            subjects = new ArrayList<>();
        }
        subjects.add(subject);
    }

    public void deleteSubject(final Subject subject){
        subjects.remove(subject);
        notifyDataSetChanged();
    }

    public enum NavOption {

        TODAY_REVIEWS(android.R.drawable.ic_menu_agenda, Constant.NAV_REVIEWS, "Reviews"),
        SUBJECTS(android.R.drawable.sym_contact_card, Constant.NAV_SUBJECTS, "Subjects");
        public int iconId;
        public int navId;
        public String name;

        NavOption(int iconId, int navId, String name){
            this.iconId = iconId;
            this.navId = navId;
            this.name = name;
        }
    }
}
