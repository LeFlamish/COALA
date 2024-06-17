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

public class ReplyAdapter extends ArrayAdapter<Reply> {

    private Context mContext;
    private List<Reply> mReplyList;

    public ReplyAdapter(Context context, List<Reply> replyList) {
        super(context, 0, replyList);
        mContext = context;
        mReplyList = replyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
        }

        Reply currentReply = mReplyList.get(position);
        TextView replyText = listItem.findViewById(R.id.replyTextView);

        replyText.setText(currentReply.getReplyText());
        replyText.setTextColor(mContext.getResources().getColor(android.R.color.black));

        return listItem;
    }
}