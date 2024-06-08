package com.example.smobileeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smobileeapp.Answer;

import java.util.List;

public class AnswerAdapter extends ArrayAdapter<Answer> {

    public AnswerAdapter(Context context, List<Answer> answers) {
        super(context, 0, answers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_answer, parent, false);
        }

        TextView answerTextView = convertView.findViewById(R.id.answerTextView);
        TextView commentCountTextView = convertView.findViewById(R.id.commentCountTextView); // 새로 추가된 TextView

        Answer answer = getItem(position);

        if (answer != null) {
            answerTextView.setText(answer.getAnswerText());
            answerTextView.setTextColor(getContext().getResources().getColor(R.color.black));

            // 댓글 개수 설정
            commentCountTextView.setText("댓글 개수: " + answer.getReplyCount());

            // 삭제된 답변에 대한 처리
            if (answer.isDeleted()) {
                // 만약 삭제된 답변이라면 숨기거나 다른 처리를 수행할 수 있습니다.
                convertView.setVisibility(View.GONE);
            } else {
                // 삭제되지 않은 경우 보이도록 설정
                convertView.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
}