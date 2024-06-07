package com.example.smobileeapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

    private Context mContext; // Context 변수 추가

    public QuestionAdapter(Context context, List<Question> questions) {
        super(context, 0, questions);
        mContext = context; // Context 초기화
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext); // mContext를 사용하여 LayoutInflater 가져오기
            convertView = inflater.inflate(R.layout.item_question, parent, false);
        }

        TextView tvProblemNum = convertView.findViewById(R.id.tv_problem_num);
        TextView problemTitleTextView = convertView.findViewById(R.id.problemTitleTextView);
        TextView questionTextView = convertView.findViewById(R.id.questionTextView);
        TextView answerCountTextView = convertView.findViewById(R.id.answerCountTextView); // 답변 개수를 표시할 TextView 추가
        Question question = getItem(position);

        if (question != null) {
            // 문제 번호 설정
            tvProblemNum.setText(String.valueOf(question.getProblemNum()));
            // 텍스트 색상 회색으로 설정
            tvProblemNum.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));

            // 문제 제목 설정
            problemTitleTextView.setText(question.getProblemTitle());
            // 질문 제목 설정
            questionTextView.setText(question.getQuestionTitle());

            // 답변 개수 설정
            answerCountTextView.setText("답변 개수: " + question.getAnswerCount());

            // 문제 번호의 난이도에 따라 배경색 설정
            tvProblemNum.setBackgroundResource(R.drawable.rounded_background);
            GradientDrawable background = (GradientDrawable) tvProblemNum.getBackground();

            String difficulty = question.getProblemTier();
            if (difficulty != null) {
                int backgroundColor = ContextCompat.getColor(getContext(), R.color.default_color); // 기본 배경색 설정
                if (difficulty.contains("골드")) {
                    backgroundColor = ContextCompat.getColor(getContext(), R.color.gold);
                } else if (difficulty.contains("실버")) {
                    backgroundColor = ContextCompat.getColor(getContext(), R.color.silver);
                } else if (difficulty.contains("브론즈")) {
                    backgroundColor = ContextCompat.getColor(getContext(), R.color.bronze);
                } else if (difficulty.contains("플래티넘")) {
                    backgroundColor = ContextCompat.getColor(getContext(), R.color.platinum);
                }
                background.setColor(backgroundColor);
            }
        }

        return convertView;
    }
}