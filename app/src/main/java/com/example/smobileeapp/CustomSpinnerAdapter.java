package com.example.smobileeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> mItems;
    private final int mResource;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mResource = resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        TextView textView = view.findViewById(R.id.spinner_item_text);
        String item = mItems.get(position);
        textView.setText(item);

        // 텍스트에 따른 색상 설정
        if (item.contains("플래티넘")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.platinum));
        } else if (item.contains("골드")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.gold));
        } else if (item.contains("실버")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.silver));
        } else if (item.contains("브론즈")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.bronze));
        } else {
            textView.setTextColor(mContext.getResources().getColor(android.R.color.black)); // 기본 색상
        }

        return view;
    }
}