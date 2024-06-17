package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

    private final Context mContext;
    private final List<Question> mQuestionList;

    public QuestionAdapter(@NonNull Context context, @NonNull List<Question> questionList) {
        super(context, 0, questionList);
        this.mContext = context;
        this.mQuestionList = questionList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_question, parent, false);
        }

        LinearLayout problem_num_layout = convertView.findViewById(R.id.problem_num_layout);
        TextView tvProblemNum = convertView.findViewById(R.id.tv_problem_num);
        TextView problemTitleTextView = convertView.findViewById(R.id.problemTitleTextView);
        TextView questionTextView = convertView.findViewById(R.id.questionTextView);
        TextView goodhelpCountTextView = convertView.findViewById(R.id.goodghelpCountTextView);
        TextView answerCountTextView = convertView.findViewById(R.id.answerCountTextView);
        ImageView isSolvedImageView = convertView.findViewById(R.id.success_image);
        Question question = getItem(position);

        if (question != null) {
            tvProblemNum.setText(String.valueOf(question.getProblemNum()));
            tvProblemNum.setTextColor(ContextCompat.getColor(mContext, R.color.light_gray));

            problemTitleTextView.setText(question.getProblemTitle());
            questionTextView.setText(question.getQuestionTitle());
            goodhelpCountTextView.setText("도움이 됐어요! : " + question.getGoodHelpCount() + "명");
            answerCountTextView.setText("답변 개수 : " + question.getAnswerCount());

            problem_num_layout.setBackgroundResource(R.drawable.rounded_background);
            // tvProblemNum.setBackgroundResource(R.drawable.rounded_background);
            GradientDrawable background = (GradientDrawable) problem_num_layout.getBackground();

            boolean solved = question.isSolved();
            if (solved) {
                isSolvedImageView.setVisibility(View.VISIBLE);
            } else {
                isSolvedImageView.setVisibility(View.INVISIBLE);
            }

            String difficulty = question.getProblemTier();
            if (difficulty != null) {
                int backgroundColor = ContextCompat.getColor(mContext, R.color.default_color);
                if (difficulty.contains("골드")) {
                    backgroundColor = ContextCompat.getColor(mContext, R.color.gold);
                } else if (difficulty.contains("실버")) {
                    backgroundColor = ContextCompat.getColor(mContext, R.color.silver);
                } else if (difficulty.contains("브론즈")) {
                    backgroundColor = ContextCompat.getColor(mContext, R.color.bronze);
                } else if (difficulty.contains("플래티넘")) {
                    backgroundColor = ContextCompat.getColor(mContext, R.color.platinum);
                }
                background.setColor(backgroundColor);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("userIdToken", question.getUserIdToken());
                    intent.putExtra("questionId", question.getQuestionId());
                    intent.putExtra("problemNum", question.getProblemNum());
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }
}